package kr.co.jiniaslog.blog.fake

import kr.co.jiniaslog.blog.domain.memo.MemoId
import kr.co.jiniaslog.blog.outbound.MemoService

class MemoFakeService : MemoService {
    private val memoRepository = mutableMapOf<MemoId, DummyMemo>()

    override fun isExistMemo(id: MemoId): Boolean {
        return memoRepository.containsKey(id)
    }

    fun addDummy(id: MemoId) : MemoId {
        memoRepository[id] = DummyMemo(id)
        return id
    }
}

data class DummyMemo(
    val id: MemoId,
)
