package kr.co.jiniaslog.memo.queries.model

import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle

class SimpleMemoInfo(
    val id: MemoId,
    val title: MemoTitle,
    val references: List<MemoReferenceInfo>,
)
