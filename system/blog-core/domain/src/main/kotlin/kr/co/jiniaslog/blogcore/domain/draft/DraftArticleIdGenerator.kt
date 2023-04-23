package kr.co.jiniaslog.blogcore.domain.draft

interface DraftArticleIdGenerator {
    fun generate(): DraftArticleId
}
