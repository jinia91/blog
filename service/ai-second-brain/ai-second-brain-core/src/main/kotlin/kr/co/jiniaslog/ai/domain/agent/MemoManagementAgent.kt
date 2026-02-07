package kr.co.jiniaslog.ai.domain.agent

import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Memo Management Agent - 메모와 폴더를 관리하는 AI 에이전트
 *
 * Tool Calling을 통해 다음 작업을 수행합니다:
 * - 메모: 생성, 수정, 삭제, 폴더 이동, 목록 조회
 * - 폴더: 생성, 이름 변경, 삭제, 상위 폴더 이동, 목록 조회
 *
 * 삭제 작업은 프롬프트에서 사용자 확인을 받도록 지시합니다.
 * ChatMemory를 사용하여 대화 히스토리를 유지하고, 삭제 확인 컨텍스트를 이해합니다.
 */
@Component
class MemoManagementAgent(
    @Qualifier("memoChatClient") private val chatClient: ChatClient,
    private val memoTools: MemoTools,
    private val chatMemory: ChatMemory
) {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val SYSTEM_PROMPT = """당신은 사용자의 메모와 폴더를 관리하는 어시스턴트입니다.

## 역할
사용자의 요청을 이해하고, 적절한 도구를 호출하여 메모와 폴더를 관리합니다.

## 사용 가능한 작업

### 메모 관리
- createMemo: 새 메모 생성 (제목과 내용 추출)
- updateMemo: 기존 메모 수정
- deleteMemo: 메모 삭제 ⚠️ 확인 필수
- moveMemoToFolder: 메모를 폴더로 이동
- listMemos: 모든 메모 목록 조회

### 폴더 관리
- createFolder: 새 폴더 생성
- renameFolder: 폴더 이름 변경
- deleteFolder: 폴더 삭제 ⚠️ 확인 필수 (하위 항목 포함)
- moveFolderToParent: 폴더를 다른 폴더로 이동
- listFolders: 모든 폴더 목록 조회

## ⚠️ 삭제 작업 필수 규칙
삭제는 되돌릴 수 없는 작업입니다. 반드시 다음 절차를 따르세요:

1. 사용자가 삭제를 요청하면, 먼저 확인 질문을 합니다:
   - "메모 '[제목]'을(를) 정말 삭제하시겠습니까?"
   - "폴더 '[이름]'과(와) 그 안의 모든 내용을 정말 삭제하시겠습니까?"

2. 사용자가 명확히 확인한 후에만 삭제 도구를 호출합니다:
   - "네", "응", "삭제해", "삭제해줘", "확인", "맞아" 등의 긍정적 응답

3. 사용자가 취소하거나 모호한 응답을 하면 삭제하지 않습니다:
   - "아니", "취소", "잠깐", "다시 생각해볼게" 등

## 예시
- "회의 메모 만들어줘: 내용..." → createMemo 호출
- "메모 수정해줘" → listMemos로 목록 확인 후 updateMemo 호출
- "이 메모 삭제해줘" → 확인 질문 후, 사용자가 확인하면 deleteMemo 호출
- "새 폴더 만들어줘" → createFolder 호출

## 응답 형식
도구 호출 결과를 자연스러운 한국어로 사용자에게 전달하세요."""

        private const val MEMO_CONVERSATION_PREFIX = "memo_agent_"
    }

    /**
     * 사용자 메시지를 처리하고 적절한 작업을 수행합니다.
     * sessionId를 사용하여 대화 히스토리를 유지합니다.
     */
    fun process(message: String, authorId: Long, sessionId: Long): AgentResponse {
        memoTools.setAuthorId(authorId)

        val conversationId = "$MEMO_CONVERSATION_PREFIX$sessionId"

        val response = try {
            chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .tools(memoTools)
                .advisors(
                    MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(conversationId)
                        .build()
                )
                .call()
                .content() ?: "요청을 처리할 수 없습니다."
        } catch (e: Exception) {
            log.error("MemoManagementAgent error: ${e.message}", e)
            return AgentResponse.Error("처리 중 오류가 발생했습니다: ${e.message}")
        }

        log.debug("MemoManagementAgent response: $response")

        return parseResponse(response)
    }

    private fun parseResponse(response: String): AgentResponse {
        // Tool 응답 파싱
        return when {
            response.startsWith("MEMO_CREATED:") -> {
                val parts = response.removePrefix("MEMO_CREATED:").split(":", limit = 3)
                if (parts.size >= 3) {
                    AgentResponse.MemoCreated(
                        memoId = parts[0].toLongOrNull() ?: 0L,
                        title = parts[1],
                        message = parts[2]
                    )
                } else {
                    AgentResponse.ChatResponse(response)
                }
            }

            response.startsWith("MEMO_UPDATED:") -> {
                val parts = response.removePrefix("MEMO_UPDATED:").split(":", limit = 2)
                AgentResponse.MemoUpdated(
                    memoId = parts[0].toLongOrNull() ?: 0L,
                    message = parts.getOrElse(1) { "메모가 수정되었습니다." }
                )
            }

            response.startsWith("MEMO_MOVED:") -> {
                val parts = response.removePrefix("MEMO_MOVED:").split(":", limit = 3)
                AgentResponse.MemoMoved(
                    memoId = parts[0].toLongOrNull() ?: 0L,
                    folderId = parts[1].toLongOrNull()?.takeIf { it != 0L },
                    message = parts.getOrElse(2) { "메모가 이동되었습니다." }
                )
            }

            response.startsWith("FOLDER_CREATED:") -> {
                val parts = response.removePrefix("FOLDER_CREATED:").split(":", limit = 3)
                AgentResponse.FolderCreated(
                    folderId = parts[0].toLongOrNull() ?: 0L,
                    name = parts[1],
                    message = parts.getOrElse(2) { "폴더가 생성되었습니다." }
                )
            }

            response.startsWith("FOLDER_RENAMED:") -> {
                val parts = response.removePrefix("FOLDER_RENAMED:").split(":", limit = 3)
                AgentResponse.FolderRenamed(
                    folderId = parts[0].toLongOrNull() ?: 0L,
                    name = parts[1],
                    message = parts.getOrElse(2) { "폴더 이름이 변경되었습니다." }
                )
            }

            response.startsWith("FOLDER_MOVED:") -> {
                val parts = response.removePrefix("FOLDER_MOVED:").split(":", limit = 3)
                AgentResponse.FolderMoved(
                    folderId = parts[0].toLongOrNull() ?: 0L,
                    parentFolderId = parts[1].toLongOrNull()?.takeIf { it != 0L },
                    message = parts.getOrElse(2) { "폴더가 이동되었습니다." }
                )
            }

            response.startsWith("DELETED:") -> {
                // FORMAT: DELETED:TYPE:ID:MESSAGE
                val parts = response.removePrefix("DELETED:").split(":", limit = 3)
                if (parts.size >= 3) {
                    AgentResponse.Deleted(
                        type = if (parts[0] == "MEMO") {
                            AgentResponse.Deleted.DeleteType.MEMO
                        } else {
                            AgentResponse.Deleted.DeleteType.FOLDER
                        },
                        targetId = parts[1].toLongOrNull() ?: 0L,
                        message = parts[2]
                    )
                } else {
                    AgentResponse.ChatResponse(response)
                }
            }

            response.startsWith("MEMO_LIST:") -> {
                val parts = response.removePrefix("MEMO_LIST:").split(":", limit = 2)
                val count = parts[0].toIntOrNull() ?: 0
                val message = parts.getOrElse(1) { "" }
                AgentResponse.MemoList(
                    memos = if (count > 0) parseMemoList(message) else emptyList(),
                    message = if (count == 0) message else "$count 개의 메모가 있습니다.\n$message"
                )
            }

            response.startsWith("FOLDER_LIST:") -> {
                val parts = response.removePrefix("FOLDER_LIST:").split(":", limit = 2)
                val count = parts[0].toIntOrNull() ?: 0
                val message = parts.getOrElse(1) { "" }
                AgentResponse.FolderList(
                    folders = if (count > 0) parseFolderList(message) else emptyList(),
                    message = if (count == 0) message else "$count 개의 폴더가 있습니다.\n$message"
                )
            }

            response.startsWith("ERROR:") -> {
                AgentResponse.Error(response.removePrefix("ERROR:").trim())
            }

            else -> {
                // Tool 호출 결과가 아닌 일반 응답 (삭제 확인 질문 등)
                AgentResponse.ChatResponse(response)
            }
        }
    }

    private fun parseMemoList(listStr: String): List<AgentResponse.MemoList.MemoSummary> {
        val regex = """\[(\d+)] (.+?)(?:\s*\(폴더:\s*(\d+)\))?$""".toRegex(RegexOption.MULTILINE)
        return regex.findAll(listStr).map { match ->
            AgentResponse.MemoList.MemoSummary(
                id = match.groupValues[1].toLong(),
                title = match.groupValues[2].trim(),
                folderId = match.groupValues[3].takeIf { it.isNotEmpty() }?.toLongOrNull()
            )
        }.toList()
    }

    private fun parseFolderList(listStr: String): List<AgentResponse.FolderList.FolderSummary> {
        val regex = """\[(\d+)] (.+?)(?:\s*\(상위:\s*(\d+)\)|\s*\(최상위\))?$""".toRegex(RegexOption.MULTILINE)
        return regex.findAll(listStr).map { match ->
            AgentResponse.FolderList.FolderSummary(
                id = match.groupValues[1].toLong(),
                name = match.groupValues[2].trim(),
                parentFolderId = match.groupValues[3].takeIf { it.isNotEmpty() }?.toLongOrNull()
            )
        }.toList()
    }
}
