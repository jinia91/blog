package kr.co.jiniaslog.blog.usecase

import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.blog.queries.IGetSimpleArticles
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class IGetSimpleArticlesQueriesTests : SimpleUnitTestContext() {

    @Nested
    inner class 커서쿼리테스트 {
        @Test
        fun `커서와 리밋이 존재하고 커서와 태그 이름이 없으면 커서 쿼리를 반환한다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = 1,
                limit = 1,
                isPublished = true,
                keyword = null,
                tagName = null
            )

            // when
            val isCursorQuery = query.isCursorQuery()

            // then
            isCursorQuery shouldBe true
        }

        @Test
        fun `커서가 존재하고 리밋이 없으면 커서 쿼리를 반환하지 않는다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = 1,
                limit = null,
                isPublished = true,
                keyword = null,
                tagName = null
            )

            // when
            val isCursorQuery = query.isCursorQuery()

            // then
            isCursorQuery shouldBe false
        }

        @Test
        fun `리밋이 존재하고 커서가 없으면 커서 쿼리를 반환하지 않는다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = null,
                limit = 1,
                isPublished = true,
                keyword = null,
                tagName = null
            )

            // when
            val isCursorQuery = query.isCursorQuery()

            // then
            isCursorQuery shouldBe false
        }

        @Test
        fun `커서와 리밋이 없으면 커서 쿼리를 반환하지 않는다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = null,
                limit = null,
                isPublished = true,
                keyword = null,
                tagName = null
            )

            // when
            val isCursorQuery = query.isCursorQuery()

            // then
            isCursorQuery shouldBe false
        }

        @Test
        fun `커서와 리밋이 있어도 키워드가 존재하면 커서 쿼리를 반환하지 않는다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = 1,
                limit = 1,
                isPublished = true,
                keyword = "keyword",
                tagName = null
            )

            // when
            val isCursorQuery = query.isCursorQuery()

            // then
            isCursorQuery shouldBe false
        }

        @Test
        fun `커서와 리밋이 있어도 태그 이름이 존재하면 커서 쿼리를 반환하지 않는다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = 1,
                limit = 1,
                isPublished = true,
                keyword = null,
                tagName = "tagName"
            )

            // when
            val isCursorQuery = query.isCursorQuery()

            // then
            isCursorQuery shouldBe false
        }
    }

    @Nested
    inner class 키워드쿼리판정테스트 {
        @Test
        fun `키워드가 존재하고 커서와 태그 이름이 없으면 키워드 쿼리를 반환한다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = null,
                limit = null,
                isPublished = true,
                keyword = "keyword",
                tagName = null
            )

            // when
            val isKeywordQuery = query.isKeywordQuery()

            // then
            isKeywordQuery shouldBe true
        }

        @Test
        fun `키워드가 있지만, 커서가 존재하면 키워드 쿼리를 반환하지 않는다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = 1,
                limit = 1,
                isPublished = true,
                keyword = "keyword",
                tagName = null
            )

            // when
            val isKeywordQuery = query.isKeywordQuery()

            // then
            isKeywordQuery shouldBe false
        }

        @Test
        fun `키워드가 있지만, 태그 이름이 존재하면 키워드 쿼리를 반환하지 않는다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = null,
                limit = null,
                isPublished = true,
                keyword = "keyword",
                tagName = "tagName"
            )

            // when
            val isKeywordQuery = query.isKeywordQuery()

            // then
            isKeywordQuery shouldBe false
        }

        @Test
        fun `키워드가 없으면 키워드 쿼리를 반환하지 않는다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = null,
                limit = null,
                isPublished = true,
                keyword = null,
                tagName = null
            )

            // when
            val isKeywordQuery = query.isKeywordQuery()

            // then
            isKeywordQuery shouldBe false
        }

        @Test
        fun `키워드가 없지만, 커서와 태그 이름이 존재하면 키워드 쿼리를 반환하지 않는다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = 1,
                limit = 1,
                isPublished = true,
                keyword = null,
                tagName = "tagName"
            )

            // when
            val isKeywordQuery = query.isKeywordQuery()

            // then
            isKeywordQuery shouldBe false
        }
    }

    @Nested
    inner class 태그쿼리판정테스트 {
        @Test
        fun `태그 이름이 존재하고 커서와 키워드가 없으면 태그 쿼리를 반환한다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = null,
                limit = null,
                isPublished = true,
                keyword = null,
                tagName = "tagName"
            )

            // when
            val isTagQuery = query.isTagQuery()

            // then
            isTagQuery shouldBe true
        }

        @Test
        fun `태그 이름이 있지만, 커서가 존재하면 태그 쿼리를 반환하지 않는다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = 1,
                limit = 1,
                isPublished = true,
                keyword = null,
                tagName = "tagName"
            )

            // when
            val isTagQuery = query.isTagQuery()

            // then
            isTagQuery shouldBe false
        }

        @Test
        fun `태그 이름이 있지만, 키워드가 존재하면 태그 쿼리를 반환하지 않는다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = null,
                limit = null,
                isPublished = true,
                keyword = "keyword",
                tagName = "tagName"
            )

            // when
            val isTagQuery = query.isTagQuery()

            // then
            isTagQuery shouldBe false
        }

        @Test
        fun `태그 이름이 없으면 태그 쿼리를 반환하지 않는다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = null,
                limit = null,
                isPublished = true,
                keyword = null,
                tagName = null
            )

            // when
            val isTagQuery = query.isTagQuery()

            // then
            isTagQuery shouldBe false
        }

        @Test
        fun `태그 이름이 없지만, 커서와 키워드가 존재하면 태그 쿼리를 반환하지 않는다`() {
            // given
            val query = IGetSimpleArticles.Query(
                cursor = 1,
                limit = 1,
                isPublished = true,
                keyword = "keyword",
                tagName = null
            )

            // when
            val isTagQuery = query.isTagQuery()

            // then
            isTagQuery shouldBe false
        }
    }
}
