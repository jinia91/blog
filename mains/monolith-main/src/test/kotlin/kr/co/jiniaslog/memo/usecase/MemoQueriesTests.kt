package kr.co.jiniaslog.memo.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.memo.domain.FolderTestFixtures
import kr.co.jiniaslog.memo.domain.MemoTestFixtures
import kr.co.jiniaslog.memo.domain.folder.FolderRepository
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.queries.ICheckMemoExisted
import kr.co.jiniaslog.memo.queries.IGetAllReferencedByMemo
import kr.co.jiniaslog.memo.queries.IGetAllReferencesByMemo
import kr.co.jiniaslog.memo.queries.IGetMemoById
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.memo.queries.ISearchAllMemoByKeyword
import kr.co.jiniaslog.memo.queries.MemoQueriesFacade
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class MemoQueriesTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var sut: MemoQueriesFacade

    @Autowired
    lateinit var folderRepository: FolderRepository

    @Autowired
    lateinit var memoRepository: MemoRepository

    @Test
    fun `특정 메모와 키워드가 있으면 연관 메모만 조회된다`() {
        // given IRecommendRelatedMemo
        val dummyFolder = folderRepository.save(FolderTestFixtures.build())
        val memo = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.entityId))
        memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.entityId))
        memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.entityId))
        memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.entityId))
        memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.entityId))
        memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.entityId))
        val relatedMemo = memoRepository.save(
            MemoTestFixtures.build(parentFolderId = dummyFolder.entityId, content = MemoContent("특정"))
        )

        // when
        val result = sut.handle(
            IRecommendRelatedMemo.Query(thisMemoId = memo.entityId, rawKeyword = "특정", requesterId = memo.authorId)
        )

        // then
        result.relatedMemoCandidates.size shouldBe 1
        result.relatedMemoCandidates[0].first shouldBe relatedMemo.entityId
        result.relatedMemoCandidates[0].second shouldBe relatedMemo.title
        result.relatedMemoCandidates[0].third shouldBe relatedMemo.content
    }

    @Test
    fun `연관 메모 조회시 자기자신은 조회되지 않는다`() {
        // given
        val dummyFolder = folderRepository.save(FolderTestFixtures.build())
        val memo = memoRepository.save(
            MemoTestFixtures.build(parentFolderId = dummyFolder.entityId, content = MemoContent("특정"))
        )

        // when
        val result = sut.handle(
            IRecommendRelatedMemo.Query(
                thisMemoId = memo.entityId,
                rawKeyword =
                "특정",
                requesterId = memo.authorId
            )
        )

        // then
        result.relatedMemoCandidates.size shouldBe 0
    }

    @Test
    fun `메모아이디로 메모를 조회할 수 있다`() {
        // given
        val dummyFolder = folderRepository.save(FolderTestFixtures.build())
        val dummyMemo = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.entityId))

        // when
        val result = sut.handle(IGetMemoById.Query(dummyMemo.entityId, dummyMemo.authorId))

        // then
        result.memoId shouldBe dummyMemo.entityId
    }

    @Test
    fun `메모가 없다면 메모아이디로 조회할 경우 예외가 발생한다`() {
        // given
        // when, then
        shouldThrow<IllegalArgumentException> {
            sut.handle(IGetMemoById.Query(MemoId(5), MemoTestFixtures.defaultAuthorId))
        }
    }

    @Test
    fun `메모가 참조하는 여러 메모가 있을때 모든 참조 메모를 조회할 수 있다`() {
        // given
        val target = memoRepository.save(MemoTestFixtures.build())

        for (i in 1..10) {
            val dummyFolder = folderRepository.save(FolderTestFixtures.build())
            val dummyReference = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.entityId))
            target.addReference(dummyReference.entityId)
            memoRepository.save(target)
        }

        // when
        val result = sut.handle(IGetAllReferencesByMemo.Query(memoId = target.entityId, requesterId = target.authorId))

        // then
        result.references.size shouldBe 10
    }

    @Test
    fun `메모가 여러메모에 참조될때, 참조된 모든 메모를 조회할 수 있다`() {
        // given
        val target = memoRepository.save(MemoTestFixtures.build())

        for (i in 1..10) {
            val dummyFolder = folderRepository.save(FolderTestFixtures.build())
            val dummyReference = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.entityId))
            dummyReference.addReference(target.entityId)
            memoRepository.save(dummyReference)
        }

        // when
        val result = sut.handle(IGetAllReferencedByMemo.Query(memoId = target.entityId, requesterId = target.authorId))

        // then
        result.referenceds.size shouldBe 10
    }

    @Test
    fun `메모가 존재하는지 확인할 수 있다`() {
        // given
        val target = memoRepository.save(MemoTestFixtures.build())

        // when
        val result = sut.handle(ICheckMemoExisted.Query(target.entityId))

        // then
        result shouldBe true
    }

    @Test
    fun `메모가 존재하지 않는지 확인할 수 있다`() {
        // given
        // when
        val result = sut.handle(ICheckMemoExisted.Query(MemoId(5)))

        // then
        result shouldBe false
    }

    @Test
    fun `키워드로 조회시 가상 폴더 하에 메모만 조회된다`() {
        // given
        val dummyParentFolder = FolderTestFixtures.build()
        folderRepository.save(dummyParentFolder)

        val emptyChildFolder = FolderTestFixtures.build(
            parent = dummyParentFolder.entityId
        )
        folderRepository.save(emptyChildFolder)

        withClue("키워드가 있는 메모") {
            (1..10).map {
                memoRepository.save(
                    MemoTestFixtures.build(title = MemoTitle("검색"))
                )
            }
        }

        withClue("키워드가 없는 메모") {
            memoRepository.save(
                MemoTestFixtures.build()
            )
        }

        // when
        val result = sut.handle(
            ISearchAllMemoByKeyword.Query(
                requesterId = MemoTestFixtures.defaultAuthorId,
                keyword = "검색"
            )
        )

        // then
        result.result.memos.size shouldBe 10
    }
}
