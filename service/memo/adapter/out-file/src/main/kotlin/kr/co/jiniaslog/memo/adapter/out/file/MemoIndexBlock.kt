package kr.co.jiniaslog.memo.adapter.out.file

import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.domain.tag.TagId
import kr.co.jiniaslog.memo.domain.tag.TagName

class MemoIndexBlock(
    val id: Long,
    val title: String,
    val content: String,
    val links: List<String>,
    val tags: List<String>,
)

fun Memo.toIndexBlock(
    tagNames: Map<TagId, TagName>,
    linksNames: Map<MemoId, MemoTitle>,
): MemoIndexBlock {
    return MemoIndexBlock(
        id = this.id.value,
        title = this.title.value,
        content = this.content.value,
        links = this.links.map { linksNames[it.linkedMemoId]!!.value },
        tags = this.tags.map { tagNames[it.tagId]!!.value },
    )
}
