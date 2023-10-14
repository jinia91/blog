package kr.co.jiniaslog.blog.domain.draft

import kr.co.jiniaslog.blog.domain.draft.DraftArticleId

interface DraftArticleIdGenerator {
    fun generate(): DraftArticleId
}
