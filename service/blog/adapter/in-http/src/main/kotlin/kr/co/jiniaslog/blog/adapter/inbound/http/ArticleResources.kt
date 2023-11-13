package kr.co.jiniaslog.blog.adapter.inbound.http

import kr.co.jiniaslog.blog.usecase.ArticleUseCases
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleResources(
    private val articleUseCases: ArticleUseCases,
) {
    @PostMapping("/articles")
    suspend fun init(): ArticleInitResponse {
        return articleUseCases
            .init(ArticleInitRequest(1).toCommand())
            .toResponse()
    }
}
