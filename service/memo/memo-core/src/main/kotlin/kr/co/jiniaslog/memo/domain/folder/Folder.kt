package kr.co.jiniaslog.memo.domain.folder

import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.co.jiniaslog.memo.domain.exception.NotOwnershipException
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.shared.adapter.out.rdb.JpaAggregate
import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.time.LocalDateTime

@Entity
@Table(name = "folder")
class Folder internal constructor(
    id: FolderId,
    name: FolderName,
    authorId: AuthorId,
    parent: FolderId?,
) : JpaAggregate<FolderId>() {
    @EmbeddedId
    @AttributeOverride(column = Column(name = "id"), name = "value")
    override val entityId: FolderId = id

    @AttributeOverride(column = Column(name = "name"), name = "value")
    var name: FolderName = name
        private set

    @AttributeOverride(column = Column(name = "author_id"), name = "value")
    val authorId: AuthorId = authorId

    @AttributeOverride(column = Column(name = "parent_id"), name = "value")
    var parent: FolderId? = parent
        private set

    fun validateOwnership(authorId: AuthorId) {
        require(this.authorId == authorId) { throw NotOwnershipException() }
    }

    fun changeName(name: FolderName) {
        this.name = name
    }

    fun changeParent(parent: Folder?) {
        require(parent?.entityId != this.entityId) { "자기 자신을 부모로 설정할 수 없습니다." }
        require(parent?.parent != this.entityId) { "상위 폴더를 자식 폴더로 설정할 수 없습니다." }
        this.parent = parent?.entityId
    }

    companion object {
        const val INIT_LIMIT = 100L
        fun init(
            authorId: AuthorId,
            parent: FolderId? = null,
        ): Folder {
            return Folder(
                id = FolderId(IdUtils.idGenerator.generate()),
                name = FolderName.UNTITLED,
                authorId = authorId,
                parent = parent,
            )
        }

        fun from(
            id: FolderId,
            name: FolderName,
            authorId: AuthorId,
            parent: FolderId?,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Folder {
            return Folder(
                id = id,
                name = name,
                authorId = authorId,
                parent = parent,
            ).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }
    }
}
