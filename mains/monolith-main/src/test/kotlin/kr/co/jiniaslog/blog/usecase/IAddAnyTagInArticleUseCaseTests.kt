package kr.co.jiniaslog.blog.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import jakarta.persistence.EntityManager
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.ArticleTestFixtures
import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.domain.tag.TagName
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.outbound.TagRepository
import kr.co.jiniaslog.blog.usecase.article.IAddAnyTagInArticle
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IAddAnyTagInArticleUseCaseTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var sut: IAddAnyTagInArticle

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Autowired
    lateinit var tagRepository: TagRepository

    @Autowired
    lateinit var em: EntityManager

    @Test
    fun `없는 태그의 이름이 주어지면 게시글에 추가할 수 있다`() {
        // given
        val article = articleRepository.save(ArticleTestFixtures.createDraftArticle())
        val tagName = TagName("tag")

        // when
        val result = sut.handle(IAddAnyTagInArticle.Command(tagName, article.entityId))

        // then
        em.clear()
        val tag = tagRepository.findByName(tagName)
        tag.shouldNotBeNull()
        val foundArticle = articleRepository.findById(result.articleId)
        foundArticle.shouldNotBeNull()
        foundArticle.tagsId.shouldNotBeEmpty()
        foundArticle.tagsId.contains(tag.id)
    }

    @Test
    fun `존재하는 태그의 이름이 주어지면 게시글에 추가할 수 있다`() {
        // given
        val article = articleRepository.save(ArticleTestFixtures.createDraftArticle())
        val tag = tagRepository.save(Tag.newOne(TagName("tag")))

        // when
        val result = sut.handle(IAddAnyTagInArticle.Command(tag.tagName, article.entityId))

        // then
        em.clear()
        val foundArticle = articleRepository.findById(result.articleId)
        foundArticle.shouldNotBeNull()
        foundArticle.tagsId.shouldNotBeEmpty()
        foundArticle.tagsId.contains(tag.id)
    }

    @Test
    fun `게시글이 없으면 태그 추가시 예외가 발생한다`() {
        // given
        val tagName = TagName("tag")

        // when, then
        shouldThrow<IllegalArgumentException> {
            sut.handle(IAddAnyTagInArticle.Command(tagName, ArticleTestFixtures.createDraftArticle().entityId))
        }
    }
}
