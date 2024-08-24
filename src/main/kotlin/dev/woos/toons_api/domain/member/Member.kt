package dev.woos.toons_api.domain.member

import dev.woos.toons_api.domain.common.BaseEntity
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("tb_member")
class Member(
    @Column
    val name: String,
    @Column
    val provider: AuthProvider,
    @Column
    val providerId: String,
) : BaseEntity() {
    fun unsubscribe(): Member {
        subscribe = false
        return this
    }

    @Column
    var verifiedEmail: String? = null

    @Column
    var subscribe = false
}