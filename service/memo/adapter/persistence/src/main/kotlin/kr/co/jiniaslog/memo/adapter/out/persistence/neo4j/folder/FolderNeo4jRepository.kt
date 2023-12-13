package kr.co.jiniaslog.memo.adapter.out.persistence.neo4j.folder

import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param

interface FolderNeo4jRepository : Neo4jRepository<FolderNeo4jEntity, Long> {
    @Query("MATCH (parent:Folder {id: ${'$'}folderId}) DETACH DELETE parent")
    fun deleteFolderAndContentsById(
        @Param("folderId") folderId: Long,
    )

    @Query(
        """
    MATCH (f:Folder)-[:CONTAINS]->(child:Folder), 
          (f)<-[:CONTAINS]-(parent:Folder),
          (f)-[:CONTAINS]->(m:Memo)
    WHERE f.id =${'$'}folderId
    RETURN f, collect(child) as children, parent, collect(m) as memos
""",
    )
    fun findByIdWithRelations(folderId: Long): FolderNeo4jEntity?
}
