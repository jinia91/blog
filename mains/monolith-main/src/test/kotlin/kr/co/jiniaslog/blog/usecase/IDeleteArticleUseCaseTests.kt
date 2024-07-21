package kr.co.jiniaslog.blog.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.ArticleTestFixtures
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.domain.tag.TagName
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.outbound.TagRepository
import kr.co.jiniaslog.blog.usecase.article.IDeleteArticle
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class IDeleteArticleUseCaseTests : TestContainerAbstractSkeleton() {
    @Autowired
    private lateinit var sut: IDeleteArticle

    @Autowired
    private lateinit var tagRepository: TagRepository

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @Autowired
    private lateinit var em: EntityManager

    @Test
    fun `게시글을 삭제하면 모든 연관관계가 해지되며 삭제상태가 된다`() {
        // given
        val tag = Tag.newOne(TagName("tag"))
        val tag2 = Tag.newOne(TagName("tag2"))
        tagRepository.save(tag)
        tagRepository.save(tag2)
        val article = ArticleTestFixtures.createPublishedArticle(
            tags = listOf(tag.entityId, tag2.entityId),
        )
        articleRepository.save(article)

        // when
        val info = sut.handle(IDeleteArticle.Command(article.entityId))

        // then
        info.articleId shouldBe article.entityId
        em.clear()
        val foundOne = articleRepository.findById(info.articleId)
        foundOne.shouldNotBeNull()
        foundOne.status shouldBe Article.Status.DELETED
    }

    @Test
    @Transactional
    fun `없는 게시글을 삭제하려하면 예외가 발생한다`() {
        // given
        val articleId = ArticleId(1L)

        // when, then
        shouldThrow<IllegalArgumentException> {
            sut.handle(IDeleteArticle.Command(articleId))
        }
    }

    @Test
    @Transactional
    fun `논리삭제된 게시글을 삭제하려하면 예외가 발생한다`() {
        // given
        val article = ArticleTestFixtures.createDeletedArticle()
        articleRepository.save(article)

        // when, then
        shouldThrow<IllegalArgumentException> {
            sut.handle(IDeleteArticle.Command(article.entityId))
        }
    }
}
