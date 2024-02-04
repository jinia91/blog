package kr.co.jiniaslog.blog.outbound

import kr.co.jiniaslog.blog.domain.memo.MemoId

interface MemoAcl {
    fun isExistMemo(id: MemoId): Boolean
}
