package kr.co.jiniaslog.user.infra

interface UserAuthTransactionHandler {
    fun <T> runInRepeatableReadTransaction(supplier: () -> T): T
}
