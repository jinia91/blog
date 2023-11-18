package kr.co.jiniaslog.blog

import kr.co.jiniaslog.shared.core.domain.DomainContext
import kr.co.jiniaslog.shared.core.domain.DomainEvent
import kr.co.jiniaslog.shared.core.domain.EventContextManager
import kr.co.jiniaslog.shared.core.domain.EventManger
import kr.co.jiniaslog.shared.core.domain.IdGenerator
import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.util.concurrent.atomic.AtomicLong

abstract class CustomBehaviorSpec : io.kotest.core.spec.style.BehaviorSpec() {
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
                    override suspend fun add(event: DomainEvent) {
                        domainContext.add(event)
                    }

                    override suspend fun clear() {
                        domainContext.clear()
                    }

                    override suspend fun getDomainEventsAndClear(): List<DomainEvent> {
                        return domainContext.toListAndClear()
                    }
                }
        }
    }
}
