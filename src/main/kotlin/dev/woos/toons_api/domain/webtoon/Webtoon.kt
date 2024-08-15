package dev.woos.toons_api.domain.webtoon

import dev.woos.toons_api.domain.common.BaseEntity
import dev.woos.toons_api.domain.common.Platform
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.DayOfWeek
import java.time.LocalDateTime


@Table("tb_webtoon")
class Webtoon(
    @Column
    var title: String,
    @Column("thumbnail_url")
    var thumbnailUrl: String,
    @Column("day_of_week")
    var dayOfWeek: DayOfWeek,
    @Column
    var link: String,
    @Column // 플랫폼 별 고유 키와 합쳐서 Unique Key 가 존재함.
    val platform: Platform,
    @Column
    val platformId: Long,
) : BaseEntity()