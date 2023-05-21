package kr.co.jiniaslog.blogcore.adapter.http.article

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import kr.co.jiniaslog.blogcore.application.article.usecase.ArticleCommands
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId

@Schema(description = "Article 공개 요청")
data class ArticlePostApiRequest(
    @field:NotNull
    @field:Min(1)
    @Schema(description = "작성자 ID", example = "1")
    val writerId: Long,
    @field:NotBlank
    @Schema(description = "제목", example = "제목", required = true)
    val title: String,
    @field:NotBlank
    @Schema(description = "내용", example = "내용", required = true)
    val content: String,
    @field:NotBlank
    @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.png", required = true)
    val thumbnailUrl: String,
    @field:Min(1)
    @Schema(description = "카테고리 ID", example = "1")
    val categoryId: Long,
    @field: NotEmpty
    @Schema(description = "태그 ID 목록", example = "[1, 2, 3]")
    val tags: Set<Long>,
    @Schema(description = "DraftArticle ID", example = "1", required = false)
    val draftArticleId: Long?,
) {
    fun toCommand(): ArticleCommands.PostArticleCommand {
        return ArticleCommands.PostArticleCommand(
            writerId = UserId(writerId),
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = CategoryId(categoryId),
            tags = tags.map { TagId(it) }.toSet(),
            draftArticleId = draftArticleId?.let { DraftArticleId(it) },
        )
    }
}

@Schema(description = "Article 공개 응답")
data class ArticlePostApiResponse(
    val articleId: Long,
)

@Schema(description = "공개 Article 수정 요청")
data class ArticleEditApiRequest(
    @field:NotNull
    @field:Min(1)
    @Schema(description = "Article ID", example = "1")
    val articleId: Long,
    @field:NotNull
    @field:Min(1)
    @Schema(description = "작성자 ID", example = "1")
    val writerId: Long,
    @field:NotBlank
    @Schema(description = "제목", example = "제목", required = true)
    val title: String,
    @field:NotBlank
    @Schema(description = "내용", example = "내용", required = true)
    val content: String,
    @field:NotBlank
    @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.png", required = true)
    val thumbnailUrl: String,
    @field:Min(1)
    @Schema(description = "카테고리 ID", example = "1")
    val categoryId: Long,
    @field: NotEmpty
    @Schema(description = "태그 ID 목록", example = "[1, 2, 3]")
    val tags: Set<Long>,
) {
    fun toCommand(): ArticleCommands.EditArticleCommand {
        return ArticleCommands.EditArticleCommand(
            writerId = UserId(writerId),
            articleId = ArticleId(articleId),
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = CategoryId(categoryId),
            tags = tags.map { TagId(it) }.toSet(),
        )
    }
}

@Schema(description = "공개 Article 수정 응답")
data class ArticleEditApiResponse(
    val articleId: Long,
)

@Schema(description = "Article 삭제 요청")
data class ArticleDeleteApiRequest(
    @field:NotNull
    @field:Min(1)
    @Schema(description = "Article ID", example = "1")
    val articleId: Long,
) {
    fun toCommand(): ArticleCommands.DeleteArticleCommand {
        return ArticleCommands.DeleteArticleCommand(
            articleId = ArticleId(articleId),
        )
    }
}

@Schema(description = "Article 삭제 응답")
data class ArticleDeleteApiResponse(
    val articleId: Long,
)
