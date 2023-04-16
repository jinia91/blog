package kr.co.jiniaslog.blogcore.adapter.http.article

import kr.co.jiniaslog.blogcore.application.article.usecase.ArticleEditCommand
import kr.co.jiniaslog.blogcore.application.article.usecase.DraftArticlePostCommand
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId

data class DraftArticlePostRequest(
    val writerId: Long,
    val title: String,
    val content: String,
    val thumbnailUrl: String?,
    val categoryId: Long?,
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
