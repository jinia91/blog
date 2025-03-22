package kr.co.jiniaslog.memo.usecase

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.memo.adapter.outbound.mysql.config.MemoDb
import kr.co.jiniaslog.memo.domain.FolderTestFixtures
import kr.co.jiniaslog.memo.domain.MemoTestFixtures
import kr.co.jiniaslog.memo.domain.folder.FolderRepository
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.user.application.infra.UserRepository
import kr.co.jiniaslog.user.domain.user.Email
import kr.co.jiniaslog.user.domain.user.NickName
import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.User
import kr.co.jiniaslog.user.domain.user.UserId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import java.time.LocalDateTime
import javax.sql.DataSource

class IDeleteAllWithoutAdminTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var sut: IDeleteAllWithoutAdmin

    @Autowired
    lateinit var folderRepository: FolderRepository

    @Autowired
    lateinit var memoRepository: MemoRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    @Qualifier(MemoDb.DATASOURCE)
    lateinit var dataSource: DataSource

    fun createDummyData(user: User) {
        val dummyFolder = folderRepository.save(
            FolderTestFixtures.build(
                authorId = AuthorId(user.entityId.value),
            )
        )
        val childFolder = folderRepository.save(
            FolderTestFixtures.build(
                parent = dummyFolder.entityId,
                authorId = AuthorId(user.entityId.value)

            )
        )
        val memos = (1..5).map {
            memoRepository.save(
                MemoTestFixtures.build(
                    parentFolderId = dummyFolder.entityId,
                    authorId = AuthorId(user.entityId.value)
                )
            )
        }
        memos[2].addReference(memos[3].entityId)
        memoRepository.save(memos[2])
    }

    @Test
    fun `모든 일반 유저의 폴더, 메모, 메모 참조를 삭제한다`() {
        // given
        val user1 = userRepository.save(User.newOne(NickName("user1"), Email("test@email.com"), null))
        val user2 = userRepository.save(User.newOne(NickName("user2"), Email("test2@email.com"), null))

        withClue("유저 1 데이터") { createDummyData(user1) }
        withClue("유저 2 데이터") { createDummyData(user2) }

        // when
        sut.handle(IDeleteAllWithoutAdmin.Command())

        // then
        dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT COUNT(*) FROM memo").use { rs ->
                    rs.next()
                    rs.getInt(1) shouldBe 0
                }
                statement.executeQuery("SELECT COUNT(*) FROM folder").use { rs ->
                    rs.next()
                    rs.getInt(1) shouldBe 0
                }
                statement.executeQuery("SELECT COUNT(*) FROM memo_reference").use { rs ->
                    rs.next()
                    rs.getInt(1) shouldBe 0
                }
            }
        }
    }

    @Test
    fun `관리자 유저의 폴더, 메모, 메모 참조를 삭제하지 않는다`() {
        // given
        val admin = userRepository.save(
            User.from(
                id = UserId(100L),
                nickName = NickName("admin"),
                email = Email("admin@Test.com"),
                roles = setOf(Role.ADMIN),
                picUrl = null,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )
        val user = userRepository.save(User.newOne(NickName("user1"), Email("test@test.com"), null))

        withClue("관리자 데이터") { createDummyData(admin) }
        withClue("유저 데이터") { createDummyData(user) }

        // when
        sut.handle(IDeleteAllWithoutAdmin.Command())

        // then
        dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT COUNT(*) FROM memo").use { rs ->
                    rs.next()
                    rs.getInt(1) shouldBe 5
                }
                statement.executeQuery("SELECT COUNT(*) FROM folder").use { rs ->
                    rs.next()
                    rs.getInt(1) shouldBe 2
                }
                statement.executeQuery("SELECT COUNT(*) FROM memo_reference").use { rs ->
                    rs.next()
                    rs.getInt(1) shouldBe 1
                }
            }
        }
    }
}
