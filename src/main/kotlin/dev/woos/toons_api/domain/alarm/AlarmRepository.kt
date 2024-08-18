package dev.woos.toons_api.domain.alarm

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AlarmRepository: CoroutineCrudRepository<Alarm, Long> {
    suspend fun findAllByMemberId(memberId: Long): Flow<Alarm>
    suspend fun deleteByIdAndMemberId(id: Long, memberId: Long)
}