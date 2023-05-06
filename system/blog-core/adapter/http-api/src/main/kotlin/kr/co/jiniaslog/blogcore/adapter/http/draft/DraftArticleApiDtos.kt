package kr.co.jiniaslog.blogcore.adapter.http.draft

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleCommands.CreateDraftArticleCommand
import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleCommands.UpdateDraftArticleCommand
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticle
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.user.UserId
import java.time.LocalDateTime

@Schema(description = "DraftArticle 생성 요청")
data class DraftArticleCreateApiRequest(
    @field:NotNull
    @field:Min(1)
    @Schema(description = "작성자 ID", example = "1")
    val writerId: Long,

    @Schema(description = "제목", example = "제목")
    val title: String?,

    @Schema(description = "내용", example = "내용")
    val content: String?,

    @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.png")
    val thumbnailUrl: String?,
) {
    fun toCommand(): CreateDraftArticleCommand {
        return CreateDraftArticleCommand(
            writerId = UserId(writerId),
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
        )
    }
}

@Schema(description = "DraftArticle 생성 응답")
data class DraftArticleCreateApiResponse(
    @field:NotNull
    @field:Min(1)
    @Schema(description = "생성된 DraftArticle ID", example = "1")
    val draftArticleId: Long,
)

@Schema(description = "DraftArticle 수정 요청")
data class DraftArticleUpdateApiRequest(
    @field:NotNull
    @field:Min(1)
    @Schema(description = "작성자 ID", example = "1")
    val writerId: Long,

    @Schema(description = "제목", example = "제목")
    val title: String?,

    @Schema(description = "내용", example = "내용")
    val content: String?,

    @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.png")
    val thumbnailUrl: String?,
) {
    fun toCommand(draftArticleId: Long): UpdateDraftArticleCommand {
        return UpdateDraftArticleCommand(
            writerId = UserId(writerId),
            draftArticleId = DraftArticleId(draftArticleId),
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
        )
    }
}

@Schema(description = "DraftArticle 수정 응답")
data class DraftArticleUpdateApiResponse(
    @field:NotNull
    @field:Min(1)
    @Schema(description = "수정된 DraftArticle ID", example = "1")
    val draftArticleId: Long,
)

@Schema(description = "DraftArticle 조회 응답")
data class DraftArticleGetApiResponse(
    @field:NotNull
    @field:Min(1)
    @Schema(description = "DraftArticle ID", example = "1")
    val draftArticleId: Long,

    @field:NotNull
    @field:Min(1)
    @Schema(description = "작성자 ID", example = "1")
    val writerId: Long,

    @Schema(description = "제목", example = "제목")
    val title: String?,

    @Schema(description = "내용", example = "내용")
    val content: String?,

    @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.png")
    val thumbnailUrl: String?,

    @Schema(description = "생성일시", example = "2021-01-01T00:00:00")
    val createdAt: LocalDateTime,

    @Schema(description = "수정일시", example = "2021-01-01T00:00:00")
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(draftArticle: DraftArticle): DraftArticleGetApiResponse {
            return DraftArticleGetApiResponse(
                draftArticleId = draftArticle.id.value,
                writerId = draftArticle.writerId.value,
                title = draftArticle.title,
                content = draftArticle.content,
                thumbnailUrl = draftArticle.thumbnailUrl,
                createdAt = draftArticle.createdDate!!,
                updatedAt = draftArticle.updatedDate!!,
            )
        }
    }
}
