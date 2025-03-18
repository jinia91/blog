package kr.co.jiniaslog.blog.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.ArticleTestFixtures
import kr.co.jiniaslog.blog.domain.CategoryTestFixtures
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.outbound.BlogTransactionHandler
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ICategorizeArticleUseCasesTests : TestContainerAbstractSkeleton() {
    @Autowired
    private lateinit var sut: ICategorizeArticle

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var transactionManager: BlogTransactionHandler

    @Test
    fun `유효한 카테고리 설정 요청이 있으면 게시글에 카테고리를 설정할 수 있다`() {
        // given
        val article = ArticleTestFixtures.createPublishedArticle()
        articleRepository.save(article)

        val parent = CategoryTestFixtures.createCategory()
        categoryRepository.save(parent)

        val child = CategoryTestFixtures.createCategory(parent = parent)
        categoryRepository.save(child)

        val command = ICategorizeArticle.Command(article.entityId, child.entityId)

        // when
        val result = sut.handle(command)

        // then
        result.articleId shouldBe article.entityId
        em.clear()
        val foundArticle = articleRepository.findById(article.entityId)
        foundArticle.shouldNotBeNull()
        foundArticle.categoryId shouldBe child.entityId
    }

    @Test
    fun `없는 게시글에 카테고리 설정을 하면 예외가 발생한다`() {
        // given
        val parent = CategoryTestFixtures.createCategory()
        categoryRepository.save(parent)

        val child = CategoryTestFixtures.createCategory(parent = parent)
        categoryRepository.save(child)

        val command = ICategorizeArticle.Command(ArticleId(1L), child.entityId)

        // when, then
        shouldThrow<IllegalArgumentException> { sut.handle(command) }
    }

    @Test
    fun `없는 카테고리로 카테고리 설정을 하면 예외가 발생한다`() {
        // given
        val article = ArticleTestFixtures.createPublishedArticle()
        articleRepository.save(article)

        val command = ICategorizeArticle.Command(article.entityId, CategoryId(1L))

        // when, then
        shouldThrow<IllegalArgumentException> { sut.handle(command) }
    }
}
