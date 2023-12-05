package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.memo.domain.tag.TagId
import kr.co.jiniaslog.memo.domain.tag.TagRepository
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

interface MemoUseCasesFacade :
    IInitMemo,
    IUpdateMemo,
    ICommitMemo

@UseCaseInteractor
internal class MemoUseCases(
    private val memoRepository: MemoRepository,
    private val tagRepository: TagRepository,
) : MemoUseCasesFacade {
    override fun handle(command: IInitMemo.Command): IInitMemo.Info {
        val tags = command.tags.map { getTag(it) }.toSet()

        val newOne =
            Memo.init(
                title = command.title,
                content = command.content,
                authorId = command.authorId,
                references = command.references,
                tags = tags,
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

            is IUpdateMemo.Command.AddTag -> {
                memo.addTag(getTag(command.tagId))
            }

            is IUpdateMemo.Command.RemoveTag -> {
                memo.removeTag(getTag(command.tagId))
            }

            is IUpdateMemo.Command.UpdateForm -> {
                memo.update(command.title, command.content)
            }
        }

        memoRepository.save(memo)
        return IUpdateMemo.Info(memo.id)
    }

    private fun getTag(tagId: TagId) =
        tagRepository.findById(tagId)
            ?: throw IllegalArgumentException("TagId : $tagId, tag not found")

    override fun handle(command: ICommitMemo.Command): ICommitMemo.Info {
        val memo = getMemo(command.memoId)
        memo.commit(command.title, command.content)
        memoRepository.save(memo)
        return ICommitMemo.Info(memo.id)
    }

    private fun getMemo(referenceId: MemoId) =
        memoRepository.findById(referenceId)
            ?: throw IllegalArgumentException("MemoId : $referenceId, memo not found")
}
