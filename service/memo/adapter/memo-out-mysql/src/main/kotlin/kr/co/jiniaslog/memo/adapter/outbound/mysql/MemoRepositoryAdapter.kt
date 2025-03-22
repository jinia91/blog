package kr.co.jiniaslog.memo.adapter.outbound.mysql

import kr.co.jiniaslog.memo.adapter.outbound.mysql.config.MemoDb.TRANSACTION_MANAGER
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import org.springframework.transaction.annotation.Transactional

@PersistenceAdapter
@Transactional(transactionManager = TRANSACTION_MANAGER)
internal class MemoRepositoryAdapter(
    private val memoJpaRepository: MemoJpaRepository,
) : MemoRepository {
    @Transactional(readOnly = true, transactionManager = TRANSACTION_MANAGER)
    override fun countByAuthorId(authorId: AuthorId): Long {
        return memoJpaRepository.countByAuthorId(authorId)
    }

    @Transactional(readOnly = true, transactionManager = TRANSACTION_MANAGER)
    override fun findById(id: MemoId): Memo? {
        return memoJpaRepository.findById(id).orElse(null)
    }

    override fun deleteById(id: MemoId) {
        memoJpaRepository.deleteById(id)
    }

    override fun save(entity: Memo): Memo {
        return memoJpaRepository.save(entity)
    }
}
