package dev.woos.toons_api.domain.board

import dev.woos.toons_api.domain.common.BaseEntity
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("tb_board")
class Board(
    @Column
    val contentType: ContentType,
    @Column
    val contentId: Long?,
) : BaseEntity() {
}