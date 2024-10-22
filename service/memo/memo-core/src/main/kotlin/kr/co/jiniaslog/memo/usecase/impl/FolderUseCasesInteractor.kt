package kr.co.jiniaslog.memo.usecase.impl

import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderRepository
import kr.co.jiniaslog.memo.usecase.FolderUseCasesFacade
import kr.co.jiniaslog.memo.usecase.IChangeFolderName
import kr.co.jiniaslog.memo.usecase.ICreateNewFolder
import kr.co.jiniaslog.memo.usecase.IDeleteFoldersRecursively
import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndFolder
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

@UseCaseInteractor
internal class FolderUseCasesInteractor(
    private val folderRepository: FolderRepository,
) : FolderUseCasesFacade {
    override fun handle(command: ICreateNewFolder.Command): ICreateNewFolder.Info {
        ensureFolderCountIsUnderLimit()
        val newOne = Folder.init(authorId = command.authorId)
        folderRepository.save(newOne)
        return ICreateNewFolder.Info(newOne.entityId, newOne.name)
    }

    private fun ensureFolderCountIsUnderLimit() {
        val totalCountOfFolder = folderRepository.count()
        if (totalCountOfFolder >= 100) {
            throw IllegalArgumentException("폴더 개수가 100개를 초과했습니다.")
        }
    }

    override fun handle(command: IChangeFolderName.Command): IChangeFolderName.Info {
        val folder = getFolder(command.folderId)
        folder.validateOwnership(command.requesterId)
        folder.changeName(command.name,)
        folderRepository.save(folder)
        return IChangeFolderName.Info(folder.entityId)
    }

    override fun handle(command: IMakeRelationShipFolderAndFolder.Command): IMakeRelationShipFolderAndFolder.Info {
        val parent = command.parentFolderId?.let {
            getFolder(it).also { folder -> folder.validateOwnership(command.requesterId) }
        }
        val child = getFolder(command.childFolderId)
        child.validateOwnership(command.requesterId)
        child.changeParent(parent)
        folderRepository.save(child)
        return IMakeRelationShipFolderAndFolder.Info(parent?.entityId, child.entityId)
    }

    override fun handle(command: IDeleteFoldersRecursively.Command): IDeleteFoldersRecursively.Info {
        val folder = getFolder(command.folderId)
        folder.validateOwnership(command.requesterId)
        folderRepository.deleteById(folder.entityId)
        return IDeleteFoldersRecursively.Info(folder.entityId)
    }

    private fun getFolder(id: FolderId) =
        folderRepository.findById(id)
            ?: throw IllegalArgumentException("FolderId : $id, folder not found")
}
