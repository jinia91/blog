package kr.co.jiniaslog.blogcore.adapter.persistence

import kr.co.jiniaslog.config.TestContainerConfig
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticle.Factory.newOne
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
        val id = DraftArticleId(1541L)
        val draftArticle = newOne(
            id = id,
            writerId = UserId(value = 2372),
            title = null,
            content = null,
            thumbnailUrl = null,
        )
        draftArticleRepository.save(draftArticle)
        em.clear()
        val temp = draftArticleRepository.getById(id)

        // then
        assertThat(temp).isNotNull
        assertThat(temp!!.id).isEqualTo(id)
    }

    @Test
    fun `tempArticle delete Test`() {
        val id = DraftArticleId(1541L)
        val draftArticle = newOne(
            id = id,
            writerId = UserId(value = 2372),
            title = null,
            content = null,
            thumbnailUrl = null,
        )
        draftArticleRepository.save(draftArticle)
        em.clear()

        draftArticleRepository.deleteById(id)

        // then
        assertThat(draftArticleRepository.getById(id)).isNull()
    }
}