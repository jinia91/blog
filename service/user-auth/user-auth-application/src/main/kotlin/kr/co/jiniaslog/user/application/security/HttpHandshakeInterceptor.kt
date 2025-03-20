package kr.co.jiniaslog.user.application.security

import kr.co.jiniaslog.blog.shared.inbound.websocket.WebsocketSecurityPreAuthInterceptor
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor

@Component
class HttpHandshakeInterceptor(
    private val httpPreAuthFilter: PreAuthFilter
) : HttpSessionHandshakeInterceptor(), WebsocketSecurityPreAuthInterceptor {
    @Throws(Exception::class)
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: Map<String?, Any?>,
    ): Boolean {
        val servletRequest = (request as ServletServerHttpRequest).servletRequest
        val userPrincipal = httpPreAuthFilter.retrieveAuthPrincipal(servletRequest)
            ?: throw AccessDeniedException("엑세스 토큰이 존재하지 않습니다")
        SecurityContextHolder.getContext().authentication = buildAuthentication(userPrincipal)
        return super.beforeHandshake(request, response, wsHandler, attributes)
    }

    private fun buildAuthentication(it: UserPrincipal) =
        ServiceAuthenticationToken(it, null, it.roles.map { role -> SimpleGrantedAuthority("ROLE_$role") }.toSet())
}
