package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.blog.domain.article.CompressionUtils.compressString
import kr.co.jiniaslog.blog.domain.article.CompressionUtils.decompressString
import kr.co.jiniaslog.shared.core.domain.ValueObject
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch

/**
 * Article 관련 단순 ValueObject 정의 - value class 만 모음
 *
 * 프로퍼티가 2개 이상인 valueObject는 data class로 정의하고 다른 파일로 분리
 *
 */

private val deltaUtil = DiffMatchPatch()

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
value class ArticleCommitVersion(val value: Long) : ValueObject, Comparable<ArticleCommitVersion> {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "article commit id must be positive" }
    }

    override fun compareTo(other: ArticleCommitVersion): Int {
        return value.compareTo(other.value)
    }
}

@JvmInline
value class ArticleTitle(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.isNotBlank()) { "article title must not be blank" }
        require(value.length in 1..100) { "article title must be between 1 and 100 characters" }
    }
}

@JvmInline
value class ArticleContent(val value: String) : ValueObject {
    init {
        validate()
    }

    operator fun plus(other: ArticleContent): ArticleContent {
        return ArticleContent(value + other.value)
    }

    override fun validate() {
        require(value.length in 0..10000) { "article content must be between 0 and 10000 characters" }
    }

    fun calculateDelta(newContent: ArticleContent): ArticleContentDelta {
        return deltaUtil.diffMain(value, newContent.value)
            .let { deltaUtil.diffToDelta(it) }
            .let { ArticleContentDelta.build(it) }
    }

    fun apply(delta: ArticleContentDelta): ArticleContent {
        val diffs = deltaUtil.diffFromDelta(value, delta.getString())
        val patches = deltaUtil.patchMake(diffs)
        return ArticleContent(deltaUtil.patchApply(patches, value)[0].toString())
    }

    companion object {
        val EMPTY = ArticleContent("")
    }
}

@JvmInline
value class ArticleContentDelta(private val value: ByteArray) : ValueObject {
    init {
        validate()
    }

    fun getString(): String {
        return decompressString(value)
    }

    fun getByteArray(): ByteArray {
        return value
    }

    override fun validate() {}

    companion object {
        fun build(value: String): ArticleContentDelta {
            compressString(value).let {
                return ArticleContentDelta(it)
            }
        }

        fun from(value: ByteArray): ArticleContentDelta {
            return ArticleContentDelta(value)
        }
    }
}

@JvmInline
value class ArticleThumbnailUrl(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.isNotBlank()) { "article thumbnail must not be blank" }
    }
}

@JvmInline
value class WriterId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "user id must be positive" }
    }
}

@JvmInline
value class StagingSnapShotId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "staging snapshot id must be positive" }
    }
}
