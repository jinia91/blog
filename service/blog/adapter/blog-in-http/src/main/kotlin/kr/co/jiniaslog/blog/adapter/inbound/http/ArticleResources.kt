package kr.co.jiniaslog.blog.adapter.inbound.http

import kr.co.jiniaslog.blog.usecase.ArticleCudFacade
import kr.cojiniaslog.shared.adapter.inbound.http.AuthUserId
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/articles")
class ArticleResources(
    private val articleFacade: ArticleCudFacade,
) {
    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    fun postNewArticle(
        @AuthUserId userId: Long?,
        @RequestBody request: ArticlePostRequest,
    ): ResponseEntity<ArticlePostResponse> {
        require(userId != null) { "userId must not be null" }
        val info = articleFacade.handle(request.toCommand(userId))

        return ResponseEntity
            .created(URI("/api/v1/articles/${info.articleId.value}"))
            .body(ArticlePostResponse(info.articleId.value))
    }
}
