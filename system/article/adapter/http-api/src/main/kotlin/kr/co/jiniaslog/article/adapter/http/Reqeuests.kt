package kr.co.jiniaslog.article.adapter.http

import kr.co.jiniaslog.article.adapter.http.domain.ArticleId
import kr.co.jiniaslog.article.adapter.http.domain.CategoryId
import kr.co.jiniaslog.article.adapter.http.domain.TagId
import kr.co.jiniaslog.article.adapter.http.domain.WriterId
import kr.co.jiniaslog.article.application.usecase.ArticleEditCommand
import kr.co.jiniaslog.article.application.usecase.ArticlePostCommand

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
            writerId = WriterId(writerId),
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
