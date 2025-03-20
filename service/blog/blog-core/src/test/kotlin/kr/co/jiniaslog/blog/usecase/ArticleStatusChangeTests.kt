package kr.co.jiniaslog.blog.usecase

import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.Article.Status.DELETED
import kr.co.jiniaslog.blog.domain.article.Article.Status.DRAFT
import kr.co.jiniaslog.blog.domain.article.Article.Status.PUBLISHED
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.usecase.article.ArticleStatusChangeFacade
import kr.co.jiniaslog.blog.usecase.article.IDeleteArticle
import kr.co.jiniaslog.blog.usecase.article.IPublishArticle
import kr.co.jiniaslog.blog.usecase.article.IUnDeleteArticle
import kr.co.jiniaslog.blog.usecase.article.IUnPublishArticle
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ArticleStatusChangeTests : SimpleUnitTestContext() {

    private val sut = ArticleStatusChangeInteractor(
        articleRepository = mockk(),
        transactionHandler = mockk()
    )

    @ParameterizedTest(name = "기존 상태가 {0}이고 변경하려는 상태가 {1}일 때 상태 변경 커맨드 {2} 를 생성할 수 있다")
    @MethodSource("provideArticleStatusChanges")
    fun `파사드 커맨드 결정 테스트`(
        asIsStatus: Article.Status,
        toBeStatus: Article.Status,
        expectedCommand: ArticleStatusChangeFacade.Command,
    ) {
        // when
        val command = sut.determineCommand(asIsStatus, toBeStatus, ArticleId(1))

        // then
        command shouldBe expectedCommand
    }

    companion object {
        @JvmStatic
        fun provideArticleStatusChanges(): Stream<Arguments> = Stream.of(
            Arguments.of(DRAFT, PUBLISHED, IPublishArticle.Command(ArticleId(1))),
            Arguments.of(PUBLISHED, PUBLISHED, IPublishArticle.Command(ArticleId(1))),
            Arguments.of(PUBLISHED, DRAFT, IUnPublishArticle.Command(ArticleId(1))),
            Arguments.of(DELETED, DRAFT, IUnDeleteArticle.Command(ArticleId(1))),
            Arguments.of(PUBLISHED, DELETED, IDeleteArticle.Command(ArticleId(1))),
            Arguments.of(DRAFT, DELETED, IDeleteArticle.Command(ArticleId(1)))
        )
    }
}
