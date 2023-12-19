package kr.co.jiniaslog.memo.domain.memo

enum class MemoState {
    DRAFT {
        override fun validate(memo: Memo) {}
    },
    COMMITTED {
        override fun validate(memo: Memo) {
            check(memo.id.value > 0) { "메모 ID는 0보다 커야 합니다." }
            check(memo.title.value.isNotBlank()) { "제목은 빈 문자열이 될 수 없습니다." }
            check(memo.content.value.isNotBlank()) { "내용은 빈 문자열이 될 수 없습니다." }
            check(memo.authorId.value > 0) { "작성자 ID는 0보다 커야 합니다." }
        }
    }, ;

    abstract fun validate(memo: Memo)
}
