package kr.co.jiniaslog.blog.domain.writer

import kr.co.jiniaslog.shared.core.domain.ValueObject

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
value class WriterName(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.isNotBlank()) { "user name must not be blank" }
    }
}
