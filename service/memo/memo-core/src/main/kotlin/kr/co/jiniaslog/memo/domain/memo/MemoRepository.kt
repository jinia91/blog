package kr.co.jiniaslog.memo.domain.memo

import kr.co.jiniaslog.shared.core.domain.Repository

interface MemoRepository : Repository<Memo, MemoId> {
    fun countByAuthorId(authorId: AuthorId): Long
    fun findAllByAuthorId(authorId: AuthorId): List<Memo>
}
