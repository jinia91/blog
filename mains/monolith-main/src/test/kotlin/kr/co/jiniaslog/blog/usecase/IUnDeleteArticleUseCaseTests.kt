package kr.co.jiniaslog.blog.usecase

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.ArticleTestFixtures
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.outbound.persistence.ArticleRepository
import kr.co.jiniaslog.blog.usecase.article.IUnDeleteArticle
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IUnDeleteArticleUseCaseTests : TestContainerAbstractSkeleton() {

    @Autowired
    private lateinit var sut: IUnDeleteArticle

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @Autowired
    private lateinit var em: EntityManager

    @Test
    fun `삭제된 게시글을 되살리면 되살릴 수 있다`() {
        // given
        val deletedArticle = ArticleTestFixtures.createDeletedArticle()
        articleRepository.save(deletedArticle)

        // when
        val info = sut.handle(IUnDeleteArticle.Command(deletedArticle.id))

        //
        info.articleId shouldBe deletedArticle.id
        em.clear()
        val foundArticle = articleRepository.findById(deletedArticle.id)
        foundArticle.shouldNotBeNull()
        foundArticle.status shouldBe Article.Status.DRAFT
    }
}
