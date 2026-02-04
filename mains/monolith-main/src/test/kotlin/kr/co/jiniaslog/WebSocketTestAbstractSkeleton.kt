package kr.co.jiniaslog

import com.ninjasquad.springmockk.MockkBean
import kr.co.jiniaslog.blog.usecase.article.ArticleUseCasesFacade
import kr.co.jiniaslog.memo.usecase.MemoUseCasesFacade
import kr.co.jiniaslog.user.domain.auth.token.TokenManager
import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.UserId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.stomp.StompSession
import java.util.concurrent.CompletableFuture

abstract class WebSocketTestAbstractSkeleton : TestContainerAbstractSkeleton() {
    @MockkBean
    protected lateinit var memoUseCases: MemoUseCasesFacade

    @MockkBean
    protected lateinit var articleUseCases: ArticleUseCasesFacade

    protected lateinit var client: StompSession

    protected lateinit var subscribeFuture: CompletableFuture<Any>

    @Autowired
    private lateinit var tokenManager: TokenManager

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
