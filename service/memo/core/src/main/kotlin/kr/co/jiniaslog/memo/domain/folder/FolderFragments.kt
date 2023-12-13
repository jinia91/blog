package kr.co.jiniaslog.memo.domain.folder

import kr.co.jiniaslog.shared.core.domain.ValueObject

@JvmInline
value class FolderId(val value: Long) : ValueObject {
    override fun validate() {
        require(value > 0) { "FolderId must be greater than 0" }
    }
}

@JvmInline
value class FolderName(val value: String) {
    companion object {
        val UNTITLED: FolderName = FolderName("untitled")
    }
}
