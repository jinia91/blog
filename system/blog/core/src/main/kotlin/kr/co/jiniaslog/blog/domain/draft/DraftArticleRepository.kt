package kr.co.jiniaslog.blog.domain.draft

import kr.co.jiniaslog.blog.domain.draft.DraftArticle
import kr.co.jiniaslog.blog.domain.draft.DraftArticleId

interface DraftArticleRepository {
    fun save(newDraftArticle: DraftArticle)
    fun update(draftArticle: DraftArticle)
    fun findById(draftArticleId: DraftArticleId): DraftArticle?
    fun deleteById(draftArticleId: DraftArticleId)
}
