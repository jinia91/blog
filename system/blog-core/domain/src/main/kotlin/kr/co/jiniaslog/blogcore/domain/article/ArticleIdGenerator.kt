package kr.co.jiniaslog.blogcore.domain.article

interface ArticleIdGenerator {
    fun generate(): ArticleId
}
