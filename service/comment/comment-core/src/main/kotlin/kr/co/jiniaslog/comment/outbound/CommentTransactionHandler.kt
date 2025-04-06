package kr.co.jiniaslog.comment.outbound

interface CommentTransactionHandler {
    fun <T> runInRepeatableReadTransaction(block: () -> T): T
}
