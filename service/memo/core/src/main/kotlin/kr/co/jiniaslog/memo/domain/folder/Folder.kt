package kr.co.jiniaslog.memo.domain.folder

import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import kr.co.jiniaslog.shared.core.domain.IdUtils

open class Folder(
    id: FolderId,
    name: FolderName,
    authorId: AuthorId,
    parent: FolderId?,
) : AggregateRoot<FolderId>() {
    override val id: FolderId = id

    var name: FolderName = name
        private set

    val authorId: AuthorId = authorId

    var parent: FolderId? = parent
        private set

    fun changeName(name: FolderName) {
        this.name = name
    }

    fun changeParent(parent: Folder?) {
        if (parent?.parent == this.id) {
            throw IllegalArgumentException("이미 두 폴더간 상하관계가 존재합니다.")
        }
        this.parent = parent?.id
    }

    companion object {
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
        ): Folder {
            return Folder(
                id = id,
                name = name,
                authorId = authorId,
                parent = parent,
            )
        }
    }
}
