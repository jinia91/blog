package kr.co.jiniaslog.blogcore.domain.draft

interface DraftArticleRepository {
    fun save(newDraftArticle: DraftArticle): DraftArticle
    fun update(draftArticle: DraftArticle): DraftArticle
    fun findById(draftArticleId: DraftArticleId): DraftArticle?
    fun deleteById(draftArticleId: DraftArticleId)
}
