package kr.co.jiniaslog.memo.adapter.outbound.mysql

import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import org.springframework.stereotype.Repository

@Repository
internal class MemoRepositoryAdapter(
    private val memoJpaRepository: MemoJpaRepository,
) : MemoRepository {
    override fun count(): Long {
        return memoJpaRepository.count()
    }

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
