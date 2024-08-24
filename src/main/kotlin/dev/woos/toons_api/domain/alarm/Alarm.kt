package dev.woos.toons_api.domain.alarm

import dev.woos.toons_api.domain.common.BaseEntity
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("tb_alarm")
class Alarm(
    @Column
    val webtoonId: Long,
    @Column
    val memberId: Long,
) : BaseEntity() {
    @Column
    var status = AlarmStatus.NOT_SENT
    @Column
    var sendAt: LocalDateTime? = null

    fun send(): Alarm{
        this.status = AlarmStatus.SENT
        this.sendAt = LocalDateTime.now()
        return this
    }

    fun sendFail(): Alarm {
        this.status = AlarmStatus.FAILURE
        return this
    }
}