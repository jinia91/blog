package kr.co.jiniaslog.memo.usecase.impl

import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderRepository
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.user.UserService
import kr.co.jiniaslog.memo.usecase.FolderUseCasesFacade
import kr.co.jiniaslog.memo.usecase.IChangeFolderName
import kr.co.jiniaslog.memo.usecase.ICreateNewFolder
import kr.co.jiniaslog.memo.usecase.IDeleteAllWithoutAdmin
import kr.co.jiniaslog.memo.usecase.IDeleteFoldersRecursively
import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndFolder
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import org.springframework.cache.annotation.CacheEvict

@UseCaseInteractor
internal class FolderUseCasesInteractor(
    private val folderRepository: FolderRepository,
    private val userService: UserService
) : FolderUseCasesFacade {
    @CacheEvict(value = ["folders"], key = "#command.authorId")
    override fun handle(command: ICreateNewFolder.Command): ICreateNewFolder.Info {
        ensureFolderCountIsUnderLimit(authorId = command.authorId)
        val newOne = Folder.init(authorId = command.authorId, parent = command.parentId)
        folderRepository.save(newOne)
        return ICreateNewFolder.Info(newOne.entityId, newOne.name, newOne.parent)
    }

    private fun ensureFolderCountIsUnderLimit(authorId: AuthorId) {
        val totalCountOfFolder = folderRepository.countByAuthorId(authorId)
        if (totalCountOfFolder >= Folder.INIT_LIMIT) {
            throw IllegalArgumentException("폴더 개수가 ${Folder.INIT_LIMIT}개를 초과했습니다.")
        }
    }

    @CacheEvict(value = ["folders"], key = "#command.requesterId")
    override fun handle(command: IChangeFolderName.Command): IChangeFolderName.Info {
        val folder = getFolder(command.folderId)
        folder.validateOwnership(command.requesterId)
        folder.changeName(command.name,)
        folderRepository.save(folder)
        return IChangeFolderName.Info(folder.entityId)
    }

    @CacheEvict(value = ["folders"], key = "#command.requesterId")
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

    @CacheEvict(value = ["folders"], key = "#command.requesterId")
    override fun handle(command: IDeleteFoldersRecursively.Command): IDeleteFoldersRecursively.Info {
        val folder = getFolder(command.folderId)
        folder.validateOwnership(command.requesterId)
        folderRepository.deleteById(folder.entityId)
        return IDeleteFoldersRecursively.Info(folder.entityId)
    }

    override fun handle(command: IDeleteAllWithoutAdmin.Command): IDeleteAllWithoutAdmin.Info {
        val adminIds = userService.retrieveAdminUserIds()
        folderRepository.deleteAllFoldersAndMemosWithout(adminIds)
        return IDeleteAllWithoutAdmin.Info()
    }

    private fun getFolder(id: FolderId) =
        folderRepository.findById(id)
            ?: throw IllegalArgumentException("FolderId : $id, folder not found")
}
