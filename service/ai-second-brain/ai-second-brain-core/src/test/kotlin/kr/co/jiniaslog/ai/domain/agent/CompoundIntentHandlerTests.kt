package kr.co.jiniaslog.ai.domain.agent

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CompoundIntentHandlerTests : SimpleUnitTestContext() {

    private val ragAgent = mockk<RagAgent>()
    private val generalChatAgent = mockk<GeneralChatAgent>()
    private val memoManagementAgent = mockk<MemoManagementAgent>()

    private val handler = CompoundIntentHandler(ragAgent, generalChatAgent, memoManagementAgent)

    @Nested
    inner class `복합 의도 처리 테스트` {
        @Test
        fun `MEMO_WRITE와 KNOWLEDGE_QUERY를 순차적으로 처리할 수 있다`() {
            // given
            every { memoManagementAgent.process(any(), any(), any()) } returns
                AgentResponse.MemoCreated(1L, "테스트 메모", "메모가 생성되었습니다.")
            every { ragAgent.chat(any(), any(), any()) } returns "관련 자료입니다."

            // when
            val result = handler.process(
                message = "메모하고 관련 자료 찾아줘",
                subIntents = listOf(Intent.MEMO_WRITE, Intent.KNOWLEDGE_QUERY),
                sessionId = 1L,
                authorId = 1L
            )

            // then
            result.shouldBeInstanceOf<AgentResponse.ChatResponse>()
            val content = (result as AgentResponse.ChatResponse).content
            content shouldContain "메모가 생성되었습니다."
            content shouldContain "관련 자료입니다."
        }

        @Test
        fun `단일 서브 의도는 해당 에이전트 응답을 그대로 반환한다`() {
            // given
            every { ragAgent.chat(any(), any(), any()) } returns "검색 결과입니다."

            // when
            val result = handler.process(
                message = "찾아줘",
                subIntents = listOf(Intent.KNOWLEDGE_QUERY),
                sessionId = 1L,
                authorId = 1L
            )

            // then
            result.shouldBeInstanceOf<AgentResponse.ChatResponse>()
            (result as AgentResponse.ChatResponse).content shouldBe "검색 결과입니다."
        }

        @Test
        fun `빈 서브 의도 목록은 기본 응답을 반환한다`() {
            // when
            val result = handler.process(
                message = "테스트",
                subIntents = emptyList(),
                sessionId = 1L,
                authorId = 1L
            )

            // then
            result.shouldBeInstanceOf<AgentResponse.ChatResponse>()
            (result as AgentResponse.ChatResponse).content shouldBe "요청을 처리할 수 없습니다."
        }

        @Test
        fun `처리 중 오류가 발생하면 Error 응답을 포함한다`() {
            // given
            every { memoManagementAgent.process(any(), any(), any()) } throws RuntimeException("DB 오류")
            every { ragAgent.chat(any(), any(), any()) } returns "검색 결과"

            // when
            val result = handler.process(
                message = "메모하고 찾아줘",
                subIntents = listOf(Intent.MEMO_WRITE, Intent.KNOWLEDGE_QUERY),
                sessionId = 1L,
                authorId = 1L
            )

            // then
            result.shouldBeInstanceOf<AgentResponse.ChatResponse>()
            val content = (result as AgentResponse.ChatResponse).content
            content shouldContain "오류"
            content shouldContain "검색 결과"
        }
    }

    @Nested
    inner class `에이전트 라우팅 테스트` {
        @Test
        fun `KNOWLEDGE_QUERY는 RagAgent로 라우팅된다`() {
            // given
            every { ragAgent.chat(any(), any(), any()) } returns "결과"

            // when
            handler.process("테스트", listOf(Intent.KNOWLEDGE_QUERY), 1L, 1L)

            // then
            verify { ragAgent.chat(any(), any(), any()) }
        }

        @Test
        fun `MEMO_WRITE는 MemoManagementAgent로 라우팅된다`() {
            // given
            every { memoManagementAgent.process(any(), any(), any()) } returns AgentResponse.ChatResponse("ok")

            // when
            handler.process("테스트", listOf(Intent.MEMO_WRITE), 1L, 1L)

            // then
            verify { memoManagementAgent.process(any(), any(), any()) }
        }

        @Test
        fun `MEMO_ORGANIZE는 MemoManagementAgent로 라우팅된다`() {
            // given
            every { memoManagementAgent.process(any(), any(), any()) } returns AgentResponse.ChatResponse("ok")

            // when
            handler.process("테스트", listOf(Intent.MEMO_ORGANIZE), 1L, 1L)

            // then
            verify { memoManagementAgent.process(any(), any(), any()) }
        }

        @Test
        fun `MEMO_SEARCH는 MemoManagementAgent로 라우팅된다`() {
            // given
            every { memoManagementAgent.process(any(), any(), any()) } returns AgentResponse.ChatResponse("ok")

            // when
            handler.process("테스트", listOf(Intent.MEMO_SEARCH), 1L, 1L)

            // then
            verify { memoManagementAgent.process(any(), any(), any()) }
        }

        @Test
        fun `GENERAL_CHAT는 GeneralChatAgent로 라우팅된다`() {
            // given
            every { generalChatAgent.chat(any(), any()) } returns "안녕하세요"

            // when
            handler.process("테스트", listOf(Intent.GENERAL_CHAT), 1L, 1L)

            // then
            verify { generalChatAgent.chat(any(), any()) }
        }
    }
}
