package kr.co.jiniaslog.blog.usecase

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import jakarta.persistence.EntityManager
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.ArticleTestFixtures
import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.domain.tag.TagName
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.outbound.BlogTransactionHandler
import kr.co.jiniaslog.blog.outbound.TagRepository
import kr.co.jiniaslog.blog.usecase.tag.IDeleteUnUsedTags
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IDeleteUnUsedTagsUseCaseTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var sut: IDeleteUnUsedTags

    @Autowired
    lateinit var tagRespository: TagRepository

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Autowired
    lateinit var em: EntityManager

    @Autowired
    lateinit var transactionHandler: BlogTransactionHandler

    @Test
    fun `사용하지 않는 태그들을 삭제할 수 있다`() {
        // given
        val tag1 = tagRespository.save(Tag.newOne(TagName("tag1")))
        val tag2 = tagRespository.save(Tag.newOne(TagName("tag2")))
        val tag3 = tagRespository.save(Tag.newOne(TagName("tag3")))

        val article = ArticleTestFixtures.createDraftArticle(
            tags = listOf(tag3)
        )

        articleRepository.save(article)

        // when
        sut.handle(IDeleteUnUsedTags.Command())

        // then
        em.clear()
        val foundTag1 = tagRespository.findById(tag1.id)
        foundTag1.shouldBeNull()

        val foundTag2 = tagRespository.findById(tag2.id)
        foundTag2.shouldBeNull()

        val foundTag3 = tagRespository.findById(tag3.id)
        foundTag3.shouldNotBeNull()
    }
}
