package kr.co.jiniaslog

import com.ninjasquad.springmockk.MockkBean
import kr.co.jiniaslog.blog.usecase.article.ArticleUseCasesFacade
import kr.co.jiniaslog.memo.usecase.MemoUseCasesFacade
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.simp.stomp.StompSession
import java.util.concurrent.CompletableFuture

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class WebSocketTestAbstractSkeleton {
    @Value("\${local.server.port}")
    protected val port = 0

    @MockkBean
    protected lateinit var memoUseCases: MemoUseCasesFacade

    @MockkBean
    protected lateinit var articleUseCases: ArticleUseCasesFacade

    protected lateinit var client: StompSession

    protected lateinit var subscribeFuture: CompletableFuture<Any>

    @Test
    fun contextLoads() {
    }
}
