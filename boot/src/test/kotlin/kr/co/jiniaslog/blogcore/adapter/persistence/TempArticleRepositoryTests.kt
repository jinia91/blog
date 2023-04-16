package kr.co.jiniaslog.blogcore.adapter.persistence

import kr.co.jiniaslog.config.TestContainerConfig
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.co.jiniaslog.blogcore.domain.article.TempArticle
import kr.co.jiniaslog.blogcore.domain.article.TempArticleId
import kr.co.jiniaslog.blogcore.domain.article.TempArticleRepository
import kr.co.jiniaslog.blogcore.domain.article.UserId
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TempArticleRepositoryTests : TestContainerConfig() {

    @Autowired
    private lateinit var tempArticleRepository: TempArticleRepository

    @PersistenceContext(unitName = CoreDB.PERSISTENT_UNIT)
    private lateinit var em: EntityManager

    @Test
    fun `tempArticle Save and get Test`() {
        val tempArticle = TempArticle.Factory.from(
            writerId = UserId(value = 2372),
            title = null,
            content = null,
            thumbnailUrl = null,
            categoryId = null
        )
        tempArticleRepository.save(tempArticle)
        em.clear()
        val temp = tempArticleRepository.getTemp(TempArticleId.getDefault())

        // then
        assertThat(temp).isNotNull
        assertThat(temp!!.id).isEqualTo(TempArticleId.getDefault())
    }

    @Test
    fun `tempArticle delete Test`() {
        val tempArticle = TempArticle.Factory.from(
            writerId = UserId(value = 2372),
            title = null,
            content = null,
            thumbnailUrl = null,
            categoryId = null
        )
        tempArticleRepository.save(tempArticle)
        em.clear()

        tempArticleRepository.delete()

        // then
        assertThat(tempArticleRepository.getTemp(TempArticleId.getDefault())).isNull()
    }
}