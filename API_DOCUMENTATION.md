# Jiniaslog API Documentation

프론트엔드 개발을 위한 API 문서입니다.

## Base URL
- **Server**: `http://localhost:7777`
- **Production**: `https://www.jiniaslog.co.kr`

---

## 인증 (Authentication)

### OAuth 로그인 URL 조회
```
GET /api/v1/auth/{provider}/url
```
**Parameters:**
- `provider` (path, required): OAuth 제공자 (예: "google")

**Response:**
```json
{
  "url": "string"
}
```

### OAuth 로그인
```
POST /api/v1/auth/{provider}/login
```
**Parameters:**
- `provider` (path, required): OAuth 제공자

**Request Body:**
```json
{
  "code": "string"  // OAuth 인증 코드
}
```

**Response:**
```json
{
  "nickName": "string",
  "email": "string",
  "roles": ["ADMIN", "USER"],
  "picUrl": "string",
  "userId": 123
}
```
*Note: 로그인 성공 시 `jiniaslog_refresh` 쿠키가 설정됨*

### 토큰 갱신
```
POST /api/v1/auth/refresh
```
**Required Cookie:** `jiniaslog_refresh`

**Response:** `OAuthLoginResponse` (위와 동일)

### 로그아웃
```
POST /api/v1/auth/logout
```
**Query Parameters:**
- `userId` (optional): 사용자 ID

---

## 게시글 (Articles)

### 게시글 목록 조회 (카드 형태)
```
GET /api/v1/articles/simple
```
**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `status` | enum | Yes | `DRAFT`, `PUBLISHED`, `DELETED` |
| `cursor` | int64 | No | 페이징 커서 (마지막 게시글 ID) |
| `limit` | int32 | No | 조회 개수 |
| `keyword` | string | No | 검색 키워드 (커서와 함께 사용 불가) |
| `tagName` | string | No | 태그 필터 (커서와 함께 사용 불가) |

### 게시글 상세 조회
```
GET /api/v1/articles/{articleId}
```
**Parameters:**
- `articleId` (path, required): 게시글 ID
- `expectedStatus` (query, required): `DRAFT`, `PUBLISHED`, `DELETED`

### 새 게시글 작성 시작
```
POST /api/v1/articles
```
**Query Parameters:**
- `userId` (optional): 사용자 ID

**Response:**
```json
{
  "articleId": 123
}
```
*초안(DRAFT) 상태로 생성됨*

### 게시글 상태 변경
```
PATCH /api/v1/articles/{articleId}
```
**Request Body:**
```json
{
  "asIsStatus": "DRAFT",      // 현재 상태
  "toBeStatus": "PUBLISHED"   // 변경할 상태
}
```
**Response:**
```json
{
  "articleId": 123
}
```

### 게시글 삭제
```
DELETE /api/v1/articles/{articleId}
```
**Response:**
```json
{
  "articleId": 123
}
```
*논리 삭제 (복원 가능)*

### 게시글에 태그 추가
```
POST /api/v1/articles/{articleId}/tags
```
**Request Body:**
```json
{
  "tagName": "string"
}
```

### 게시글에서 태그 제거
```
DELETE /api/v1/articles/{articleId}/tags/{tagName}
```

---

## 메모 (Memos)

### 폴더 및 메모 전체 조회
```
GET /api/v1/folders
```
**Query Parameters:**
- `userId` (optional): 사용자 ID

**Response:**
```json
{
  "folderInfos": [
    {
      "id": 1,
      "name": "폴더명",
      "parent": null,
      "memos": [
        {
          "id": 1,
          "title": "메모 제목"
        }
      ],
      "sequence": "a"
    }
  ]
}
```

### 메모 검색
```
GET /api/v1/memos
```
**Query Parameters:**
- `query` (required): 검색어
- `userId` (optional): 사용자 ID

**Response:**
```json
{
  "result": {
    "name": "검색 결과",
    "memos": [
      { "id": 1, "title": "메모 제목" }
    ]
  }
}
```

### 메모 상세 조회
```
GET /api/v1/memos/{id}
```
**Response:**
```json
{
  "memoId": 1,
  "title": "메모 제목",
  "content": "메모 내용",
  "references": [
    { "rootId": 1, "referenceId": 2 }
  ]
}
```

### 새 메모 생성
```
POST /api/v1/memos
```
**Request Body:**
```json
{
  "parentFolderId": 1  // optional
}
```
**Response:**
```json
{
  "memoId": 123
}
```

### 메모 삭제
```
DELETE /api/v1/memos/{id}
```

### 메모 순서 변경
```
PUT /api/v1/memos/{memoId}/sequence
```
**Request Body:**
```json
{
  "sequence": "b"  // LexoRank 등 정렬용 문자열
}
```

### 메모 부모 폴더 변경
```
PUT /api/v1/memos/{id}/parent
```
**Request Body:**
```json
{
  "folderId": 1  // null이면 루트로 이동
}
```

### 메모 참조 조회
```
GET /api/v1/memos/{id}/references
```
**Response:**
```json
{
  "references": [
    { "rootId": 1, "referenceId": 2 }
  ]
}
```

### 역참조 메모 조회
```
GET /api/v1/memos/{id}/referenced
```
**Response:**
```json
{
  "referenceds": [
    { "id": 2, "title": "참조하는 메모 제목" }
  ]
}
```

### 관련 메모 추천
```
GET /api/v1/memos/{id}/recommended
```
**Query Parameters:**
- `keyword` (required): 추천용 키워드

**Response:**
```json
{
  "memos": [
    { "memoId": 1, "title": "제목", "content": "내용" }
  ]
}
```

---

## 폴더 (Folders)

### 새 폴더 생성
```
POST /api/v1/folders
```
**Request Body:**
```json
{
  "parentId": 1  // optional, null이면 루트
}
```
**Response:**
```json
{
  "folder": {
    "id": 1,
    "name": "New Folder",
    "parent": null,
    "children": [],
    "memos": []
  }
}
```

### 폴더 이름 변경
```
PUT /api/v1/folders/{folderId}/name
```
**Request Body:**
```json
{
  "folderId": 1,
  "name": "새 폴더명"
}
```

### 폴더 부모 변경
```
PUT /api/v1/folders/{folderId}/parent
```
**Request Body:**
```json
{
  "parentId": 2  // null이면 루트로 이동
}
```

### 폴더 순서 변경
```
PUT /api/v1/folders/{folderId}/sequence
```
**Request Body:**
```json
{
  "sequence": "a"
}
```

### 폴더 삭제
```
DELETE /api/v1/folders/{folderId}
```

---

## 댓글 (Comments)

### 댓글 목록 조회
```
GET /api/v1/comments
```
**Query Parameters:**
- `refId` (required): 참조 대상 ID (예: 게시글 ID)
- `refType` (required): `ARTICLE`

**Response:**
```json
{
  "comments": [
    {
      "id": 1,
      "content": "댓글 내용",
      "nickname": "작성자",
      "authorId": 123,
      "createdAt": "2024-01-01T00:00:00",
      "profileImageUrl": "https://...",
      "deleted": false
    }
  ]
}
```

### 댓글 작성
```
POST /api/v1/comments
```
**Request Body:**
```json
{
  "refId": 1,
  "refType": "ARTICLE",
  "userName": "string",     // 비회원인 경우
  "password": "string",     // 비회원인 경우
  "parentId": 1,            // 대댓글인 경우
  "content": "댓글 내용"
}
```

### 댓글 삭제
```
DELETE /api/v1/comments/{commentId}
```
**Request Body:**
```json
{
  "password": "string"  // 비회원 댓글인 경우 필요
}
```

---

## 태그 (Tags)

### 인기 태그 조회
```
GET /api/v1/tags/top
```
**Query Parameters:**
- `n` (required): 조회할 태그 수

**Response:**
```json
{
  "tags": [
    { "id": 1, "name": "태그명" }
  ]
}
```

---

## 이미지 (Media)

### 이미지 업로드
```
POST /api/v1/media/image
```
**Request Body:** `multipart/form-data`
- `image`: 이미지 파일

**Response:**
```json
{
  "url": "https://..."
}
```

---

## AI Second Brain

### 채팅 세션

#### 세션 목록 조회
```
GET /api/ai/sessions
```
**Query Parameters:**
- `userId` (required): 사용자 ID

**Response:**
```json
[
  {
    "sessionId": 1,
    "title": "세션 제목",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
]
```

#### 새 세션 생성
```
POST /api/ai/sessions
```
**Query Parameters:**
- `userId` (required): 사용자 ID

**Request Body:**
```json
{
  "title": "새 대화"  // optional
}
```
**Response:**
```json
{
  "sessionId": 1,
  "title": "새 대화"
}
```

#### 세션 채팅 히스토리 조회
```
GET /api/ai/sessions/{sessionId}/messages
```
**Parameters:**
- `sessionId` (path, required): 세션 ID
- `userId` (query, required): 사용자 ID

**Response:**
```json
[
  {
    "messageId": 1,
    "role": "USER",        // USER, ASSISTANT, SYSTEM
    "content": "메시지 내용",
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

### AI 챗봇

#### AI와 대화
```
POST /api/ai/chat
```
**Query Parameters:**
- `userId` (required): 사용자 ID

**Request Body:**
```json
{
  "sessionId": 1,
  "message": "오늘 배운 내용을 정리해줘"
}
```
**Response:**
```json
{
  "sessionId": 1,
  "response": "AI 응답 메시지",
  "createdMemoId": 123  // 메모가 생성된 경우
}
```

### 메모 추천

#### 관련 메모 추천
```
GET /api/ai/recommend
```
**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `userId` | int64 | Yes | 사용자 ID |
| `query` | string | No | 검색 쿼리 |
| `memoId` | int64 | No | 현재 메모 ID |
| `topK` | int32 | No | 추천 개수 (기본값: 5) |

**Response:**
```json
[
  {
    "memoId": 1,
    "title": "메모 제목",
    "contentPreview": "내용 미리보기...",
    "similarity": 0.85
  }
]
```

### 임베딩 동기화

#### 메모 임베딩 동기화
```
POST /api/ai/sync
```
**Query Parameters:**
- `userId` (required): 사용자 ID

**Request Body:**
```json
{
  "target": "all"  // "all" 또는 특정 메모 ID
}
```
**Response:**
```json
{
  "syncedCount": 10,
  "message": "동기화 완료"
}
```

---

## SEO

### 사이트맵 조회
```
GET /api/seo/sitemap
```
**Content-Type:** `application/xml;charset=utf-8`

### RSS 피드 조회
```
GET /api/seo/rss
```
**Content-Type:** `application/rss+xml`

---

# WebSocket API

## 연결 설정

### 엔드포인트
```
/ws
```

### 연결 방식
- **Protocol**: STOMP over WebSocket
- **SockJS Fallback**: 지원됨
- **Allowed Origins**:
  - `https://www.jiniaslog.co.kr`
  - `http://localhost:3000`

### JavaScript 연결 예시
```javascript
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const socket = new SockJS('http://localhost:7777/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, (frame) => {
  console.log('Connected: ' + frame);

  // 구독
  stompClient.subscribe('/topic/memoResponse', (message) => {
    console.log(JSON.parse(message.body));
  });
});
```

---

## 메모 WebSocket

### 메모 내용 업데이트
**Destination:** `/app/memo/updateMemo`
**Response Topic:** `/topic/memoResponse`

**Request Payload:**
```json
{
  "type": "UpdateMemo",
  "id": 123,
  "content": "메모 내용",
  "title": "메모 제목"
}
```

**Response:**
```json
{
  "id": 123
}
```

### 메모 참조 업데이트
**Destination:** `/app/memo/updateReferences`
**Response Topic:** `/topic/memoResponse/updateReferences`

**Request Payload:**
```json
{
  "type": "UpdateReferences",
  "id": 123,
  "references": [456, 789]  // 참조할 메모 ID 목록
}
```

**Response:**
```json
{
  "id": 123
}
```

---

## 게시글 WebSocket

### 게시글 내용 업데이트
**Destination:** `/app/article/updateArticle`
**Response Topic:** `/topic/articleResponse`

**Request Payload:**
```json
{
  "type": "UpdateArticle",
  "articleId": 123,
  "content": "게시글 내용",
  "title": "게시글 제목",
  "thumbnailUrl": "https://..."
}
```

**Response:**
```json
{
  "id": 123
}
```

---

## WebSocket 사용 시나리오

### 1. 메모 실시간 저장
메모 편집 시 debounce를 적용하여 변경사항을 WebSocket으로 전송:
```javascript
// 메모 편집 시
stompClient.send('/app/memo/updateMemo', {}, JSON.stringify({
  type: 'UpdateMemo',
  id: memoId,
  title: title,
  content: content
}));
```

### 2. 게시글 실시간 저장
게시글 작성 시 (DRAFT 상태) 실시간으로 저장:
```javascript
stompClient.send('/app/article/updateArticle', {}, JSON.stringify({
  type: 'UpdateArticle',
  articleId: articleId,
  title: title,
  content: content,
  thumbnailUrl: thumbnailUrl
}));
```

---

## 에러 응답

모든 API는 에러 발생 시 다음 HTTP 상태 코드를 반환할 수 있습니다:

| Status Code | Description |
|-------------|-------------|
| 400 | Bad Request - 잘못된 요청 |
| 401 | Unauthorized - 인증 필요 |
| 403 | Forbidden - 권한 없음 |
| 404 | Not Found - 리소스를 찾을 수 없음 |
| 500 | Internal Server Error - 서버 오류 |

---

## 데이터 타입 참조

### ArticleStatus (게시글 상태)
- `DRAFT`: 초안
- `PUBLISHED`: 게시됨
- `DELETED`: 삭제됨

### RefType (참조 타입)
- `ARTICLE`: 게시글

### MessageRole (채팅 메시지 역할)
- `USER`: 사용자 메시지
- `ASSISTANT`: AI 응답
- `SYSTEM`: 시스템 메시지
