package kr.co.jiniaslog.ai.adapter.outbound.memo

import kr.co.jiniaslog.ai.outbound.MemoCommandService
import kr.co.jiniaslog.memo.adapter.inbound.acl.MemoAclInboundAdapter
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter

@PersistenceAdapter
class MemoCommandServiceAdapter(
    private val memoAcl: MemoAclInboundAdapter,
) : MemoCommandService {

    override fun createMemo(authorId: Long, title: String, content: String): Long {
        return memoAcl.createMemo(authorId, title, content)
    }
}
