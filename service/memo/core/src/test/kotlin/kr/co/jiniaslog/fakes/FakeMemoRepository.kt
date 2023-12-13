package kr.co.jiniaslog.fakes

import io.mockk.mockk
import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.memo.domain.memo.SimpleMemoInfo
import java.time.LocalDateTime

class FakeMemoRepository : MemoRepository {
    private val memos = mutableListOf<Memo>()

    override fun save(memo: Memo): Memo {
        memos.add(memo)
        memo.apply {
            createdAt = createdAt ?: LocalDateTime.now()
            updatedAt = LocalDateTime.now()
        }
        return memo
    }

    override fun findByRelatedMemo(keyword: String): List<SimpleMemoInfo> {
        return listOf(mockk<SimpleMemoInfo>())
    }

    override fun findById(id: MemoId): Memo? {
        return memos.find { it.id == id }
    }

    override fun findAll(): List<Memo> {
        return memos
    }

    override fun deleteById(id: MemoId) {
        memos.removeIf { it.id == id }
    }
}
