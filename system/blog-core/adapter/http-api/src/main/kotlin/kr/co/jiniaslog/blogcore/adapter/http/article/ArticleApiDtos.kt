package kr.co.jiniaslog.blogcore.adapter.http.article

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.jiniaslog.blogcore.application.article.usecase.ArticleEditCommand
import kr.co.jiniaslog.blogcore.application.article.usecase.DraftArticlePostCommand
import kr.co.jiniaslog.blogcore.application.article.usecase.PublishedArticlePostCommand
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId

@Schema(description = "아티클 저장 요청")
data class DraftArticlePostRequest(
    @Schema(description = "작성자 ID", defaultValue = "1")
    val writerId: Long,
    @Schema(description = "제목", defaultValue = "제목입니다.")
    val title: String,
    @Schema(description = "내용", defaultValue = "내용입니다.")
    val content: String,
    @Schema(description = "썸네일 URL")
    val thumbnailUrl: String?,
    @Schema(description = "카테고리 ID", defaultValue = "1")
    val categoryId: Long?,
    @Schema(description = "태그 ID 목록", defaultValue = "[1, 2, 3]")
    val tags: Set<Long>,
) {
    fun toCommand(): DraftArticlePostCommand {
        return DraftArticlePostCommand(
            writerId = UserId(writerId),
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = if (categoryId == null) null else CategoryId(categoryId),
            tags = tags.map { TagId(it) }.toSet(),
        )
    }
}

@Schema(description = "아티클 저장 응답")
data class DraftArticlePostResponse(
    @Schema(description = "아티클 ID", defaultValue = "1")
    val articleId: Long,
)

@Schema(description = "공개 아티클 등록")
data class PublishedArticlePostRequest(
    @Schema(description = "작성자 ID", defaultValue = "1")
    val writerId: Long,
    @Schema(description = "제목", defaultValue = "제목입니다.")
    val title: String,
    @Schema(description = "내용", defaultValue = "내용입니다.")
    val content: String,
    @Schema(description = "썸네일 URL")
    val thumbnailUrl: String,
    @Schema(description = "카테고리 ID", defaultValue = "1")
    val categoryId: Long,
    @Schema(description = "태그 ID 목록", defaultValue = "[1, 2, 3]")
    val tags: Set<Long>,
) {
    fun toCommand(): PublishedArticlePostCommand {
        return PublishedArticlePostCommand(
            writerId = UserId(writerId),
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = CategoryId(categoryId),
            tags = tags.map { TagId(it) }.toSet(),
        )
    }
}

@Schema(description = "공개 아티클 등록 응답")
data class PublishedArticlePostResponse(
    @Schema(description = "아티클 ID", defaultValue = "1")
    val articleId: Long,
)

data class ArticleEditRequest(
    val articleId: Long,
    val title: String,
    val content: String,
    val thumbnailUrl: String,
    val categoryId: Long,
    val tags: Set<Long>,
) {
    fun toCommand(): ArticleEditCommand {
        return ArticleEditCommand(
            userId = UserId(articleId),
            articleId = ArticleId(articleId),
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = CategoryId(categoryId),
            tags = tags.map { TagId(it) }.toSet(),
        )
    }
}
