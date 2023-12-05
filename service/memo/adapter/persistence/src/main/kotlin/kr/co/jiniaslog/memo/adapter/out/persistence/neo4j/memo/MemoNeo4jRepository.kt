package kr.co.jiniaslog.memo.adapter.out.persistence.neo4j.memo

import org.springframework.data.neo4j.repository.Neo4jRepository

interface MemoNeo4jRepository : Neo4jRepository<MemoNeo4jEntity, Long>
