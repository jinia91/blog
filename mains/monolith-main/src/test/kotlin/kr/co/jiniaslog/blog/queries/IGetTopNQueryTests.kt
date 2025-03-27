package kr.co.jiniaslog.blog.queries

import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.ArticleTestFixtures
import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.domain.tag.TagName
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.outbound.TagRepository
import kr.co.jiniaslog.blog.usecase.tag.IGetTopNTags
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IGetTopNQueryTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var sut: IGetTopNTags

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Autowired
    lateinit var tagRepository: TagRepository

    @Autowired
    lateinit var em: EntityManager

    @Test
    fun `태그 사용이 많은 순으로 조회할 수 있다`() {
        // given
        val tag1 = tagRepository.save(Tag.newOne(TagName("tag1")))
        val tag2 = tagRepository.save(Tag.newOne(TagName("tag2")))
        val tag3 = tagRepository.save(Tag.newOne(TagName("tag3")))
        val tag4 = tagRepository.save(Tag.newOne(TagName("tag4")))
        val tag5 = tagRepository.save(Tag.newOne(TagName("tag5")))

        articleRepository.save(ArticleTestFixtures.createPublishedArticle(tags = listOf(tag1)))
        articleRepository.save(ArticleTestFixtures.createPublishedArticle(tags = listOf(tag1, tag3)))
        articleRepository.save(ArticleTestFixtures.createPublishedArticle(tags = listOf(tag1, tag2, tag3)))
        articleRepository.save(ArticleTestFixtures.createPublishedArticle(tags = listOf(tag1, tag2, tag3, tag4, tag5)))

        // when
        val result = sut.handle(IGetTopNTags.Query(3))

        // then
        result.tags.size shouldBe 3
        result.tags.keys shouldBe setOf(tag1.entityId, tag3.entityId, tag2.entityId)
    }
}
