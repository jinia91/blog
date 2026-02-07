package kr.co.jiniaslog.ai.adapter.outbound.memo

import kr.co.jiniaslog.ai.outbound.MemoInfo
import kr.co.jiniaslog.ai.outbound.MemoQueryClient
import kr.co.jiniaslog.memo.adapter.inbound.acl.MemoAclInboundAdapter
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter

@PersistenceAdapter
class MemoQueryClientAdapter(
    private val memoAcl: MemoAclInboundAdapter,
) : MemoQueryClient {

    override fun getMemoById(memoId: Long): MemoInfo? {
        return memoAcl.getMemoById(memoId)?.let { aclInfo ->
            MemoInfo(
                id = aclInfo.id,
                authorId = aclInfo.authorId,
                title = aclInfo.title,
                content = aclInfo.content,
            )
        }
    }

    override fun getAllMemosByAuthorId(authorId: Long): List<MemoInfo> {
        return memoAcl.getAllMemosByAuthorId(authorId).map { aclInfo ->
            MemoInfo(
                id = aclInfo.id,
                authorId = aclInfo.authorId,
                title = aclInfo.title,
                content = aclInfo.content,
            )
        }
    }
}
