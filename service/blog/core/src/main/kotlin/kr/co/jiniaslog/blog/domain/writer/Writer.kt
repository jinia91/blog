package kr.co.jiniaslog.blog.domain.writer

import kr.co.jiniaslog.shared.core.domain.AggregateRoot

class Writer(
    id: WriterId?,
    name: WriterName,
) : AggregateRoot<WriterId>() {
    override val id: WriterId = id!!

    var name: WriterName = name
        private set

    companion object {
        fun create(
            id: WriterId,
            name: WriterName,
        ): Writer {
            return Writer(
                id = id,
                name = name,
            )
        }
    }
}
