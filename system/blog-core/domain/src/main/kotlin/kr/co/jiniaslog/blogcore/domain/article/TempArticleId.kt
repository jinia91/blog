package kr.co.jiniaslog.blogcore.domain.article

import kr.co.jiniaslog.shared.core.domain.ValueObject

data class TempArticleId(val value: Long) : ValueObject {
    companion object {
        const val TEMP_ARTICLE_STATIC_ID = 1L
        fun getDefault() = TempArticleId(TEMP_ARTICLE_STATIC_ID)
    }
}
