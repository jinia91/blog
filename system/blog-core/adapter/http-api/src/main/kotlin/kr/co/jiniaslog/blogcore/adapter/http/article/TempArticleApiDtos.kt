package kr.co.jiniaslog.blogcore.adapter.http.article

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.jiniaslog.blogcore.application.article.usecase.TempArticlePostCommand
import kr.co.jiniaslog.blogcore.domain.article.TempArticle
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.user.UserId

@Schema(description = "임시 아티클 저장 요청")
data class TempArticleSaveRequest(
    @Schema(description = "제목", defaultValue = "제목입니다.")
    val title: String? = null,

    @Schema(description = "내용", defaultValue = "내용입니다.")
    val content: String? = null,

    @Schema(description = "썸네일 URL")
    val thumbnailUrl: String? = null,

    @Schema(description = "작성자 ID", defaultValue = "1")
    val writerId: Long,

    @Schema(description = "카테고리 ID", defaultValue = "1")
    val categoryId: Long? = null,
) {
    fun toCommand(): TempArticlePostCommand = TempArticlePostCommand(
        title = title,
        content = content,
        thumbnailUrl = thumbnailUrl,
        writerId = UserId(writerId),
        categoryId = categoryId?.let { CategoryId(it) },
    )
}

@Schema(description = "임시 아티클 조회 응답")
data class TempArticleGetResponse(
    @Schema(description = "제목", defaultValue = "제목입니다.")
    val title: String? = null,

    @Schema(description = "내용", defaultValue = "내용입니다.")
    val content: String? = null,

    @Schema(description = "썸네일 URL")
    val thumbnailUrl: String? = null,

    @Schema(description = "작성자 ID", defaultValue = "1")
    val writerId: Long? = null,

    @Schema(description = "카테고리 ID", defaultValue = "1")
    val categoryId: Long? = null,
) {
    companion object {
        fun from(findOne: TempArticle?): TempArticleGetResponse? = findOne?.let {
            TempArticleGetResponse(
                title = it.title,
                content = it.content,
                thumbnailUrl = it.thumbnailUrl,
                writerId = it.writerId.value,
                categoryId = it.categoryId?.value,
            )
        }
    }
}
