package kr.co.jiniaslog.memo.adapter.out.persistence.neo4j.folder

import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param

interface FolderNeo4jRepository : Neo4jRepository<FolderNeo4jEntity, Long> {
    @Query("MATCH (parent:Folder {id: \$folderId}) DETACH DELETE parent")
    fun deleteFolderAndContentsById(
        @Param("folderId") folderId: Long,
    )
}
