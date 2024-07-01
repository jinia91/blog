package kr.co.jiniaslog.memo.usecase

import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.memo.domain.FolderTestFixtures
import kr.co.jiniaslog.memo.domain.MemoTestFixtures
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.outbound.FolderRepository
import kr.co.jiniaslog.memo.outbound.MemoRepository
import kr.co.jiniaslog.memo.queries.IGetAllMemos
import kr.co.jiniaslog.memo.queries.IGetAllReferencedByMemo
import kr.co.jiniaslog.memo.queries.IGetAllReferencesByMemo
import kr.co.jiniaslog.memo.queries.IGetMemoById
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
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
    fun `메모 전체를 조회하면 모든 메모가 조회된다`() {
        // given
        val dummyFolder = folderRepository.save(FolderTestFixtures.build())
        val dummyMemo = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.id))
        val dummy2Memo = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.id))

        // when
        val result = sut.handle(IGetAllMemos.Query())

        // then
        result.size shouldBe 2
    }

    @Test
    fun `특정 메모와 키워드가 있으면 연관 메모만 조회된다`() {
        // given IRecommendRelatedMemo
        val dummyFolder = folderRepository.save(FolderTestFixtures.build())
        val memo = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.id))
        val unRelatedMemo1 = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.id))
        val unRelatedMemo2 = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.id))
        val unRelatedMemo3 = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.id))
        val unRelatedMemo4 = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.id))
        val unRelatedMemo5 = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.id))
        val relatedMemo = memoRepository.save(
            MemoTestFixtures.build(parentFolderId = dummyFolder.id, content = MemoContent("test"))
        )

        // when
        val result = sut.handle(IRecommendRelatedMemo.Query(thisMemoId = memo.id, rawKeyword = "test"))

        // then
        result.relatedMemoCandidates.size shouldBe 5
        result.relatedMemoCandidates[0].first shouldBe relatedMemo.id
        result.relatedMemoCandidates[0].second shouldBe relatedMemo.title
        result.relatedMemoCandidates[0].third shouldBe relatedMemo.content
    }

    @Test
    fun `메모아이디로 메모를 조회할 수 있다`() {
        // given
        val dummyFolder = folderRepository.save(FolderTestFixtures.build())
        val dummyMemo = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.id))

        // when
        val result = sut.handle(IGetMemoById.Query(dummyMemo.id))

        // then
        result.memoId shouldBe dummyMemo.id
    }

    @Test
    fun `메모가 없다면 메모아이디로 조회할 경우 예외가 발생한다`() {
        // given
        val dummyFolder = folderRepository.save(FolderTestFixtures.build())
        val dummyMemo = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.id))

        // when
        val result = sut.handle(IGetMemoById.Query(dummyMemo.id))

        // then
        result.memoId shouldBe dummyMemo.id
    }

    @Test
    fun `메모가 참조하는 여러 메모가 있을때 모든 참조 메모를 조회할 수 있다`() {
        // given
        val target = memoRepository.save(MemoTestFixtures.build())

        for (i in 1..10) {
            val dummyFolder = folderRepository.save(FolderTestFixtures.build())
            val dummyReference = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.id))
            target.addReference(dummyReference.id)
            memoRepository.save(target)
        }

        // when
        val result = sut.handle(IGetAllReferencesByMemo.Query(memoId = target.id))

        // then
        result.references.size shouldBe 10
    }

    @Test
    fun `메모가 여러메모에 참조될때, 참조된 모든 메모를 조회할 수 있다`() {
        // given
        val target = memoRepository.save(MemoTestFixtures.build())

        for (i in 1..10) {
            val dummyFolder = folderRepository.save(FolderTestFixtures.build())
            val dummyReference = memoRepository.save(MemoTestFixtures.build(parentFolderId = dummyFolder.id))
            dummyReference.addReference(target.id)
            memoRepository.save(dummyReference)
        }

        // when
        val result = sut.handle(IGetAllReferencedByMemo.Query(memoId = target.id))

        // then
        result.referenceds.size shouldBe 10
    }

    @Test
    fun `메모가 존재하는지 확인할 수 있다`() {
        // given
        val target = memoRepository.save(MemoTestFixtures.build())

        // when
        val result = sut.handle(IGetMemoById.Query(target.id))

        // then
        result.memoId shouldBe target.id
    }
}
