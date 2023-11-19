package kr.co.jiniaslog.blog.adapter.acl.user

import kr.co.jiniaslog.blog.domain.writer.Writer
import kr.co.jiniaslog.blog.domain.writer.WriterId
import kr.co.jiniaslog.blog.domain.writer.WriterName
import kr.co.jiniaslog.blog.domain.writer.WriterProvider
import kr.co.jiniaslog.shared.core.annotation.CustomComponent

// stub
@CustomComponent
class UserProviderAdapter : WriterProvider {
    override suspend fun getWriter(writerId: WriterId): Writer? {
        return Writer(
            id = writerId,
            name = WriterName("jinia"),
        )
    }

    override suspend fun isExist(writerId: WriterId): Boolean {
        return true
    }
}
