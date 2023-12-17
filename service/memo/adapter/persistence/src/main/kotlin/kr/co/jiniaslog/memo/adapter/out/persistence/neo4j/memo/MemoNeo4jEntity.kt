package kr.co.jiniaslog.memo.adapter.out.persistence.neo4j.memo

import kr.co.jiniaslog.memo.adapter.out.persistence.neo4j.Neo4jAbstractBaseNode
import kr.co.jiniaslog.memo.adapter.out.persistence.neo4j.folder.FolderNeo4jEntity
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoReference
import kr.co.jiniaslog.memo.domain.memo.MemoState
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Property
import org.springframework.data.neo4j.core.schema.Relationship
import java.time.LocalDateTime

@Node("memo")
class MemoNeo4jEntity(
    @Id
    override val id: Long,
    @Property("authorId")
    val authorId: Long,
    @Property("title")
    var title: String,
    @Property("content")
    var content: String,
    @Relationship(type = "references", direction = Relationship.Direction.OUTGOING)
    val references: Set<MemoNeo4jEntity>,
    @Property("state")
    var state: MemoState,
    @Property("parentFolder")
    @Relationship(type = "CONTAINS_MEMO", direction = Relationship.Direction.INCOMING)
    var parentFolder: FolderNeo4jEntity?,
    createdAt: LocalDateTime?,
    updatedAt: LocalDateTime?,
) : Neo4jAbstractBaseNode(createdAt, updatedAt) {
    fun toDomain(): Memo {
        return Memo.from(
            id = MemoId(this.id),
            authorId = AuthorId(this.authorId),
            title = MemoTitle(this.title),
            content = MemoContent(this.content),
            reference = this.references.map { MemoReference(MemoId(this.id), MemoId(it.id)) }.toMutableSet(),
            state = this.state,
            parentFolderId = this.parentFolder?.let { FolderId(it.id) },
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
        )
    }
}