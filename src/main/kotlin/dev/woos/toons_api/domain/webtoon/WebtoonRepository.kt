package dev.woos.toons_api.domain.webtoon

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import reactor.core.publisher.Flux

interface WebtoonRepository: CoroutineCrudRepository<Webtoon, Long>, CustomWebtoonRepository {
    suspend fun findAllByIdIn(ids: List<Long>): Flow<Webtoon>
}