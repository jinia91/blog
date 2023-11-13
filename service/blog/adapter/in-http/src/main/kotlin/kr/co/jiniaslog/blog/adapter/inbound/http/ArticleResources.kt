package kr.co.jiniaslog.blog.adapter.inbound.http

import kr.co.jiniaslog.blog.usecase.ArticleUseCases
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleResources(
    private val articleUseCases: ArticleUseCases,
) {
    @PostMapping("/articles")
    suspend fun init(
        @RequestBody request: ArticleInitRequest
    ): ArticleInitResponse {
        return articleUseCases
            .init(request.toCommand())
            .toResponse()
    }

    @PostMapping("/articles/{articleId}")
    suspend fun commit(
        @RequestBody request: ArticleCommitRequest
    ): ArticleCommitResponse {
        return articleUseCases
            .commit(request.toCommand())
            .toResponse()
    }
}
