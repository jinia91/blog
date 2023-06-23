package kr.co.jiniaslog.blogcore.adapter.persistence.draft

import kr.co.jiniaslog.blogcore.domain.draft.DraftArticle
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleIdGenerator
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleRepository
import kr.co.jiniaslog.blogcore.domain.user.UserId
import kr.co.jiniaslog.shared.core.domain.IdGenerator
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
internal class DraftArticleRepositoryAdapter(
    private val draftArticleJpaRepository: DraftArticleJpaRepository,
    private val idGenerator: IdGenerator,
) : DraftArticleRepository, DraftArticleIdGenerator {
    override fun save(newDraftArticle: DraftArticle): DraftArticle {
        val returnPm = draftArticleJpaRepository.save(newDraftArticle.toPm().apply { isNewFlag = true })
        newDraftArticle.syncAuditAfterPersist(
            returnPm.createdDate!!,
            returnPm.updatedDate!!,
        )
        return newDraftArticle
    }

    override fun update(draftArticle: DraftArticle): DraftArticle {
        val returnPm = draftArticleJpaRepository.save(draftArticle.toPm().apply { isNewFlag = false })
        draftArticle.syncAuditAfterPersist(
            returnPm.createdDate!!,
            returnPm.updatedDate!!,
        )
        return draftArticle
    }

    override fun findById(draftArticleId: DraftArticleId): DraftArticle? = draftArticleJpaRepository.findById(draftArticleId.value)
        .getOrNull()?.toDomain()

    override fun deleteById(draftArticleId: DraftArticleId) {
        draftArticleJpaRepository.deleteById(draftArticleId.value)
    }

    override fun generate(): DraftArticleId {
        return DraftArticleId(idGenerator.generate())
    }

    private fun DraftArticle.toPm(): DraftArticlePM = DraftArticlePM(
        id = id.value,
        title = title,
        content = content,
        thumbnailUrl = thumbnailUrl,
        writerId = writerId.value,
        createdDate = createdDate,
        updatedDate = updatedDate,
    )

    private fun DraftArticlePM.toDomain(): DraftArticle = DraftArticle.Factory.from(
        id = DraftArticleId(id),
        title = title,
        content = content,
        thumbnailUrl = thumbnailUrl,
        writerId = UserId(writerId),
        createdAt = createdDate!!,
        updatedAt = updatedDate!!,
    )
}
