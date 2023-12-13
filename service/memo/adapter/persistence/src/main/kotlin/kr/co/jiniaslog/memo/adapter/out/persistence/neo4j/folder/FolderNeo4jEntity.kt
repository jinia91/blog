package kr.co.jiniaslog.memo.adapter.out.persistence.neo4j.folder

import kr.co.jiniaslog.memo.adapter.out.persistence.neo4j.memo.MemoNeo4jEntity
import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderName
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoId
import org.springframework.data.domain.Persistable
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Property
import org.springframework.data.neo4j.core.schema.Relationship

@Node("folder")
class FolderNeo4jEntity(
    @Id
    val id: Long,
    @Property("name")
    var name: String,
    @Property("authorId")
    val authorId: Long,
    @Property("parent")
    var parent: FolderNeo4jEntity?,
    @Property("children")
    @Relationship(type = "CONTAINS", direction = Relationship.Direction.OUTGOING)
    val children: MutableSet<FolderNeo4jEntity>,
    @Property("memos")
    @Relationship(type = "CONTAINS", direction = Relationship.Direction.OUTGOING)
    val memos: MutableSet<MemoNeo4jEntity>,
) : Persistable<Long> {
    override fun isNew(): Boolean = true

    override fun getId(): Long = id

    fun toDomain(): Folder {
        return Folder.from(
            id = FolderId(this.id),
            name = FolderName(this.name),
            authorId = AuthorId(this.authorId),
            parent = this.parent?.let { FolderId(it.id) },
            children = this.children.map { FolderId(it.id) }.toMutableSet(),
            memos = this.memos.map { MemoId(it.id) }.toMutableSet(),
        )
    }
}
