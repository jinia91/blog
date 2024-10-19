package kr.co.jiniaslog.memo.adapter.out.neo4j.folder

import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param

internal interface FolderNeo4jRepository : Neo4jRepository<FolderNeo4jEntity, Long> {
    @Query(
        """
        MATCH (parentFolder:folder { id: ${'$'}folderId })
OPTIONAL MATCH (parentFolder)-[:CONTAINS*]->(childFolder:folder)
OPTIONAL MATCH (parentFolder)-[:CONTAINS_MEMO]->(memo1:memo)
OPTIONAL MATCH (childFolder)-[:CONTAINS_MEMO]->(memo2:memo)
DETACH DELETE parentFolder, childFolder, memo1, memo2
    """,
    )
    fun deleteFolderRecursivelyById(
        @Param("folderId") folderId: Long,
    )

    @Query("MATCH (parent:folder)-[r:CONTAINS]->(child:folder) WHERE child.id = ${'$'}folderId detach DELETE r")
    fun deleteRelationshipContainsById(
        @Param("folderId") folderId: Long,
    )

    @Query(
        """
    MATCH (f:folder)-[a:CONTAINS*0..]->(f2:folder)
    WHERE f.authorId = ${'$'}authorId
    WITH f, f2, a
    OPTIONAL MATCH (f)-[b:CONTAINS_MEMO]->(m:memo)
    OPTIONAL MATCH (m)-[c:REFERENCE]->(ref:memo)
RETURN f, collect(f2) AS children, collect(m) AS memos, collect(ref) AS references, collect(a) AS contains, collect(b) AS containsMemo, collect(c) AS reference"""
    )
    fun findAllByAuthorId(@Param("authorId") authorId: Long): List<FolderNeo4jEntity>
}
