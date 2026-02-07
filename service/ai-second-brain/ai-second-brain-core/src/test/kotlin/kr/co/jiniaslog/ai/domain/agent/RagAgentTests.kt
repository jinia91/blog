package kr.co.jiniaslog.ai.domain.agent

import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeEmpty
import io.mockk.mockk
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.client.ChatClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RagAgentTests : SimpleUnitTestContext() {

    @Nested
    inner class `buildSystemPrompt 테스트` {
        @Test
        fun `시스템 프롬프트를 생성할 수 있다`() {
            // given
            val chatClient = mockk<ChatClient>(relaxed = true)
            val ragAgent = RagAgent(chatClient)

            // when
            val systemPrompt = ragAgent.buildSystemPrompt()

            // then
            systemPrompt.shouldNotBeEmpty()
        }

        @Test
        fun `시스템 프롬프트에 현재 시간 정보가 포함된다`() {
            // given
            val chatClient = mockk<ChatClient>(relaxed = true)
            val ragAgent = RagAgent(chatClient)

            // when
            val systemPrompt = ragAgent.buildSystemPrompt()

            // then
            systemPrompt shouldContain "현재:"
            systemPrompt shouldContain LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
        }

        @Test
        fun `시스템 프롬프트에 오늘 날짜가 포함된다`() {
            // given
            val chatClient = mockk<ChatClient>(relaxed = true)
            val ragAgent = RagAgent(chatClient)
            val today = LocalDateTime.now()
            val expectedDate = today.format(DateTimeFormatter.ofPattern("MM월 dd일"))

            // when
            val systemPrompt = ragAgent.buildSystemPrompt()

            // then
            systemPrompt shouldContain "오늘:"
            systemPrompt shouldContain expectedDate
        }

        @Test
        fun `시스템 프롬프트에 내일 날짜가 포함된다`() {
            // given
            val chatClient = mockk<ChatClient>(relaxed = true)
            val ragAgent = RagAgent(chatClient)
            val tomorrow = LocalDateTime.now().plusDays(1)
            val expectedDate = tomorrow.format(DateTimeFormatter.ofPattern("MM월 dd일"))

            // when
            val systemPrompt = ragAgent.buildSystemPrompt()

            // then
            systemPrompt shouldContain "내일:"
            systemPrompt shouldContain expectedDate
        }

        @Test
        fun `시스템 프롬프트에 모레 날짜가 포함된다`() {
            // given
            val chatClient = mockk<ChatClient>(relaxed = true)
            val ragAgent = RagAgent(chatClient)
            val dayAfterTomorrow = LocalDateTime.now().plusDays(2)
            val expectedDate = dayAfterTomorrow.format(DateTimeFormatter.ofPattern("MM월 dd일"))

            // when
            val systemPrompt = ragAgent.buildSystemPrompt()

            // then
            systemPrompt shouldContain "모레:"
            systemPrompt shouldContain expectedDate
        }

        @Test
        fun `시스템 프롬프트에 기본 시스템 프롬프트 텍스트가 포함된다`() {
            // given
            val chatClient = mockk<ChatClient>(relaxed = true)
            val ragAgent = RagAgent(chatClient)

            // when
            val systemPrompt = ragAgent.buildSystemPrompt()

            // then
            systemPrompt shouldContain "당신은 사용자의 개인 지식 관리 시스템 AI 어시스턴트입니다"
            systemPrompt shouldContain "사용자의 메모를 참조하여 정확하고 도움이 되는 답변을 제공합니다"
            systemPrompt shouldContain "현재 시간 정보"
        }
    }
}
