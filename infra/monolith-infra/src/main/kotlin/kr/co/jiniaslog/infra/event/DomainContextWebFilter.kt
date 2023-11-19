package kr.co.jiniaslog.infra.event

import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.withContext
import kr.co.jiniaslog.shared.core.domain.DomainContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import reactor.util.context.Context

private val log = mu.KotlinLogging.logger {}

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
internal class DomainContextWebFilter : CoWebFilter() {
    override suspend fun filter(
        exchange: ServerWebExchange,
        chain: CoWebFilterChain,
    ) = Context.of(DomainContext.key, DomainContext()).asCoroutineContext().let {
        withContext(it) {
            chain.filter(exchange)
        }
    }
}
