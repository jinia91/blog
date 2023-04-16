package kr.co.jiniaslog.blogcore.adapter.http.article

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.jiniaslog.blogcore.adapter.http.config.ExceptionApiResponse
import kr.co.jiniaslog.blogcore.adapter.http.config.ExceptionsApiResponses
import kr.co.jiniaslog.blogcore.application.article.usecase.DraftArticlePostUseCase
import kr.co.jiniaslog.blogcore.application.article.usecase.PublishedArticlePostUseCase
import kr.co.jiniaslog.blogcore.domain.article.ArticleNotValidException
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException
import kr.co.jiniaslog.shared.core.domain.ValidationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/articles")
@Tag(name = "아티클", description = "아티클 관련 API")
internal class ArticleResource(
    private val draftArticlePostUseCase: DraftArticlePostUseCase,
    private val publishedArticlePostUseCase: PublishedArticlePostUseCase,
) {
    @Operation(summary = "아티클 초안을 등록한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "아티클 초안 저장 성공"),
        ],
    )
    @ExceptionsApiResponses(
        value = [
            ExceptionApiResponse(
                httpStatusCode = HttpStatus.BAD_REQUEST,
                exceptions = [
                    ArticleNotValidException::class,
                    ValidationException::class,
                ],
            ),
            ExceptionApiResponse(
                httpStatusCode = HttpStatus.NOT_FOUND,
                exceptions = [
                    ResourceNotFoundException::class,
                ],
            ),
        ],
    )
    @PostMapping("/draft")
    fun postDraftArticle(@RequestBody request: DraftArticlePostRequest): ResponseEntity<DraftArticlePostResponse> {
        val articleId = draftArticlePostUseCase.post(request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(DraftArticlePostResponse(articleId = articleId.value))
    }

    @Operation(summary = "공개 아티클을 등록한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "아티클 공개 등록 성공"),
        ],
    )
    @ExceptionsApiResponses(
        value = [
            ExceptionApiResponse(
                httpStatusCode = HttpStatus.BAD_REQUEST,
                exceptions = [
                    ArticleNotValidException::class,
                    ValidationException::class,
                ],
            ),
            ExceptionApiResponse(
                httpStatusCode = HttpStatus.NOT_FOUND,
                exceptions = [
                    ResourceNotFoundException::class,
                ],
            ),
        ],
    )
    @PostMapping("/published")
    fun postPublishedArticle(@RequestBody request: PublishedArticlePostRequest): ResponseEntity<PublishedArticlePostResponse> {
        val articleId = publishedArticlePostUseCase.post(request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(PublishedArticlePostResponse(articleId = articleId.value))
    }
}
