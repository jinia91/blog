package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoIndexStorage
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.memo.domain.tag.TagRepository
import kr.co.jiniaslog.memo.usecase.ICommitMemo.CommitMemoCommand
import kr.co.jiniaslog.memo.usecase.ICommitMemo.CommitMemoInfo
import kr.co.jiniaslog.memo.usecase.IGetRecommendRelatedMemo.GetRecommendRelatedMemoCommand
import kr.co.jiniaslog.memo.usecase.IGetRecommendRelatedMemo.GetRecommendRelatedMemoInfo
import kr.co.jiniaslog.memo.usecase.IInitMemo.InitMemoCommand
import kr.co.jiniaslog.memo.usecase.IInitMemo.InitMemoInfo
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

interface MeMoUseCasesFacade :
    IInitMemo, ICommitMemo, IGetRecommendRelatedMemo

@UseCaseInteractor
internal class MemoUseCaseImpl(
    private val memoRepository: MemoRepository,
    private val memoIndexStorage: MemoIndexStorage,
    private val tagRepository: TagRepository,
) : MeMoUseCasesFacade {
    override fun init(command: InitMemoCommand): InitMemoInfo {
        val newOne = Memo.init(id = memoRepository.nextId(), content = command.content)
        memoRepository.save(newOne)
        memoIndexStorage.saveIndex(newOne)
        return InitMemoInfo(newOne.id.value)
    }

    override fun commit(command: CommitMemoCommand): CommitMemoInfo {
        val newOne =
            memoRepository.findById(command.memoId)
                ?: throw IllegalArgumentException("memo not found")

        newOne.commit(
            title = command.title,
            linkedList = command.linkedList,
            content = command.content,
//            tags = command.tags,
        )

        memoRepository.save(newOne)
        memoIndexStorage.saveIndex(newOne)
        return CommitMemoInfo(newOne.id.value)
    }

    override fun getRecommendRelatedMemo(command: GetRecommendRelatedMemoCommand): GetRecommendRelatedMemoInfo {
        val result = memoIndexStorage.searchRelatedMemo(command.query)
        return GetRecommendRelatedMemoInfo(result)
    }
}
