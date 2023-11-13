package kr.co.jiniaslog.infra.event

import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.withContext
import kr.co.jiniaslog.infra.event.DomainContext.Companion.DOMAIN_EVENT_KEY
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import reactor.util.context.Context

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
internal class DomainContextFilter : CoWebFilter() {
    override suspend fun filter(
        exchange: ServerWebExchange,
        chain: CoWebFilterChain,
    ) {
        val context = Context.of(DOMAIN_EVENT_KEY, DomainContext()).asCoroutineContext()
        withContext(context) {
            chain.filter(exchange)
        }
    }
}
