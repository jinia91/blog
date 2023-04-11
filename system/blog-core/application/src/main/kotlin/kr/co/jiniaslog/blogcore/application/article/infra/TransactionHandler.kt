package kr.co.jiniaslog.blogcore.application.article.infra

import java.util.function.Supplier

interface TransactionHandler {
    fun <T> runInReadCommittedTransaction(supplier: Supplier<T>): T
}
