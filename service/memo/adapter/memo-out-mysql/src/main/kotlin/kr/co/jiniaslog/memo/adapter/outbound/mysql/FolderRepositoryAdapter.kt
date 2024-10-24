package kr.co.jiniaslog.memo.adapter.outbound.mysql

import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderRepository
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter

@PersistenceAdapter
internal class FolderRepositoryAdapter(
    private val folderJpaRepository: FolderJpaRepository
) : FolderRepository {
    override fun count(): Long {
        return folderJpaRepository.count()
    }

    override fun findById(id: FolderId): Folder? {
        return folderJpaRepository.findById(id).orElse(null)
    }

    override fun deleteById(id: FolderId) {
        folderJpaRepository.deleteById(id)
    }

    override fun save(entity: Folder): Folder {
        return folderJpaRepository.save(entity)
    }
}
