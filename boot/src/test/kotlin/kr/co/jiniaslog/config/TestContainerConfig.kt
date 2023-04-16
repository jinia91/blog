package kr.co.jiniaslog.config
import io.restassured.RestAssured
import kr.co.jiniaslog.CustomAutoConfigYmlImportInitializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestExecutionListeners(DependencyInjectionTestExecutionListener::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(initializers = [CustomAutoConfigYmlImportInitializer::class])
class TestContainerConfig {

    @Autowired
    protected lateinit var dbCleaner: DbCleaner

    @Value("\${local.server.port}")
    private lateinit var port: Number

    @BeforeEach
    fun setup() {
        RestAssured.port = port.toInt()
        RestAssured.baseURI = "http://localhost"
    }

    @AfterEach
    fun tearDown() {
        dbCleaner.cleanAll()
    }

    companion object {

        private const val RDB_CHARSET = "--character-set-server=utf8mb4"
        private const val RDB_COLLATION = "--collation-server=utf8mb4_unicode_ci"
        private const val RDB_INIT_SQL = "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci"

        private val blogCoreDbContainer: MySQLContainer<*> = MySQLContainer("mysql:8.0")
            .withCommand(RDB_CHARSET, RDB_COLLATION)
            .withDatabaseName("blogcore")

        private val userDbContainer: MySQLContainer<*> = MySQLContainer("mysql:8.0")
            .withCommand(RDB_CHARSET, RDB_COLLATION)
            .withDatabaseName("user")

        private val idDbContainer: MySQLContainer<*> = MySQLContainer("mysql:8.0")
            .withCommand(RDB_CHARSET, RDB_COLLATION)
            .withDatabaseName("id")

        private val rdbContainers = listOf(blogCoreDbContainer, userDbContainer, idDbContainer)

        @DynamicPropertySource
        @JvmStatic
        fun injectTestProperties(registry: DynamicPropertyRegistry) {
            for (mySQLContainer in rdbContainers) {
                registry.add("spring.datasource.${mySQLContainer.databaseName}.jdbc-url") { mySQLContainer.jdbcUrl }
                registry.add("spring.datasource.${mySQLContainer.databaseName}.driver-class-name") { mySQLContainer.driverClassName }
                registry.add("spring.datasource.${mySQLContainer.databaseName}.username") { mySQLContainer.username }
                registry.add("spring.datasource.${mySQLContainer.databaseName}.password") { mySQLContainer.password }
                registry.add("spring.datasource.${mySQLContainer.databaseName}.connection-init-sql") { RDB_INIT_SQL }
            }
        }

        init {
            rdbContainers.forEach { it.start() }
        }
    }
}