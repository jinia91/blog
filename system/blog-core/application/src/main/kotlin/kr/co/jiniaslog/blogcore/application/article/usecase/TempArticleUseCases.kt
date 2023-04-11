package kr.co.jiniaslog.blogcore.application.article.usecase

import kr.co.jiniaslog.blogcore.domain.article.TempArticle
import kr.co.jiniaslog.blogcore.domain.article.UserId
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.tag.TagId

interface TempArticleUseCases {
    fun post(command: TempArticlePostCommand)
    fun delete()
    fun findOne(): TempArticle?
}

data class TempArticlePostCommand(
    val userId: UserId,
    val title: String?,
    val content: String?,
    val thumbnailUrl: String?,
    val categoryId: CategoryId?,
    val tags: Set<TagId>,
)
