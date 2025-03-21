package kr.co.jiniaslog.memo.adapter.outbound.mysql

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderRepository
import kr.co.jiniaslog.memo.domain.folder.QFolder.folder
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.QMemo.memo
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import org.springframework.transaction.annotation.Transactional

@PersistenceAdapter
@Transactional
internal class FolderRepositoryAdapter(
    private val folderJpaRepository: FolderJpaRepository,
    private val memoJpaQueryFactory: JPAQueryFactory,
    private val memoFolderNativeRepository: MemoFolderMapper,
) : FolderRepository {
    @Transactional(readOnly = true)
    override fun countByAuthorId(authorId: AuthorId): Long {
        return folderJpaRepository.countByAuthorId(authorId)
    }

    @Transactional(readOnly = true)
    override fun findById(id: FolderId): Folder? {
        return folderJpaRepository.findById(id).orElse(null)
    }

    override fun deleteAllFoldersAndMemosWithout(adminIds: List<AuthorId>) {
        memoJpaQueryFactory.delete(memo)
            .where(memo.authorId.notIn(adminIds))
            .execute()
        memoJpaQueryFactory.delete(folder)
            .where(folder.authorId.notIn(adminIds))
            .execute()
    }

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun deleteById(id: FolderId) {
        memoFolderNativeRepository.createRecursiveFolderTable(id.value)
        memoFolderNativeRepository.createMemoIdTable()
        memoFolderNativeRepository.deleteMemoReferences()
        memoFolderNativeRepository.deleteMemos()
        memoFolderNativeRepository.deleteFolders()
    }

    override fun save(entity: Folder): Folder {
        return folderJpaRepository.save(entity)
    }
}
