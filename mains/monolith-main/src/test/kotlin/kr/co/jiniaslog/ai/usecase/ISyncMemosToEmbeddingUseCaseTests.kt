package kr.co.jiniaslog.ai.usecase

import io.kotest.matchers.shouldBe
import io.mockk.verify
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.ai.outbound.EmbeddingStore
import kr.co.jiniaslog.ai.outbound.MemoEmbeddingDocument
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ISyncMemosToEmbeddingUseCaseTests : TestContainerAbstractSkeleton() {

    @Autowired
    private lateinit var syncMemoToEmbedding: ISyncMemoToEmbedding

    @Autowired
    private lateinit var syncAllMemosToEmbedding: ISyncAllMemosToEmbedding

    @Autowired
    private lateinit var deleteMemoEmbedding: IDeleteMemoEmbedding

    @Autowired
    private lateinit var embeddingStore: EmbeddingStore

    @Nested
    inner class `단일 메모 임베딩 동기화 테스트` {
        @Test
        fun `메모를 임베딩 저장소에 동기화할 수 있다`() {
            // given
            val command = ISyncMemoToEmbedding.Command(
                memoId = 1L,
                authorId = 100L,
                title = "테스트 메모",
                content = "테스트 내용입니다."
            )

            // when
            syncMemoToEmbedding(command)

            // then
            verify {
                embeddingStore.store(
                    MemoEmbeddingDocument(
                        memoId = 1L,
                        authorId = 100L,
                        title = "테스트 메모",
                        content = "테스트 내용입니다."
                    )
                )
            }
        }
    }

    @Nested
    inner class `전체 메모 임베딩 동기화 테스트` {
        @Test
        fun `사용자의 모든 메모를 동기화하면 동기화된 개수를 반환한다`() {
            // given
            val authorId = 100L

            // when
            val result = syncAllMemosToEmbedding(
                ISyncAllMemosToEmbedding.Command(authorId = authorId)
            )

            // then
            result.syncedCount shouldBe 0 // 메모가 없으므로 0
        }
    }

    @Nested
    inner class `메모 임베딩 삭제 테스트` {
        @Test
        fun `메모 임베딩을 삭제할 수 있다`() {
            // given
            val memoId = 1L

            // when
            deleteMemoEmbedding(IDeleteMemoEmbedding.Command(memoId = memoId))

            // then
            verify { embeddingStore.delete(memoId) }
        }
    }
}
