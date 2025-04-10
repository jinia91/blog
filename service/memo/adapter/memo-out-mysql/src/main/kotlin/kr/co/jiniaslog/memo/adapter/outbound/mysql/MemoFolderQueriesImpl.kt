package kr.co.jiniaslog.memo.adapter.outbound.mysql

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.jiniaslog.memo.adapter.outbound.mysql.config.MemoDb
import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.QMemo
import kr.co.jiniaslog.memo.domain.memo.QMemo.memo
import kr.co.jiniaslog.memo.queries.FolderQueriesFacade
import kr.co.jiniaslog.memo.queries.ICheckMemoExisted
import kr.co.jiniaslog.memo.queries.IGetAllReferencedByMemo
import kr.co.jiniaslog.memo.queries.IGetAllReferencesByMemo
import kr.co.jiniaslog.memo.queries.IGetFoldersAllInHierirchyByAuthorId
import kr.co.jiniaslog.memo.queries.IGetMemoById
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.memo.queries.ISearchAllMemoByKeyword
import kr.co.jiniaslog.memo.queries.MemoQueriesFacade
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import org.springframework.cache.annotation.Cacheable
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrElse

@PersistenceAdapter
@Transactional(readOnly = true, transactionManager = MemoDb.TRANSACTION_MANAGER)
internal class MemoFolderQueriesImpl(
    private val memoJpaQueryFactory: JPAQueryFactory,
    private val memoRepository: MemoJpaRepository,
    private val folderRepository: FolderJpaRepository,
) : MemoQueriesFacade, FolderQueriesFacade {
    override fun handle(query: IRecommendRelatedMemo.Query): IRecommendRelatedMemo.Info {
        val result = memoJpaQueryFactory.selectFrom(memo)
            .where(
                memo.entityId.value.ne(query.thisMemoId.value)
                    .and(memo.title.value.contains(query.keyword).or(memo.content.value.contains(query.keyword)))
            )
            .fetch()
        return IRecommendRelatedMemo.Info(
            result.map { memo ->
                Triple(
                    memo.entityId,
                    memo.title,
                    memo.content
                )
            }
        )
    }

    override fun handle(query: IGetMemoById.Query): IGetMemoById.Info {
        return memoRepository.findById(query.memoId).getOrElse {
            throw IllegalArgumentException("memo not found")
        }.let {
            IGetMemoById.Info(
                memoId = it.entityId,
                title = it.title,
                content = it.content,
                references = it.getReferences().map { reference ->
                    IGetMemoById.Info.ReferenceInfo(
                        referenceId = reference.referenceId,
                        rootId = reference.rootId
                    )
                }.toSet()
            )
        }
    }

    override fun handle(query: IGetAllReferencesByMemo.Query): IGetAllReferencesByMemo.Info {
        val memo = memoJpaQueryFactory.selectFrom(memo)
            .where(memo.entityId.value.eq(query.memoId.value))
            .fetchOne()
        val references = memo?.getReferences()?.map {
            it.referenceId.value
        }
        val referencesMemo = memoJpaQueryFactory.selectFrom(QMemo.memo)
            .where(QMemo.memo.entityId.value.`in`(references))
            .fetch()

        return IGetAllReferencesByMemo.Info(
            references = referencesMemo.map {
                IGetAllReferencesByMemo.ReferenceInfo(
                    id = it.entityId,
                    title = it.title
                )
            }.toSet()
        )
    }

    override fun handle(query: IGetAllReferencedByMemo.Query): IGetAllReferencedByMemo.Info {
        val memo = memoJpaQueryFactory.selectFrom(memo)
            .where(memo.entityId.value.eq(query.memoId.value))
            .fetchOne()

        val referencingMemo = memoJpaQueryFactory.selectFrom(QMemo.memo)
            .where(QMemo.memo._references.any().referenceId.value.eq(query.memoId.value))
            .fetch()

        return IGetAllReferencedByMemo.Info(
            referenceds = referencingMemo.map {
                IGetAllReferencedByMemo.ReferencedInfo(
                    id = it.entityId,
                    title = it.title
                )
            }.toSet()
        )
    }

    override fun handle(query: ICheckMemoExisted.Query): Boolean {
        return memoJpaQueryFactory.selectFrom(memo)
            .where(memo.entityId.value.eq(query.memoId.value))
            .fetchCount() > 0
    }

    @Cacheable("folders", key = "#query.requesterId")
    override fun handle(query: IGetFoldersAllInHierirchyByAuthorId.Query): IGetFoldersAllInHierirchyByAuthorId.Info {
        val allFolders = folderRepository.findAllByAuthorId(query.requesterId)
        val allMemoes = memoRepository.findAllByAuthorId(query.requesterId)

        val folderMapWithParent = allFolders.groupBy { it.parent?.value }
        val memoMapWithFolder = allMemoes.groupBy { it.parentFolderId?.value }
        val memoMap = allMemoes.associateBy { it.entityId.value }

        fun createMemoInfo(memo: Memo): IGetFoldersAllInHierirchyByAuthorId.MemoInfo {
            return IGetFoldersAllInHierirchyByAuthorId.MemoInfo(
                id = memo.entityId.value,
                title = memo.title.value,
                references = memo.getReferences().map { reference ->
                    IGetFoldersAllInHierirchyByAuthorId.MemoReferenceInfo(
                        id = reference.referenceId.value,
                        title = memoMap[reference.referenceId.value]?.title?.value!!
                    )
                }
            )
        }

        fun createFolderInfo(
            folder: Folder,
            parent: IGetFoldersAllInHierirchyByAuthorId.FolderInfo?,
        ): IGetFoldersAllInHierirchyByAuthorId.FolderInfo {
            val memos =
                memoMapWithFolder[folder.entityId.value]?.map { createMemoInfo(it) } ?: mutableListOf()
            return IGetFoldersAllInHierirchyByAuthorId.FolderInfo(
                id = folder.entityId.value,
                name = folder.name.value,
                parent = parent,
                children = mutableListOf(),
                memos = memos
            )
        }

        fun getFolderTree(result: List<IGetFoldersAllInHierirchyByAuthorId.FolderInfo>): List<IGetFoldersAllInHierirchyByAuthorId.FolderInfo> {
            if (result.isEmpty()) return result
            for (parent in result) {
                val children = folderMapWithParent[parent.id]?.map { child ->
                    createFolderInfo(child, parent.copy())
                } ?: listOf()
                parent.children = children
                getFolderTree(children)
            }
            return result
        }

        val rootFolders = folderMapWithParent[null]?.map { it ->
            createFolderInfo(it, null)
        } ?: mutableListOf()
        val uncategorized = IGetFoldersAllInHierirchyByAuthorId.FolderInfo(
            id = null,
            name = "Uncategorized",
            parent = null,
            children = mutableListOf(),
            memos = memoMapWithFolder[null]?.map { createMemoInfo(it) }?.toMutableList() ?: mutableListOf()
        )
        return IGetFoldersAllInHierirchyByAuthorId.Info(
            folderInfos = getFolderTree(rootFolders) + uncategorized
        )
    }

    override fun handle(query: ISearchAllMemoByKeyword.Query): ISearchAllMemoByKeyword.Info {
        if (query.keyword.isNotEmpty()) {
            val result = memoJpaQueryFactory.selectFrom(memo)
                .where(
                    memo.title.value.contains(query.keyword)
                        .or(memo.content.value.contains(query.keyword))
                ).fetch()

            return ISearchAllMemoByKeyword.Info(
                result = ISearchAllMemoByKeyword.ResultInfo(
                    memos = result.map {
                        ISearchAllMemoByKeyword.MemoInfo(
                            id = it.entityId.value,
                            title = it.title.value
                        )
                    }
                )
            )
        }
        return ISearchAllMemoByKeyword.Info(
            result = ISearchAllMemoByKeyword.ResultInfo(
                memos = emptyList()
            )
        )
    }
}
