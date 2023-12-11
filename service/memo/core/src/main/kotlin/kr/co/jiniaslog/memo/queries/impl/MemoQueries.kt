package kr.co.jiniaslog.memo.queries.impl

import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.domain.tag.TagRepository
import kr.co.jiniaslog.memo.queries.IGetAllMemos
import kr.co.jiniaslog.memo.queries.IGetAllTags
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

interface MemoQueriesFacade :
    IGetAllMemos,
    IRecommendRelatedMemo,
    IGetAllTags,
    IGetMemoById

@UseCaseInteractor
internal class MemoQueries(
    private val memoRepository: MemoRepository,
    private val tagRepository: TagRepository,
) : MemoQueriesFacade {
    override fun handle(query: IGetAllMemos.Query): List<IGetAllMemos.Info> {
        return memoRepository.findAll().map {
            IGetAllMemos.Info(
                memoId = it.id,
                title = it.title,
                references = it.references,
            )
        }
    }

    override fun handle(query: IRecommendRelatedMemo.Query): IRecommendRelatedMemo.Info {
        return IRecommendRelatedMemo.Info(
            relatedMemoCandidates =
                memoRepository.findByRelatedMemo(query.query)
                    .filterNot { it.id == query.thisId.value }
                    .take(5)
                    .map { MemoId(it.id) to MemoTitle(it.title) },
        )
    }

    override fun handle(query: IGetAllTags.Query): List<IGetAllTags.Info> {
        return tagRepository.findAll().map { IGetAllTags.Info(tagId = it.id, name = it.name) }
    }

    override fun handle(query: IGetMemoById.Query): IGetMemoById.Info {
        val memo = memoRepository.findById(query.memoId) ?: throw IllegalArgumentException("memo not found")
        return IGetMemoById.Info(
            memoId = memo.id,
            title = memo.title,
            content = memo.content,
            references = memo.references.map { IGetMemoById.Info.Reference(rootId = it.rootId, referenceId = it.referenceId) }.toSet(),
        )
    }
}
