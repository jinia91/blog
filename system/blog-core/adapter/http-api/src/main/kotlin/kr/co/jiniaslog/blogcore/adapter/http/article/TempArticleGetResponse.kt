package kr.co.jiniaslog.blogcore.adapter.http.article

import kr.co.jiniaslog.blogcore.domain.article.TempArticle

data class TempArticleGetResponse(
    val title: String? = null,
    val content: String? = null,
    val thumbnailUrl: String? = null,
    val writerId: Long? = null,
    val categoryId: Long? = null,
) {
    companion object {
        fun from(findOne: TempArticle?): TempArticleGetResponse = findOne?.let {
            TempArticleGetResponse(
                title = it.title,
                content = it.content,
                thumbnailUrl = it.thumbnailUrl,
                writerId = it.writerId.value,
                categoryId = it.categoryId?.value,
            )
        } ?: TempArticleGetResponse()
    }
}
