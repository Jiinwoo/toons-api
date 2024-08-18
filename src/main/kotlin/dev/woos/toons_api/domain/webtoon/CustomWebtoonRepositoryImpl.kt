package dev.woos.toons_api.domain.webtoon

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toFlux

@Component
class CustomWebtoonRepositoryImpl(
    private val template: R2dbcEntityTemplate
) : CustomWebtoonRepository {
    override suspend fun upsertBatch(webtoons: List<Webtoon>): List<Long> {
        val sql = """
            INSERT INTO tb_webtoon (title, thumbnail_url, day_of_week, link, platform, platform_id)
            VALUES (:title, :thumbnailUrl, :dayOfWeek, :link, :platform, :platformId)
            ON DUPLICATE KEY UPDATE
                title = VALUES(title),
                thumbnail_url = VALUES(thumbnail_url),
                day_of_week = VALUES(day_of_week),
                link = VALUES(link)
        """.trimIndent()
        return template.databaseClient.inConnectionMany { connection ->
            val statement = connection.createStatement(sql)
            webtoons
                .forEachIndexed { index, webtoon ->
                    statement
                        .returnGeneratedValues("id")
                        .bind("title", webtoon.title)
                        .bind("thumbnailUrl", webtoon.thumbnailUrl)
                        .bind("dayOfWeek", webtoon.dayOfWeek.name)
                        .bind("link", webtoon.link)
                        .bind("platform", webtoon.platform.name)
                        .bind("platformId", webtoon.platformId)
                    if (index != webtoons.size - 1) {
                        statement.add()
                    }
                }

            statement.execute().toFlux()
                .flatMap { result ->
                    result.map { row, _ ->
                        row.get("id", Long::class.java)!!
                    }
                }
        }.collectList().awaitSingle()
    }

    override suspend fun findAllWithFilters(
        pageable: Pageable,
        title: String?,
        days: List<String>?,
        platforms: List<String>?
    ): Flow<Webtoon> {
        val query = Query.query(createCriteria(title, days, platforms))
            .with(pageable)

        return template.select(query, Webtoon::class.java).asFlow()
    }

    override suspend fun countWithFilters(
        title: String?,
        days: List<String>?,
        platforms: List<String>?
    ): Long {
        val query = Query.query(createCriteria(title, days, platforms))
        return template.count(query, Webtoon::class.java).awaitSingle()
    }

    private suspend fun createCriteria(
        title: String?,
        days: List<String>?,
        platforms: List<String>?
    ): Criteria {
        var criteria = Criteria.empty()
        if (!title.isNullOrEmpty()) {
            title.let { criteria = criteria.and("title").like("%$title%") }
        }
        if (!days.isNullOrEmpty()) {
            days.let { criteria = criteria.and("day_of_week").`in`(it) }
        }
        if (!platforms.isNullOrEmpty()) {
            platforms.let { criteria = criteria.and("platform").`in`(it) }
        }

        return criteria
    }

}