package kr.co.jiniaslog.memo.adapter.inbound.http

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.queries.FolderQueriesFacade
import kr.co.jiniaslog.memo.queries.IGetFoldersAllInHierirchy
import kr.co.jiniaslog.memo.usecase.FolderUseCasesFacade
import kr.co.jiniaslog.memo.usecase.ICreateNewFolder
import kr.co.jiniaslog.memo.usecase.IDeleteFoldersRecursively
import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndFolder
import kr.cojiniaslog.shared.adapter.inbound.http.AuthUserId
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
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

@RestController
@RequestMapping("/api/v1/folders")
@PreAuthorize("hasRole('ADMIN')")
class FolderResources(
    private val folderUseCases: FolderUseCasesFacade,
    private val folderQueries: FolderQueriesFacade,
) {
    @PostMapping()
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun createNewFolder(
        @AuthUserId userId: Long?,
    ): ResponseEntity<InitFolderResponse> {
        val info =
            folderUseCases.handle(ICreateNewFolder.Command(AuthorId(userId!!)))
        return ResponseEntity.created(
            java.net.URI("/api/v1/folders/${info.id.value}"),
        ).body(InitFolderResponse(info.id.value, info.folderName.value))
    }

    @PutMapping("/{folderId}/name")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun changeFolderName(
        @RequestBody request: ChangeFolderNameRequest,
    ): ResponseEntity<ChangeFolderNameResponse> {
        val info =
            folderUseCases.handle(request.toCommand())
        return ResponseEntity.ok(ChangeFolderNameResponse(info.folderId.value))
    }

    @PutMapping("/{folderId}/parent/{parentFolderId}")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun makeRelationshipWithFolders(
        @PathVariable folderId: Long,
        @PathVariable parentFolderId: Long?,
    ): ResponseEntity<MakeFolderRelationshipResponse> {
        val info =
            folderUseCases.handle(
                IMakeRelationShipFolderAndFolder.Command(
                    parentFolderId?.let { FolderId(parentFolderId) },
                    FolderId(folderId),
                ),
            )
        return ResponseEntity.ok(MakeFolderRelationshipResponse(info.parentFolderId?.value, info.childFolderId.value))
    }

    @DeleteMapping("/{folderId}")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun deleteFolder(
        @PathVariable folderId: Long,
    ): ResponseEntity<DeleteFolderResponse> {
        val info =
            folderUseCases.handle(IDeleteFoldersRecursively.Command(FolderId(folderId)))
        return ResponseEntity.ok(DeleteFolderResponse(info.folderId.value))
    }

    @GetMapping()
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun getFoldersAndMemoAll(
        @RequestParam(required = false) query: String?,
    ): ResponseEntity<FolderAndMemoResponse> {
        return ResponseEntity.ok(
            folderQueries.handle(IGetFoldersAllInHierirchy.Query(query)).toResponse()
        )
    }
}
