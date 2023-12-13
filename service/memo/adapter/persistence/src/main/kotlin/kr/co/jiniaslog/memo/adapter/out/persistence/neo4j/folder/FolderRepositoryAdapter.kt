package kr.co.jiniaslog.memo.adapter.out.persistence.neo4j.folder

import kr.co.jiniaslog.memo.adapter.out.persistence.neo4j.memo.MemoNeo4jRepository
import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderRepository
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import kotlin.jvm.optionals.getOrNull

@PersistenceAdapter
class FolderRepositoryAdapter(
    private val folderNeo4jRepository: FolderNeo4jRepository,
    private val memoNeo4jRepository: MemoNeo4jRepository,
) : FolderRepository {
    override fun findById(id: FolderId): Folder? {
        return folderNeo4jRepository.findById(id.value).orElse(null)?.toDomain()
    }

    override fun findAll(): List<Folder> {
        return folderNeo4jRepository.findAll().map { it.toDomain() }
    }

    override fun deleteById(id: FolderId) {
        folderNeo4jRepository.deleteFolderAndContentsById(id.value)
    }

    override fun save(entity: Folder): Folder {
        var pm = folderNeo4jRepository.findById(entity.id.value).getOrNull()

        pm =
            when (pm) {
                null -> { // is new
                    val parentFolder =
                        entity.parent?.let {
                            folderNeo4jRepository.findById(it.value).getOrNull()
                        }
                    val childrenFolder =
                        entity.children.map {
                            folderNeo4jRepository.findById(it.value).getOrNull()
                        }.filterNotNull().toMutableSet()
                    val memos =
                        entity.memos.map {
                            memoNeo4jRepository.findById(it.value).getOrNull()
                        }.filterNotNull().toMutableSet()

                    val folderNeo4jEntity =
                        FolderNeo4jEntity(
                            id = entity.id.value,
                            name = entity.name.value,
                            authorId = entity.authorId.value,
                            parent = parentFolder,
                            children = childrenFolder,
                            memos = memos,
                        )
                    folderNeo4jRepository.save(folderNeo4jEntity)
                }

                else -> { // update
                    val origin: FolderNeo4jEntity = pm
                    val updateData: Folder = entity

                    // name
                    if (origin.name != updateData.name.value) {
                        origin.name = updateData.name.value
                    }

                    // parent
                    val updatedParentId = updateData.parent?.value
                    if ((origin.parent?.id) != updatedParentId) {
                        origin.parent =
                            updatedParentId?.let {
                                folderNeo4jRepository.findById(it).getOrNull()
                            }
                    }

                    // children
                    val updatedChildrenIds = updateData.children.map { it.value }.toSet()
                    val addedChildrenIds = updatedChildrenIds.minus(origin.children.map { it.id }.toSet())
                    addedChildrenIds.forEach {
                        val child = folderNeo4jRepository.findById(it).getOrNull()
                        if (child != null) {
                            origin.children.add(child)
                        }
                    }
                    val removedChildrenIds = origin.children.map { it.id }.toSet().minus(updatedChildrenIds)
                    removedChildrenIds.forEach {
                        val child = folderNeo4jRepository.findById(it).getOrNull()
                        if (child != null) {
                            origin.children.remove(child)
                        }
                    }
                    // memos
                    val updatedMemosIds = updateData.memos.map { it.value }.toSet()
                    val addedMemosIds = updatedMemosIds.minus(origin.memos.map { it.id }.toSet())
                    addedMemosIds.forEach {
                        val memo = memoNeo4jRepository.findById(it).getOrNull()
                        if (memo != null) {
                            origin.memos.add(memo)
                        }
                    }
                    val removedMemosIds = origin.memos.map { it.id }.toSet().minus(updatedMemosIds)
                    removedMemosIds.forEach {
                        val memo = memoNeo4jRepository.findById(it).getOrNull()
                        if (memo != null) {
                            origin.memos.remove(memo)
                        }
                    }
                    folderNeo4jRepository.save(origin)
                }
            }
        return pm.toDomain()
    }
}
