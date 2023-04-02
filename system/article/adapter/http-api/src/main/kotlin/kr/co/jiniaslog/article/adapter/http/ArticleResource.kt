package kr.co.jiniaslog.article.adapter.http

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
