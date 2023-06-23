package kr.co.jiniaslog.blogcore.adapter.http.category

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import kr.co.jiniaslog.blogcore.application.category.usecase.CategoryCommands
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException
import kr.co.jiniaslog.shared.core.domain.ValidationException
import kr.co.jiniaslog.shared.http.swagger.ExceptionApiResponse
import kr.co.jiniaslog.shared.http.swagger.ExceptionsApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/categories")
class CategoryResource(
    private val categoryCommands: CategoryCommands,
) {
    @PutMapping("")
    @Operation(summary = "카테고리 동기화")
    @ApiResponses(ApiResponse(responseCode = "200", description = "카테고리 동기화 성공"))
    @ExceptionsApiResponses(
        ExceptionApiResponse(httpStatusCode = HttpStatus.NOT_FOUND, exceptions = [ResourceNotFoundException::class]),
        ExceptionApiResponse(httpStatusCode = HttpStatus.BAD_REQUEST, exceptions = [ValidationException::class]),
    )
    fun syncCategories(
        @RequestBody @Valid
        request: CategorySyncApiRequest,
    ): ResponseEntity<Unit> {
        categoryCommands.syncCategories(request.toCommand())
        return ResponseEntity.ok().build()
    }
}
