package kr.co.jiniaslog.memo.adapter.out.neo4j.queries

import kr.co.jiniaslog.memo.adapter.out.neo4j.folder.FolderNeo4jEntity
import kr.co.jiniaslog.memo.adapter.out.neo4j.folder.FolderNeo4jRepository
import kr.co.jiniaslog.memo.adapter.out.neo4j.memo.MemoNeo4jEntity
import kr.co.jiniaslog.memo.adapter.out.neo4j.memo.MemoNeo4jRepository
import kr.co.jiniaslog.memo.domain.exception.NotOwnershipException
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderName
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.queries.FolderQueriesFacade
import kr.co.jiniaslog.memo.queries.ICheckMemoExisted
import kr.co.jiniaslog.memo.queries.IGetAllReferencedByMemo
import kr.co.jiniaslog.memo.queries.IGetAllReferencesByMemo
import kr.co.jiniaslog.memo.queries.IGetFoldersAllInHierirchyByAuthorId
import kr.co.jiniaslog.memo.queries.IGetMemoById
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.memo.queries.MemoQueriesFacade
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrNull

@PersistenceAdapter
@Transactional(readOnly = true)
internal open class MemoFolderImplQueries(
    private val memoNeo4jRepository: MemoNeo4jRepository,
    private val folderNeo4jRepository: FolderNeo4jRepository,
) : MemoQueriesFacade, FolderQueriesFacade {

    override fun handle(query: IRecommendRelatedMemo.Query): IRecommendRelatedMemo.Info {
        val relatedMemoCandidates =
            memoNeo4jRepository.findByKeywordFullTextSearchingLimit6ByAuthorId(query.keyword, query.requesterId.value)
                .filterNot { it.id == query.thisMemoId.value }
                .take(5)
                .map { Triple(it.id, it.title, it.content) }

        return IRecommendRelatedMemo.Info(
            relatedMemoCandidates =
            relatedMemoCandidates.map {
                Triple(
                    MemoId(it.first),
                    MemoTitle(it.second),
                    MemoContent(it.third),
                )
            },
        )
    }

    override fun handle(query: IGetMemoById.Query): IGetMemoById.Info {
        val memo =
            memoNeo4jRepository.findById(query.memoId.value).getOrNull()
                ?: throw IllegalArgumentException("memo not found")
        require(memo.authorId == query.requesterId.value) { throw NotOwnershipException() }
        return IGetMemoById.Info(
            memoId = MemoId(memo.id),
            title = MemoTitle(memo.title),
            content = MemoContent(memo.content),
            references =
            memo.references.map {
                IGetMemoById.Info.ReferenceInfo(
                    rootId = MemoId(it.id),
                    referenceId = MemoId(it.id),
                )
            }.toSet(),
        )
    }

    // todo : 위의 유즈케이스와 분리하여 더 최적화하도록 고려
    override fun handle(query: IGetAllReferencesByMemo.Query): IGetAllReferencesByMemo.Info {
        val memo =
            memoNeo4jRepository.findById(query.memoId.value).getOrNull()
                ?: throw IllegalArgumentException("memo not found")
        require(memo.authorId == query.requesterId.value) { throw NotOwnershipException() }
        return IGetAllReferencesByMemo.Info(
            references =
            memo.references.map {
                IGetAllReferencesByMemo.ReferenceInfo(
                    id = MemoId(it.id),
                    title = MemoTitle(it.title),
                )
            }.toSet(),
        )
    }

    override fun handle(query: IGetAllReferencedByMemo.Query): IGetAllReferencedByMemo.Info {
        val result = memoNeo4jRepository.findReferencingMemos(query.memoId.value)
        result.forEach {
            require(it.authorId == query.requesterId.value) { throw NotOwnershipException() }
        }
        return IGetAllReferencedByMemo.Info(
            referenceds =
            result.map {
                IGetAllReferencedByMemo.ReferencedInfo(
                    id = MemoId(it.id),
                    title = MemoTitle(it.title),
                )
            }.toSet(),
        )
    }

    override fun handle(query: ICheckMemoExisted.Query): Boolean {
        return memoNeo4jRepository.existsById(query.memoId.value)
    }

    override fun handle(query: IGetFoldersAllInHierirchyByAuthorId.Query): IGetFoldersAllInHierirchyByAuthorId.Info {
        if (query.value.isNullOrBlank()) {
            return findAllByAuthorId(query.requesterId)
        }
        return findByKeyword(query)
    }

    private fun findAllByAuthorId(authorId: AuthorId): IGetFoldersAllInHierirchyByAuthorId.Info {
        val foldersWithAllJob = CompletableFuture.supplyAsync {
            folderNeo4jRepository.findAllByAuthorId(authorId.value)
        }
        val orphansMemoJob = CompletableFuture.supplyAsync {
            memoNeo4jRepository.findAllByAuthorIdAndParentFolderIsNull(authorId.value)
                .filter { it.parentFolder == null }
        }
        val foldersWithAll = foldersWithAllJob.join()
        val folderMap = foldersWithAll.associate { it.id to it.toFolderInfo() }

        foldersWithAll.forEach { folder ->
            val folderInfo = folderMap[folder.id]
            folderInfo?.children = folderMap.values.filter { it.parent?.id == folderInfo?.id }
        }

        val orphansMemo = orphansMemoJob.join()
        return IGetFoldersAllInHierirchyByAuthorId.Info(
            folderMap.values.filter { it.parent == null } +
                IGetFoldersAllInHierirchyByAuthorId.FolderInfo(
                    id = null,
                    name = FolderName("Uncategorized"),
                    parent = null,
                    children = emptyList(),
                    memos = orphansMemo.map { it.toMemoInfo() },
                ),
        )
    }

    private fun findByKeyword(query: IGetFoldersAllInHierirchyByAuthorId.Query): IGetFoldersAllInHierirchyByAuthorId.Info {
        val memos = memoNeo4jRepository.findByKeywordFullTextSearching(query.value!!, query.requesterId.value)
        return IGetFoldersAllInHierirchyByAuthorId.Info(
            listOf(
                IGetFoldersAllInHierirchyByAuthorId.FolderInfo(
                    id = null,
                    name = FolderName("검색 결과"),
                    parent = null,
                    children = emptyList(),
                    memos = memos.map { it.toMemoInfo() },
                ),
            ),
        )
    }

    private fun FolderNeo4jEntity.toFolderInfo(): IGetFoldersAllInHierirchyByAuthorId.FolderInfo {
        return IGetFoldersAllInHierirchyByAuthorId.FolderInfo(
            id = FolderId(this.id),
            name = FolderName(this.name),
            parent = this.parent?.toFolderInfo(),
            children = listOf(),
            memos = this.memos.map { it.toMemoInfo() },
        )
    }

    private fun MemoNeo4jEntity.toMemoInfo(): IGetFoldersAllInHierirchyByAuthorId.MemoInfo {
        return IGetFoldersAllInHierirchyByAuthorId.MemoInfo(
            id = MemoId(this.id),
            title = MemoTitle(this.title),
            references = this.references.map {
                IGetFoldersAllInHierirchyByAuthorId.MemoReferenceInfo(
                    MemoId(it.id),
                    MemoTitle(it.title)
                )
            },
        )
    }
}
