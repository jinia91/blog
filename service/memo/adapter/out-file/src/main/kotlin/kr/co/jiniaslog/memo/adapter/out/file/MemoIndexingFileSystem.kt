package kr.co.jiniaslog.memo.adapter.out.file

import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.shared.core.annotation.CustomComponent
import org.apache.lucene.analysis.ko.KoreanAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.MMapDirectory
import java.nio.file.Paths

@CustomComponent
class MemoIndexingFileSystem {
    private val analyzer = KoreanAnalyzer()
    private val indexStorage = MMapDirectory(Paths.get(INDEX_PATH))

    fun saveIndex(memoIndexBlock: MemoIndexBlock) {
        val config = IndexWriterConfig(analyzer)
        IndexWriter(indexStorage, config).use { writer ->
            val doc =
                Document().apply {
                    add(Field("id", memoIndexBlock.id.toString(), StringField.TYPE_STORED))
                    add(Field("title", memoIndexBlock.title, StringField.TYPE_STORED))
                    add(Field("content", memoIndexBlock.content, TextField.TYPE_NOT_STORED))
                    add(Field("links", memoIndexBlock.links.joinToString(","), TextField.TYPE_NOT_STORED))
                    add(Field("tags", memoIndexBlock.tags.joinToString(","), TextField.TYPE_NOT_STORED))
                }
            writer.addDocument(doc)
        }
    }

    fun searchRelatedMemos(
        queryStr: String,
        numResults: Int,
    ): List<Pair<MemoId, MemoTitle>> {
        DirectoryReader.open(indexStorage).use { reader ->
            val searcher = IndexSearcher(reader)
            val query = QueryParser("content", analyzer).parse(queryStr)
            val hits = searcher.search(query, numResults).scoreDocs
            val results =
                hits.map { hit ->
                    Pair(
                        searcher.doc(hit.doc).get("id").let { MemoId(it.toLong()) },
                        searcher.doc(hit.doc).get("title").let { MemoTitle(it) },
                    )
                }
            return results
        }
    }

    companion object {
        private const val INDEX_PATH = "memo/index"
    }
}
