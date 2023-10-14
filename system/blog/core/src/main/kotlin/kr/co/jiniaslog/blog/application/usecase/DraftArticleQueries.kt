package kr.co.jiniaslog.blog.application.usecase

import kr.co.jiniaslog.blog.domain.draft.DraftArticle
import kr.co.jiniaslog.blog.domain.draft.DraftArticleId

interface DraftArticleQueries {
    fun getDraftArticle(draftArticleId: DraftArticleId): DraftArticle
}
