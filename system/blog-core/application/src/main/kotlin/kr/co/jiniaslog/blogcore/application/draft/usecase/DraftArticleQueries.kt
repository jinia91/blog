package kr.co.jiniaslog.blogcore.application.draft.usecase

import kr.co.jiniaslog.blogcore.domain.draft.DraftArticle
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId

interface DraftArticleQueries {
    fun getDraftArticle(draftArticleId: DraftArticleId): DraftArticle
}
