package kr.co.jiniaslog.blogcore.adapter.http.article

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import kr.co.jiniaslog.blogcore.application.article.usecase.TempArticleFindOneUseCase
import kr.co.jiniaslog.blogcore.application.article.usecase.TempArticlePostUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/articles/temp")
class TempArticleResource(
    private val tempArticlePostUseCase: TempArticlePostUseCase,
    private val tempArticleFindOneUseCase: TempArticleFindOneUseCase,
) {
    @Operation(summary = "임시 아티클을 저장한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "임시 아티클 저장 성공", content = [Content()]),
        ],
    )
    @PostMapping
    fun autoSaveTemp(@RequestBody request: TempArticleSaveRequest): ResponseEntity<Nothing> {
        tempArticlePostUseCase.post(request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @Operation(summary = "임시 아티클을 조회한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "임시 아티클 조회 성공"),
            ApiResponse(responseCode = "204", description = "임시 아티클이 없음", content = [Content()]),
        ],
    )
    @GetMapping
    fun getTempArticle(): ResponseEntity<TempArticleGetResponse?> =
        TempArticleGetResponse.from(tempArticleFindOneUseCase.findOne())?.let {
            ResponseEntity
                .status(HttpStatus.OK)
                .body(it)
        } ?: ResponseEntity
            .status(HttpStatus.NO_CONTENT).build()
}
