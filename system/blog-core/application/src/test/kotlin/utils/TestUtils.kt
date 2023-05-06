package utils

import io.mockk.every
import kr.co.jiniaslog.blogcore.application.infra.TransactionHandler
import java.util.function.Supplier

object TestUtils {
    fun doTransactionDefaultStubbing(stub: TransactionHandler) {
        every<Unit> { stub.runInReadCommittedTransaction(any()) } answers {
            val supplier = arg<Supplier<*>>(0)
            supplier.get()
        }
    }
}
