package kr.co.jiniaslog.blogcore.adapter.persistence.draft

import kr.co.jiniaslog.blogcore.domain.draft.DraftArticle
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleRepository
import kr.co.jiniaslog.blogcore.domain.user.UserId
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

@Repository
internal class DraftArticleRepositoryAdapter(
    private val draftArticleJpaRepository: DraftArticleJpaRepository,
) : DraftArticleRepository {
    override fun save(newDraftArticle: DraftArticle) {
        draftArticleJpaRepository.save(
            newDraftArticle.toPm()
                .apply { createdDate = LocalDateTime.now() },
        )
    }

    override fun getById(draftArticleId: DraftArticleId): DraftArticle? = draftArticleJpaRepository.findById(draftArticleId.value)
        .getOrNull()?.toDomain()

    override fun deleteById(draftArticleId: DraftArticleId) {
        draftArticleJpaRepository.deleteById(draftArticleId.value)
    }

    private fun DraftArticle.toPm(): DraftArticlePM = DraftArticlePM(
        id = id.value,
        title = title,
        content = content,
        thumbnailUrl = thumbnailUrl,
        writerId = writerId.value,
    )

    private fun DraftArticlePM.toDomain(): DraftArticle = DraftArticle.Factory.fromPm(
        id = DraftArticleId(id),
        title = title,
        content = content,
        thumbnailUrl = thumbnailUrl,
        writerId = UserId(writerId),
        createdAt = createdDate!!,
        updatedAt = updatedDate!!,
    )
}
