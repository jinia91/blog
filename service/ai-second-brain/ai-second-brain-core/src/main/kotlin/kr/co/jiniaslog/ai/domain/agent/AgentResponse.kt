package kr.co.jiniaslog.ai.domain.agent

sealed interface AgentResponse {
    // 일반 채팅 응답
    data class ChatResponse(val content: String) : AgentResponse

    // 메모 관련 응답
    data class MemoCreated(val memoId: Long, val title: String, val message: String) : AgentResponse
    data class MemoUpdated(val memoId: Long, val message: String) : AgentResponse
    data class MemoMoved(val memoId: Long, val folderId: Long?, val message: String) : AgentResponse

    // 폴더 관련 응답
    data class FolderCreated(val folderId: Long, val name: String, val message: String) : AgentResponse
    data class FolderRenamed(val folderId: Long, val name: String, val message: String) : AgentResponse
    data class FolderMoved(val folderId: Long, val parentFolderId: Long?, val message: String) : AgentResponse

    // 삭제 완료 응답
    data class Deleted(
        val type: DeleteType,
        val targetId: Long,
        val message: String
    ) : AgentResponse {
        enum class DeleteType {
            MEMO, FOLDER
        }
    }

    // 조회 응답
    data class MemoList(val memos: List<MemoSummary>, val message: String) : AgentResponse {
        data class MemoSummary(val id: Long, val title: String, val folderId: Long?)
    }

    data class FolderList(val folders: List<FolderSummary>, val message: String) : AgentResponse {
        data class FolderSummary(val id: Long, val name: String, val parentFolderId: Long?)
    }

    // 오류 응답
    data class Error(val message: String) : AgentResponse
}
