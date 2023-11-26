package kr.co.jiniaslog.memo.adapter.inbound.websocket

import kr.co.jiniaslog.memo.usecase.MeMoUseCasesFacade
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val useCasesFacade: MeMoUseCasesFacade,
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(MemoHandler(useCasesFacade), "/memo").setAllowedOrigins("*")
    }
}
