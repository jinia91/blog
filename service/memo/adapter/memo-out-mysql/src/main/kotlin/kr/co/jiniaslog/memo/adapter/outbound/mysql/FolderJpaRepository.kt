package kr.co.jiniaslog.memo.adapter.outbound.mysql

import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.folder.FolderId
import org.springframework.data.jpa.repository.JpaRepository

interface FolderJpaRepository : JpaRepository<Folder, FolderId>
