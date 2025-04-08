package kr.co.jiniaslog.comment.adapter.inbound.http

import kr.co.jiniaslog.comment.adapter.inbound.http.dto.CreateCommentRequest
import kr.co.jiniaslog.comment.adapter.inbound.http.dto.CreateCommentResponse
import kr.co.jiniaslog.comment.adapter.inbound.http.dto.DeleteCommentRequest
import kr.co.jiniaslog.comment.adapter.inbound.http.dto.DeleteCommentResponse
import kr.co.jiniaslog.comment.adapter.inbound.http.dto.GetCommentsResponse
import kr.co.jiniaslog.comment.adapter.inbound.http.dto.toResponse
import kr.co.jiniaslog.comment.domain.Comment
import kr.co.jiniaslog.comment.domain.CommentId
import kr.co.jiniaslog.comment.domain.ReferenceId
import kr.co.jiniaslog.comment.queries.CommentQueriesFacade
import kr.co.jiniaslog.comment.queries.IGetHierarchyCommentsByRef
import kr.co.jiniaslog.comment.usecase.CommentUseCasesFacade
import kr.co.jiniaslog.comment.usecase.IDeleteComment
import kr.cojiniaslog.shared.adapter.inbound.http.AuthUserId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/comments")
class CommentResources(
    private val commentUseCases: CommentUseCasesFacade,
    private val commentQueries: CommentQueriesFacade
) {
    @PostMapping
    fun createComment(
        @AuthUserId userId: Long?,
        @RequestBody request: CreateCommentRequest,
    ): ResponseEntity<CreateCommentResponse> {
        val command = request.toCommand(userId)
        val info = commentUseCases.handle(command)
        val response = info.toResponse()
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getComments(
        @RequestParam(required = true)
        refId: Long,
        @RequestParam(required = true)
        refType: Comment.RefType,
    ): ResponseEntity<GetCommentsResponse> {
        val query = IGetHierarchyCommentsByRef.Query(
            refId = ReferenceId(refId),
            refType = refType,
        )
        val info = commentQueries.handle(query)
        val response = GetCommentsResponse(
            comments = info.comments
        )
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @AuthUserId userId: Long?,
        @PathVariable commentId: Long,
        @RequestBody request: DeleteCommentRequest,
    ): ResponseEntity<DeleteCommentResponse> {
        val command = IDeleteComment.Command(
            commentId = CommentId(commentId),
            authorId = userId,
            password = request.password,
        )
        val info = commentUseCases.handle(command)
        val response = DeleteCommentResponse(
            commentId = info.commentId.value,
        )
        return ResponseEntity.ok(response)
    }
}
