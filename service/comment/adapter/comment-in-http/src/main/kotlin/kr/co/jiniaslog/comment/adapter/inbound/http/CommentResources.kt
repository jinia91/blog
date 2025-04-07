package kr.co.jiniaslog.comment.adapter.inbound.http

import kr.co.jiniaslog.comment.usecase.CommentUseCasesFacade
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/comments")
class CommentResources(
    private val commentUseCases: CommentUseCasesFacade,
) {
    @PostMapping
    fun createComment(
        request: CommentCreate,
    ): CommentCreateResponse {
        val command = request.toCommand()
        val info = commentUseCases.handle(command)
        return info.toResponse()
    }
}
