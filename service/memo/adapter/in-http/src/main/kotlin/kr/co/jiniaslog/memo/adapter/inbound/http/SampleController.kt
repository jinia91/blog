package kr.co.jiniaslog.memo.adapter.inbound.http

import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.usecase.ICommitMemo
import kr.co.jiniaslog.memo.usecase.MemoUseCasesFacade
import kr.co.jiniaslog.shared.core.domain.IdUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController(
    private val memoUseCases: MemoUseCasesFacade,
) {
    @PostMapping("/initMemo")
    fun initMemo(
        @RequestBody request: MemoRequest,
    ) {
        memoUseCases.handle(
            ICommitMemo.Command(
                authorId = AuthorId(1),
                memoId = MemoId(IdUtils.generate()),
                title = MemoTitle(request.title),
                content = MemoContent(request.content),
                linkedList = request.linkedList.map { MemoId(it) },
                tags = listOf(),
            ),
        )
    }
}

data class MemoRequest(
    val title: String,
    val content: String,
    val linkedList: List<Long>,
)
