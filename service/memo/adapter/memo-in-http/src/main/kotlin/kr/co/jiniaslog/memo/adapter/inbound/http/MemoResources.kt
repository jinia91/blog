package kr.co.jiniaslog.memo.adapter.inbound.http

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.queries.IGetAllReferencedByMemo
import kr.co.jiniaslog.memo.queries.IGetAllReferencesByMemo
import kr.co.jiniaslog.memo.queries.IGetMemoById
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.memo.queries.QueriesMemoFacade
import kr.co.jiniaslog.memo.usecase.IDeleteMemo
import kr.co.jiniaslog.memo.usecase.IInitMemo
import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndMemo
import kr.co.jiniaslog.memo.usecase.UseCasesMemoFacade
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

private val log = mu.KotlinLogging.logger { }

@RestController
@RequestMapping("/api/v1/memos")
@PreAuthorize("hasRole('ADMIN')")
class MemoResources(
    private val memoUseCases: UseCasesMemoFacade,
    private val memoQueries: QueriesMemoFacade,
) {
    @PostMapping
    fun createEmptyMemo(
        @AuthUserId userId: Long?,
        @RequestBody request: InitMemoRequest,
    ): ResponseEntity<InitMemoResponse> {
        require(userId != null) { "반드시 인증된 사용자여야합니다" }
        val info =
            memoUseCases.handle(
                IInitMemo.Command(
                    authorId = AuthorId(userId),
                    parentFolderId = request.parentFolderId?.let { FolderId(it) },
                ),
            )
        return ResponseEntity
            .created(URI("/api/v1/memos/${info.id.value}"))
            .body(InitMemoResponse(info.id.value))
    }

    @DeleteMapping("/{id}")
    fun deleteMemoById(
        @PathVariable id: Long,
    ): ResponseEntity<Unit> {
        memoUseCases.handle(IDeleteMemo.Command(MemoId(id)))
        return ResponseEntity
            .status(204)
            .build()
    }

    @PutMapping("/{id}/parent")
    fun addParentFolder(
        @PathVariable id: Long,
        @RequestBody request: AddParentFolderRequest,
    ): ResponseEntity<AddParentFolderResponse> {
        val response =
            memoUseCases.handle(
                IMakeRelationShipFolderAndMemo.Command(
                    memoId = MemoId(id),
                    folderId = request.folderId?.let { FolderId(it) },
                ),
            ).toResponse()
        return ResponseEntity
            .ok(response)
    }

    @GetMapping("/{id}")
    fun getMemoById(
        @PathVariable id: Long,
    ): ResponseEntity<GetMemoByIdResponse> {
        val response =
            memoQueries.handle(IGetMemoById.Query(MemoId(id)))
                .toResponse()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/recommended")
    fun recommendRelatedMemo(
        @PathVariable id: Long,
        @RequestParam keyword: String,
    ): ResponseEntity<MemoResponse> {
        val response =
            memoQueries.handle(IRecommendRelatedMemo.Query(keyword, MemoId(id)))
                .toResponse()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/references")
    fun getAllReferencesByMemo(
        @PathVariable id: Long,
    ): ResponseEntity<GetAllReferencesByMemoResponse> {
        val reference =
            memoQueries.handle(IGetAllReferencesByMemo.Query(MemoId(id)))
                .toResponse()
        return ResponseEntity.ok(reference)
    }

    @GetMapping("/{id}/referenced")
    fun getAllReferencedByMemo(
        @PathVariable id: Long,
    ): ResponseEntity<GetAllReferencedByMemoResponse> {
        val referenced =
            memoQueries.handle(IGetAllReferencedByMemo.Query(MemoId(id)))
                .toResponse()
        return ResponseEntity.ok(referenced)
    }
}
