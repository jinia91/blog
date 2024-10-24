package kr.co.jiniaslog.memo.domain.folder

import kr.co.jiniaslog.shared.core.domain.Repository

interface FolderRepository : Repository<Folder, FolderId> {
    fun count(): Long
}
