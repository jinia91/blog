package kr.co.jiniaslog.blog.domain.tag

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject
import java.io.Serializable

@Embeddable
data class TagName(val value: String) : ValueObject, Serializable {
    init {
        validate()
    }
    override fun validate() {
        require(value.length <= 20) { "태그 이름은 20자를 넘을 수 없습니다." }
        require(value.isNotEmpty()) { "태그 이름은 필수입니다." }
    }
}
