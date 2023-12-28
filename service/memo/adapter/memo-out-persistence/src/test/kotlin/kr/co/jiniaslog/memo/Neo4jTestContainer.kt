package kr.co.jiniaslog.memo

import kr.co.jiniaslog.App
import kr.co.jiniaslog.memo.outbound.FolderRepository
import kr.co.jiniaslog.memo.outbound.MemoRepository
import kr.co.jiniaslog.memo.usecase.AbstractFolderUseCaseTests
import kr.co.jiniaslog.memo.usecase.MemoAbstractUseCaseTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [App::class])
class Neo4jTestContainer {
    @Autowired
    protected lateinit var neo4jDbCleaner: Neo4jDbCleaner

    @AfterEach
    fun tearDown() {
        neo4jDbCleaner.tearDown()
    }

    @Nested
    inner class MemoUseCaseNeo4jTest
        @Autowired
        constructor(
            memoRepository: MemoRepository,
            folderRepository: FolderRepository,
        ) : MemoAbstractUseCaseTest(
                memoRepository = memoRepository,
                folderRepository = folderRepository,
            )

    @Nested
    inner class FolderUseCaseNeo4jTest
        @Autowired
        constructor(
            folderRepository: FolderRepository,
        ) : AbstractFolderUseCaseTests(
                folderRepository = folderRepository,
            )

    companion object {
        @Container
        var neo4j = Neo4jContainer("neo4j:5")

        @DynamicPropertySource
        @JvmStatic
        fun neo4jProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.neo4j.uri") { neo4j.boltUrl }
            registry.add("spring.neo4j.authentication.username") { "neo4j" }
            registry.add("spring.neo4j.authentication.password") { "password" }
        }
    }
}
