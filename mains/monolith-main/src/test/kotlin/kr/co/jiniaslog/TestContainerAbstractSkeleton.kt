package kr.co.jiniaslog

import io.restassured.RestAssured
import kr.co.jiniaslog.memo.Neo4jDbCleaner
import kr.co.jiniaslog.utils.DbCleaner
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class TestContainerAbstractSkeleton {
    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    protected lateinit var neo4jDbCleaner: Neo4jDbCleaner

    @Autowired
    protected lateinit var rdbDbCleaner: DbCleaner

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @AfterEach
    fun tearDown() {
        neo4jDbCleaner.tearDown()
        rdbDbCleaner.tearDownAll()
    }

    companion object {
        private const val RDB_CHARSET = "--character-set-server=utf8mb4"
        private const val RDB_COLLATION = "--collation-server=utf8mb4_unicode_ci"
        private const val RDB_INIT_SQL = "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci"

        @Container
        var neo4j = Neo4jContainer("neo4j:5")

        @Container
        var userDb: MySQLContainer<*> =
            MySQLContainer("mysql:8.0")
                .withCommand(RDB_CHARSET, RDB_COLLATION)
                .withDatabaseName("user")

        @DynamicPropertySource
        @JvmStatic
        fun neo4jProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.neo4j.uri") { neo4j.boltUrl }
            registry.add("spring.neo4j.authentication.username") { "neo4j" }
            registry.add("spring.neo4j.authentication.password") { "password" }
            registry.add("spring.datasource.user.jdbc-url") { userDb.jdbcUrl }
            registry.add("spring.datasource.user.username") { userDb.username }
            registry.add("spring.datasource.user.password") { userDb.password }
            registry.add("spring.datasource.user.driver-class-name") { userDb.driverClassName }
            registry.add("spring.datasource.user.connection-init-sql") { RDB_INIT_SQL }
        }
    }
}