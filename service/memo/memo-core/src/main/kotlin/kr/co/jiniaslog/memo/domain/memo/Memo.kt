package kr.co.jiniaslog.memo.domain.memo

import jakarta.persistence.AttributeOverride
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import kr.co.jiniaslog.memo.domain.exception.NotOwnershipException
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.shared.adapter.out.rdb.JpaAggregate
import kr.co.jiniaslog.shared.core.domain.IdUtils
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.LocalDateTime

@Entity
@Table(
    name = "memo",
    indexes = [
        Index(name = "idx_memo_author_id", columnList = "author_id"),
        Index(name = "idx_memo_parent_folder_id", columnList = "parent_folder_id"),
    ]
)
class Memo private constructor(
    id: MemoId,
    authorId: AuthorId,
    title: MemoTitle,
    content: MemoContent,
    references: MutableSet<MemoReference>,
    parentFolderId: FolderId?,
) : JpaAggregate<MemoId>() {
    @EmbeddedId
    @AttributeOverride(column = Column(name = "id"), name = "value")
    override val entityId: MemoId = id

    @AttributeOverride(column = Column(name = "author_id"), name = "value")
    val authorId: AuthorId = authorId

    @AttributeOverride(column = Column(name = "title"), name = "value")
    var title: MemoTitle = title
        private set

    @AttributeOverride(
        column = Column(name = "content", columnDefinition = "TEXT"),
        name = "value"
    )
    var content: MemoContent = content
        private set

    @ElementCollection
    @CollectionTable(name = "memo_reference")
    @Fetch(FetchMode.JOIN)
    private var _references: MutableSet<MemoReference> = references

    @AttributeOverride(column = Column(name = "parent_folder_id"), name = "value")
    var parentFolderId: FolderId? = parentFolderId
        private set

    fun getReferences(): Set<MemoReference> {
        return _references
    }

    fun validateOwnership(requesterId: AuthorId) {
        require(this.authorId == requesterId) { throw NotOwnershipException() }
    }

    fun update(
        title: MemoTitle? = null,
        content: MemoContent? = null,
    ) {
        title?.let {
            require(it.value.isNotEmpty()) { "title must not be empty" }
            this.title = it
        }
        content?.let {
            require(it.value.isNotEmpty()) { "content must not be empty" }
            this.content = it
        }
    }

    fun addReference(referenceId: MemoId) {
        this._references.add(MemoReference(this.entityId, referenceId))
    }

    fun removeReference(referenceId: MemoId) {
        this._references.remove(MemoReference(this.entityId, referenceId))
    }

    fun setParentFolder(folderId: FolderId?) {
        this.parentFolderId = folderId
    }

    override fun toString(): String {
        return "Memo(id=$entityId, authorId=$authorId, title=$title, content=$content, reference=$_references)"
    }

    fun updateReferences(references: Set<MemoId>) {
        this._references = references.map { MemoReference(this.entityId, it) }.toMutableSet()
    }

    companion object {
        const val INIT_LIMIT = 1000L
        fun init(
            authorId: AuthorId,
            parentFolderId: FolderId?,
        ): Memo {
            return Memo(
                id = MemoId(IdUtils.idGenerator.generate()),
                content = MemoContent.EMPTY,
                title = MemoTitle.UNTITLED,
                references = mutableSetOf(),
                authorId = authorId,
                parentFolderId = parentFolderId,
            )
        }

        fun from(
            id: MemoId,
            authorId: AuthorId,
            title: MemoTitle,
            content: MemoContent,
            reference: MutableSet<MemoReference>,
            parentFolderId: FolderId?,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Memo {
            return Memo(
                id = id,
                authorId = authorId,
                content = content,
                title = title,
                references = reference,
                parentFolderId = parentFolderId,
            ).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }
    }
}
