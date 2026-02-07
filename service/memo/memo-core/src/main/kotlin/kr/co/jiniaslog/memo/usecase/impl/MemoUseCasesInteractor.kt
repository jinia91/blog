package kr.co.jiniaslog.memo.usecase.impl

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderRepository
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoCreatedEvent
import kr.co.jiniaslog.memo.domain.memo.MemoDeletedEvent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.memo.domain.memo.MemoUpdatedEvent
import kr.co.jiniaslog.memo.usecase.ICreateMemoWithContent
import kr.co.jiniaslog.memo.usecase.IDeleteMemo
import kr.co.jiniaslog.memo.usecase.IInitMemo
import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndMemo
import kr.co.jiniaslog.memo.usecase.IReorderMemo
import kr.co.jiniaslog.memo.usecase.IUpdateMemoContents
import kr.co.jiniaslog.memo.usecase.IUpdateMemoReferences
import kr.co.jiniaslog.memo.usecase.MemoUseCasesFacade
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import org.springframework.cache.annotation.CacheEvict
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional

@UseCaseInteractor
internal class MemoUseCasesInteractor(
    private val memoRepository: MemoRepository,
    private val folderRepository: FolderRepository,
    private val eventPublisher: ApplicationEventPublisher,
) : MemoUseCasesFacade {
    @CacheEvict(value = ["folders"], key = "#command.authorId")
    @Transactional("memoTransactionManager")
    override fun handle(command: IInitMemo.Command): IInitMemo.Info {
        ensureMemoCountIsUnderLimit(authorId = command.authorId)
        command.parentFolderId?.let { getFolder(it) }
        val newOne =
            Memo.init(
                authorId = command.authorId,
                parentFolderId = command.parentFolderId,
            )
        memoRepository.save(newOne)
        eventPublisher.publishEvent(
            MemoCreatedEvent(
                memoId = newOne.entityId.value,
                authorId = newOne.authorId.value,
                title = newOne.title.value,
                content = newOne.content.value,
            )
        )
        return IInitMemo.Info(newOne.entityId)
    }

    private fun ensureMemoCountIsUnderLimit(authorId: AuthorId) {
        val totalCountOfMemo = memoRepository.countByAuthorId(authorId)
        if (totalCountOfMemo >= Memo.INIT_LIMIT) {
            throw IllegalArgumentException("메모 개수가 ${Memo.INIT_LIMIT}개를 초과했습니다.")
        }
    }

    @CacheEvict(value = ["folders"], key = "#command.authorId")
    @Transactional("memoTransactionManager")
    override fun handle(command: ICreateMemoWithContent.Command): ICreateMemoWithContent.Info {
        ensureMemoCountIsUnderLimit(authorId = command.authorId)
        command.parentFolderId?.let { getFolder(it) }

        val memo = Memo.init(
            authorId = command.authorId,
            parentFolderId = command.parentFolderId,
        )
        memo.update(command.title, command.content)

        memoRepository.save(memo)

        eventPublisher.publishEvent(
            MemoCreatedEvent(
                memoId = memo.entityId.value,
                authorId = memo.authorId.value,
                title = memo.title.value,
                content = memo.content.value,
            )
        )

        return ICreateMemoWithContent.Info(memo.entityId)
    }

    @Transactional("memoTransactionManager")
    override fun handle(command: IUpdateMemoContents.Command): IUpdateMemoContents.Info {
        val memo = getMemo(command.memoId)
        memo.update(command.title, command.content)
        memoRepository.save(memo)
        eventPublisher.publishEvent(
            MemoUpdatedEvent(
                memoId = memo.entityId.value,
                authorId = memo.authorId.value,
                title = memo.title.value,
                content = memo.content.value,
            )
        )
        return IUpdateMemoContents.Info(memo.entityId)
    }

    private fun validateMemoExistence(id: MemoId) {
        getMemo(id)
    }

    @Transactional("memoTransactionManager")
    @CacheEvict(value = ["folders"], key = "#command.requesterId")
    override fun handle(command: IDeleteMemo.Command): IDeleteMemo.Info {
        val memo = getMemo(command.id)
        memo.validateOwnership(command.requesterId)
        val memoId = memo.entityId.value
        memoRepository.deleteById(memo.entityId)
        eventPublisher.publishEvent(MemoDeletedEvent(memoId = memoId))
        return IDeleteMemo.Info()
    }

    @CacheEvict(value = ["folders"], key = "#command.requesterId")
    override fun handle(command: IMakeRelationShipFolderAndMemo.Command): IMakeRelationShipFolderAndMemo.Info {
        val folder = command.folderId?.let {
            getFolder(it).also { it.validateOwnership(command.requesterId) }
        }
        val memo = getMemo(command.memoId)
        memo.validateOwnership(command.requesterId)

        memo.setParentFolder(folder?.entityId)

        memoRepository.save(memo)
        return IMakeRelationShipFolderAndMemo.Info(memo.entityId, command.folderId)
    }

    @CacheEvict(value = ["folders"], allEntries = true) // TODO: cache key
    override fun handle(command: IUpdateMemoReferences.Command): IUpdateMemoReferences.Info {
        val memo = getMemo(command.memoId)

        when (command) {
            is IUpdateMemoReferences.Command.RemoveReference -> {
                validateMemoExistence(command.referenceId)
                memo.removeReference(command.referenceId)
            }

            is IUpdateMemoReferences.Command.UpdateReferences -> {
                command.references.forEach { validateCircularDependency(it, memo) }
                memo.updateReferences(command.references)
            }
        }

        memoRepository.save(memo)
        return IUpdateMemoReferences.Info(memo.entityId)
    }

    private fun validateCircularDependency(id: MemoId, targetMemo: Memo) {
        val memo = getMemo(id)
        memo.getReferences().forEach {
            if (it.referenceId == targetMemo.entityId) {
                throw IllegalArgumentException("순환참조가 발생합니다")
            }
        }
    }

    @CacheEvict(value = ["folders"], key = "#command.requesterId")
    override fun handle(command: IReorderMemo.Command): IReorderMemo.Info {
        val memo = getMemo(command.memoId)
        memo.validateOwnership(command.requesterId)
        memo.changeSequence(command.newSequence)
        memoRepository.save(memo)
        return IReorderMemo.Info(memo.entityId, memo.sequence)
    }

    private fun getFolder(id: FolderId) =
        folderRepository.findById(id)
            ?: throw IllegalArgumentException("FolderId : $id, folder not found")

    private fun getMemo(id: MemoId) =
        memoRepository.findById(id)
            ?: throw IllegalArgumentException("MemoId : $id, memo not found")
}
