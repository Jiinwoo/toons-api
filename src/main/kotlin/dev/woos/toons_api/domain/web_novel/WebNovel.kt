package dev.woos.toons_api.domain.web_novel

import dev.woos.toons_api.domain.common.BaseEntity
import dev.woos.toons_api.domain.common.Platform
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("tb_web_novel")
class WebNovel(
    @Column
    var title: String,
    @Column("thumbnail_url")
    var thumbnailUrl: String,
    @Column
    var link: String,
    @Column // 플랫폼 별 고유 키와 합쳐서 Unique Key 가 존재함.
    val platform: Platform,
    @Column
    val platformId: Long,
) : BaseEntity()