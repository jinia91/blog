package kr.co.jiniaslog.shared

import kr.co.jiniaslog.shared.core.domain.TransactionHandler

class TestTransactionHandler : TransactionHandler {
    override fun <T> runInRepeatableReadTransaction(supplier: () -> T): T {
        return supplier()
    }
}
