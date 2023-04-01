package kr.co.jiniaslog.article.application.infra

import java.util.function.Supplier

interface TransactionHandler {
    fun <T> runInReadCommittedTransaction(supplier: Supplier<T>): T
}
