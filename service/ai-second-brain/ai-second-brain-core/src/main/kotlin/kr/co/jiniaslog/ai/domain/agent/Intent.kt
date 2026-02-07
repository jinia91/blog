package kr.co.jiniaslog.ai.domain.agent

enum class Intent {
    QUESTION, // 질문 - RAG 필요
    MEMO_MANAGEMENT, // 메모/폴더 관리 요청 (생성, 수정, 삭제, 이동, 조회)
    GENERAL_CHAT // 일반 대화
}
