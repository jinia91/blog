package kr.co.jiniaslog.blog.adapter.outbound.mysql

import kr.co.jiniaslog.blog.outbound.BlogTransactionHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Component
class BlogTransactionHandlerImpl : BlogTransactionHandler {
    @Transactional("blogTransactionManager", isolation = Isolation.REPEATABLE_READ)
    override fun <T> runInRepeatableReadTransaction(block: () -> T): T {
        return block()
    }
}
