package kr.co.jiniaslog.memo.adapter.outbound.mysql

import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoId
import org.springframework.data.jpa.repository.JpaRepository

interface MemoJpaRepository : JpaRepository<Memo, MemoId> {
    fun findAllByAuthorId(authorId: AuthorId): List<Memo>
}
