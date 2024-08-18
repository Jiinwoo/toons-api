package dev.woos.toons_api.domain.webtoon

interface WebtoonCrawler {
    suspend fun getNaverWebtoonList(): Result<List<Webtoon>>
    suspend fun getNaverCompletedWebtoonTitles(): Result<List<Pair<Long, String>>>
    suspend fun getKakaoGeneralWeekdaysWebtoonList(): Result<List<Webtoon>>
    suspend fun getKakaoNovelWeekdaysWebtoonList(): Result<List<Webtoon>>
    suspend fun getKakaoCompletedWebtoonTitles(): Result<List<Pair<Long, String>>>
}