package kr.co.jiniaslog.blog.shared.inbound.websocket

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager

@Configuration
@EnableWebSecurity
@EnableWebSocketSecurity
class WebSocketSecurityConfig {
    @Bean
    fun authorizationManager(
        messages: MessageMatcherDelegatingAuthorizationManager.Builder
    ): AuthorizationManager<Message<*>> {
        messages.anyMessage().hasRole("ADMIN")
        return messages.build()
    }
}
