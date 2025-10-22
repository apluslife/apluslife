# API Specification: 게시판 댓글 API

**Feature**: 게시판 댓글 기능
**Date**: 2025-01-21
**API Version**: v1
**Base URL**: `/api/boards/{boardId}/comments`

## Overview

게시판 댓글 기능의 RESTful API 명세입니다. 중첩 리소스 패턴을 사용하여 댓글이 게시글에 종속된 관계를 명확히 표현합니다.

---

## Authentication

모든 API는 Spring Security 세션 기반 인증을 사용합니다.

- **조회 (GET)**: 인증 불필요
- **작성/수정/삭제 (POST/PUT/DELETE)**: 로그인 필수 (`ROLE_USER` 이상)

---

## Endpoints

### 1. 댓글 목록 조회

**GET** `/api/boards/{boardId}/comments`

특정 게시글의 모든 댓글을 작성시간 순으로 조회합니다.

#### Request

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `boardId` | Long | Yes | 게시글 ID |

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| None | - | - | - |

**Headers:**
```
GET /api/boards/123/comments HTTP/1.1
Host: localhost:8080
Accept: application/json
```

#### Response

**Status**: `200 OK`

**Body**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "boardId": 123,
      "authorName": "홍길동",
      "content": "좋은 글 감사합니다!",
      "createdAt": "2025-01-21T10:30:00",
      "updatedAt": null,
      "isModified": false,
      "isAuthor": true
    },
    {
      "id": 2,
      "boardId": 123,
      "authorName": "김철수",
      "content": "도움이 많이 되었습니다.",
      "createdAt": "2025-01-21T11:15:00",
      "updatedAt": "2025-01-21T12:00:00",
      "isModified": true,
      "isAuthor": false
    }
  ],
  "message": "댓글 목록을 조회했습니다.",
  "timestamp": "2025-01-21T14:00:00"
}
```

**Error Responses:**

`404 Not Found` - 게시글이 존재하지 않음
```json
{
  "success": false,
  "error": "BOARD_NOT_FOUND",
  "message": "존재하지 않는 게시글입니다.",
  "timestamp": "2025-01-21T14:00:00"
}
```

---

### 2. 댓글 작성

**POST** `/api/boards/{boardId}/comments`

새로운 댓글을 작성합니다. (로그인 필수)

#### Request

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `boardId` | Long | Yes | 게시글 ID |

**Headers:**
```
POST /api/boards/123/comments HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Accept: application/json
Cookie: JSESSIONID=... (Spring Security 세션)
```

**Body**:
```json
{
  "content": "좋은 글 감사합니다!"
}
```

**Validation Rules:**
- `content`: 필수, 1~1000자

#### Response

**Status**: `201 Created`

**Body**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "boardId": 123,
    "authorName": "홍길동",
    "content": "좋은 글 감사합니다!",
    "createdAt": "2025-01-21T10:30:00",
    "updatedAt": null,
    "isModified": false,
    "isAuthor": true
  },
  "message": "댓글이 작성되었습니다.",
  "timestamp": "2025-01-21T10:30:00"
}
```

**Error Responses:**

`400 Bad Request` - 입력 검증 실패
```json
{
  "success": false,
  "error": "VALIDATION_ERROR",
  "message": "댓글 내용을 입력해주세요.",
  "fieldErrors": {
    "content": "댓글은 1~1000자 이내로 작성해주세요"
  },
  "timestamp": "2025-01-21T10:30:00"
}
```

`401 Unauthorized` - 로그인 필요
```json
{
  "success": false,
  "error": "UNAUTHORIZED",
  "message": "로그인이 필요합니다.",
  "timestamp": "2025-01-21T10:30:00"
}
```

`404 Not Found` - 게시글이 존재하지 않음
```json
{
  "success": false,
  "error": "BOARD_NOT_FOUND",
  "message": "존재하지 않는 게시글입니다.",
  "timestamp": "2025-01-21T10:30:00"
}
```

---

### 3. 댓글 수정

**PUT** `/api/boards/{boardId}/comments/{commentId}`

기존 댓글을 수정합니다. (작성자 본인만 가능)

#### Request

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `boardId` | Long | Yes | 게시글 ID |
| `commentId` | Long | Yes | 댓글 ID |

**Headers:**
```
PUT /api/boards/123/comments/1 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Accept: application/json
Cookie: JSESSIONID=...
```

**Body**:
```json
{
  "content": "수정된 댓글 내용입니다."
}
```

#### Response

**Status**: `200 OK`

**Body**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "boardId": 123,
    "authorName": "홍길동",
    "content": "수정된 댓글 내용입니다.",
    "createdAt": "2025-01-21T10:30:00",
    "updatedAt": "2025-01-21T11:00:00",
    "isModified": true,
    "isAuthor": true
  },
  "message": "댓글이 수정되었습니다.",
  "timestamp": "2025-01-21T11:00:00"
}
```

**Error Responses:**

`403 Forbidden` - 작성자가 아님
```json
{
  "success": false,
  "error": "FORBIDDEN",
  "message": "댓글 수정 권한이 없습니다.",
  "timestamp": "2025-01-21T11:00:00"
}
```

`404 Not Found` - 댓글이 존재하지 않음
```json
{
  "success": false,
  "error": "COMMENT_NOT_FOUND",
  "message": "존재하지 않는 댓글입니다.",
  "timestamp": "2025-01-21T11:00:00"
}
```

`409 Conflict` - 동시 수정 충돌 (낙관적 잠금)
```json
{
  "success": false,
  "error": "OPTIMISTIC_LOCK_ERROR",
  "message": "다른 사용자가 수정했습니다. 새로고침 후 다시 시도해주세요.",
  "timestamp": "2025-01-21T11:00:00"
}
```

---

### 4. 댓글 삭제

**DELETE** `/api/boards/{boardId}/comments/{commentId}`

댓글을 삭제합니다. (작성자 또는 관리자만 가능)

#### Request

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `boardId` | Long | Yes | 게시글 ID |
| `commentId` | Long | Yes | 댓글 ID |

**Headers:**
```
DELETE /api/boards/123/comments/1 HTTP/1.1
Host: localhost:8080
Accept: application/json
Cookie: JSESSIONID=...
```

#### Response

**Status**: `200 OK`

**Body**:
```json
{
  "success": true,
  "data": null,
  "message": "댓글이 삭제되었습니다.",
  "timestamp": "2025-01-21T12:00:00"
}
```

**Error Responses:**

`403 Forbidden` - 권한 없음 (작성자도 아니고 관리자도 아님)
```json
{
  "success": false,
  "error": "FORBIDDEN",
  "message": "댓글 삭제 권한이 없습니다.",
  "timestamp": "2025-01-21T12:00:00"
}
```

`404 Not Found` - 댓글이 존재하지 않음
```json
{
  "success": false,
  "error": "COMMENT_NOT_FOUND",
  "message": "존재하지 않는 댓글입니다.",
  "timestamp": "2025-01-21T12:00:00"
}
```

---

## Response Schema

### CommentResponse

```json
{
  "id": "Long - 댓글 ID",
  "boardId": "Long - 게시글 ID",
  "authorName": "String - 작성자 이름",
  "content": "String - 댓글 내용 (1~1000자)",
  "createdAt": "ISO 8601 DateTime - 작성시간",
  "updatedAt": "ISO 8601 DateTime | null - 수정시간",
  "isModified": "Boolean - 수정 여부",
  "isAuthor": "Boolean - 현재 사용자가 작성자인지 여부"
}
```

### ApiResponse (Generic Wrapper)

```json
{
  "success": "Boolean - 성공 여부",
  "data": "T | null - 응답 데이터",
  "message": "String - 사용자 친화적 메시지",
  "error": "String | null - 에러 코드 (실패 시)",
  "fieldErrors": "Object | null - 필드별 검증 에러 (검증 실패 시)",
  "timestamp": "ISO 8601 DateTime - 응답 시간"
}
```

---

## Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `VALIDATION_ERROR` | 400 | 입력 검증 실패 |
| `UNAUTHORIZED` | 401 | 로그인 필요 |
| `FORBIDDEN` | 403 | 권한 없음 |
| `BOARD_NOT_FOUND` | 404 | 게시글 미존재 |
| `COMMENT_NOT_FOUND` | 404 | 댓글 미존재 |
| `OPTIMISTIC_LOCK_ERROR` | 409 | 동시 수정 충돌 |
| `INTERNAL_SERVER_ERROR` | 500 | 서버 내부 오류 |

---

## Rate Limiting

초기 버전에서는 Rate Limiting을 적용하지 않습니다. 향후 스팸 방지를 위해 다음 제한 고려:

- 댓글 작성: 사용자당 1분에 10개
- 댓글 수정: 댓글당 1분에 5회

---

## CORS Policy

동일 출처 정책 (Same-Origin)을 사용합니다. 필요 시 SecurityConfig에서 CORS 설정 추가.

---

## Security Considerations

1. **XSS 방지**:
   - 서버: 입력 검증 (`@NotBlank`, `@Size`)
   - 클라이언트: Thymeleaf `th:text` 자동 이스케이핑

2. **SQL Injection 방지**:
   - JPA 파라미터 바인딩 자동 적용

3. **CSRF 방지**:
   - Spring Security CSRF 토큰 사용 (기본 활성화)

4. **권한 검증**:
   - Controller: `@PreAuthorize("isAuthenticated()")`
   - Service: `isOwner || hasRole('ADMIN')` 로직 검증

---

## Example Usage (cURL)

### 댓글 목록 조회
```bash
curl -X GET "http://localhost:8080/api/boards/123/comments" \
     -H "Accept: application/json"
```

### 댓글 작성
```bash
curl -X POST "http://localhost:8080/api/boards/123/comments" \
     -H "Content-Type: application/json" \
     -H "Cookie: JSESSIONID=ABC123..." \
     -d '{
       "content": "좋은 글 감사합니다!"
     }'
```

### 댓글 수정
```bash
curl -X PUT "http://localhost:8080/api/boards/123/comments/1" \
     -H "Content-Type: application/json" \
     -H "Cookie: JSESSIONID=ABC123..." \
     -d '{
       "content": "수정된 내용입니다."
     }'
```

### 댓글 삭제
```bash
curl -X DELETE "http://localhost:8080/api/boards/123/comments/1" \
     -H "Cookie: JSESSIONID=ABC123..."
```

---

## Versioning Strategy

현재 API는 버전 1.0입니다. 향후 호환성이 깨지는 변경 시:

- URL Versioning: `/api/v2/boards/{boardId}/comments`
- 또는 Header Versioning: `Accept: application/vnd.apluslife.v2+json`

---

## Change Log

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2025-01-21 | 초기 API 명세 작성 |

---

## Next Steps

- Controller 구현 시 이 명세를 참조하여 일관성 유지
- 통합 테스트에서 모든 엔드포인트 및 에러 케이스 검증
- Postman Collection 생성하여 수동 테스트 지원
