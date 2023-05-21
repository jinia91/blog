package kr.co.jiniaslog.blogcore.adapter.http.article

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import kr.co.jiniaslog.blogcore.application.article.usecase.ArticleCommands
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException
import kr.co.jiniaslog.shared.http.swagger.ExceptionApiResponse
import kr.co.jiniaslog.shared.http.swagger.ExceptionsApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/articles")
internal class ArticleResource(
    private val articleCommands: ArticleCommands,
) {
    @PostMapping("")
    @Operation(summary = "아티클 게시")
    @ApiResponses(ApiResponse(responseCode = "201", description = "아티클 게시"))
    @ExceptionsApiResponses(
        ExceptionApiResponse(httpStatusCode = HttpStatus.NOT_FOUND, exceptions = [ResourceNotFoundException::class]),
    )
    fun createDraftArticle(
        @RequestBody @Valid
        request: ArticlePostApiRequest,
    ): ResponseEntity<ArticlePostApiResponse> =
        articleCommands.post(request.toCommand())
            .let {
                ResponseEntity.status(HttpStatus.CREATED)
                    .body(ArticlePostApiResponse(it.articleId.value))
            }
}
