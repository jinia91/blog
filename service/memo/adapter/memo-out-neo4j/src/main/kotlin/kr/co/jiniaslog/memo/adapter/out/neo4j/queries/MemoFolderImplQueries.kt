package kr.co.jiniaslog.memo.adapter.out.neo4j.queries

import kr.co.jiniaslog.memo.adapter.out.neo4j.folder.FolderNeo4jEntity
import kr.co.jiniaslog.memo.adapter.out.neo4j.folder.FolderNeo4jRepository
import kr.co.jiniaslog.memo.adapter.out.neo4j.memo.MemoNeo4jEntity
import kr.co.jiniaslog.memo.adapter.out.neo4j.memo.MemoNeo4jRepository
import kr.co.jiniaslog.memo.domain.exception.NotOwnershipException
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.queries.FolderQueriesFacade
import kr.co.jiniaslog.memo.queries.IGetAllReferencedByMemo
import kr.co.jiniaslog.memo.queries.IGetAllReferencesByMemo
import kr.co.jiniaslog.memo.queries.IGetFoldersAllInHierirchyByAuthorId
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.memo.queries.MemoQueriesFacade
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@PersistenceAdapter
@Transactional(readOnly = true)
internal open class MemoFolderImplQueries(
    private val memoNeo4jRepository: MemoNeo4jRepository,
    private val folderNeo4jRepository: FolderNeo4jRepository,
) : MemoQueriesFacade, FolderQueriesFacade {
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

    override fun handle(query: IGetFoldersAllInHierirchyByAuthorId.Query): IGetFoldersAllInHierirchyByAuthorId.Info {
        if (query.value.isNullOrBlank()) {
            return findAllByAuthorId(query.requesterId)
        }
        return findByKeyword(query)
    }

    private fun findAllByAuthorId(authorId: AuthorId): IGetFoldersAllInHierirchyByAuthorId.Info {
        val foldersWithDepth = folderNeo4jRepository.findAllByAuthorId(authorId.value).toList()
        val folderMap = foldersWithDepth.associate { it.id to it.toFolderInfo() }
        val memos = memoNeo4jRepository.findAllByAuthorId(authorId.value).groupBy { it.parentFolder?.id }

        foldersWithDepth.forEach { folder ->
            val folderInfo = folderMap[folder.id]
            folderInfo?.children = folderMap.values.filter { it.parent?.id == folderInfo?.id }
            folderInfo?.memos = memos[folderInfo?.id]?.map { it.toMemoInfo() } ?: emptyList()
        }

        return IGetFoldersAllInHierirchyByAuthorId.Info(
            folderMap.values.filter { it.parent == null } +
                IGetFoldersAllInHierirchyByAuthorId.FolderInfo(
                    id = null,
                    name = "Uncategorized",
                    parent = null,
                    children = emptyList(),
                    memos = memos[null]?.map { it.toMemoInfo() } ?: emptyList(),
                ),
        )
    }

    private fun findByKeyword(query: IGetFoldersAllInHierirchyByAuthorId.Query): IGetFoldersAllInHierirchyByAuthorId.Info {
        val memos = memoNeo4jRepository.findByKeywordFullTextSearching(query.value!!, query.requesterId.value)
        return IGetFoldersAllInHierirchyByAuthorId.Info(
            listOf(
                IGetFoldersAllInHierirchyByAuthorId.FolderInfo(
                    id = null,
                    name = "검색 결과",
                    parent = null,
                    children = emptyList(),
                    memos = memos.map { it.toMemoInfo() },
                ),
            ),
        )
    }

    private fun FolderNeo4jEntity.toFolderInfo(): IGetFoldersAllInHierirchyByAuthorId.FolderInfo {
        return IGetFoldersAllInHierirchyByAuthorId.FolderInfo(
            id = this.id,
            name = this.name,
            parent = this.parent?.toFolderInfo(),
            children = listOf(),
            memos = listOf(),
        )
    }

    private fun MemoNeo4jEntity.toMemoInfo(): IGetFoldersAllInHierirchyByAuthorId.MemoInfo {
        return IGetFoldersAllInHierirchyByAuthorId.MemoInfo(
            id = this.id,
            title = this.title,
            references = this.references.map {
                IGetFoldersAllInHierirchyByAuthorId.MemoReferenceInfo(
                    it.id,
                    it.title
                )
            },
        )
    }
}
