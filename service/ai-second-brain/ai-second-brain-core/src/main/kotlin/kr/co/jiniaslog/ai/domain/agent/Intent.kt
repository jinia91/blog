package kr.co.jiniaslog.ai.domain.agent

enum class Intent {
    KNOWLEDGE_QUERY, // 지식 검색/질문 (기존 QUESTION 대체)
    MEMO_WRITE, // 메모 생성/수정 (기존 MEMO_MANAGEMENT에서 분리)
    MEMO_ORGANIZE, // 폴더 관리/이동/정리
    MEMO_SEARCH, // 메모 검색/조회
    GENERAL_CHAT, // 일반 대화 (유지)
    COMPOUND // 복합 의도 (여러 의도가 섞인 요청)
}
