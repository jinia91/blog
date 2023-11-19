package kr.co.jiniaslog.blog.adapter.inbound.http

import kotlinx.coroutines.currentCoroutineContext
import kr.co.jiniaslog.blog.usecase.ArticleUseCases
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

private val log = mu.KotlinLogging.logger { }

@RestController
class ArticleResources(
    private val articleUseCases: ArticleUseCases,
) {
    @PostMapping("/articles")
    suspend fun init(
        @RequestBody request: ArticleInitRequest,
    ): ArticleInitResponse {
        val currentCoroutineContext = currentCoroutineContext()
        log.info { "currentCoroutineContext : $currentCoroutineContext" }
        return articleUseCases
            .init(request.toCommand())
            .toResponse()
    }

    @PostMapping("/articles/{articleId}")
    suspend fun commit(
        @RequestBody request: ArticleCommitRequest,
    ): ArticleCommitResponse {
        return articleUseCases
            .commit(request.toCommand())
            .toResponse()
    }

    @PostMapping("/articles/{articleId}/staging")
    suspend fun staging(
        @RequestBody request: ArticleStagingRequest,
    ): ArticleStagingResponse {
        return articleUseCases
            .staging(request.toCommand())
            .toResponse()
    }

    @DeleteMapping("/articles/{articleId}")
    suspend fun delete(
        @RequestBody request: ArticleDeleteRequest,
    ): ArticleDeleteResponse {
        return articleUseCases
            .delete(request.toCommand())
            .toResponse()
    }

    @PostMapping("/articles/{articleId}/publish")
    suspend fun publish(
        @RequestBody request: ArticlePublishRequest,
    ): ArticlePublishResponse {
        return articleUseCases
            .publish(request.toCommand())
            .toResponse()
    }
}
