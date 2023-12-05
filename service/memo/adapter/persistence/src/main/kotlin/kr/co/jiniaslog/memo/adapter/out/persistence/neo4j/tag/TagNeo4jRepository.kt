package kr.co.jiniaslog.memo.adapter.out.persistence.neo4j.tag

import org.springframework.data.neo4j.repository.Neo4jRepository

interface TagNeo4jRepository : Neo4jRepository<TagNeo4jEntity, Long> {
    fun findByName(name: String): TagNeo4jEntity?
}
