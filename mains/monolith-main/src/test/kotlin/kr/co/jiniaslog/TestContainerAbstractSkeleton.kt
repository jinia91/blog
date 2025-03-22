package kr.co.jiniaslog

import com.redis.testcontainers.RedisContainer
import io.mockk.mockk
import io.restassured.RestAssured
import kr.co.jiniaslog.media.outbound.ImageUploader
import kr.co.jiniaslog.utils.CacheCleaner
import kr.co.jiniaslog.utils.MySqlRdbCleaner
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@TestConfiguration
class ContextWithTestContainerConfig {
    @Bean
    @Primary
    fun imageUploader(): ImageUploader {
        return mockk()
    }
}

/**
 * 통합 테스트 공용 세팅을 위한 최상위 부모 조상 클래스
 *
 * 테스트 컨테이너 graceful shutdown은 지나치게 오래걸리기때문에 한번 수행후 재사용을 위해 reuse 방식으로 세팅
 *
 * vim ~/.testcontainers.properties
 *
 * testcontainers.reuse.enable=true
 *
 * 위의 테스트 컨테이너 환경세팅이 필요하다
 *
 */
@Testcontainers
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [ContextWithTestContainerConfig::class],
)
abstract class TestContainerAbstractSkeleton {
    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    protected lateinit var mySqlRdbCleaner: MySqlRdbCleaner

    @Autowired
    protected lateinit var cacheCleaner: CacheCleaner

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @AfterEach
    fun tearDown() {
        mySqlRdbCleaner.tearDownAll()
        cacheCleaner.burst()
    }

    @Test
    fun contextLoads() {
    }

    companion object {
        private const val RDB_CHARSET = "--character-set-server=utf8mb4"
        private const val RDB_COLLATION = "--collation-server=utf8mb4_unicode_ci"
        private const val RDB_INIT_SQL = "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci"

        @JvmStatic
        val userDb: MySQLContainer<*> = MySQLContainer("mysql:8.0")
            .withCommand(RDB_CHARSET, RDB_COLLATION)
            .withDatabaseName("jiniaslog_user")
            .withReuse(true)

        @JvmStatic
        val blogDb: MySQLContainer<*> =
            MySQLContainer("mysql:8.0")
                .withCommand(RDB_CHARSET, RDB_COLLATION)
                .withDatabaseName("jiniaslog_blog")
                .withReuse(true)

        @JvmStatic
        val memoDb: MySQLContainer<*> =
            MySQLContainer("mysql:8.0")
                .withCommand(RDB_CHARSET, RDB_COLLATION)
                .withDatabaseName("jiniaslog_memo")
                .withReuse(true)

        @JvmStatic
        val redis: RedisContainer = RedisContainer("redis:7.0")
            .withReuse(true)

        init {
            userDb.start()
            blogDb.start()
            memoDb.start()
            redis.start()
        }

        @DynamicPropertySource
        @JvmStatic
        fun testProperty(registry: DynamicPropertyRegistry) {
            // user db
            registry.add("spring.datasource.user.jdbc-url") { userDb.jdbcUrl }
            registry.add("spring.datasource.user.username") { userDb.username }
            registry.add("spring.datasource.user.password") { userDb.password }
            registry.add("spring.datasource.user.driver-class-name") { userDb.driverClassName }
            registry.add("spring.datasource.user.connection-init-sql") { RDB_INIT_SQL }

            // blog db
            registry.add("spring.datasource.blog.jdbc-url") { blogDb.jdbcUrl }
            registry.add("spring.datasource.blog.username") { blogDb.username }
            registry.add("spring.datasource.blog.password") { blogDb.password }
            registry.add("spring.datasource.blog.driver-class-name") { blogDb.driverClassName }
            registry.add("spring.datasource.blog.connection-init-sql") { RDB_INIT_SQL }

            // memo db
            registry.add("spring.datasource.memo.jdbc-url") { memoDb.jdbcUrl }
            registry.add("spring.datasource.memo.username") { memoDb.username }
            registry.add("spring.datasource.memo.password") { memoDb.password }
            registry.add("spring.datasource.memo.driver-class-name") { memoDb.driverClassName }
            registry.add("spring.datasource.memo.connection-init-sql") { RDB_INIT_SQL }

            // redis
            registry.add("spring.data.redis.host") { redis.host }
            registry.add("spring.data.redis.port") { redis.redisPort }
        }
    }
}
