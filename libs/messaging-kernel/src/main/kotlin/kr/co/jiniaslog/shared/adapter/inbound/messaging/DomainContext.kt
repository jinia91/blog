package kr.co.jiniaslog.shared.adapter.inbound.messaging

import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.withContext
import kr.co.jiniaslog.shared.core.domain.DomainContext
import reactor.util.context.Context

// todo : after spring 6.1 release, remove this code and use coroutine aop!
suspend fun withDomainContext(block: suspend () -> Any?) {
    withContext(Context.of(DomainContext.DOMAIN_EVENT_KEY, DomainContext()).asCoroutineContext()) {
        block()
    }
}
