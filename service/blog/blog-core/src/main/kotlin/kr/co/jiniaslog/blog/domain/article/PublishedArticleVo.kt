package kr.co.jiniaslog.blog.domain.article

import java.time.LocalDateTime

/**
 * Published Article Value Object
 *
 * 불변 값 객체로서 엔티티가 아닌 계층 이동과 계층 공용 데이터 홀더역할
 *
 * Published된 게시글의 정보를 담는다
 *
 */
data class PublishedArticleVo(
    val id: String,
    val title: String,
    val content: String,
    val thumbnailUrl: String,
    val status: String,
    val tags: List<String>,
    val hit: Int = 0,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    fun toTrimmedArticleVo(): PublishedArticleVo {
        return PublishedArticleVo(
            id = id,
            title = title,
            content = content.take(100),
            thumbnailUrl = thumbnailUrl,
            status = status,
            tags = tags,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
