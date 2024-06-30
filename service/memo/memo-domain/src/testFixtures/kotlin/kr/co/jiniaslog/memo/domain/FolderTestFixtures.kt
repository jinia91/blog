package kr.co.jiniaslog.memo.domain

import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderName
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.time.LocalDateTime

object FolderTestFixtures {
    fun build(
        id: FolderId = FolderId(IdUtils.generate()),
        name: FolderName = FolderName("test folder"),
        authorId: AuthorId = AuthorId(IdUtils.generate()),
        parent: FolderId? = null,
        createdAt: LocalDateTime? = null,
        updatedAt: LocalDateTime? = null,
    ): Folder {
        return Folder.from(
            id = id,
            name = name,
            authorId = authorId,
            parent = parent,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}
