package kr.co.jiniaslog.memo.adapter.inbound.http

import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.queries.IGetAllMemos
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.memo.queries.impl.MemoQueriesFacade
import kr.co.jiniaslog.memo.usecase.impl.MemoUseCasesFacade
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController(
    private val memoUseCases: MemoUseCasesFacade,
    private val memoQueriesFacade: MemoQueriesFacade,
) {
    @RequestMapping("/api/v1/memos")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun getMemos(): MemoResponse {
        return memoQueriesFacade.handle(IGetAllMemos.Query())
            .toResponse()
    }

    @RequestMapping("/api/v1/memos2")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun getMemosByKeyword(
        @RequestParam keyword: String,
    ): RecommenedMemoResponse {
        return memoQueriesFacade.handle(IRecommendRelatedMemo.Query(keyword))
            .toResponse()
    }
}

private fun IRecommendRelatedMemo.Info.toResponse(): RecommenedMemoResponse {
    return RecommenedMemoResponse(
        relatedMemoCandidates = this.relatedMemoCandidates,
    )
}

data class RecommenedMemoResponse(
    val relatedMemoCandidates: List<Pair<MemoId, MemoTitle>>,
)

data class MemoResponse(
    val memos: List<IGetAllMemos.Info>,
)

fun List<IGetAllMemos.Info>.toResponse(): MemoResponse {
    return MemoResponse(
        memos = this,
    )
}
