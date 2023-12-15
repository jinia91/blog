package kr.co.jiniaslog.memo.domain.memo

import kr.co.jiniaslog.memo.queries.model.SimpleMemoInfo
import kr.co.jiniaslog.shared.core.domain.Repository

interface MemoRepository : Repository<Memo, MemoId> {
    fun findByRelatedMemo(keyword: String): List<SimpleMemoInfo>
}
