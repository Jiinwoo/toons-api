package dev.woos.toons_api.domain.alarm

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

data class WebtoonAlarmCount(
    val webtoonId: Long,
    val alarmCount: Long
)

interface AlarmRepository : CoroutineCrudRepository<Alarm, Long> {
    suspend fun findAllByMemberId(memberId: Long): Flow<Alarm>
    suspend fun findAllByWebtoonIdInAndStatusIs(webtoonId: List<Long>, status: AlarmStatus): Flow<Alarm>
    suspend fun deleteByIdAndMemberId(id: Long, memberId: Long)

    @Query(
        """
        SELECT a.webtoon_id, COUNT(*) as alarm_count 
        FROM tb_alarm a 
        GROUP BY a.webtoon_id 
        ORDER BY alarm_count DESC 
        LIMIT 10
    """
    )
    fun findTop10MostAlarmRegisteredWebtoons(): Flow<WebtoonAlarmCount>
}
