package dev.woos.toons_api.usecase

import dev.woos.toons_api.api.dto.WebtoonDto
import dev.woos.toons_api.domain.alarm.AlarmRepository
import dev.woos.toons_api.domain.alarm.AlarmStatus
import dev.woos.toons_api.domain.member.MemberRepository
import dev.woos.toons_api.domain.webtoon.WebtoonCrawler
import dev.woos.toons_api.domain.webtoon.WebtoonRepository
import dev.woos.toons_api.infra.EmailService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class WebtoonService(
    private val webtoonRepository: WebtoonRepository,
    private val webtoonCrawler: WebtoonCrawler,
    private val alarmRepository: AlarmRepository,
    private val emailService: EmailService,
    private val memberRepository: MemberRepository
) {
    private val logger = KotlinLogging.logger {}
    suspend fun crawlerNAVER() {
        val crawlerResult = webtoonCrawler.getNaverWebtoonList().getOrThrow()
        val ids = webtoonRepository.upsertBatch(crawlerResult)
        logger.info { "upserted webtoons: $ids" }
    }

    suspend fun crawlerCompletedNAVER() {
        val crawlerResult = webtoonCrawler.getNaverCompletedWebtoonTitles().getOrThrow()
        logger.info { "crawlerResult : ${crawlerResult}" }
        val platformIds = crawlerResult.map { it.first }
        webtoonRepository.findAllByIdIn(platformIds).map {
            it.isCompleted()
            it
        }.also { webtoonRepository.saveAll(it).collect() }
    }

    suspend fun crawlerKAKAO() = coroutineScope {
        val generalWeekdaysResponse = async { webtoonCrawler.getKakaoGeneralWeekdaysWebtoonList().getOrThrow() }
        val novelWeekdaysResponse = async { webtoonCrawler.getKakaoNovelWeekdaysWebtoonList().getOrThrow() }
        val crawlerResult = generalWeekdaysResponse.await() + novelWeekdaysResponse.await()
        val ids = webtoonRepository.upsertBatch(crawlerResult)
        logger.info { "upserted webtoons: $ids" }
    }

    suspend fun crawlerCompletedKAKAO() {
        val crawlerResult = webtoonCrawler.getKakaoCompletedWebtoonTitles().getOrThrow()
        logger.info { "crawlerResult : ${crawlerResult}" }
        val platformIds = crawlerResult.map { it.first }
        webtoonRepository.findAllByIdIn(platformIds).map {
            it.isCompleted()
            it
        }.also { webtoonRepository.saveAll(it).collect() }
    }

    @Transactional(readOnly = true)
    suspend fun findAllWithFilters(
        pageable: Pageable, title: String?, days: List<String>?, platforms: List<String>?
    ): Page<WebtoonDto> = coroutineScope {
        val webtoonsDeferred = async {
            webtoonRepository.findAllWithFilters(pageable, title, days, platforms).toList()
        }
        val countDeferred = async {
            webtoonRepository.countWithFilters(title, days, platforms)
        }

        val webtoonList = webtoonsDeferred.await()
        val count = countDeferred.await()

        PageImpl(
            webtoonList.map {
                WebtoonDto(
                    id = it.id,
                    title = it.title,
                    thumbnailUrl = it.thumbnailUrl,
                    platform = it.platform,
                    dayOfWeek = it.dayOfWeek,
                    link = it.link
                )
            }.toList(), pageable, count
        )
    }

    //    @Transactional
    suspend fun sendEndedWebtoonAlert() {
        val endedWebtoons = webtoonRepository.findTop10ByCompletedTrueOrderByUpdatedAtDesc()
            .toList()

        val relatedAlarms = alarmRepository
            .findAllByWebtoonIdInAndStatusIs(
                endedWebtoons.map { it.id }.toList(),
                AlarmStatus.NOT_SENT
            )
            .toList()

        val groupByMemberId = relatedAlarms.groupBy { it.memberId }

        val memberIds = relatedAlarms.map { it.memberId }.toList()

        val members =
            memberRepository.findAllByIdInAndVerifiedEmailIsNotNullAndSubscribeIsTrue(memberIds).toList()
        logger.info { "발송 대상 알람 수: ${relatedAlarms.size}" }
        logger.info { "발송 대상 회원 수: ${members.size}" }
        val alarmsToUpdate = coroutineScope {
            val jobs = members.map { member ->
                val alarms = groupByMemberId[member.id]!!
                val webtoons = alarms.map { alarm ->
                    endedWebtoons.find { it.id == alarm.webtoonId }!!
                }
                async(Dispatchers.IO) {
                    val result = emailService.sendEndedWebtoonAlert(
                        member.verifiedEmail!!,
                        member.name,
                        webtoons.map {
                            EmailService.WebtoonEmailDto(
                                link = it.link,
                                thumbnailUrl = it.thumbnailUrl,
                                title = it.title
                            )
                        },
                        member.id
                    )
                    result to alarms
                }
            }
            jobs.awaitAll()
                .flatMap { (result, alarms) ->
                    logger.info { "email sent: ${result.isSuccess} ${alarms.size}" }
                    if (result.isFailure) {
                        alarms.map { it.sendFail() }
                    } else {
                        alarms.map { it.send() }
                    }
                }
        }
        logger.info { "알람 업데이트 수: ${alarmsToUpdate.size}" }
        alarmRepository.saveAll(alarmsToUpdate).collect()


    }


}
