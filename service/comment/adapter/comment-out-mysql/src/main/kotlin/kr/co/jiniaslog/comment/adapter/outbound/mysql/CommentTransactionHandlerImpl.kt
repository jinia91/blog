package kr.co.jiniaslog.comment.adapter.outbound.mysql

import kr.co.jiniaslog.comment.outbound.CommentTransactionHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Component
class CommentTransactionHandlerImpl : CommentTransactionHandler {
    @Transactional("commentTransactionManager", isolation = Isolation.REPEATABLE_READ)
    override fun <T> runInRepeatableReadTransaction(block: () -> T): T {
        return block()
    }
}
