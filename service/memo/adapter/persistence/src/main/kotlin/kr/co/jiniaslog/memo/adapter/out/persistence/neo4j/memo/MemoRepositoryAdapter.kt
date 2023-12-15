package kr.co.jiniaslog.memo.adapter.out.persistence.neo4j.memo

import kr.co.jiniaslog.memo.adapter.out.persistence.neo4j.folder.FolderNeo4jRepository
import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.queries.model.MemoReferenceInfo
import kr.co.jiniaslog.memo.queries.model.SimpleMemoInfo
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter

@PersistenceAdapter
class MemoRepositoryAdapter(
    private val memoNeo4jRepository: MemoNeo4jRepository,
    private val folderNeo4jRepository: FolderNeo4jRepository,
) : MemoRepository {
    override fun findByRelatedMemo(keyword: String): List<SimpleMemoInfo> {
        return memoNeo4jRepository.findByKeywordFullTextSearching(keyword).map {
            SimpleMemoInfo(
                id = MemoId(it.id),
                title = MemoTitle(it.title),
                references =
                    it.references.map { reference ->
                        MemoReferenceInfo(
                            id = MemoId(reference.id),
                            title = MemoTitle(reference.title),
                        )
                    },
            )
        }
    }

    override fun findById(id: MemoId): Memo? {
        return memoNeo4jRepository.findById(id.value).orElse(null)?.toDomain()
    }

    override fun findAll(): List<Memo> {
        return memoNeo4jRepository.findAll().map { it.toDomain() }
    }

    override fun deleteById(id: MemoId) {
        memoNeo4jRepository.deleteById(id.value)
    }

    override fun save(entity: Memo): Memo {
        val reference =
            entity.references.map {
                memoNeo4jRepository.findById(it.referenceId.value).orElse(null)
            }.toSet()

        val parentFolder =
            entity.parentFolderId?.let {
                folderNeo4jRepository.findById(it.value).orElse(null)
            }

        val memoNeo4jEntity =
            MemoNeo4jEntity(
                id = entity.id.value,
                authorId = entity.authorId.value,
                title = entity.title.value,
                content = entity.content.value,
                state = entity.state,
                references = reference,
                parentFolder = parentFolder,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
            )

        return memoNeo4jRepository.save(memoNeo4jEntity).toDomain()
    }
}
