package dev.woos.toons_api.domain.webtoon

interface CustomWebtoonRepository {
    suspend fun upsertBatch(webtoons: List<Webtoon>): List<Long>
}