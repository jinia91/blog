package kr.co.jiniaslog.memo.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.memo.adapter.outbound.mysql.config.MemoDb
import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderRepository
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDateTime
import javax.sql.DataSource

class MemoUseCaseTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var memoRepository: MemoRepository

    @Autowired
    @Qualifier(MemoDb.DATASOURCE)
    lateinit var datasource: DataSource

    @Autowired
    lateinit var folderRepository: FolderRepository

    @Autowired
    lateinit var sut: MemoUseCasesFacade

    @Test
    fun `유효한 메모 초기화 요청시 메모는 초기화 된다`() {
        // given
        val command =
            IInitMemo.Command(
                authorId = AuthorId(1),
                parentFolderId = null,
            )

        // when
        val info = sut.handle(command)

        // then
        info.id shouldNotBe null
        memoRepository.findById(info.id) shouldNotBe null
    }

    @Test
    fun `메모 초기화시 존재하지 않는 폴더를 부모로 지정하면 실패한다`() {
        // given
        val command =
            IInitMemo.Command(
                authorId = AuthorId(1),
                parentFolderId = FolderId(9999),
            )
        // when
        // then
        shouldThrow<IllegalArgumentException> {
            sut.handle(command)
        }
    }

    @Test
    fun `메모 초기화시 부모 폴더를 지정하면 메모는 지정된 부모 폴더를 가진다`() {
        // given
        val folder =
            folderRepository.save(
                Folder.init(
                    authorId = AuthorId(1),
                ),
            )
        val command =
            IInitMemo.Command(
                authorId = AuthorId(1),
                parentFolderId = folder.entityId,
            )
        // when
        val info = sut.handle(command)

        // then
        info.id shouldNotBe null
        val createdMemo = memoRepository.findById(info.id)
        createdMemo shouldNotBe null
        createdMemo!!.parentFolderId shouldBe folder.entityId
    }

    @Test
    fun `메모 초기화시 메모 개수가 제한을 초과하면 실패한다`() {
        // given
        val capedMemoCount = Memo.INIT_LIMIT.toInt()
        val jdbcTemplate = JdbcTemplate(datasource)
        val dateTime = LocalDateTime.now()
        jdbcTemplate.batchUpdate(
            "INSERT INTO memo (id, author_id, content, title, parent_folder_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?,?)",
            (1..capedMemoCount).map { i ->
                arrayOf(i.toLong(), 1, "content", "title", null, dateTime, dateTime)
            }
        )
        // when
        shouldThrow<IllegalArgumentException> {
            sut.handle(IInitMemo.Command(AuthorId(1), null))
        }
    }

    @Test
    fun `유효한 메모가 주어지고 업데이트할 유효한 제목이 주어지면 메모는 업데이트 된다`() {
        // given
        val memo =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = null,
                ),
            )
        val command =
            IUpdateMemoContents.Command(
                memoId = memo.entityId,
                title = MemoTitle("title"),
            )

        // when
        val info = sut.handle(command)

        // then
        info.id shouldNotBe null
        val foundTarget = memoRepository.findById(info.id)
        foundTarget shouldNotBe null
        foundTarget!!.title shouldBe MemoTitle("title")
    }

    @Test
    fun `유효한 메모가 주어지고 유효한 메모를 업데이트하면 메모는 업데이트 된다`() {
        // given
        val memo =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = null,
                ),
            )
        val command =
            IUpdateMemoContents.Command(
                memoId = memo.entityId,
                content = MemoContent("content"),
            )

        // when
        val info = sut.handle(command)

        // then
        info.id shouldNotBe null
        val foundTarget = memoRepository.findById(info.id)
        foundTarget shouldNotBe null
        foundTarget!!.content shouldBe MemoContent("content")
    }

    @Test
    fun `유효한 메모가 주어지고 유효한 참조 업데이트 커맨드가 주어지면 메모는 참조가 추가된다`() {
        // given
        val rootMemo =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = null,
                ),
            )
        val referenceTarget =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = null,
                ),
            )

        val command =
            IUpdateMemoReferences.Command.UpdateReferences(
                memoId = rootMemo.entityId,
                references = setOf(referenceTarget.entityId),
            )

        // when
        val info = sut.handle(command)

        // then
        info.id shouldNotBe null
        val foundTarget = memoRepository.findById(info.id)
        foundTarget shouldNotBe null
        foundTarget!!.getReferences().size shouldBe 1
        foundTarget.getReferences().first().referenceId shouldBe referenceTarget.entityId
        foundTarget.getReferences().first().rootId shouldBe rootMemo.entityId
    }

    @Test
    fun `유효한 메모가 주어지고 유효한 참조 업데이트 커맨드가 주어지면 메모는 참조가 추가된다 2`() {
        // given
        val rootMemo =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = null,
                ),
            )
        val referenceTarget =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = null,
                ),
            )
        rootMemo.addReference(referenceTarget.entityId)
        memoRepository.save(rootMemo)

        val referenceTarget2 =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = null,
                ),
            )
        val command =
            IUpdateMemoReferences.Command.UpdateReferences(
                memoId = rootMemo.entityId,
                references = setOf(referenceTarget.entityId, referenceTarget2.entityId),
            )

        // when
        val info = sut.handle(command)

        // then
        info.id shouldNotBe null
        val foundTarget = memoRepository.findById(info.id)
        foundTarget shouldNotBe null
        foundTarget!!.getReferences().size shouldBe 2
    }

    @Test
    fun `유효한 메모가 주어지고 유효한 참조 삭제 커맨드가 주어지면 메모는 참조가 삭제된다`() {
        // given
        val rootMemo =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = null,
                ),
            )
        val referenceTarget =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = null,
                ),
            )
        rootMemo.addReference(referenceTarget.entityId)
        memoRepository.save(rootMemo)

        val command =
            IUpdateMemoReferences.Command.RemoveReference(
                memoId = rootMemo.entityId,
                referenceId = referenceTarget.entityId,
            )

        // when
        val info = sut.handle(command)

        // then
        info.id shouldNotBe null
        val foundTarget = memoRepository.findById(info.id)
        foundTarget shouldNotBe null
        foundTarget!!.getReferences().size shouldBe 0
    }

    @Test
    fun `유효한 메모가 주어지고 자기자신을 참조하면 실패한다`() {
        // given
        val rootMemo =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = null,
                ),
            )

        val command =
            IUpdateMemoReferences.Command.UpdateReferences(
                memoId = rootMemo.entityId,
                references = setOf(rootMemo.entityId),
            )

        // when
        // then
        shouldThrow<IllegalArgumentException> {
            sut.handle(command)
        }
    }

    @Test
    fun `유효한 메모가 주어지고 삭제 명령을 하면 메모는 삭제된다`() {
        // given
        val memo =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = null,
                ),
            )
        val command =
            IDeleteMemo.Command(
                id = memo.entityId,
                requesterId = AuthorId(1),
            )

        // when
        sut.handle(command)

        // then
        memoRepository.findById(memo.entityId) shouldBe null
    }

    @Test
    fun `유효한 메모가 주어지고 부모 폴더를 설정하면 부모폴더가 메모에 설정된다`() {
        // given
        val folder =
            folderRepository.save(
                kr.co.jiniaslog.memo.domain.folder.Folder.init(
                    authorId = AuthorId(1),
                ),
            )
        val memo =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = null,
                ),
            )
        val command =
            IMakeRelationShipFolderAndMemo.Command(
                memoId = memo.entityId,
                folderId = folder.entityId,
                requesterId = AuthorId(1),
            )

        // when
        val info = sut.handle(command)

        // then
        info.memoId shouldNotBe null
        info.memoId shouldBe memo.entityId
        info.folderId shouldBe folder.entityId
        val foundedMemo = memoRepository.findById(info.memoId)
        foundedMemo shouldNotBe null
        foundedMemo!!.parentFolderId shouldBe folder.entityId
    }

    @Test
    fun `부모가 있는 메모를 부모가 없는 메모로 변경하면 부모가 없는 메모로 변경된다`() {
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
            IMakeRelationShipFolderAndMemo.Command(
                memoId = memo.entityId,
                folderId = null,
                requesterId = AuthorId(1),
            )

        // when
        val info = sut.handle(command)

        // then
        info.memoId shouldNotBe null
        info.memoId shouldBe memo.entityId
        info.folderId shouldBe null
        val foundedMemo = memoRepository.findById(info.memoId)
    }

    @Test
    fun `유효한 메모에 없는 부모를 설정하면 실패한다`() {
        // given
        val memo =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = null,
                ),
            )
        val command =
            IMakeRelationShipFolderAndMemo.Command(
                memoId = memo.entityId,
                folderId = FolderId(1),
                requesterId = AuthorId(1),
            )

        // when
        // then
        shouldThrow<IllegalArgumentException> {
            sut.handle(command)
        }
    }

    @Test
    fun `부모가 있는 메모의 부모를 변경하면 변경된다`() {
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
        val memo =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = folder1.entityId,
                ),
            )
        val command =
            IMakeRelationShipFolderAndMemo.Command(
                memoId = memo.entityId,
                folderId = folder2.entityId,
                requesterId = AuthorId(1),
            )

        // when
        val info = sut.handle(command)

        // then
        info.memoId shouldNotBe null
        info.memoId shouldBe memo.entityId
        info.folderId shouldBe folder2.entityId
        val foundedMemo = memoRepository.findById(info.memoId)
        foundedMemo shouldNotBe null
        foundedMemo!!.parentFolderId shouldBe folder2.entityId
    }

    @Test
    fun `A를 참조하는 B 메모를 A메모가 참조하려하면 순환참조 예외가 발생한다`() {
        // given
        val rootMemo =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = null,
                ),
            )
        val referenceTarget =
            memoRepository.save(
                Memo.init(
                    authorId = AuthorId(1),
                    parentFolderId = null,
                ),
            )
        rootMemo.addReference(referenceTarget.entityId)
        memoRepository.save(rootMemo)

        val command =
            IUpdateMemoReferences.Command.UpdateReferences(
                memoId = referenceTarget.entityId,
                references = setOf(rootMemo.entityId),
            )

        // when
        // then
        shouldThrow<IllegalArgumentException> {
            sut.handle(command)
        }
    }
}
