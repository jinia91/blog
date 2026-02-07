package kr.co.jiniaslog.ai.adapter.outbound.memo

import kr.co.jiniaslog.ai.outbound.MemoCommandService
import kr.co.jiniaslog.memo.adapter.inbound.acl.MemoAclInboundAdapter
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter

@PersistenceAdapter
class MemoCommandServiceAdapter(
    private val memoAcl: MemoAclInboundAdapter,
) : MemoCommandService {

    // ============ Memo Operations ============

    override fun createMemo(authorId: Long, title: String, content: String): Long {
        return memoAcl.createMemo(authorId, title, content)
    }

    override fun updateMemo(memoId: Long, requesterId: Long, title: String?, content: String?): Long {
        return memoAcl.updateMemo(memoId, requesterId, title, content)
    }

    override fun deleteMemo(memoId: Long, requesterId: Long) {
        memoAcl.deleteMemo(memoId, requesterId)
    }

    override fun linkMemoToFolder(memoId: Long, folderId: Long?, requesterId: Long) {
        memoAcl.linkMemoToFolder(memoId, folderId, requesterId)
    }

    // ============ Folder Operations ============

    override fun createFolder(authorId: Long, name: String, parentFolderId: Long?): Long {
        return memoAcl.createFolder(authorId, name, parentFolderId)
    }

    override fun renameFolder(folderId: Long, requesterId: Long, name: String) {
        memoAcl.renameFolder(folderId, requesterId, name)
    }

    override fun deleteFolder(folderId: Long, requesterId: Long) {
        memoAcl.deleteFolder(folderId, requesterId)
    }

    override fun linkFolderToParent(childFolderId: Long, parentFolderId: Long?, requesterId: Long) {
        memoAcl.linkFolderToParent(childFolderId, parentFolderId, requesterId)
    }

    // ============ Query Operations ============

    override fun getMemoById(memoId: Long): MemoCommandService.MemoInfo? {
        return memoAcl.getMemoById(memoId)?.let {
            MemoCommandService.MemoInfo(
                id = it.id,
                title = it.title,
                content = it.content,
                folderId = it.folderId,
            )
        }
    }

    override fun getAllMemos(authorId: Long): List<MemoCommandService.MemoInfo> {
        return memoAcl.getAllMemosByAuthorId(authorId).map {
            MemoCommandService.MemoInfo(
                id = it.id,
                title = it.title,
                content = it.content,
                folderId = it.folderId,
            )
        }
    }

    override fun getAllFolders(authorId: Long): List<MemoCommandService.FolderInfo> {
        return memoAcl.getAllFoldersByAuthorId(authorId).map {
            MemoCommandService.FolderInfo(
                id = it.id,
                name = it.name,
                parentFolderId = it.parentFolderId,
            )
        }
    }
}
