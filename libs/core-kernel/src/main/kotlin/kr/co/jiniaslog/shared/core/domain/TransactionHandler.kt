package kr.co.jiniaslog.shared.core.domain

interface TransactionHandler {
    suspend fun <T> runInRepeatableReadTransaction(supplier: suspend () -> T): T
}
