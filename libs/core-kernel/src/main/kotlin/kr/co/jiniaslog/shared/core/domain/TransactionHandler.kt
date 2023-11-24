package kr.co.jiniaslog.shared.core.domain

interface TransactionHandler {
    fun <T> runInRepeatableReadTransaction(supplier: suspend () -> T): T
}
