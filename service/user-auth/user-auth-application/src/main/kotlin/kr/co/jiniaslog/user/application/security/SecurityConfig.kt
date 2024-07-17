package kr.co.jiniaslog.user.application.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val preAuthFilter: PreAuthFilter,
) {
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .addFilter(preAuthFilter)
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/media").authenticated()
                it.requestMatchers("/api/v1/memos/**", "/api/v1/memos").hasRole("ADMIN")
                it.requestMatchers("/api/v1/folders/**", "/api/v1/folders").hasRole("ADMIN")
                it.requestMatchers("/api/v1/articles/**", "/api/v1/articles").hasRole("ADMIN")
                it.requestMatchers("/api/v1/categories/**", "/api/v1/categories").hasRole("ADMIN")
                it.anyRequest().permitAll()
            }
            .headers { it.frameOptions(Customizer { it.disable() }) }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling {
                it.authenticationEntryPoint(CustomAuthenticationEntryPoint())
                it.accessDeniedHandler(CustomAccessDeniedHandler())
            }
            .build()
    }
}

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        authException: AuthenticationException?,
    ) {
        response.sendError(
            HttpServletResponse.SC_UNAUTHORIZED,
            "인증되지 않았습니다: 인증 토큰이 없거나 유효하지 않습니다",
        )
    }
}

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        accessDeniedException: org.springframework.security.access.AccessDeniedException?,
    ) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다")
    }
}
