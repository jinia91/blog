package kr.co.jiniaslog.article.adapter.http

import kr.co.jiniaslog.article.adapter.http.domain.CategoryId
import kr.co.jiniaslog.article.adapter.http.domain.TagId
import kr.co.jiniaslog.article.adapter.http.domain.WriterId
import kr.co.jiniaslog.article.application.usecase.ArticlePostCommand
import kr.co.jiniaslog.article.application.usecase.ArticlePostUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ArticleResource(
    private val articlePostUseCase: ArticlePostUseCase,
) {
    @PostMapping("/articles")
    fun postArticle(@RequestBody request: ArticlePostRequest) {
        articlePostUseCase.postArticle(request.toCommand())
    }
}

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
