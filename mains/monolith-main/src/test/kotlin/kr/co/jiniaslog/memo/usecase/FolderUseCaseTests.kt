package kr.co.jiniaslog.memo.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.memo.adapter.outbound.mysql.config.MemoDb
import kr.co.jiniaslog.memo.domain.FolderTestFixtures
import kr.co.jiniaslog.memo.domain.exception.NotOwnershipException
import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderName
import kr.co.jiniaslog.memo.domain.folder.FolderRepository
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDateTime
import javax.sql.DataSource

class FolderUseCaseTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var folderRepository: FolderRepository

    @Autowired
    lateinit var memoRepository: MemoRepository

    @Autowired
    @Qualifier(MemoDb.DATASOURCE)
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var sut: FolderUseCasesFacade

    @Test
    fun `유효한 폴더 초기화 요청시 폴더는 초기화 된다`() {
        // given
        val command =
            ICreateNewFolder.Command(
                authorId = AuthorId(1),
            )

        // when
        val info = sut.handle(command)

        // then
        info.id shouldNotBe null
        folderRepository.findById(info.id) shouldNotBe null
    }

    @Test
    fun `제한 폴더수 이상 폴더 초기화 요청시 예외가 발생한다`() {
        // given
        val jdbcTemplate = JdbcTemplate(dataSource)
        val dateTime = LocalDateTime.now()
        jdbcTemplate.batchUpdate(
            "INSERT INTO folder (id, name, author_id, parent_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)",
            (1..Folder.INIT_LIMIT).map {
                arrayOf(it, "name", 1, null, dateTime, dateTime)
            }
        )
        val command =
            ICreateNewFolder.Command(
                authorId = AuthorId(1),
            )

        // when & then
        shouldThrow<IllegalArgumentException> {
            sut.handle(command)
        }
    }

    @Test
    fun `유효한 폴더가 있고 유효한 폴더 이름 변경 요청이 있으면 폴더이름이 변경된다`() {
        // given
        val folder =
            folderRepository.save(
                Folder.init(
                    authorId = AuthorId(1),
                ),
            )
        val command =
            IChangeFolderName.Command(
                folderId = folder.entityId,
                name = FolderName("name"),
                requesterId = FolderTestFixtures.defaultAuthorId,
            )
        // when
        val info = sut.handle(command)
        // then
        info.folderId shouldNotBe null
        folderRepository.findById(info.folderId) shouldNotBe null
    }

    @Test
    fun `유효한 폴더가 있고 유효한 폴더 삭제 요청이 있으면 폴더가 삭제된다`() {
        // given
        val folder =
            folderRepository.save(
                Folder.init(
                    authorId = AuthorId(1),
                ),
            )
        val command =
            IDeleteFoldersRecursively.Command(
                folderId = folder.entityId,
                requesterId = FolderTestFixtures.defaultAuthorId,
            )
        // when
        val info = sut.handle(command)
        // then
        folderRepository.findById(info.folderId) shouldBe null
    }

    @Test
    fun `유효한 폴더가 있고, 폴더가 폴더를 가지고 있을때, 유효한 폴더 삭제 요청이 있으면 재귀적으로 모든 폴더가 삭제된다`() {
        // given
        val parentFolder =
            folderRepository.save(
                Folder.init(
                    authorId = AuthorId(1),
                ),
            )
        val childFolder =
            folderRepository.save(
                Folder.init(
                    authorId = AuthorId(1),
                    parent = parentFolder.entityId,
                ),
            )
        val command =
            IDeleteFoldersRecursively.Command(
                folderId = parentFolder.entityId,
                requesterId = FolderTestFixtures.defaultAuthorId,
            )
        // when
        val info = sut.handle(command)
        // then
        info.folderId shouldNotBe null
        folderRepository.findById(info.folderId) shouldBe null
        folderRepository.findById(childFolder.entityId) shouldBe null
    }

    @Test
    fun `유효한 폴더가 있고, 폴더가 메모를 가지고 있을때 유효한 폴더 삭제 요청이 있으면 모두 삭제된다`() {
        // given
        val folder =
            folderRepository.save(
                Folder.init(
                    authorId = AuthorId(1),
                ),
            )
        val memo =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = folder.entityId,
                ),
            )

        val command =
            IDeleteFoldersRecursively.Command(
                folderId = folder.entityId,
                requesterId = FolderTestFixtures.defaultAuthorId,
            )
        // when
        val info = sut.handle(command)
        // then
        info.folderId shouldNotBe null
        folderRepository.findById(info.folderId) shouldBe null
        memoRepository.findById(memo.entityId) shouldBe null
    }

    @Test
    fun `유효한 폴더가 있고, 폴더가 복잡한 계층구조로 폴더와 메모를 가지고 있어도 루트 폴더를 삭제하면 모두 삭제된다`() {
        // given
        val rootFolder =
            folderRepository.save(
                Folder.init(
                    authorId = AuthorId(1),
                ),
            )
        val childFolder1 =
            folderRepository.save(
                Folder.init(
                    authorId = AuthorId(1),
                    parent = rootFolder.entityId,
                ),
            )
        val childFolder2 =
            folderRepository.save(
                Folder.init(
                    authorId = AuthorId(1),
                    parent = childFolder1.entityId,
                ),
            )
        val memo =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = childFolder2.entityId,
                ),
            )

        val command =
            IDeleteFoldersRecursively.Command(
                folderId = rootFolder.entityId,
                requesterId = FolderTestFixtures.defaultAuthorId,
            )
        // when
        val info = sut.handle(command)
        // then
        info.folderId shouldNotBe null
        folderRepository.findById(info.folderId) shouldBe null
        folderRepository.findById(childFolder1.entityId) shouldBe null
        folderRepository.findById(childFolder2.entityId) shouldBe null
        memoRepository.findById(memo.entityId) shouldBe null
    }

    @Test
    fun `유효한 폴더 관계 설정 요청이 있고 유효한 폴더 둘이 있으면 폴더 관계가 설정된다`() {
        // given
        val folder1 =
            folderRepository.save(
                Folder.init(
                    authorId = AuthorId(1),
                ),
            )
        val folder2 =
            folderRepository.save(
                Folder.init(
                    authorId = AuthorId(1),
                ),
            )
        val command =
            IMakeRelationShipFolderAndFolder.Command(
                parentFolderId = folder1.entityId,
                childFolderId = folder2.entityId,
                requesterId = FolderTestFixtures.defaultAuthorId,
            )
        // when
        val info = sut.handle(command)
        // then
        info.parentFolderId shouldNotBe null
        info.childFolderId shouldNotBe null
        folderRepository.findById(info.parentFolderId!!) shouldNotBe null
        folderRepository.findById(info.childFolderId) shouldNotBe null
        info.parentFolderId shouldBe folder1.entityId
        info.childFolderId shouldBe folder2.entityId
    }

    @Test
    fun `없는 폴더에 이름 변경을 요청하면 예외가 발생한다`() {
        // given
        val command =
            IChangeFolderName.Command(
                folderId = FolderId(1),
                name = FolderName("name"),
                requesterId = FolderTestFixtures.defaultAuthorId,
            )
        // when & then
        shouldThrow<IllegalArgumentException> {
            sut.handle(command)
        }
    }

    @Test
    fun `폴더 이름 변경 요청자가 폴더 작성자와 다르면 예외가 발생한다`() {
        // given
        val folder =
            folderRepository.save(
                Folder.init(
                    authorId = AuthorId(1),
                ),
            )
        val command =
            IChangeFolderName.Command(
                folderId = folder.entityId,
                name = FolderName("name"),
                requesterId = AuthorId(2),
            )
        // when & then
        shouldThrow<NotOwnershipException> {
            sut.handle(command)
        }
    }
}
