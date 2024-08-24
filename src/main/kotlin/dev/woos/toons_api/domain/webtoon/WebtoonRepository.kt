package dev.woos.toons_api.domain.webtoon

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface WebtoonRepository : CoroutineCrudRepository<Webtoon, Long>, CustomWebtoonRepository {
    suspend fun findAllByIdIn(ids: List<Long>): Flow<Webtoon>
    suspend fun findTop10ByCompletedTrueOrderByUpdatedAtDesc(): Flow<Webtoon>
}