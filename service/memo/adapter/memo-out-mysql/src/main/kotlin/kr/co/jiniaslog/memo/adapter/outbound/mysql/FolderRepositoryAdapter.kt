package kr.co.jiniaslog.memo.adapter.outbound.mysql

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.jiniaslog.memo.adapter.outbound.mysql.config.MemoDb.TRANSACTION_MANAGER
import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderRepository
import kr.co.jiniaslog.memo.domain.folder.QFolder.folder
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.QMemo.memo
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import org.springframework.transaction.annotation.Transactional

@PersistenceAdapter
@Transactional(transactionManager = TRANSACTION_MANAGER)
internal class FolderRepositoryAdapter(
    private val folderJpaRepository: FolderJpaRepository,
    private val memoJpaQueryFactory: JPAQueryFactory,
    private val memoFolderNativeRepository: MemoFolderMapper,
) : FolderRepository {
    @Transactional(readOnly = true, transactionManager = TRANSACTION_MANAGER)
    override fun countByAuthorId(authorId: AuthorId): Long {
        return folderJpaRepository.countByAuthorId(authorId)
    }

    @Transactional(readOnly = true, transactionManager = TRANSACTION_MANAGER)
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

    override fun deleteById(id: FolderId) {
        val folderIds = memoFolderNativeRepository.findRecursiveFolderIds(id.value)
            .map { FolderId(it) }
        val memoIds = memoJpaQueryFactory.select(memo.entityId)
            .from(memo)
            .where(memo.parentFolderId.`in`(folderIds))
            .fetch()
        memoJpaQueryFactory.delete(memo)
            .where(memo.entityId.`in`(memoIds))
            .execute()
        memoJpaQueryFactory.delete(folder)
            .where(folder.entityId.`in`(folderIds))
            .execute()
    }

    override fun save(entity: Folder): Folder {
        return folderJpaRepository.save(entity)
    }
}
