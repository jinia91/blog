package kr.co.jiniaslog.blog.domain.category

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.IdUtils
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject
import java.io.Serializable

@Embeddable
data class CategoryId(
    val value: Long,
) : ValueObject, Serializable {
    init {
        validate()
    }
    override fun validate() {
        require(value > 0) { "카테고리 id는 양수여야 합니다." }
    }

    companion object {
        fun newOne(): CategoryId = CategoryId(IdUtils.generate())
    }
}
