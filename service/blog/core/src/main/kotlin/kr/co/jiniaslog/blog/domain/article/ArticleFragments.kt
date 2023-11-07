package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.shared.core.domain.ValueObject

/**
 * Article 관련 단순 ValueObject 정의 - value class 만 모음
 *
 * 프로퍼티가 2개 이상인 valueObject는 data class로 정의하고 다른 파일로 분리
 *
 */

@JvmInline
value class ArticleId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "article id must be positive" }
    }
}

@JvmInline
value class ArticleCommitVersion(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "article commit id must be positive" }
    }
}

@JvmInline
value class ArticleTitle(val value: String): ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.isNotBlank()) { "article title must not be blank" }
        require(value.length in 1..100) { "article title must be between 1 and 100 characters" }
    }
}

@JvmInline
value class ArticleContent(val value: String): ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.isNotBlank()) { "article content must not be blank" }
        require(value.length in 1..10000) { "article content must be between 1 and 10000 characters" }
    }
}

@JvmInline
value class ArticleThumbnailUrl(val value: String): ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.isNotBlank()) { "article thumbnail must not be blank" } }
}
