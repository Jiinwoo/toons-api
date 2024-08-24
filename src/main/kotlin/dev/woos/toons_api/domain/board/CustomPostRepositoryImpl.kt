package dev.woos.toons_api.domain.board

import dev.woos.toons_api.domain.member.AuthProvider
import dev.woos.toons_api.domain.member.Member
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.flow
import java.time.LocalDateTime

class CustomPostRepositoryImpl(
    private val template: R2dbcEntityTemplate
) : CustomPostRepository {
    override suspend fun findAllByBoardIdWithMemberAndWebtoonAndDeletedAtIsNull(
        size: Int,
        offset: Long,
        boardId: Long
    ): Flow<Post> {
        val selectSql = """
            SELECT p.id, p.title, p.content, p.created_at, p.updated_at, p.board_id, p.member_id, p.tag, m.name, m.provider, m.provider_id, m.verified_email, m.subscribe
            FROM tb_post p
            INNER JOIN tb_member m ON p.member_id = m.id
            WHERE p.board_id = :boardId AND p.deleted_at IS NULL
            ORDER BY p.created_at DESC
            LIMIT :limit OFFSET :offset
        """.trimIndent()
        return template.databaseClient.sql(selectSql)
            .bind("boardId", boardId)
            .bind("limit", size)
            .bind("offset", offset)
            .map { row ->
                Post(
                    boardId = row["board_id"] as Long,
                    memberId = row["member_id"] as Long,
                    title = row["title"] as String,
                    content = row["content"] as String,
                    tag = row["tag"] as String?,
                ).apply {
                    id = row["id"] as Long
                    createdAt = row["created_at"] as LocalDateTime
                    updatedAt = row["updated_at"] as LocalDateTime
                    member = Member(
                        name = row["name"] as String,
                        provider = AuthProvider.valueOf(row["provider"] as String),
                        providerId = row["provider_id"] as String,
                    ).apply {
                        verifiedEmail = row["verified_email"] as String?
                        subscribe = row["subscribe"] as Boolean
                    }
                }
            }
            .flow()
    }
}