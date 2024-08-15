package dev.woos.toons_api.domain.webtoon

import dev.woos.toons_api.domain.common.Platform
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import java.time.DayOfWeek

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
}