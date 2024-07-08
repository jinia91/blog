package kr.co.jiniaslog.memo.adapter.inbound.http

import kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel.AddParentFolderRequest
import kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel.GetAllReferencedByMemoResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel.GetAllReferencesByMemoResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel.GetMemoByIdResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel.InitMemoRequest
import kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel.InitMemoResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel.RecommendRelatedMemoResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel.toResponse
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
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/memos")
@PreAuthorize("hasRole('ADMIN')")
class MemoResources(
    private val memoUseCases: MemoUseCasesFacade,
    private val memoQueries: MemoQueriesFacade,
) {
    @PostMapping
    fun createEmptyMemo(
        @AuthUserId userId: Long?,
        @RequestBody request: InitMemoRequest,
    ): ResponseEntity<InitMemoResponse> {
        val command = IInitMemo.Command(
            authorId = AuthorId(userId!!),
            parentFolderId = request.parentFolderId?.let { FolderId(it) },
        )
        val info = memoUseCases.handle(command)
        return ResponseEntity
            .created(URI("/api/v1/memos/${info.id.value}"))
            .body(InitMemoResponse(info.id.value))
    }

    @DeleteMapping("/{id}")
    fun deleteMemoById(
        @PathVariable id: Long,
    ): ResponseEntity<Unit> {
        val command = IDeleteMemo.Command(MemoId(id))
        memoUseCases.handle(command)
        return ResponseEntity
            .status(204)
            .build()
    }

    @PutMapping("/{id}/parent")
    fun addParentFolder(
        @PathVariable id: Long,
        @RequestBody request: AddParentFolderRequest,
    ): ResponseEntity<AddParentFolderResponse> {
        val command = IMakeRelationShipFolderAndMemo.Command(
            memoId = MemoId(id),
            folderId = request.folderId?.let { FolderId(it) },
        )
        val info = memoUseCases.handle(command)
        return ResponseEntity.ok(info.toResponse())
    }

    @GetMapping("/{id}")
    fun getMemoById(
        @PathVariable id: Long,
    ): ResponseEntity<GetMemoByIdResponse> {
        val query = IGetMemoById.Query(MemoId(id))
        val info = memoQueries.handle(query)
        return ResponseEntity.ok(info.toResponse())
    }

    @GetMapping("/{id}/recommended")
    fun recommendRelatedMemo(
        @PathVariable id: Long,
        @RequestParam keyword: String,
    ): ResponseEntity<RecommendRelatedMemoResponse> {
        val query = IRecommendRelatedMemo.Query(keyword, MemoId(id))
        val info = memoQueries.handle(query)
        return ResponseEntity.ok(info.toResponse())
    }

    @GetMapping("/{id}/references")
    fun getAllReferencesByMemo(
        @PathVariable id: Long,
    ): ResponseEntity<GetAllReferencesByMemoResponse> {
        val query = IGetAllReferencesByMemo.Query(MemoId(id))
        val info = memoQueries.handle(query)
        return ResponseEntity.ok(info.toResponse())
    }

    @GetMapping("/{id}/referenced")
    fun getAllReferencedByMemo(
        @PathVariable id: Long,
    ): ResponseEntity<GetAllReferencedByMemoResponse> {
        val query = IGetAllReferencedByMemo.Query(MemoId(id))
        val info = memoQueries.handle(query)
        return ResponseEntity.ok(info.toResponse())
    }
}
