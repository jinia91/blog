package kr.co.jiniaslog.blogcore.adapter.http.article

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.jiniaslog.blogcore.adapter.http.config.ExceptionApiResponse
import kr.co.jiniaslog.blogcore.application.article.usecase.DraftArticlePostUseCase
import kr.co.jiniaslog.blogcore.domain.article.ArticleNotValidException
import kr.co.jiniaslog.shared.core.domain.ValidationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/articles")
@Tag(name = "아티클", description = "아티클 관련 API")
internal class ArticleResource(
    private val draftArticlePostUseCase: DraftArticlePostUseCase,
) {
    @Operation(summary = "아티클 초안을 저장한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "아티클 초안 저장 성공", content = [Content()]),
        ],
    )
    @ExceptionApiResponse(
        httpStatusCode = HttpStatus.BAD_REQUEST,
        exceptions = [
            ArticleNotValidException::class,
            ValidationException::class,
        ],
    )
    @PostMapping("/draft")
    fun postArticle(@RequestBody request: DraftArticlePostRequest) {
        draftArticlePostUseCase.post(request.toCommand())
    }
}
