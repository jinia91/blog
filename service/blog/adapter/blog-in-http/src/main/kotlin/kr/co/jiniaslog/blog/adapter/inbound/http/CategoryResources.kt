package kr.co.jiniaslog.blog.adapter.inbound.http

import kr.co.jiniaslog.blog.adapter.inbound.http.dto.SyncCategoriesRequest
import kr.co.jiniaslog.blog.usecase.category.ISyncCategories
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/categories")
class CategoryResources(
    private val categoryUseCases: ISyncCategories
) {
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun syncCategories(
        @RequestBody request: SyncCategoriesRequest
    ): ResponseEntity<Unit> {
        val command = request.toCommand()
        categoryUseCases.handle(command)
        return ResponseEntity.ok(null)
    }
}
