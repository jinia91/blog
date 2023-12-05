package kr.co.jiniaslog.memo.queries

import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

interface MemoQueriesFacade :
    IRecommendRelatedMemo

@UseCaseInteractor
internal class MemoQueries(
    private val memoRepository: MemoRepository,
) : MemoQueriesFacade {
    override fun handle(query: IRecommendRelatedMemo.Query): IRecommendRelatedMemo.Info {
        TODO("Not yet implemented")
    }
}
