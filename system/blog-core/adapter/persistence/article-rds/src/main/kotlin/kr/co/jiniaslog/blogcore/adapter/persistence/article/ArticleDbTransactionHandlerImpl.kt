package kr.co.jiniaslog.blogcore.adapter.persistence.article

import kr.co.jiniaslog.blogcore.application.article.infra.TransactionHandler
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
