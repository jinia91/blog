package kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel

// todo swagger 문서화

data class MemoViewModel(
    val memoId: Long,
    val title: String,
    val content: String,
)

data class InitMemoRequest(
    val parentFolderId: Long?,
)

data class InitMemoResponse(
    val memoId: Long,
)
