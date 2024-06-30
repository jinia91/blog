package kr.co.jiniaslog

import io.restassured.module.mockmvc.RestAssuredMockMvc
import kr.co.jiniaslog.media.inbound.http.ImageController
import kr.co.jiniaslog.user.application.security.AccessTokenConfig
import kr.co.jiniaslog.user.application.security.AuthProvider
import kr.co.jiniaslog.user.application.security.PreAuthFilter
import kr.co.jiniaslog.user.application.security.SecurityConfig
import kr.co.jiniaslog.user.domain.auth.token.TokenManger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc

@TestConfiguration
class SecurityTestContextConfig {
    @Bean
    fun authProvider(): AuthProvider {
        return AuthProvider()
    }

    @Bean
    fun preAuthFilter(
        jwtTokenManager: TokenManger,
        authenticationProvider: AuthProvider,
    ): PreAuthFilter {
        return PreAuthFilter(jwtTokenManager, authenticationProvider)
    }
}

@WebMvcTest(
    controllers = [ImageController::class],
)
@Import(value = [SecurityTestContextConfig::class, AccessTokenConfig::class, SecurityConfig::class])
abstract class RestTestAbstractSkeleton {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        RestAssuredMockMvc
            .mockMvc(mockMvc)
    }

    @Test
    fun contextLoads() {
    }
}
