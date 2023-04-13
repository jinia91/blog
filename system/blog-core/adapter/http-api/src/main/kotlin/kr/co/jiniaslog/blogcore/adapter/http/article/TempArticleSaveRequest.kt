package kr.co.jiniaslog.blogcore.adapter.http.article

import kr.co.jiniaslog.blogcore.application.article.usecase.TempArticlePostCommand
import kr.co.jiniaslog.blogcore.domain.article.UserId
import kr.co.jiniaslog.blogcore.domain.category.CategoryId

data class TempArticleSaveRequest(
    val title: String? = null,
    val content: String? = null,
    val thumbnailUrl: String? = null,
    val writerId: Long,
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
