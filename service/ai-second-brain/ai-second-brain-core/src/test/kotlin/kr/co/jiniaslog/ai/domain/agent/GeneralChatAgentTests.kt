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

class GeneralChatAgentTests : SimpleUnitTestContext() {

    private val chatClient = mockk<ChatClient>(relaxed = true)
    private val agent = GeneralChatAgent(chatClient)

    @Nested
    inner class `buildSystemPrompt 테스트` {
        @Test
        fun `시스템 프롬프트는 비어있지 않다`() {
            // when
            val prompt = agent.buildSystemPrompt()

            // then
            prompt.shouldNotBeEmpty()
        }

        @Test
        fun `시스템 프롬프트에 현재 날짜 정보가 포함된다`() {
            // when
            val prompt = agent.buildSystemPrompt()

            // then
            prompt shouldContain "현재"
        }

        @Test
        fun `시스템 프롬프트에 오늘 날짜 정보가 포함된다`() {
            // when
            val prompt = agent.buildSystemPrompt()

            // then
            prompt shouldContain "오늘"
        }

        @Test
        fun `시스템 프롬프트에 내일 날짜 정보가 포함된다`() {
            // when
            val prompt = agent.buildSystemPrompt()

            // then
            prompt shouldContain "내일"
        }

        @Test
        fun `시스템 프롬프트에 모레 날짜 정보가 포함된다`() {
            // when
            val prompt = agent.buildSystemPrompt()

            // then
            prompt shouldContain "모레"
        }

        @Test
        fun `시스템 프롬프트에 기본 안내 문구가 포함된다`() {
            // when
            val prompt = agent.buildSystemPrompt()

            // then
            prompt shouldContain "AI 어시스턴트"
        }

        @Test
        fun `시스템 프롬프트에 현재 연도가 포함된다`() {
            // given
            val currentYear = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"))

            // when
            val prompt = agent.buildSystemPrompt()

            // then
            prompt shouldContain currentYear
        }
    }
}
