package kr.co.jiniaslog.memo.adapter.inbound.http

import kr.co.jiniaslog.memo.adapter.inbound.http.dto.ChangeFolderNameRequest
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.ChangeFolderNameResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.CreateNewFolderResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.DeleteFolderResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.GetFolderAndMemoResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.MakeFolderRelationshipRequest
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.MakeFolderRelationshipResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.toResponse
import kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel.FolderViewModel
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.queries.FolderQueriesFacade
import kr.co.jiniaslog.memo.queries.IGetFoldersAllInHierirchyByAuthorId
import kr.co.jiniaslog.memo.usecase.FolderUseCasesFacade
import kr.co.jiniaslog.memo.usecase.ICreateNewFolder
import kr.co.jiniaslog.memo.usecase.IDeleteFoldersRecursively
import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndFolder
import kr.cojiniaslog.shared.adapter.inbound.http.AuthUserId
import org.springframework.http.ResponseEntity
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

private val log = mu.KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/folders")
class FolderResources(
    private val folderUseCases: FolderUseCasesFacade,
    private val folderQueries: FolderQueriesFacade,
) {
    @PostMapping()
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun createNewFolder(
        @AuthUserId userId: Long?,
    ): ResponseEntity<CreateNewFolderResponse> {
        val info = folderUseCases.handle(ICreateNewFolder.Command(AuthorId(userId!!)))
        return ResponseEntity.created(
            java.net.URI("/api/v1/folders/${info.id.value}"),
        ).body(
            CreateNewFolderResponse(
                FolderViewModel(
                    id = info.id.value,
                    name = info.folderName.value,
                    parent = null,
                    children = mutableListOf(),
                    memos = mutableListOf()
                )
            )
        )
    }

    @PutMapping("/{folderId}/name")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun changeFolderName(
        @AuthUserId userId: Long?,
        @RequestBody request: ChangeFolderNameRequest,
    ): ResponseEntity<ChangeFolderNameResponse> {
        val info = folderUseCases.handle(request.toCommand(AuthorId(userId!!)))
        return ResponseEntity.ok(ChangeFolderNameResponse(info.folderId.value))
    }

    @PutMapping("/{folderId}/parent")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun makeRelationshipWithFolders(
        @PathVariable folderId: Long,
        @AuthUserId userId: Long?,
        @RequestBody request: MakeFolderRelationshipRequest,
    ): ResponseEntity<MakeFolderRelationshipResponse> {
        val parentId = request.parentId?.let { FolderId(request.parentId) }
        val info =
            folderUseCases.handle(
                IMakeRelationShipFolderAndFolder.Command(
                    parentId,
                    FolderId(folderId),
                    AuthorId(userId!!),
                ),
            )
        return ResponseEntity.ok(MakeFolderRelationshipResponse(info.parentFolderId?.value, info.childFolderId.value))
    }

    @DeleteMapping("/{folderId}")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun deleteFolder(
        @AuthUserId userId: Long?,
        @PathVariable folderId: Long,
    ): ResponseEntity<DeleteFolderResponse> {
        val info =
            folderUseCases.handle(IDeleteFoldersRecursively.Command(FolderId(folderId), AuthorId(userId!!)))
        return ResponseEntity.ok(DeleteFolderResponse(info.folderId.value))
    }

    @GetMapping
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun getFoldersAndMemoAll(
        @RequestParam(required = false) query: String?,
        @AuthUserId userId: Long?,
    ): ResponseEntity<GetFolderAndMemoResponse> {
        log.info { "query: $query" }
        return ResponseEntity.ok(
            folderQueries.handle(IGetFoldersAllInHierirchyByAuthorId.Query(query, AuthorId(userId!!))).toResponse()
        )
    }
}
