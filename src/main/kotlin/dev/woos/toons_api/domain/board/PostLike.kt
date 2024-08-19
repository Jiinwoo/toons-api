package dev.woos.toons_api.domain.board

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("tb_post_like")
class PostLike(
    @Id
    val id: Long = 0,
    @Column
    val postId: Long,
    @Column
    val memberId: Long,
    @Column
    val createdAt: LocalDateTime = LocalDateTime.now(),
)