package kr.co.jiniaslog
import kr.co.jiniaslog.blogcore.adapter.persistence.DbCleaner
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
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
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestExecutionListeners(DependencyInjectionTestExecutionListener::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(initializers = [CustomAutoConfigYmlImportInitializer::class])
class TestContainerConfig {

    @Autowired
    protected lateinit var dbCleaner: DbCleaner

    @AfterEach
    fun tearDown() {
        dbCleaner.cleanAll()
    }


    companion object {

        private const val RDB_CHARSET = "--character-set-server=utf8mb4"
        private const val RDB_COLLATION = "--collation-server=utf8mb4_unicode_ci"
        private const val RDB_INIT_SQL = "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci"

        @Container
        @JvmField
        val blogCoreDbContainer: MySQLContainer<*> = MySQLContainer("mysql:8.0")
            .withCommand(RDB_CHARSET, RDB_COLLATION)
            .withDatabaseName("blogcore")

        @Container
        @JvmField
        val userDbContainer: MySQLContainer<*> = MySQLContainer("mysql:8.0")
            .withCommand(RDB_CHARSET, RDB_COLLATION)
            .withDatabaseName("user")

        @Container
        @JvmField
        val idDbContainer: MySQLContainer<*> = MySQLContainer("mysql:8.0")
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