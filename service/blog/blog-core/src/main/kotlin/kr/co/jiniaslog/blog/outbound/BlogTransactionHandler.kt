package kr.co.jiniaslog.blog.outbound

interface BlogTransactionHandler {
    fun <T> runInRepeatableReadTransaction(block: () -> T): T
}
