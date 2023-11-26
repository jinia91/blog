package kr.co.jiniaslog.memo.domain.memo

interface MemoIndexStorage {
    fun deleteIndex(memo: Memo)

    fun updateIndex(memo: Memo)

    fun searchRelatedMemo(query: String): List<Pair<MemoId, MemoTitle>>

    fun saveIndex(memo: Memo)
}
