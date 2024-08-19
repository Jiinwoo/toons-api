package dev.woos.toons_api.domain.board

import dev.woos.toons_api.domain.common.BaseEntity
import dev.woos.toons_api.domain.member.Member
import dev.woos.toons_api.domain.webtoon.Webtoon
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("tb_post")
class Post(
    @Column
    val boardId: Long,
    @Column
    val memberId: Long,
    @Column
    var title: String,
    @Column
    var content: String,
    @Column
    var tag: String?,
) : BaseEntity() {

    @Column
    var likeCount: Int = 0

    fun increaseLikeCount() {
        likeCount++
    }

    fun decreaseLikeCount() {
        likeCount--
    }

    @Column
    var deletedAt: LocalDateTime? = null

    fun delete() {
        deletedAt = LocalDateTime.now()
    }

    @Transient
    lateinit var member: Member
}