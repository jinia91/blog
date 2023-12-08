package kr.co.jiniaslog.memo.domain.memo

import kr.co.jiniaslog.shared.core.domain.Repository

interface MemoRepository : Repository<Memo, MemoId> {
    fun findByRelatedMemo(keyword: String): List<SimpleMemoInfo>
}
