package kr.co.jiniaslog.memo.adapter.inbound.acl

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderName
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.queries.FolderQueriesFacade
import kr.co.jiniaslog.memo.queries.ICheckMemoExisted
import kr.co.jiniaslog.memo.queries.IGetAllMemosByAuthorId
import kr.co.jiniaslog.memo.queries.IGetFoldersAllInHierirchyByAuthorId
import kr.co.jiniaslog.memo.queries.IGetMemoById
import kr.co.jiniaslog.memo.queries.MemoQueriesFacade
import kr.co.jiniaslog.memo.usecase.FolderUseCasesFacade
import kr.co.jiniaslog.memo.usecase.IChangeFolderName
import kr.co.jiniaslog.memo.usecase.ICreateMemoWithContent
import kr.co.jiniaslog.memo.usecase.ICreateNewFolder
import kr.co.jiniaslog.memo.usecase.IDeleteFoldersRecursively
import kr.co.jiniaslog.memo.usecase.IDeleteMemo
import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndFolder
import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndMemo
import kr.co.jiniaslog.memo.usecase.IUpdateMemoContents
import kr.co.jiniaslog.memo.usecase.MemoUseCasesFacade
import org.springframework.stereotype.Controller

@Controller
class MemoAclInboundAdapter(
    private val memoQueries: MemoQueriesFacade,
    private val memoUseCases: MemoUseCasesFacade,
    private val folderQueries: FolderQueriesFacade,
    private val folderUseCases: FolderUseCasesFacade,
) {
    // ============ Memo Queries ============

    fun isExistMemo(id: Long): Boolean {
        return memoQueries.handle(
            ICheckMemoExisted.Query(MemoId(id)),
        )
    }

    fun getMemoById(memoId: Long): MemoAclInfo? {
        return try {
            val info = memoQueries.handle(
                IGetMemoById.Query(
                    memoId = MemoId(memoId),
                    requesterId = AuthorId(0L), // ACL에서는 requesterId 검증을 하지 않음
                )
            )
            MemoAclInfo(
                id = info.memoId.value,
                authorId = 0L, // IGetMemoById.Info에 authorId가 없으므로 기본값
                title = info.title.value,
                content = info.content.value,
                folderId = null, // IGetMemoById.Info에 parentFolderId가 없음
            )
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun getAllMemosByAuthorId(authorId: Long): List<MemoAclInfo> {
        return memoQueries.handle(IGetAllMemosByAuthorId.Query(AuthorId(authorId))).map { info ->
            MemoAclInfo(
                id = info.id,
                authorId = info.authorId,
                title = info.title,
                content = info.content,
                folderId = null, // 이 쿼리에서는 폴더 정보를 제공하지 않음
            )
        }
    }

    // ============ Memo Commands ============

    fun createMemo(authorId: Long, title: String, content: String): Long {
        return memoUseCases.handle(
            ICreateMemoWithContent.Command(
                authorId = AuthorId(authorId),
                title = MemoTitle(title),
                content = MemoContent(content),
            )
        ).memoId.value
    }

    fun updateMemo(memoId: Long, requesterId: Long, title: String?, content: String?): Long {
        return memoUseCases.handle(
            IUpdateMemoContents.Command(
                memoId = MemoId(memoId),
                title = title?.let { MemoTitle(it) },
                content = content?.let { MemoContent(it) },
            )
        ).id.value
    }

    fun deleteMemo(memoId: Long, requesterId: Long) {
        memoUseCases.handle(
            IDeleteMemo.Command(
                id = MemoId(memoId),
                requesterId = AuthorId(requesterId),
            )
        )
    }

    fun linkMemoToFolder(memoId: Long, folderId: Long?, requesterId: Long) {
        memoUseCases.handle(
            IMakeRelationShipFolderAndMemo.Command(
                memoId = MemoId(memoId),
                folderId = folderId?.let { FolderId(it) },
                requesterId = AuthorId(requesterId),
            )
        )
    }

    // ============ Folder Queries ============

    fun getAllFoldersByAuthorId(authorId: Long): List<FolderAclInfo> {
        val result = folderQueries.handle(
            IGetFoldersAllInHierirchyByAuthorId.Query(
                requesterId = AuthorId(authorId),
            )
        )
        return flattenFolders(result.folderInfos)
    }

    private fun flattenFolders(
        folders: List<IGetFoldersAllInHierirchyByAuthorId.FolderInfo>,
        parentId: Long? = null
    ): List<FolderAclInfo> {
        val result = mutableListOf<FolderAclInfo>()
        for (folder in folders) {
            folder.id?.let { id ->
                result.add(
                    FolderAclInfo(
                        id = id,
                        name = folder.name ?: "",
                        parentFolderId = parentId,
                    )
                )
                result.addAll(flattenFolders(folder.children, id))
            }
        }
        return result
    }

    // ============ Folder Commands ============

    fun createFolder(authorId: Long, name: String, parentFolderId: Long?): Long {
        val result = folderUseCases.handle(
            ICreateNewFolder.Command(
                authorId = AuthorId(authorId),
                parentId = parentFolderId?.let { FolderId(it) },
            )
        )
        // 폴더 생성 후 이름 변경
        if (name.isNotBlank()) {
            folderUseCases.handle(
                IChangeFolderName.Command(
                    folderId = result.id,
                    name = FolderName(name),
                    requesterId = AuthorId(authorId),
                )
            )
        }
        return result.id.value
    }

    fun renameFolder(folderId: Long, requesterId: Long, name: String) {
        folderUseCases.handle(
            IChangeFolderName.Command(
                folderId = FolderId(folderId),
                name = FolderName(name),
                requesterId = AuthorId(requesterId),
            )
        )
    }

    fun deleteFolder(folderId: Long, requesterId: Long) {
        folderUseCases.handle(
            IDeleteFoldersRecursively.Command(
                folderId = FolderId(folderId),
                requesterId = AuthorId(requesterId),
            )
        )
    }

    fun linkFolderToParent(childFolderId: Long, parentFolderId: Long?, requesterId: Long) {
        folderUseCases.handle(
            IMakeRelationShipFolderAndFolder.Command(
                parentFolderId = parentFolderId?.let { FolderId(it) },
                childFolderId = FolderId(childFolderId),
                requesterId = AuthorId(requesterId),
            )
        )
    }

    // ============ Data Classes ============

    data class MemoAclInfo(
        val id: Long,
        val authorId: Long,
        val title: String,
        val content: String,
        val folderId: Long?,
    )

    data class FolderAclInfo(
        val id: Long,
        val name: String,
        val parentFolderId: Long?,
    )
}
