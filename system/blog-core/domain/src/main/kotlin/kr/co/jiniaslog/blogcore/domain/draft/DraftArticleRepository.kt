package kr.co.jiniaslog.blogcore.domain.draft

interface DraftArticleRepository {
    fun save(newDraftArticle: DraftArticle): DraftArticle
    fun getById(draftArticleId: DraftArticleId): DraftArticle?
    fun deleteById(draftArticleId: DraftArticleId)
}
