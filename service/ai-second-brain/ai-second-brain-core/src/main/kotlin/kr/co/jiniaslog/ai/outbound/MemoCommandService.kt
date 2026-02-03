package kr.co.jiniaslog.ai.outbound

interface MemoCommandService {
    fun createMemo(authorId: Long, title: String, content: String): Long
}
