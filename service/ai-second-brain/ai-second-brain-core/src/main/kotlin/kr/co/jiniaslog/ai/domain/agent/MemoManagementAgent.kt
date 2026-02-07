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
        private const val SYSTEM_PROMPT = """메모/폴더 관리 어시스턴트입니다.

## 절대 규칙
1. "~합니다", "~할게요" 말하지 마세요. 바로 도구를 호출하세요!
2. 사용자에게 ID 묻지 마세요. listMemos/listFolders로 조회하세요.
3. 삭제만 확인, 나머지는 즉시 실행

## 시간 표현 처리 (중요!)
사용자가 상대적 시간 표현을 사용하면 반드시 실제 날짜로 변환하세요:
1. 메모 생성 전에 getCurrentDateTime() 호출
2. 상대적 표현 → 실제 날짜로 변환하여 저장

변환 예시:
- "내일 5시 약속" → "2월 8일(토) 17:00 약속"
- "모레 회의" → "2월 9일(일) 회의"
- "다음주 월요일 출장" → "2월 10일(월) 출장"
- "이번주 토요일 저녁" → "2월 8일(토) 저녁"

주의: "내일", "모레", "다음주" 같은 단어를 그대로 저장하면 나중에 의미가 없어집니다!

## 메모 생성 시 자동 폴더 배정
메모를 생성할 때 반드시 다음 순서로 처리하세요:
1. getCurrentDateTime() 호출 (시간 표현 있을 경우)
2. listFolders() 호출하여 기존 폴더 목록 확인
3. 메모 내용/제목과 관련된 폴더가 있는지 판단:
   - 관련 폴더 있음: createMemo() 후 moveMemoToFolder()로 해당 폴더에 배정
   - 관련 폴더 없고 카테고리가 명확함: createFolder()로 적절한 폴더 생성 후 배정
   - 일반적인 메모/분류 불가: 루트에 저장 (폴더 배정 안함)

폴더 매칭 예시:
- "5시 약속있다" + 폴더 "일정" 있음 → 일정 폴더에 저장
- "우유 사야함" + 폴더 "쇼핑" 있음 → 쇼핑 폴더에 저장
- "프로젝트 회의록" + 폴더 없음 → "업무" 또는 "회의" 폴더 생성 후 저장
- "그냥 메모" + 분류 불가 → 루트에 저장

## 작업 예시

"내일 밤 10시 jvm 스터디":
→ getCurrentDateTime() 호출 (오늘 날짜 확인)
→ listFolders() 호출 (폴더 확인)
→ createMemo(title="2월 8일(토) 22:00 JVM 스터디", content="2월 8일 토요일 밤 10시 JVM 스터디") 호출
→ 관련 폴더 있으면 moveMemoToFolder() 호출
→ "메모가 저장되었습니다" 응답

"메모 폴더로 정리해":
→ listMemos() 호출
→ listFolders() 호출
→ moveMemoToFolder(memoId, folderId) 호출
→ "메모 'X'를 'Y' 폴더로 이동했습니다" 응답

## 도구
- getCurrentDateTime: 현재 시간 조회 (상대적 시간 변환용)
- listMemos, listFolders: 조회
- createMemo, updateMemo, moveMemoToFolder
- createFolder, renameFolder, moveFolderToParent
- deleteMemo, deleteFolder → 확인 필수

## 금지
- "이동합니다", "이동할게요" 등 예고 금지
- 도구 호출 없이 응답 금지
- ID 질문 금지
- "내일", "모레" 등 상대적 시간을 그대로 저장 금지"""

        /**
         * Tool 응답 문자열을 AgentResponse 객체로 파싱합니다.
         * 테스트 가능하도록 companion object에 정의되었습니다.
         */
        internal fun parseResponse(response: String): AgentResponse {
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

    /**
     * 사용자 메시지를 처리하고 적절한 작업을 수행합니다.
     * sessionId를 conversationId로 사용하여 다른 에이전트와 메모리를 공유합니다.
     */
    fun process(message: String, authorId: Long, sessionId: Long): AgentResponse {
        memoTools.setAuthorId(authorId)

        // RagAgent와 동일한 conversationId 사용하여 컨텍스트 공유
        val conversationId = sessionId.toString()

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

        return Companion.parseResponse(response)
    }
}
