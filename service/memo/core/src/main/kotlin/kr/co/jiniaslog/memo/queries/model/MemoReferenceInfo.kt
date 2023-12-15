package kr.co.jiniaslog.memo.queries.model

import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle

data class MemoReferenceInfo(
    val id: MemoId,
    val title: MemoTitle,
)
