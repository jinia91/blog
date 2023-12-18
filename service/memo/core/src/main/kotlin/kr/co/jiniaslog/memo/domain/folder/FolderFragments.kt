package kr.co.jiniaslog.memo.domain.folder

import kr.co.jiniaslog.shared.core.domain.ValueObject

@JvmInline
value class FolderId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "FolderId must be greater than 0" }
    }
}

@JvmInline
value class FolderName(val value: String) : ValueObject {
    init {
        validate()
    }

    companion object {
        val UNTITLED: FolderName = FolderName("untitled")
    }

    override fun validate() {
        require(value.isNotBlank()) { "FolderName must not be blank" }
    }
}
