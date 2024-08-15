package dev.woos.toons_api.domain.webtoon

interface NAVERCrawler {
    suspend fun getWebtoonList(): Result<List<Webtoon>>
    suspend fun getCompletedWebtoonTitles(): Result<List<Pair<Long, String>>>
}