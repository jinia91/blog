package kr.co.jiniaslog.user

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.user.application.infra.TokenStore
import kr.co.jiniaslog.user.application.usecase.IRefreshToken
import kr.co.jiniaslog.user.domain.auth.token.RefreshToken
import kr.co.jiniaslog.user.domain.auth.token.TokenManger
import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.UserId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CompletableFuture

abstract class IRefreshTokenTests {
    @Autowired
    lateinit var sut: IRefreshToken

    @Autowired
    lateinit var tokenManger: TokenManger

    @Autowired
    lateinit var tokenStore: TokenStore

    @Test
    fun `포멧이 유효하지 않은 리프레시 토큰으로 갱신 요청을 하면 실패한다`() {
        // given
        val command =
            IRefreshToken.Command(
                refreshToken = RefreshToken("12345678901"),
            )

        // when, then
        shouldThrow<IllegalArgumentException> {
            sut.handle(command)
        }
    }

    @Test
    fun `포멧이 유효한 리프레시 토큰이여도 스토어에 저장되어있지 않다면 실패한다`() {
        // given
        val refreshToken = tokenManger.generateRefreshToken(UserId(1L), setOf(Role.USER))

        val command =
            IRefreshToken.Command(
                refreshToken = refreshToken,
            )

        // when, then
        shouldThrow<IllegalArgumentException> {
            sut.handle(command)
        }
    }

    @Test
    fun `저장된 리프레시 토큰이 존재하고, 해당 리프레시 토큰으로 갱신 요청을 하면 성공한다`() {
        // given context
        val userId = UserId(1L)
        val accessToken = tokenManger.generateAccessToken(userId, setOf(Role.USER))
        val refreshToken = tokenManger.generateRefreshToken(userId, setOf(Role.USER))
        tokenStore.save(userId, accessToken, refreshToken)


        val command =
            IRefreshToken.Command(
                refreshToken = refreshToken,
            )

        // when
        val info = sut.handle(command)

        // then
        info.accessToken shouldNotBe null
        info.refreshToken shouldNotBe null

        // 갱신 체크
        info.accessToken.value shouldNotBe accessToken.value
        info.refreshToken.value shouldNotBe refreshToken.value

        // tearDown
        tokenStore.delete(userId)
    }

    @Test
    fun `저장된 리프레시 토큰이 존재하고, temp 리프레시 토큰으로 갱신 요청을 하면 성공한다`() {
        // given context
        val userId = UserId(1L)
        val accessToken = tokenManger.generateAccessToken(userId, setOf(Role.USER))
        val tempRefreshToken = tokenManger.generateRefreshToken(userId, setOf(Role.USER))
        tokenStore.save(userId, accessToken, tempRefreshToken)
        val newRefreshToken = tokenManger.generateRefreshToken(userId, setOf(Role.USER))
        tokenStore.save(userId, accessToken, newRefreshToken)

        val command =
            IRefreshToken.Command(
                refreshToken = tempRefreshToken,
            )

        // when
        val info = sut.handle(command)

        // then
        info.accessToken shouldNotBe null
        info.refreshToken shouldNotBe null

        // 갱신 체크
        info.accessToken.value shouldNotBe accessToken.value
        info.refreshToken.value shouldNotBe tempRefreshToken.value

        // tearDown
        tokenStore.delete(userId)
    }

    @Test
    fun `동일 유저아이디에 대한 동시성 요청시, 후속 요청은 단순 조회만 한다`() {
        // given
        val userId = UserId(1L)
        val accessToken = tokenManger.generateAccessToken(userId, setOf(Role.USER))
        val refreshToken = tokenManger.generateRefreshToken(userId, setOf(Role.USER))
        tokenStore.save(userId, accessToken, refreshToken)

        val command =
            IRefreshToken.Command(
                refreshToken = refreshToken,
            )

        // when
        val task1 =
            CompletableFuture.supplyAsync {
                sut.handle(command)
            }
        val task2 =
            CompletableFuture.supplyAsync {
                sut.handle(command)
            }

        // then
        val result: IRefreshToken.Info = task1.get()
        val result2: IRefreshToken.Info = task2.get()

        result.accessToken.value shouldNotBe accessToken.value
        result.refreshToken.value shouldNotBe refreshToken.value

        result.accessToken.value shouldBe result2.accessToken.value
        result.refreshToken.value shouldBe result2.refreshToken.value

        // tearDown
        tokenStore.delete(userId)
    }
}
