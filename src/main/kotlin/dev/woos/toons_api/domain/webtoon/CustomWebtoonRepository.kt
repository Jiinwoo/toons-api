package dev.woos.toons_api.domain.webtoon

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable

interface CustomWebtoonRepository {
    suspend fun findAllWithFilters(
        pageable: Pageable,
        title: String?,
        days: List<String>?,
        platforms: List<String>?
    ): Flow<Webtoon>

    suspend fun countWithFilters(
        title: String?,
        days: List<String>?,
        platforms: List<String>?
    ): Long

    suspend fun upsertBatch(webtoons: List<Webtoon>): List<Long>
}