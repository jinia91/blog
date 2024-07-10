package kr.co.jiniaslog.blog.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.ArticleTestFixtures
import kr.co.jiniaslog.blog.domain.article.ArticleContents
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.outbound.persistence.ArticleRepository
import kr.co.jiniaslog.blog.usecase.article.IUpdateArticleContents
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IUndateArticleContentsUseCaseTests : TestContainerAbstractSkeleton() {

    @Autowired
    private lateinit var sut: IUpdateArticleContents

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @Autowired
    private lateinit var em: EntityManager

    @Test
    fun `유효한 게시글 내용들이 주어지면 이를 수정할 수 있다`() {
        // given
        val article = ArticleTestFixtures.createPublishedArticle()
        articleRepository.save(article)

        val articleContents = ArticleContents(
            title = "수정된 제목",
            contents = "수정된 내용",
            thumbnailUrl = "수정된 썸네일"
        )

        val command = IUpdateArticleContents.Command(article.entityId, articleContents)

        // when
        val result = sut.handle(command)

        // then
        result.shouldNotBeNull()
        em.clear()
        val updatedArticle = articleRepository.findById(article.entityId)
        updatedArticle.shouldNotBeNull()
        updatedArticle.articleContents.title shouldBe "수정된 제목"
        updatedArticle.articleContents.contents shouldBe "수정된 내용"
        updatedArticle.articleContents.thumbnailUrl shouldBe "수정된 썸네일"
    }

    @Test
    fun `존재하지 않는 게시글을 수정하려하면 예외가 발생한다`() {
        // given
        val articleContents = ArticleContents(
            title = "수정된 제목",
            contents = "수정된 내용",
            thumbnailUrl = "수정된 썸네일"
        )

        val command = IUpdateArticleContents.Command(ArticleId(3), articleContents)

        // when, then
        shouldThrow<IllegalArgumentException> {
            sut.handle(command)
        }
    }
}
