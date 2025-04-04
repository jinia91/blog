package kr.co.jiniaslog.memo.queries

import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle

interface IRecommendRelatedMemo {
    fun handle(query: Query): Info

    class Query(
        rawKeyword: String,
        val thisMemoId: MemoId,
        val requesterId: AuthorId,
    ) {
        val keyword: String = rawKeyword.also {
            require(it.isNotBlank()) { "keyword should not be blank" }
        }.replace("\\", "\\\\") // 역슬래시
            .replace("(", "\\(") // 여는 괄호
            .replace(")", "\\)") // 닫는 괄호
            .replace("\"", "\\\"") // 따옴표
            .replace("'", "\\'") // 작은따옴표
    }
    data class Info(
        val relatedMemoCandidates: List<Triple<MemoId, MemoTitle, MemoContent>>,
    )
}
