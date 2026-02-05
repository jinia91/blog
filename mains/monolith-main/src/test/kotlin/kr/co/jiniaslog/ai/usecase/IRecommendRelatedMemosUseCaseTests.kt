package kr.co.jiniaslog.ai.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.ai.outbound.EmbeddingStore
import kr.co.jiniaslog.ai.outbound.MemoInfo
import kr.co.jiniaslog.ai.outbound.MemoQueryService
import kr.co.jiniaslog.ai.outbound.SimilarMemo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IRecommendRelatedMemosUseCaseTests : TestContainerAbstractSkeleton() {

    @Autowired
    private lateinit var recommendRelatedMemos: IRecommendRelatedMemos

    @Autowired
    private lateinit var embeddingStore: EmbeddingStore

    @Autowired
    private lateinit var memoQueryService: MemoQueryService

    @Nested
    inner class `메모 추천 테스트` {
        @Test
        fun `쿼리로 관련 메모를 추천받을 수 있다`() {
            // given
            val authorId = 100L
            val query = "테스트 검색어"

            every { embeddingStore.searchSimilar(query, authorId, 5) } returns listOf(
                SimilarMemo(memoId = 1L, title = "관련 메모 1", content = "내용 1", similarity = 0.9),
                SimilarMemo(memoId = 2L, title = "관련 메모 2", content = "내용 2", similarity = 0.8),
            )

            // when
            val recommendations = recommendRelatedMemos(
                IRecommendRelatedMemos.Query(
                    authorId = authorId,
                    query = query,
                    topK = 5
                )
            )

            // then
            recommendations.size shouldBe 2
            recommendations[0].title shouldBe "관련 메모 1"
            recommendations[0].similarity shouldBe 0.9
        }

        @Test
        fun `쿼리와 currentMemoId가 모두 없으면 예외가 발생한다`() {
            // given
            val authorId = 100L

            // when & then
            shouldThrow<IllegalArgumentException> {
                recommendRelatedMemos(
                    IRecommendRelatedMemos.Query(
                        authorId = authorId,
                        query = null,
                        currentMemoId = null
                    )
                )
            }
        }

        @Test
        fun `topK 개수만큼 추천을 받을 수 있다`() {
            // given
            val authorId = 100L
            val query = "검색어"
            val topK = 3

            every { embeddingStore.searchSimilar(query, authorId, topK) } returns listOf(
                SimilarMemo(memoId = 1L, title = "메모 1", content = "내용", similarity = 0.9),
                SimilarMemo(memoId = 2L, title = "메모 2", content = "내용", similarity = 0.8),
                SimilarMemo(memoId = 3L, title = "메모 3", content = "내용", similarity = 0.7),
            )

            // when
            val recommendations = recommendRelatedMemos(
                IRecommendRelatedMemos.Query(
                    authorId = authorId,
                    query = query,
                    topK = topK
                )
            )

            // then
            recommendations.size shouldBe 3
        }

        @Test
        fun `현재 메모는 추천 결과에서 제외된다`() {
            // given
            val authorId = 100L
            val currentMemoId = 1L
            val query = "검색어"

            every { embeddingStore.searchSimilar(query, authorId, 5) } returns listOf(
                SimilarMemo(memoId = 1L, title = "현재 메모", content = "내용", similarity = 1.0),
                SimilarMemo(memoId = 2L, title = "다른 메모", content = "내용", similarity = 0.8),
            )

            // when
            val recommendations = recommendRelatedMemos(
                IRecommendRelatedMemos.Query(
                    authorId = authorId,
                    query = query,
                    currentMemoId = currentMemoId
                )
            )

            // then
            recommendations.size shouldBe 1
            recommendations[0].memoId shouldBe 2L
        }

        @Test
        fun `currentMemoId로 관련 메모를 추천받을 수 있다`() {
            // given
            val authorId = 100L
            val currentMemoId = 1L

            every { memoQueryService.getMemoById(currentMemoId) } returns MemoInfo(
                id = currentMemoId,
                authorId = authorId,
                title = "현재 메모 제목",
                content = "현재 메모 내용"
            )

            every { embeddingStore.searchSimilar(any(), eq(authorId), any()) } returns listOf(
                SimilarMemo(memoId = 1L, title = "현재 메모", content = "내용", similarity = 1.0),
                SimilarMemo(memoId = 2L, title = "관련 메모 1", content = "내용 1", similarity = 0.8),
                SimilarMemo(memoId = 3L, title = "관련 메모 2", content = "내용 2", similarity = 0.7),
            )

            // when
            val recommendations = recommendRelatedMemos(
                IRecommendRelatedMemos.Query(
                    authorId = authorId,
                    query = null,
                    currentMemoId = currentMemoId,
                    topK = 5
                )
            )

            // then
            recommendations.size shouldBe 2
            recommendations.none { it.memoId == currentMemoId } shouldBe true
        }

        @Test
        fun `존재하지 않는 메모 ID로 추천 요청 시 예외가 발생한다`() {
            // given
            val authorId = 100L
            val nonExistentMemoId = 99999L

            every { memoQueryService.getMemoById(nonExistentMemoId) } returns null

            // when & then
            shouldThrow<IllegalArgumentException> {
                recommendRelatedMemos(
                    IRecommendRelatedMemos.Query(
                        authorId = authorId,
                        query = null,
                        currentMemoId = nonExistentMemoId
                    )
                )
            }
        }
    }
}
