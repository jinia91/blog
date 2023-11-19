package kr.co.jiniaslog.blog.domain.writer

interface WriterProvider {
    suspend fun getWriter(writerId: WriterId): Writer?

    suspend fun isExist(writerId: WriterId): Boolean
}
