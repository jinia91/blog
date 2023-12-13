package kr.co.jiniaslog.memo.domain.folder

import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.shared.core.domain.AggregateRoot

open class Folder(
    override val id: FolderId,
    open val name: FolderName,
    open val authorId: AuthorId,
    open val parent: FolderId?,
    open val children: MutableSet<FolderId>,
    open val memos: MutableSet<MemoId>,
) : AggregateRoot<FolderId>() {
    companion object {
        fun from(
            id: FolderId,
            name: FolderName,
            authorId: AuthorId,
            parent: FolderId?,
            children: MutableSet<FolderId>,
            memos: MutableSet<MemoId>,
        ): Folder {
            return Folder(
                id = id,
                name = name,
                authorId = authorId,
                parent = parent,
                children = children,
                memos = memos,
            )
        }
    }
}
