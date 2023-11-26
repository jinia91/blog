package kr.co.jiniaslog.memo.adapter.out.file

import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoIndexStorage
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.domain.tag.TagRepository
import kr.co.jiniaslog.shared.core.annotation.CustomComponent

@CustomComponent
class MemoIndexStorageAdapter(
    private val memoIndexingFileSystem: MemoIndexingFileSystem,
    private val tagRepository: TagRepository,
    private val memoRepository: MemoRepository,
) : MemoIndexStorage {
    override fun saveIndex(memo: Memo) {
        val tagNames =
            memo.tags.map {
                tagRepository.findById(it.tagId) ?: throw IllegalArgumentException("tag not found")
            }.associateBy({ it.id }, { it.name })
        val linksNames =
            memo.links.map {
                memoRepository.findById(it.linkedMemoId) ?: throw IllegalArgumentException("memo not found")
            }.associateBy({ it.id }, { it.title })
        memoIndexingFileSystem.saveIndex(memo.toIndexBlock(tagNames, linksNames))
    }

    override fun deleteIndex(memo: Memo) {
        TODO("Not yet implemented")
    }

    override fun updateIndex(memo: Memo) {
        TODO("Not yet implemented")
    }

    override fun searchRelatedMemo(query: String): List<Pair<MemoId, MemoTitle>> {
        return memoIndexingFileSystem.searchRelatedMemos(query, 10)
    }
}
