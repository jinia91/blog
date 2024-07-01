package kr.co.jiniaslog.memo.adapter.inbound.acl

import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.queries.ICheckMemoExisted
import kr.co.jiniaslog.memo.queries.MemoQueriesFacade
import org.springframework.stereotype.Controller

@Controller
class MemoAclInboundAdapter(
    private val memoQueries: MemoQueriesFacade,
) {
    fun isExistMemo(id: Long): Boolean {
        return memoQueries.handle(
            ICheckMemoExisted.Query(MemoId(id)),
        )
    }
}
