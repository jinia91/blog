package kr.co.jiniaslog.memo.domain.memo

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@Embeddable
data class MemoContent(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {}

    companion object {
        val EMPTY: MemoContent = MemoContent("")
    }
}
