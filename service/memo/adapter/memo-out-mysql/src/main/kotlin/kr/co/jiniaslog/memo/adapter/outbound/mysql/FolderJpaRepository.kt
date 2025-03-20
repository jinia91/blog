package kr.co.jiniaslog.memo.adapter.outbound.mysql

import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import org.springframework.data.jpa.repository.JpaRepository

interface FolderJpaRepository : JpaRepository<Folder, FolderId> {
    fun findAllByAuthorId(authorId: AuthorId): List<Folder>

    fun countByAuthorId(authorId: AuthorId): Long
}
