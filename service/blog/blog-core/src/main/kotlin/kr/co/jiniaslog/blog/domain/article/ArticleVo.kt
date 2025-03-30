package kr.co.jiniaslog.blog.domain.article

import java.time.LocalDateTime

/**
 * Published Article Value Object
 *
 * 불변 값 객체로서 엔티티가 아닌 계층 이동과 계층 공용 데이터 홀더역할
 *
 * 상태에따라 단일 데이터만 담으므로 해당 상태를 명시적으로 표현해야한다
 *
 */
data class ArticleVo(
    val id: Long,
    val title: String,
    val content: String,
    val thumbnailUrl: String,
    val status: String,
    val hit: Int = 0,
    val tags: Map<Long, String>,
    val createdAt: LocalDateTime,
    val voDataStatus: Article.Status
) {
    fun toSimplifiedArticleVo(): ArticleVo {
        return ArticleVo(
            id = id,
            title = title,
            content = content.take(100),
            thumbnailUrl = thumbnailUrl,
            status = status,
            tags = tags,
            createdAt = createdAt,
            voDataStatus = voDataStatus
        )
    }

    companion object {
        fun from(it: Article): ArticleVo {
            val articleContentByPublished =
                if (it.status == Article.Status.PUBLISHED) it.articleContents else it.draftContents
            return ArticleVo(
                id = it.id.value,
                title = articleContentByPublished.title,
                thumbnailUrl = articleContentByPublished.thumbnailUrl,
                content = articleContentByPublished.contents,
                createdAt = it.createdAt!!,
                tags = it.tagsInfo.mapKeys { it.key.id },
                status = it.status.name,
                voDataStatus = if (it.status == Article.Status.PUBLISHED) Article.Status.PUBLISHED else Article.Status.DRAFT
            )
        }
    }
}
