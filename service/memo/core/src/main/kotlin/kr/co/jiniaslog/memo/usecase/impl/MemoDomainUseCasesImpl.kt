package kr.co.jiniaslog.memo.usecase.impl

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderRepository
import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.memo.usecase.IDeleteMemo
import kr.co.jiniaslog.memo.usecase.IInitMemo
import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndMemo
import kr.co.jiniaslog.memo.usecase.IUpdateMemo
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

interface MemoUseCasesFacade :
    IInitMemo,
    IUpdateMemo,
    IDeleteMemo,
    IMakeRelationShipFolderAndMemo

@UseCaseInteractor
internal class MemoUseCases(
    private val memoRepository: MemoRepository,
    private val folderRepository: FolderRepository,
) : MemoUseCasesFacade {
    override fun handle(command: IInitMemo.Command): IInitMemo.Info {
        val newOne =
            Memo.init(
                title = command.title,
                content = command.content,
                authorId = command.authorId,
                references = command.references,
            )
        memoRepository.save(newOne)
        return IInitMemo.Info(newOne.id)
    }

    override fun handle(command: IUpdateMemo.Command): IUpdateMemo.Info {
        val memo = getMemo(command.memoId)

        when (command) {
            is IUpdateMemo.Command.AddReference -> {
                getMemo(command.referenceId)
                memo.addReference(command.referenceId)
            }

            is IUpdateMemo.Command.RemoveReference -> {
                getMemo(command.referenceId)
                memo.removeReference(command.referenceId)
            }

            is IUpdateMemo.Command.UpdateForm -> {
                memo.update(command.title, command.content)
            }
        }

        memoRepository.save(memo)
        return IUpdateMemo.Info(memo.id)
    }

    override fun handle(command: IDeleteMemo.Command): IDeleteMemo.Info {
        val memo = getMemo(command.id)
        memoRepository.deleteById(memo.id)
        return IDeleteMemo.Info()
    }

    override fun handle(command: IMakeRelationShipFolderAndMemo.Command): IMakeRelationShipFolderAndMemo.Info {
        val folder =
            when (command.folderId) {
                null -> null
                else -> getFolder(command.folderId)
            }
        val memo = getMemo(command.memoId)

        memo.setParentFolder(command.folderId)
        memoRepository.save(memo)
        return IMakeRelationShipFolderAndMemo.Info(memo.id, command.folderId)
    }

    private fun getFolder(id: FolderId) =
        (
            folderRepository.findById(id)
                ?: throw IllegalArgumentException("FolderId : $id, folder not found")
        )

    private fun getMemo(id: MemoId) =
        memoRepository.findById(id)
            ?: throw IllegalArgumentException("MemoId : $id, memo not found")
}
