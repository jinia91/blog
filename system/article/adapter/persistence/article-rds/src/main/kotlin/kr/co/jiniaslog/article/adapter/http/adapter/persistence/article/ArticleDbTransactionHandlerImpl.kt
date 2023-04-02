package kr.co.jiniaslog.article.adapter.http.adapter.persistence.article

import kr.co.jiniaslog.article.application.infra.TransactionHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.function.Supplier

@Component
class ArticleDbTransactionHandlerImpl : TransactionHandler {

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Throws(Exception::class)
    override fun <T> runInReadCommittedTransaction(supplier: Supplier<T>): T {
        return supplier.get()
    }
}
