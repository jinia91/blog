package kr.co.jiniaslog.blog.shared.inbound.websocket

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.HandshakeInterceptor

private val log = mu.KotlinLogging.logger { }

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocketSecurity
class WebSocketConfig(
    private val websocketPreAuthInterceptor: WebsocketSecurityPreAuthInterceptor
) : WebSocketMessageBrokerConfigurer {
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("https://www.jiniaslog.co.kr")
            .addInterceptors(websocketPreAuthInterceptor)
            .withSockJS()
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic")
    }

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.addAllowedOriginPattern("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return CorsWebFilter(source)
    }

    @Bean("csrfChannelInterceptor")
    fun csrfChannelInterceptor(): ChannelInterceptor {
        return object : ChannelInterceptor {}
    }
}

interface WebsocketSecurityPreAuthInterceptor : HandshakeInterceptor
