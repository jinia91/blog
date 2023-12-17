package kr.co.jiniaslog.memo.queries.impl

import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.memo.queries.IGetAllMemos
import kr.co.jiniaslog.memo.queries.IGetMemoById
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

interface MemoQueriesFacade :
    IGetAllMemos,
    IRecommendRelatedMemo,
    IGetMemoById

@UseCaseInteractor
internal class MemoQueries(
    private val memoRepository: MemoRepository,
) : MemoQueriesFacade {
    override fun handle(query: IGetAllMemos.Query): List<IGetAllMemos.Info> {
        return memoRepository.findAll().map {
            IGetAllMemos.Info(
                memoId = it.id,
                title = it.title,
                references = it.references,
            )
        }
    }

    override fun handle(query: IRecommendRelatedMemo.Query): IRecommendRelatedMemo.Info {
        return IRecommendRelatedMemo.Info(
            relatedMemoCandidates =
                memoRepository.findByRelatedMemo(query.query)
                    .filterNot { it.id == query.thisId }
                    .take(5)
                    .map { (it.id to it.title) },
        )
    }

    override fun handle(query: IGetMemoById.Query): IGetMemoById.Info {
        val memo = memoRepository.findById(query.memoId) ?: throw IllegalArgumentException("memo not found")
        return IGetMemoById.Info(
            memoId = memo.id,
            title = memo.title,
            content = memo.content,
            references = memo.references.map { IGetMemoById.Info.Reference(rootId = it.rootId, referenceId = it.referenceId) }.toSet(),
        )
    }
}
