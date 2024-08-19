package kr.co.jiniaslog.memo.adapter.inbound.http

import kr.co.jiniaslog.memo.adapter.inbound.http.dto.AddParentFolderRequest
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.AddParentFolderResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.CreateEmptyMemoResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.GetAllReferencedByMemoResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.GetAllReferencesByMemoResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.GetMemoByIdResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.InitMemoRequest
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.RecommendRelatedMemoResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.toResponse
import kr.co.jiniaslog.memo.domain.exception.NotOwnershipException
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.queries.IGetAllReferencedByMemo
import kr.co.jiniaslog.memo.queries.IGetAllReferencesByMemo
import kr.co.jiniaslog.memo.queries.IGetMemoById
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.memo.queries.MemoQueriesFacade
import kr.co.jiniaslog.memo.usecase.IDeleteMemo
import kr.co.jiniaslog.memo.usecase.IInitMemo
import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndMemo
import kr.co.jiniaslog.memo.usecase.MemoUseCasesFacade
import kr.cojiniaslog.shared.adapter.inbound.http.AuthUserId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.awt.SystemColor.info
import java.net.URI

private val log = mu.KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/memos")
class MemoResources(
    private val memoUseCases: MemoUseCasesFacade,
    private val memoQueries: MemoQueriesFacade,
) {
    @ExceptionHandler(value = [NotOwnershipException::class])
    fun notOwnershipExceptionHandler(e: NotOwnershipException): ResponseEntity<Any> {
        return ResponseEntity.status(403).body(e.message)
    }

    @PostMapping
    fun createEmptyMemo(
        @AuthUserId userId: Long?,
        @RequestBody request: InitMemoRequest,
    ): ResponseEntity<CreateEmptyMemoResponse> {
        val command = IInitMemo.Command(
            authorId = AuthorId(userId!!),
            parentFolderId = request.parentFolderId?.let { FolderId(it) },
        )
        val info = memoUseCases.handle(command)
        return ResponseEntity
            .created(URI("/api/v1/memos/${info.id.value}"))
            .body(CreateEmptyMemoResponse(info.id.value))
    }

    @DeleteMapping("/{id}")
    fun deleteMemoById(
        @PathVariable id: Long,
        @AuthUserId userId: Long?,
    ): ResponseEntity<Unit> {
        val command = IDeleteMemo.Command(MemoId(id), AuthorId(userId!!))
        memoUseCases.handle(command)
        return ResponseEntity
            .status(204)
            .build()
    }

    @PutMapping("/{id}/parent")
    fun addParentFolder(
        @PathVariable id: Long,
        @AuthUserId userId: Long?,
        @RequestBody request: AddParentFolderRequest,
    ): ResponseEntity<AddParentFolderResponse> {
        val command = IMakeRelationShipFolderAndMemo.Command(
            memoId = MemoId(id),
            folderId = request.folderId?.let { FolderId(it) },
            requesterId = AuthorId(userId!!),
        )
        val info = memoUseCases.handle(command)
        return ResponseEntity.ok(info.toResponse())
    }

    @GetMapping("/{id}")
    fun getMemoById(
        @PathVariable id: Long,
        @AuthUserId userId: Long?,
    ): ResponseEntity<GetMemoByIdResponse> {
        log.info { "getMemoById 쿼리 호출: $id" }
        val query = IGetMemoById.Query(MemoId(id), AuthorId(userId!!))
        val info = memoQueries.handle(query)
        return ResponseEntity.ok(info.toResponse())
    }

    @GetMapping("/{id}/recommended")
    fun recommendRelatedMemo(
        @PathVariable id: Long,
        @RequestParam keyword: String,
        @AuthUserId userId: Long?,
    ): ResponseEntity<RecommendRelatedMemoResponse> {
        val query = IRecommendRelatedMemo.Query(keyword, MemoId(id), AuthorId(userId!!))
        val info = memoQueries.handle(query)
        return ResponseEntity.ok(info.toResponse())
    }

    @GetMapping("/{id}/references")
    fun getAllReferencesByMemo(
        @PathVariable id: Long,
        @AuthUserId userId: Long?,
    ): ResponseEntity<GetAllReferencesByMemoResponse> {
        val query = IGetAllReferencesByMemo.Query(MemoId(id), AuthorId(userId!!))
        val info = memoQueries.handle(query)
        return ResponseEntity.ok(info.toResponse())
    }

    @GetMapping("/{id}/referenced")
    fun getAllReferencedByMemo(
        @PathVariable id: Long,
        @AuthUserId userId: Long?,
    ): ResponseEntity<GetAllReferencedByMemoResponse> {
        val query = IGetAllReferencedByMemo.Query(MemoId(id), AuthorId(userId!!))
        val info = memoQueries.handle(query)
        return ResponseEntity.ok(info.toResponse())
    }
}
