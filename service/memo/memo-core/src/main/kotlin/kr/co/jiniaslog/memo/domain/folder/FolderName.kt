package kr.co.jiniaslog.memo.domain.folder

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject
import java.io.Serializable

@Embeddable
data class FolderName(val value: String) : ValueObject, Serializable {
    init {
        validate()
    }

    companion object {
        val UNTITLED: FolderName = FolderName("untitled")
    }

    override fun validate() {
        require(value.isNotBlank()) { "FolderName 은 빈 문자열일 수 없습니다." }
    }
}
