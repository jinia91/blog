package kr.co.jiniaslog.user.application.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import org.springframework.stereotype.Component
import java.util.stream.Collectors

@Component
class PreAuthFilter(
    private val jwtTokenProvider: JwtTokenGenerator,
    authenticationProvider: AuthProvider,
) : AbstractPreAuthenticatedProcessingFilter() {
    init {
        setAuthenticationManager(ProviderManager(authenticationProvider))
    }

    override fun getPreAuthenticatedPrincipal(httpServletRequest: HttpServletRequest): Any? {
        val token = resolveToken(httpServletRequest)
        if (!isValidTokenFormat(token)) {
            return null
        }

        if (!jwtTokenProvider.validateToken(token!!)) {
            return null
        }

        val userId = jwtTokenProvider.getUserId(token).value
        val rolesSet: Set<String> =
            jwtTokenProvider.getRole(token).stream()
                .map { it.name }
                .collect(Collectors.toSet())
        return UserPrincipal(userId, rolesSet)
    }

    private fun resolveToken(req: HttpServletRequest): String? {
        val token =
            try {
                req.cookies.find { it.name == "jiniaslog_access" }
            } catch (e: NullPointerException) {
                null
            }
        return token?.value
    }

    private fun isValidTokenFormat(token: String?): Boolean {
        return token != null
    }

    override fun getPreAuthenticatedCredentials(httpServletRequest: HttpServletRequest?): Any {
        return ""
    }

    override fun principalChanged(
        request: HttpServletRequest?,
        currentAuthentication: Authentication?,
    ): Boolean {
        return super.principalChanged(request, currentAuthentication)
    }
}
