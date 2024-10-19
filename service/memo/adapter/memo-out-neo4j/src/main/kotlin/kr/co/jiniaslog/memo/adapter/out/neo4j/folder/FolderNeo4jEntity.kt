package kr.co.jiniaslog.memo.adapter.out.neo4j.folder

import kr.co.jiniaslog.memo.adapter.out.neo4j.Neo4jAbstractBaseNode
import kr.co.jiniaslog.memo.adapter.out.neo4j.memo.MemoNeo4jEntity
import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderName
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Property
import org.springframework.data.neo4j.core.schema.Relationship
import java.time.LocalDateTime

@Node("folder")
internal class FolderNeo4jEntity(
    @Id
    override val id: Long,
    @Property("name")
    var name: String,
    @Property("authorId")
    val authorId: Long,
    @Relationship(type = "CONTAINS", direction = Relationship.Direction.INCOMING)
    var parent: FolderNeo4jEntity?,
    @Relationship(type = "CONTAINS", direction = Relationship.Direction.OUTGOING)
    var children: MutableSet<FolderNeo4jEntity> = mutableSetOf(),
    @Relationship(type = "CONTAINS_MEMO", direction = Relationship.Direction.OUTGOING)
    var memos: MutableSet<MemoNeo4jEntity> = mutableSetOf(),
    createdAt: LocalDateTime?,
    updatedAt: LocalDateTime?,
) : Neo4jAbstractBaseNode(createdAt, updatedAt) {
    @PersistenceCreator
    constructor(
        id: Long,
        name: String,
        authorId: Long,
        createdAt: LocalDateTime?,
        updatedAt: LocalDateTime?
    ) : this(id, name, authorId, null, mutableSetOf(), mutableSetOf(), createdAt, updatedAt)

    fun toDomain(): Folder {
        return Folder.from(
            id = FolderId(this.id),
            name = FolderName(this.name),
            authorId = AuthorId(this.authorId),
            parent = this.parent?.let { FolderId(it.id) },
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
        )
    }

    override fun toString(): String {
        return "FolderNeo4jEntity(id=$id, name='$name', authorId=$authorId, parent=$parent, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}
