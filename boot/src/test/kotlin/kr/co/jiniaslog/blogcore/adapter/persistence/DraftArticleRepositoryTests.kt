package kr.co.jiniaslog.blogcore.adapter.persistence

import kr.co.jiniaslog.config.TestContainerConfig
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticle
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleRepository
import kr.co.jiniaslog.blogcore.domain.user.UserId
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class DraftArticleRepositoryTests : TestContainerConfig() {

    @Autowired
    private lateinit var draftArticleRepository: DraftArticleRepository

    @PersistenceContext(unitName = CoreDB.PERSISTENT_UNIT)
    private lateinit var em: EntityManager

    @Test
    fun `tempArticle Save and get Test`() {
        val draftArticle = DraftArticle.Factory.from(
            writerId = UserId(value = 2372),
            title = null,
            content = null,
            thumbnailUrl = null,
            categoryId = null
        )
        draftArticleRepository.save(draftArticle)
        em.clear()
        val temp = draftArticleRepository.getById(DraftArticleId.getDefault())

        // then
        assertThat(temp).isNotNull
        assertThat(temp!!.id).isEqualTo(DraftArticleId.getDefault())
    }

    @Test
    fun `tempArticle delete Test`() {
        val draftArticle = DraftArticle.Factory.from(
            writerId = UserId(value = 2372),
            title = null,
            content = null,
            thumbnailUrl = null,
            categoryId = null
        )
        draftArticleRepository.save(draftArticle)
        em.clear()

        draftArticleRepository.delete()

        // then
        assertThat(draftArticleRepository.getById(DraftArticleId.getDefault())).isNull()
    }
}