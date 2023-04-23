package kr.co.jiniaslog.blogcore.adapter.http.draft

import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.jiniaslog.blogcore.adapter.http.config.ExceptionApiResponse
import kr.co.jiniaslog.blogcore.adapter.http.config.ExceptionsApiResponses
import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleCommands
import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleCommands.DeleteDraftArticleCommand
import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleQueries
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/articles/draft")
@Tag(name = "아티클 초안", description = "아티클 초안 API")
internal class DraftArticleResource(
    private val draftArticleCommands: DraftArticleCommands,
    private val draftArticleQueries: DraftArticleQueries,
) {

    @PostMapping
    @ApiResponses(ApiResponse(responseCode = "201", description = "아티클 초안 생성 성공"))
    @ExceptionsApiResponses(
        ExceptionApiResponse(httpStatusCode = HttpStatus.NOT_FOUND, exceptions = [ResourceNotFoundException::class]),
    )
    fun createDraftArticle(
        @RequestBody request: DraftArticleCreateApiRequest,
    ): ResponseEntity<DraftArticleCreateApiResponse> =
        draftArticleCommands.createDraftArticle(request.toCommand())
            .let {
                ResponseEntity.status(HttpStatus.CREATED)
                    .body(DraftArticleCreateApiResponse(it.draftArticleId))
            }

    @GetMapping("/{draftArticleId}")
    @ApiResponses(ApiResponse(responseCode = "200", description = "아티클 초안 조회 성공"))
    @ExceptionsApiResponses(
        ExceptionApiResponse(httpStatusCode = HttpStatus.NOT_FOUND, exceptions = [ResourceNotFoundException::class]),
    )
    fun getDraftArticle(
        @PathVariable draftArticleId: Long,
    ): ResponseEntity<DraftArticleGetApiResponse> =
        draftArticleQueries.getDraftArticle(DraftArticleId(draftArticleId))
            .let { ResponseEntity.ok(DraftArticleGetApiResponse.from(it)) }

    @PutMapping("/{draftArticleId}")
    @ApiResponses(ApiResponse(responseCode = "200", description = "아티클 초안 수정 성공"))
    @ExceptionsApiResponses(
        ExceptionApiResponse(httpStatusCode = HttpStatus.NOT_FOUND, exceptions = [ResourceNotFoundException::class]),
    )
    fun updateDraftArticle(
        @PathVariable draftArticleId: Long,
        @RequestBody request: DraftArticleUpdateApiRequest,
    ): ResponseEntity<DraftArticleUpdateApiResponse> =
        draftArticleCommands.updateDraftArticle(request.toCommand(draftArticleId))
            .let { ResponseEntity.ok(DraftArticleUpdateApiResponse(it.draftArticleId)) }

    @DeleteMapping("/{draftArticleId}")
    @ApiResponses(ApiResponse(responseCode = "204", description = "아티클 초안 삭제 성공", content = []))
    fun deleteDraftArticle(
        @PathVariable draftArticleId: Long,
    ): ResponseEntity<Unit> =
        draftArticleCommands.deleteDraftArticle(DeleteDraftArticleCommand(DraftArticleId(draftArticleId)))
            .let { ResponseEntity.noContent().build() }
}
