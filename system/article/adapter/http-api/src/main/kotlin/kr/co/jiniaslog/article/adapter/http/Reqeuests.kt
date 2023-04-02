package kr.co.jiniaslog.article.adapter.http

import kr.co.jiniaslog.article.application.usecase.ArticleEditCommand
import kr.co.jiniaslog.article.application.usecase.ArticlePostCommand
import kr.co.jiniaslog.article.domain.ArticleId
import kr.co.jiniaslog.article.domain.CategoryId
import kr.co.jiniaslog.article.domain.TagId
import kr.co.jiniaslog.article.domain.UserId

data class ArticlePostRequest(
    val writerId: Long,
    val title: String,
    val content: String,
    val thumbnailUrl: String,
    val categoryId: Long,
    val tags: Set<Long>,
) {
    fun toCommand(): ArticlePostCommand {
        return ArticlePostCommand(
            userId = UserId(writerId),
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = CategoryId(categoryId),
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
            articleId = ArticleId(articleId),
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = CategoryId(categoryId),
            tags = tags.map { TagId(it) }.toSet(),
        )
    }
}
