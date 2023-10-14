package kr.co.jiniaslog.blog.application.infra

import java.util.function.Supplier

interface TransactionHandler {
    fun <T> runInReadCommittedTransaction(supplier: Supplier<T>): T
}
