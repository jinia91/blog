package kr.co.jiniaslog.shared

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kr.co.jiniaslog.shared.core.domain.DomainContext
import kr.co.jiniaslog.shared.core.domain.DomainEvent
import kr.co.jiniaslog.shared.core.domain.EventContextManager
import kr.co.jiniaslog.shared.core.domain.EventManger
import kr.co.jiniaslog.shared.core.domain.IdGenerator
import kr.co.jiniaslog.shared.core.domain.IdUtils
import kr.co.jiniaslog.shared.core.domain.TransactionHandler
import java.util.concurrent.atomic.AtomicLong

abstract class CustomBehaviorSpec : BehaviorSpec() {
    protected val testTransactionHandler =
        object : TransactionHandler {
            override fun <T> runInRepeatableReadTransaction(supplier: () -> T): T {
                return supplier()
            }
        }

    override fun isolationMode() = IsolationMode.InstancePerTest

    override suspend fun afterEach(
        testCase: TestCase,
        result: TestResult,
    ) {
        domainContext.clear()
        super.afterEach(testCase, result)
    }

    companion object {
        private var atomicSequence = AtomicLong(1)
        private val domainContext = DomainContext()

        init {
            IdUtils.idGenerator =
                object : IdGenerator {
                    override fun generate(): Long {
                        return atomicSequence.getAndIncrement()
                    }
                }

            EventManger.eventContextManager =
                object : EventContextManager {
                    override fun add(event: DomainEvent) {
                        domainContext.add(event)
                    }

                    override fun clear() {
                        domainContext.clear()
                    }

                    override fun getDomainEventsAndClear(): List<DomainEvent> {
                        return domainContext.toListAndClear()
                    }
                }
        }
    }
}
