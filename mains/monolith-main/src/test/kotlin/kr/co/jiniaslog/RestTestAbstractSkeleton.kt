package kr.co.jiniaslog

import com.ninjasquad.springmockk.MockkBean
import io.restassured.module.mockmvc.RestAssuredMockMvc
import kr.co.jiniaslog.media.inbound.http.ImageResources
import kr.co.jiniaslog.media.usecase.ImageUseCasesFacade
import kr.co.jiniaslog.memo.adapter.inbound.http.MemoResources
import kr.co.jiniaslog.memo.queries.QueriesMemoFacade
import kr.co.jiniaslog.memo.usecase.UseCasesMemoFacade
import kr.co.jiniaslog.user.application.security.AccessTokenConfig
import kr.co.jiniaslog.user.application.security.AuthProvider
import kr.co.jiniaslog.user.application.security.PreAuthFilter
import kr.co.jiniaslog.user.application.security.SecurityConfig
import kr.co.jiniaslog.user.domain.auth.token.TokenManger
import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.UserId
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
    controllers = [
        ImageResources::class,
        MemoResources::class
    ]
)
@Import(value = [SecurityTestContextConfig::class, AccessTokenConfig::class, SecurityConfig::class])
abstract class RestTestAbstractSkeleton {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var tokenManager: TokenManger

    @MockkBean
    protected lateinit var imageService: ImageUseCasesFacade

    @MockkBean
    protected lateinit var memoService: UseCasesMemoFacade

    @MockkBean
    protected lateinit var memoQueries: QueriesMemoFacade

    @BeforeEach
    fun setup() {
        RestAssuredMockMvc
            .mockMvc(mockMvc)
    }

    @Test
    fun contextLoads() {
    }

    protected fun getTestAdminUserToken(): String {
        return tokenManager.generateAccessToken(
            UserId(1L),
            setOf(Role.ADMIN)
        ).value
    }

    protected fun getTestUserToken(): String {
        return tokenManager.generateAccessToken(
            UserId(1L),
            setOf(Role.USER)
        ).value
    }
}
