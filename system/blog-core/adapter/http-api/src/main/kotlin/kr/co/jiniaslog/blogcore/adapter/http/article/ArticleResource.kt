package kr.co.jiniaslog.blogcore.adapter.http.article

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import kr.co.jiniaslog.blogcore.application.article.usecase.ArticleCommands
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException
import kr.co.jiniaslog.shared.http.swagger.ExceptionApiResponse
import kr.co.jiniaslog.shared.http.swagger.ExceptionsApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    @PutMapping("/{articleId}")
    @Operation(summary = "아티클 수정")
    @ApiResponses(ApiResponse(responseCode = "200", description = "아티클 수정"))
    @ExceptionsApiResponses(
        ExceptionApiResponse(httpStatusCode = HttpStatus.NOT_FOUND, exceptions = [ResourceNotFoundException::class]),
    )
    fun editArticle(
        @PathVariable
        @Min(1)
        articleId: Long,
        @RequestBody @Valid
        request: ArticleEditApiRequest,
    ): ResponseEntity<ArticleEditApiResponse> =
        articleCommands.edit(request.toCommand())
            .let {
                ResponseEntity.status(HttpStatus.OK)
                    .body(ArticleEditApiResponse(it.articleId.value))
            }

    @DeleteMapping("/{articleId}")
    @Operation(summary = "아티클 삭제")
    @ApiResponses(ApiResponse(responseCode = "204", description = "아티클 삭제"))
    @ExceptionsApiResponses(
        ExceptionApiResponse(httpStatusCode = HttpStatus.NOT_FOUND, exceptions = [ResourceNotFoundException::class]),
    )
    fun deleteArticle(
        @PathVariable
        @Min(1)
        articleId: Long,
    ): ResponseEntity<Unit> =
        articleCommands.delete(ArticleCommands.DeleteArticleCommand(ArticleId(articleId)))
            .let { ResponseEntity.noContent().build() }
}
