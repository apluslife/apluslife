# Research: 게시판 댓글 기능

**Date**: 2025-01-21
**Feature**: 게시판 댓글 기능
**Status**: Completed

## Overview

A+Life 홈페이지에 게시판 댓글 기능을 추가하기 위한 기술 조사 문서입니다. 기존 Spring Boot 아키텍처를 활용하여 최소한의 변경으로 안전하게 기능을 추가하는 방법을 연구했습니다.

---

## 1. JPA 엔티티 설계: Comment 엔티티

### Decision
`Comment` 엔티티는 다음 구조로 설계합니다:
- `id` (Long, @Id, @GeneratedValue) - 기본 키
- `board` (Board, @ManyToOne) - 게시글 외래키
- `user` (User, @ManyToOne) - 작성자 외래키
- `content` (String) - 댓글 내용 (1~1000자)
- `createdAt` (LocalDateTime) - 작성시간
- `updatedAt` (LocalDateTime) - 수정시간

### Rationale
- **JPA 관계 매핑**: `@ManyToOne`으로 Board, User와 관계 설정
- **양방향 관계**: Board 엔티티에 `@OneToMany List<Comment> comments` 추가하여 댓글 개수 조회 최적화
- **Cascade 설정**: Board 삭제 시 댓글 자동 삭제 (CascadeType.REMOVE)
- **Lazy Loading**: 성능 최적화를 위해 기본 FetchType.LAZY 사용
- **Auditing**: `@CreatedDate`, `@LastModifiedDate` 사용하여 자동 시간 관리

### Alternatives Considered
- **별도 테이블 없이 JSON 컬럼 사용**: 거부 - 쿼리 성능 저하, JPA 관계 매핑 불가
- **NoSQL(MongoDB) 사용**: 거부 - 기존 MSSQL 아키텍처와 불일치, 복잡도 증가
- **Embedded 타입**: 거부 - 댓글은 독립 엔티티로 관리 필요

---

## 2. 댓글 개수 조회 최적화 전략

### Decision
**방법 1 (초기 구현)**: Board 엔티티의 `@OneToMany` 관계에서 `comments.size()` 사용
**방법 2 (성능 최적화 필요 시)**: Board 엔티티에 `commentCount` 필드 추가하고 댓글 작성/삭제 시 증감

### Rationale
- **방법 1의 장점**: 구현 단순, 데이터 정합성 보장, 추가 컬럼 불필요
- **방법 1의 단점**: 게시글 목록 조회 시 N+1 쿼리 문제 발생 가능
- **방법 2의 장점**: 게시글 목록 조회 성능 우수 (단일 SELECT 쿼리)
- **방법 2의 단점**: 댓글 작성/삭제 시 Board 업데이트 필요, 정합성 관리 복잡

**초기 구현**: 방법 1 사용, 성능 이슈 발생 시 방법 2로 전환
**N+1 해결**: `@EntityGraph` 또는 JPQL `LEFT JOIN FETCH` 사용

### Alternatives Considered
- **Redis 캐싱**: 거부 - 초기 구현 단계에서 과도한 복잡도, 인프라 추가 필요
- **MyBatis 커스텀 쿼리**: 고려 가능 - 복잡한 조회가 필요한 경우 MyBatis로 최적화된 쿼리 작성

---

## 3. Spring Security 권한 제어 설계

### Decision
- **댓글 작성**: `@PreAuthorize("isAuthenticated()")` - 로그인한 모든 사용자
- **댓글 수정**: 작성자 본인만 가능 - Service 계층에서 `comment.getUser().getId().equals(currentUserId)` 검증
- **댓글 삭제**: 작성자 또는 관리자 - Service 계층에서 `isOwner || hasRole('ADMIN')` 검증
- **댓글 조회**: 인증 불필요 - 비로그인 사용자도 조회 가능

### Rationale
- **Controller 레벨 인증**: `@PreAuthorize`로 기본 인증 체크
- **Service 레벨 권한**: 세부 권한(본인 여부)은 비즈니스 로직에서 검증
- **일관성**: 기존 A+Life 보안 아키텍처와 동일한 패턴 사용

### Alternatives Considered
- **Spring Security ACL**: 거부 - 과도한 복잡도, 단순 소유권 검증에는 불필요
- **Custom Voter**: 거부 - 간단한 로직이므로 Service 레벨 검증으로 충분
- **AOP 기반 권한 검증**: 고려 가능 - 재사용성이 필요한 경우 적용

---

## 4. XSS 방지 전략

### Decision
- **Thymeleaf 자동 이스케이핑**: `th:text="${comment.content}"` 사용 (기본 설정)
- **입력값 검증**: `@NotBlank`, `@Size(max=1000)` 사용
- **추가 검증 불필요**: HTML 태그 허용 안 함 (일반 텍스트만)

### Rationale
- **Thymeleaf 기본 동작**: `th:text`는 자동으로 HTML 이스케이핑 적용
- **헌장 준수**: Constitution III. 보안 우선 원칙의 XSS 방지 요구사항 충족
- **단순성**: 추가 라이브러리 불필요, 기존 Thymeleaf 기능 활용

### Alternatives Considered
- **OWASP Java HTML Sanitizer**: 거부 - 댓글은 순수 텍스트만 허용하므로 불필요
- **Markdown 지원**: 거부 - 초기 요구사항에 없음, 향후 확장 시 고려
- **HTML 태그 일부 허용**: 거부 - 보안 리스크 증가, 요구사항에 없음

---

## 5. 동시성 제어: 낙관적 잠금 (Optimistic Locking)

### Decision
Comment 엔티티에 `@Version Long version` 필드 추가

```java
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;  // JPA 낙관적 잠금

    // ... 기타 필드
}
```

### Rationale
- **동시 수정 방지**: 두 사용자가 동시에 같은 댓글을 수정하려는 경우 감지
- **OptimisticLockException**: 충돌 시 예외 발생, 사용자에게 재시도 안내
- **성능**: 비관적 잠금 대비 성능 우수, 댓글 수정은 저빈도 작업
- **JPA 표준**: `@Version` 어노테이션으로 간단히 구현 가능

### Alternatives Considered
- **비관적 잠금 (Pessimistic Locking)**: 거부 - 댓글 동시 수정은 드문 케이스, 불필요한 DB 잠금
- **타임스탬프 기반 검증**: 거부 - JPA `@Version`이 더 안전하고 표준적
- **잠금 없음**: 거부 - 명세서의 엣지 케이스 요구사항 미충족

---

## 6. 테스트 전략

### Decision

#### 6.1 단위 테스트 (`BoardCommentServiceTest`)
- **도구**: JUnit 5, Mockito
- **범위**: Service 계층 비즈니스 로직
- **Mock**: Repository, Security Context
- **테스트 케이스**:
  - 댓글 작성 성공
  - 댓글 수정 성공 (본인)
  - 댓글 수정 실패 (타인)
  - 댓글 삭제 성공 (본인)
  - 댓글 삭제 성공 (관리자)
  - 댓글 삭제 실패 (권한 없음)
  - 입력 검증 실패 (빈 내용, 1000자 초과)

#### 6.2 통합 테스트 (`BoardCommentControllerTest`)
- **도구**: `@SpringBootTest`, `@AutoConfigureMockMvc`
- **범위**: Controller → Service → Repository → H2 DB
- **테스트 케이스**:
  - POST /api/comments - 댓글 작성
  - PUT /api/comments/{id} - 댓글 수정
  - DELETE /api/comments/{id} - 댓글 삭제
  - GET /boards/{id} - 게시글 상세 (댓글 목록 포함)

#### 6.3 Repository 테스트 (`BoardCommentRepositoryTest`)
- **도구**: `@DataJpaTest`
- **범위**: JPA 쿼리 메소드 검증
- **테스트 케이스**:
  - `findByBoardIdOrderByCreatedAtAsc` - 게시글별 댓글 조회
  - `countByBoardId` - 게시글별 댓글 개수

### Rationale
- **테스트 피라미드**: 단위 > 통합 > E2E 순서로 커버리지 확보
- **H2 인메모리 DB**: 빠른 테스트 실행, 격리 보장
- **커버리지 목표**: 85% 이상 (헌장 준수)

### Alternatives Considered
- **Testcontainers (실제 MSSQL)**: 거부 - 초기 단계에서 불필요, H2로 충분
- **E2E 테스트 (Selenium)**: 거부 - 초기 요구사항 없음, 수동 테스트로 대체

---

## 7. API 설계 패턴

### Decision
RESTful API 설계:
- `POST /api/comments` - 댓글 작성
- `GET /api/comments?boardId={boardId}` - 댓글 목록 조회
- `PUT /api/comments/{id}` - 댓글 수정
- `DELETE /api/comments/{id}` - 댓글 삭제

**또는** 중첩 리소스 패턴 (추천):
- `POST /api/boards/{boardId}/comments` - 댓글 작성
- `GET /api/boards/{boardId}/comments` - 댓글 목록 조회
- `PUT /api/boards/{boardId}/comments/{id}` - 댓글 수정
- `DELETE /api/boards/{boardId}/comments/{id}` - 댓글 삭제

### Rationale
- **중첩 리소스 패턴**: 댓글은 게시글에 종속된 리소스이므로 URL에 명시적으로 표현
- **RESTful 원칙**: HTTP 메소드 의미 준수 (POST=생성, PUT=수정, DELETE=삭제)
- **일관성**: A+Life 기존 API 패턴 확인 후 결정 필요

### Alternatives Considered
- **GraphQL**: 거부 - 기존 REST 아키텍처와 불일치
- **RPC 스타일 (/createComment)**: 거부 - RESTful 원칙 위반

---

## 8. UI/UX 구현 방식

### Decision
- **Thymeleaf Server-Side Rendering**: 게시글 상세 페이지에 댓글 섹션 포함
- **Fragment 활용**: `templates/front/board/fragments/comment-section.html` 생성
- **AJAX 부분 업데이트** (선택사항): 댓글 작성/삭제 시 페이지 새로고침 없이 업데이트

### Rationale
- **일관성**: 기존 A+Life Thymeleaf 기반 아키텍처 유지
- **단순성**: 초기 구현은 전체 페이지 렌더링, 필요 시 AJAX로 개선
- **점진적 개선**: Fragment 분리로 재사용성 확보

### Alternatives Considered
- **SPA (React/Vue)**: 거부 - 기존 아키텍처와 불일치, 복잡도 증가
- **htmx**: 고려 가능 - AJAX 부분 업데이트가 필요한 경우 경량 라이브러리 사용

---

## 9. 로깅 및 모니터링

### Decision
- **기본 로깅**: 기존 LoggingInterceptor 활용 (자동 요청/응답 로깅)
- **비즈니스 이벤트 로깅**:
  - `logger.info("댓글 작성: boardId={}, userId={}", boardId, userId)`
  - `logger.warn("댓글 삭제 실패: 권한 없음 - commentId={}, userId={}", id, userId)`
- **예외 로깅**: GlobalExceptionHandler에서 자동 처리

### Rationale
- **기존 인프라 활용**: 새로운 로깅 설정 불필요
- **추적 가능성**: 헌장 VI. 관찰 가능성 원칙 준수
- **문제 해결**: 사용자 행동 추적, 보안 이벤트 모니터링

### Alternatives Considered
- **Elastic Stack (ELK)**: 거부 - 초기 단계에서 과도한 인프라
- **Custom Audit 테이블**: 거부 - 현재 요구사항 없음, Log4j2로 충분

---

## 10. 마이그레이션 전략

### Decision
#### 10.1 데이터베이스 마이그레이션
- **개발 환경**: JPA `ddl-auto=update` 사용 (자동 테이블 생성)
- **운영 환경**: JPA `ddl-auto=validate` + 수동 SQL 스크립트

**초기 테이블 생성 스크립트** (MSSQL):
```sql
CREATE TABLE comment (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    board_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content NVARCHAR(1000) NOT NULL,
    version BIGINT DEFAULT 0,
    created_at DATETIME2 NOT NULL,
    updated_at DATETIME2,
    FOREIGN KEY (board_id) REFERENCES board(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES member(idx)
);

CREATE INDEX idx_comment_board_id ON comment(board_id);
CREATE INDEX idx_comment_created_at ON comment(created_at);
```

#### 10.2 배포 계획
1. **개발 환경**: Feature 브랜치에서 개발 및 테스트
2. **통합 환경**: develop 브랜치 병합 후 통합 테스트
3. **운영 배포**: 마이그레이션 스크립트 실행 후 애플리케이션 배포

### Rationale
- **안전성**: 운영 환경에서는 수동 마이그레이션으로 데이터 손실 방지
- **버전 관리**: SQL 스크립트를 Git에 포함하여 이력 관리
- **롤백 가능**: 문제 발생 시 테이블 삭제 후 롤백 가능 (초기 기능이므로 데이터 손실 위험 낮음)

### Alternatives Considered
- **Flyway/Liquibase**: 고려 가능 - 향후 마이그레이션이 빈번해지면 도입
- **무중단 배포**: 거부 - 초기 기능이므로 짧은 다운타임 허용

---

## Summary

모든 기술적 결정은 **A+Life 헌장의 7가지 원칙을 준수**하며, **기존 Spring Boot 아키텍처를 최대한 활용**하는 방향으로 수립되었습니다.

### 핵심 기술 스택
- **Backend**: Spring Boot 3.5.6, Spring Data JPA, Spring Security 6
- **Frontend**: Thymeleaf 3, Bootstrap 5, jQuery
- **Database**: MSSQL (prod), H2 (dev)
- **Testing**: JUnit 5, Mockito, Spring Boot Test

### 다음 단계
Phase 1 (Design)에서 다음 아티팩트를 생성합니다:
1. **data-model.md**: Comment 엔티티 상세 설계
2. **contracts/**: REST API 명세 (OpenAPI)
3. **quickstart.md**: 개발자 가이드

**연구 완료**: 모든 NEEDS CLARIFICATION 해결됨 ✅
