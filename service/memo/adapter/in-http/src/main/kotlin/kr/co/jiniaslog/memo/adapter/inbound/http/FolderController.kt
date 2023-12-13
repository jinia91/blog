package kr.co.jiniaslog.memo.adapter.inbound.http

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.usecase.IDeleteFoldersRecursively
import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndFolder
import kr.co.jiniaslog.memo.usecase.impl.FolderUseCasesFacade
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class FolderController(
    private val folderUseCases: FolderUseCasesFacade,
) {
    @PostMapping("/folder")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun initFolder(
        @RequestBody request: InitFolderRequest,
    ): InitFolderResponse {
        val info =
            folderUseCases.handle(request.toCommand())
        return InitFolderResponse(info.id.value)
    }

    @PutMapping("/folder/{folderId}")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun changeFolderName(
        @RequestBody request: ChangeFolderNameRequest,
    ): ChangeFolderNameResponse {
        val info =
            folderUseCases.handle(request.toCommand())
        return ChangeFolderNameResponse(info.folderId.value)
    }

    @PutMapping("/folder/{folderId}/parent/{parentFolderId}")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun makeRelationshipWithFolders(
        @PathVariable folderId: Long,
        @PathVariable parentFolderId: Long?,
    ): MakeFolderRelationshipResponse {
        val info =
            folderUseCases.handle(
                IMakeRelationShipFolderAndFolder.Command(parentFolderId?.let { FolderId(parentFolderId) }, FolderId(folderId)),
            )
        return MakeFolderRelationshipResponse(info.parentFolderId?.value, info.childFolderId.value)
    }

    @DeleteMapping("/folder/{folderId}")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun deleteFolder(
        @PathVariable folderId: Long,
    ): DeleteFolderResponse {
        val info =
            folderUseCases.handle(IDeleteFoldersRecursively.Command(FolderId(folderId)))
        return DeleteFolderResponse(info.folderId.value)
    }
}
