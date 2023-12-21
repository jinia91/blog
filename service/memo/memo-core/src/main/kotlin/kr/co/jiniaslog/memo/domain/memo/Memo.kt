package kr.co.jiniaslog.memo.domain.memo

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.time.LocalDateTime

class Memo private constructor(
    id: MemoId,
    authorId: AuthorId,
    content: MemoContent,
    title: MemoTitle,
    references: MutableSet<MemoReference>,
    memoState: MemoState,
    parentFolderId: FolderId?,
) : AggregateRoot<MemoId>() {
    override val id: MemoId = id

    val authorId: AuthorId = authorId

    var title: MemoTitle = title
        private set

    var content: MemoContent = content
        private set

    private var _references: MutableSet<MemoReference> = references

    val references: Set<MemoReference>
        get() = _references.toSet()

    var state: MemoState = memoState
        private set

    var parentFolderId: FolderId? = parentFolderId
        private set

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
        this._references.add(MemoReference(this.id, referenceId))
    }

    fun removeReference(referenceId: MemoId) {
        this._references.remove(MemoReference(this.id, referenceId))
    }

    fun commit(
        title: MemoTitle,
        content: MemoContent,
    ) {
        update(title, content)
        MemoState.COMMITTED.validate(memo = this)
        this.state = MemoState.COMMITTED
    }

    fun setParentFolder(folderId: FolderId?) {
        this.parentFolderId = folderId
    }

    override fun toString(): String {
        return "Memo(id=$id, authorId=$authorId, title=$title, content=$content, reference=$_references, memoState=$state)"
    }

    companion object {
        fun init(
            title: MemoTitle = MemoTitle(""),
            content: MemoContent = MemoContent(""),
            authorId: AuthorId,
            parentFolderId: FolderId? = null,
            references: Set<MemoId> = setOf(),
        ): Memo {
            val id = MemoId(IdUtils.idGenerator.generate())
            return Memo(
                id = id,
                content = content,
                title = title,
                references = references.map { MemoReference(id, it) }.toMutableSet(),
                authorId = authorId,
                memoState = MemoState.DRAFT,
                parentFolderId = parentFolderId,
            )
        }

        fun from(
            id: MemoId,
            authorId: AuthorId,
            title: MemoTitle,
            content: MemoContent,
            reference: MutableSet<MemoReference>,
            state: MemoState,
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
                memoState = state,
                parentFolderId = parentFolderId,
            ).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }.also {
                it.state.validate(it)
            }
        }
    }
}
