package kr.co.jiniaslog.utils

import org.springframework.data.neo4j.core.Neo4jClient
import org.springframework.stereotype.Component

@Component
class Neo4jDbCleaner(private val neo4jClient: Neo4jClient) : DbCleaner {

    override fun tearDownAll() {
        neo4jClient.query("MATCH (n) DETACH DELETE n").run()
    }
}
