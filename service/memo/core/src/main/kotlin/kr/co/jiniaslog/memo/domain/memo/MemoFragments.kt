package kr.co.jiniaslog.memo.domain.memo

import kr.co.jiniaslog.shared.core.domain.ValueObject

@JvmInline
value class MemoContent(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        if (value.isEmpty()) {
            throw IllegalArgumentException("메모 컨텐츠는 1자 이상이어야 합니다.")
        }
    }
}

@JvmInline
value class MemoId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        if (value < 0) {
            throw IllegalArgumentException("메모 ID는 0 이상이어야 합니다.")
        }
    }
}

@JvmInline
value class MemoTitle(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        if (value.isEmpty()) {
            throw IllegalArgumentException("메모 제목은 1자 이상이어야 합니다.")
        }
    }

    companion object {
        fun from(content: MemoContent): MemoTitle {
            return MemoTitle(content.value.take(10))
        }
    }
}

@JvmInline
value class MemoTagId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        if (value < 0) {
            throw IllegalArgumentException("메모 태그 ID는 0 이상이어야 합니다.")
        }
    }
}

@JvmInline
value class AuthorId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        if (value < 0) {
            throw IllegalArgumentException("작성자 ID는 0 이상이어야 합니다.")
        }
    }
}

@JvmInline
value class MemoLinkId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        if (value < 0) {
            throw IllegalArgumentException("메모 링크 ID는 0 이상이어야 합니다.")
        }
    }
}

enum class MemoLinkType {
    REFERENCE,
}

enum class MemoStatus {
    STAGED,
    COMMITTED,
}
