package kr.co.jiniaslog
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
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

    companion object {
        @Container
        @JvmField
        var blogCoreDbContainer = MySQLContainer("mysql:8.0")
            .withCommand("--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci")
            .withDatabaseName("blogcore")

        @Container
        @JvmField
        var userDbContainer = MySQLContainer("mysql:8.0")
            .withCommand("--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci")
            .withDatabaseName("user")

        @Container
        @JvmField
        var idDbContainer = MySQLContainer("mysql:8.0")
            .withCommand("--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci")
            .withDatabaseName("id")


        @DynamicPropertySource
        @JvmStatic
        fun dynamicProperty(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.blogcore.jdbcUrl") { blogCoreDbContainer.jdbcUrl }
            registry.add("spring.datasource.blogcore.driverClassName") { blogCoreDbContainer.driverClassName }
            registry.add("spring.datasource.blogcore.username") { blogCoreDbContainer.username }
            registry.add("spring.datasource.blogcore.password") { blogCoreDbContainer.password }
            registry.add("spring.datasource.blogcore.connection-init-sql") { "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci" }

            registry.add("spring.datasource.user.jdbcUrl") { userDbContainer.jdbcUrl }
            registry.add("spring.datasource.user.driverClassName") { userDbContainer.driverClassName }
            registry.add("spring.datasource.user.username") { userDbContainer.username }
            registry.add("spring.datasource.user.password") { userDbContainer.password }
            registry.add("spring.datasource.user.connection-init-sql") { "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci" }

            registry.add("spring.datasource.id.jdbcUrl") { idDbContainer.jdbcUrl }
            registry.add("spring.datasource.id.driverClassName") { idDbContainer.driverClassName }
            registry.add("spring.datasource.id.username") { idDbContainer.username }
            registry.add("spring.datasource.id.password") { idDbContainer.password }
            registry.add("spring.datasource.id.connection-init-sql") { "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci" }

        }

        init {
            blogCoreDbContainer.start()
            userDbContainer.start()
            idDbContainer.start()
        }

    }
}