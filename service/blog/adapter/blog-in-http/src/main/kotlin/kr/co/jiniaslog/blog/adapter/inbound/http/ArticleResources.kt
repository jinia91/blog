package kr.co.jiniaslog.blog.adapter.inbound.http

import java.net.URI
import kr.co.jiniaslog.blog.usecase.IPostNewArticle
import kr.co.jiniaslog.blog.usecase.UseCasesArticleFacade
import kr.cojiniaslog.shared.adapter.inbound.http.AuthUserId
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/articles")
class ArticleResources(
   private val articleFacade: UseCasesArticleFacade
) {
    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    fun postNewArticle(
        @AuthUserId userId: Long?,
        @RequestBody request: ArticlePostRequest,
        ) : ResponseEntity<ArticlePostResponse> {
        require(userId != null) { "userId must not be null" }
        val info = articleFacade.handle(request.toCommand(userId))

        return ResponseEntity
            .created(URI("/api/v1/articles/${info.id.value}"))
            .body(ArticlePostResponse(info.id.value))
    }
}
