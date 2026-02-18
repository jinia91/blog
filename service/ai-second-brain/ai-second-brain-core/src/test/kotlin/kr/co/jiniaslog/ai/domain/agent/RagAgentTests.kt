package kr.co.jiniaslog.ai.domain.agent

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeEmpty
import io.mockk.every
import io.mockk.mockk
import kr.co.jiniaslog.ai.outbound.MemoInfo
import kr.co.jiniaslog.ai.outbound.MemoQueryClient
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.client.ChatClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import io.kotest.matchers.collections.shouldContain as collectionShouldContain

class RagAgentTests : SimpleUnitTestContext() {

    private fun createRagAgent(
        memoQueryClient: MemoQueryClient = mockk(relaxed = true),
    ): RagAgent {
        val chatClient = mockk<ChatClient>(relaxed = true)
        return RagAgent(chatClient, memoQueryClient)
    }

    @Nested
    inner class `buildSystemPrompt 테스트` {
        @Test
        fun `시스템 프롬프트를 생성할 수 있다`() {
            val ragAgent = createRagAgent()

            val systemPrompt = ragAgent.buildSystemPrompt()

            systemPrompt.shouldNotBeEmpty()
        }

        @Test
        fun `시스템 프롬프트에 현재 시간 정보가 포함된다`() {
            val ragAgent = createRagAgent()

            val systemPrompt = ragAgent.buildSystemPrompt()

            systemPrompt shouldContain "현재:"
            systemPrompt shouldContain LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
        }

        @Test
        fun `시스템 프롬프트에 오늘 날짜가 포함된다`() {
            val ragAgent = createRagAgent()
            val expectedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM월 dd일"))

            val systemPrompt = ragAgent.buildSystemPrompt()

            systemPrompt shouldContain "오늘:"
            systemPrompt shouldContain expectedDate
        }

        @Test
        fun `시스템 프롬프트에 내일 날짜가 포함된다`() {
            val ragAgent = createRagAgent()
            val expectedDate = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("MM월 dd일"))

            val systemPrompt = ragAgent.buildSystemPrompt()

            systemPrompt shouldContain "내일:"
            systemPrompt shouldContain expectedDate
        }

        @Test
        fun `시스템 프롬프트에 모레 날짜가 포함된다`() {
            val ragAgent = createRagAgent()
            val expectedDate = LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ofPattern("MM월 dd일"))

            val systemPrompt = ragAgent.buildSystemPrompt()

            systemPrompt shouldContain "모레:"
            systemPrompt shouldContain expectedDate
        }

        @Test
        fun `시스템 프롬프트에 기본 시스템 프롬프트 텍스트가 포함된다`() {
            val ragAgent = createRagAgent()

            val systemPrompt = ragAgent.buildSystemPrompt()

            systemPrompt shouldContain "당신은 사용자의 개인 지식 관리 시스템 AI 어시스턴트입니다"
            systemPrompt shouldContain "사용자의 메모를 참조하여 정확하고 도움이 되는 답변을 제공합니다"
            systemPrompt shouldContain "현재 시간 정보"
        }

        @Test
        fun `추가 컨텍스트가 시스템 프롬프트에 포함된다`() {
            val ragAgent = createRagAgent()

            val systemPrompt = ragAgent.buildSystemPrompt("테스트 컨텍스트")

            systemPrompt shouldContain "테스트 컨텍스트"
        }
    }

    @Nested
    inner class `extractKeywords 테스트` {
        @Test
        fun `메시지에서 키워드를 추출한다`() {
            val ragAgent = createRagAgent()

            val keywords = ragAgent.extractKeywords("스프링 부트 설정 방법")

            keywords collectionShouldContain "스프링"
            keywords collectionShouldContain "부트"
        }

        @Test
        fun `불용어는 제외된다`() {
            val ragAgent = createRagAgent()

            val keywords = ragAgent.extractKeywords("뭐 알려줘 해줘")

            keywords.shouldBeEmpty()
        }

        @Test
        fun `최대 3개 키워드만 반환한다`() {
            val ragAgent = createRagAgent()

            val keywords = ragAgent.extractKeywords("가나다라 마바사아 자차카타 파하가나")

            keywords shouldHaveSize 3
        }

        @Test
        fun `2글자 미만 단어는 제외된다`() {
            val ragAgent = createRagAgent()

            val keywords = ragAgent.extractKeywords("a 나 스프링")

            keywords collectionShouldContain "스프링"
        }
    }

    @Nested
    inner class `buildKeywordContext 테스트` {
        @Test
        fun `키워드 검색 결과가 없으면 없음을 반환한다`() {
            val memoQueryClient = mockk<MemoQueryClient>()
            every { memoQueryClient.searchByKeyword(any(), any(), any()) } returns emptyList()
            val ragAgent = createRagAgent(memoQueryClient)

            val context = ragAgent.buildKeywordContext("스프링", 1L)

            context shouldContain "없음"
        }

        @Test
        fun `키워드 검색 결과가 있으면 메모 출처와 내용이 포함된다`() {
            val memoQueryClient = mockk<MemoQueryClient>()
            every { memoQueryClient.searchByKeyword(1L, any(), 3) } returns emptyList()
            every { memoQueryClient.searchByKeyword(1L, "스프링", 3) } returns listOf(
                MemoInfo(id = 1L, authorId = 1L, title = "스프링 가이드", content = "스프링 설명")
            )
            val ragAgent = createRagAgent(memoQueryClient)

            val context = ragAgent.buildKeywordContext("스프링 부트", 1L)

            context shouldContain "[메모: 스프링 가이드]"
            context shouldContain "스프링 설명"
        }

        @Test
        fun `메시지에서 키워드를 추출할 수 없으면 없음을 반환한다`() {
            val ragAgent = createRagAgent()

            val context = ragAgent.buildKeywordContext("뭐 해줘", 1L)

            context shouldContain "없음"
        }
    }
}
