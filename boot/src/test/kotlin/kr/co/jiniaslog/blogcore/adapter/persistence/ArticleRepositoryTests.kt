package kr.co.jiniaslog.blogcore.adapter.persistence

import kr.co.jiniaslog.config.TestContainerConfig
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.co.jiniaslog.blogcore.domain.article.Article
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.article.ArticleRepository
import kr.co.jiniaslog.blogcore.domain.article.TempArticle
import kr.co.jiniaslog.blogcore.domain.article.TempArticleId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ArticleRepositoryTests : TestContainerConfig() {

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @PersistenceContext(unitName = CoreDB.PERSISTENT_UNIT)
    private lateinit var em: EntityManager

    @Test
    fun `Article Save and get Test`() {
        val article = Article.Factory.newDraftOne(
            id = ArticleId(value = 5823),
            userId = UserId(value = 2724),
            title = "iudicabit",
            content = "facilisis",
            thumbnailUrl = null,
            categoryId = null,
            tags = setOf(TagId(1))
        )
        articleRepository.save(article)
        em.clear()
        val foundOne = articleRepository.findById(article.id)

        // then
        assertThat(foundOne).isNotNull
        assertThat(foundOne!!.id).isEqualTo(article.id)
    }

//    @Test
//    fun `article delete Test`() {
//        val article = Article.Factory.newDraftOne(
//            id = ArticleId(value = 5823),
//            userId = UserId(value = 2724),
//            title = "iudicabit",
//            content = "facilisis",
//            thumbnailUrl = null,
//            categoryId = null,
//            tags = setOf(TagId(1))
//        )
//        articleRepository.save(article)
//        em.clear()
//
//        articleRepository
//
//        // then
//        assertThat(articleRepository.getTemp(TempArticleId.getDefault())).isNull()
//    }
}