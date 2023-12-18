package kr.co.jiniaslog.memo.adapter.inbound.http

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.queries.IGetAllMemos
import kr.co.jiniaslog.memo.queries.IGetMemoById
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.memo.queries.QueriesMemoFacade
import kr.co.jiniaslog.memo.usecase.IDeleteMemo
import kr.co.jiniaslog.memo.usecase.IInitMemo
import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndMemo
import kr.co.jiniaslog.memo.usecase.impl.MemoUseCasesFacade
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val log = mu.KotlinLogging.logger { }

@RestController
@RequestMapping("/api/v1")
class MemoController(
    private val memoUseCases: MemoUseCasesFacade,
    private val memoQueries: QueriesMemoFacade,
) {
    @PostMapping("/memo")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun initMemo(
        @RequestBody request: InitMemoRequest,
    ): InitMemoResponse {
        log.info { "initMemo request: $request" }
        val info =
            memoUseCases.handle(
                IInitMemo.Command(authorId = AuthorId(request.authorId)),
            )
        return InitMemoResponse(info.id.value)
    }

    @GetMapping("/memo")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun getMemos(
        @RequestParam keyword: String?,
        @RequestParam thisId: Long?,
    ): MemoResponse {
        log.info { "getMemos request: $keyword, $thisId" }
        if (keyword.isNullOrBlank() && thisId == null) {
            return memoQueries.handle(IGetAllMemos.Query()).sortedBy { it.memoId.value }
                .toResponse()
        }

        return memoQueries.handle(IRecommendRelatedMemo.Query(keyword!!, MemoId(thisId!!)))
            .toResponse()
    }

    @GetMapping("/memo/{id}")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun getMemoById(
        @PathVariable id: Long,
    ): GetMemoByIdResponse {
        log.info { "getMemoById request: $id" }
        return memoQueries.handle(IGetMemoById.Query(MemoId(id)))
            .toResponse()
    }

    @DeleteMapping("/memo/{id}")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun deleteMemoById(
        @PathVariable id: Long,
    ): DeleteMemoByIdResponse {
        memoUseCases.handle(IDeleteMemo.Command(MemoId(id)))
        return DeleteMemoByIdResponse()
    }

    @PutMapping("/memo/{id}/folder/{folderId}")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun addParentFolder(
        @PathVariable id: Long,
        @PathVariable folderId: Long,
    ): AddParentFolderResponse {
        log.info { "addParentFolder request: $id, $folderId" }
        return memoUseCases.handle(
            IMakeRelationShipFolderAndMemo.Command(
                memoId = MemoId(id),
                folderId = folderId.takeIf { it != -1L }?.let { FolderId(it) },
            ),
        ).toResponse()
    }
}

private fun IMakeRelationShipFolderAndMemo.Info.toResponse(): AddParentFolderResponse {
    return AddParentFolderResponse(
        memoId = this.memoId.value,
        folderId = this.folderId?.value,
    )
}
