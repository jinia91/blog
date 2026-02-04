package kr.co.jiniaslog.memo.adapter.inbound.acl

import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.queries.ICheckMemoExisted
import kr.co.jiniaslog.memo.queries.IGetAllMemosByAuthorId
import kr.co.jiniaslog.memo.queries.IGetMemoById
import kr.co.jiniaslog.memo.queries.MemoQueriesFacade
import kr.co.jiniaslog.memo.usecase.IInitMemo
import kr.co.jiniaslog.memo.usecase.IUpdateMemoContents
import kr.co.jiniaslog.memo.usecase.MemoUseCasesFacade
import org.springframework.stereotype.Controller

@Controller
class MemoAclInboundAdapter(
    private val memoQueries: MemoQueriesFacade,
    private val memoUseCases: MemoUseCasesFacade,
) {
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
            )
        }
    }

    fun createMemo(authorId: Long, title: String, content: String): Long {
        // 1. 빈 메모 초기화
        val initInfo = memoUseCases.handle(
            IInitMemo.Command(
                authorId = AuthorId(authorId),
                parentFolderId = null,
            )
        )

        // 2. 메모 내용 업데이트
        memoUseCases.handle(
            IUpdateMemoContents.Command(
                memoId = initInfo.id,
                title = MemoTitle(title),
                content = MemoContent(content),
            )
        )

        return initInfo.id.value
    }

    data class MemoAclInfo(
        val id: Long,
        val authorId: Long,
        val title: String,
        val content: String,
    )
}
