package kr.co.jiniaslog.blogcore.adapter.persistence

import kr.co.jiniaslog.config.TestContainerConfig
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.co.jiniaslog.blogcore.domain.article.Article
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.article.ArticleRepository
import kr.co.jiniaslog.blogcore.domain.category.Category
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.category.CategoryRepository
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.domain.AbstractPersistable_.id

class ArticleRepositoryTests : TestContainerConfig() {

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @PersistenceContext(unitName = CoreDB.PERSISTENT_UNIT)
    private lateinit var em: EntityManager

    @Test
    fun `Article Save and get Test`() {
        givenCategories()
        val article = Article.Factory.newPublishedArticle(
            id = ArticleId(value = 5823),
            writerId = UserId(value = 2724),
            title = "iudicabit",
            content = "facilisis",
            thumbnailUrl = "tmp",
            categoryId = CategoryId(2),
            tags = setOf(TagId(1)),
            draftArticleId = null
        )

        articleRepository.save(article)
        em.clear()
        val foundOne = articleRepository.findById(article.id)

        // then
        assertThat(foundOne).isNotNull
        assertThat(foundOne!!.id).isEqualTo(article.id)
    }

    @Test
    fun `article delete Test`() {
        givenCategories()
        val articleId = ArticleId(value = 5823)
        val article = Article.Factory.newPublishedArticle(
            id = articleId,
            writerId = UserId(value = 2724),
            title = "iudicabit",
            content = "facilisis",
            thumbnailUrl = "tmp",
            categoryId = CategoryId(2),
            tags = setOf(TagId(1)),
            draftArticleId = null
        )
        articleRepository.save(article)
        em.clear()
        articleRepository.delete(articleId)
        em.clear()

        // then
        assertThat(articleRepository.findById(articleId)).isNull()
    }

    private fun givenCategories() {
        val parentCategory = Category.newOne(
            id = CategoryId(1),
            label = "parent",
            parentId = null,
            order = 1
        )

        categoryRepository.save(parentCategory)

        val childCategory = Category.newOne(
            id = CategoryId(2),

            label = "child",
            parentId = CategoryId(1),
            order = 1
        )

        categoryRepository.save(childCategory)
    }
}