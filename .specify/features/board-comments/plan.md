# Implementation Plan: 게시판 댓글 기능

**Branch**: `001-board-comments` | **Date**: 2025-01-21 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `.specify/features/board-comments/spec.md`

## Summary

A+Life 홈페이지에 게시판 댓글 기능을 추가합니다. 사용자는 게시글에 댓글을 작성, 조회, 수정, 삭제할 수 있으며, 관리자는 모든 댓글을 관리할 수 있습니다. Spring Boot 계층형 아키텍처를 따르며, Spring Data JPA를 사용하여 Comment 엔티티를 관리하고, Thymeleaf로 UI를 구현합니다. 보안은 Spring Security로 관리되며, XSS 방지를 위해 입력값 이스케이핑을 적용합니다.

## Technical Context

**Language/Version**: Java 17+
**Primary Dependencies**: Spring Boot 3.5.6, Spring Data JPA, Spring Security 6, Thymeleaf 3, MyBatis 3.0.3
**Storage**: MSSQL (prod), H2 (dev) - JPA 기반 Comment 엔티티 관리
**Testing**: JUnit 5, Spring Boot Test, Mockito
**Target Platform**: Web Application (서버 사이드 렌더링)
**Project Type**: Web - 기존 모놀리식 Spring Boot 애플리케이션에 기능 추가
**Performance Goals**:

- 댓글 50개 포함 게시글 상세 페이지 로드 < 2초
- 댓글 작성/수정/삭제 응답 시간 < 1초
- 동시 100명 사용자 처리 가능
  **Constraints**:
- 댓글 내용 1~1000자 제한
- XSS 공격 100% 차단
- Spring Security ADMIN/USER 역할 기반 접근 제어
  **Scale/Scope**:
- 초기 예상 사용자 1,000명
- 게시글당 평균 10개 댓글
- 전체 댓글 수 10,000개 이내

## Constitution Check

_GATE: Must pass before Phase 0 research. Re-check after Phase 1 design._

### ✅ I. 계층형 아키텍처 (Layered Architecture)

- **Presentation Layer**: `BoardCommentController` - HTTP 요청/응답, 입력 검증
- **Business Layer**: `BoardCommentService` - 댓글 CRUD 비즈니스 로직, 권한 검증
- **Data Access Layer**: `BoardCommentRepository` (JPA), `Comment` 엔티티
- **Infrastructure Layer**: 기존 SecurityConfig, DatabaseConfig 활용

**상태**: ✅ PASS - 명확한 계층 분리

### ✅ II. 도메인 중심 설계 (Domain-Driven Design)

- 패키지: `com.apluslife.web.front.comment` (공개 게시판 댓글 도메인)
  - `controller/BoardCommentController.java`
  - `service/BoardCommentService.java`
  - `repository/BoardCommentRepository.java`
  - `entity/Comment.java`
  - `dto/CommentRequest.java`, `CommentResponse.java`

**상태**: ✅ PASS - front 도메인에 comment 하위 패키지 생성

### ✅ III. 보안 우선 (Security-First) - NON-NEGOTIABLE

- **인증/인가**: `@PreAuthorize("hasRole('USER')")` - 댓글 작성/수정/삭제
- **인증/인가**: `@PreAuthorize("hasRole('ADMIN')")` - 모든 댓글 삭제
- **입력 검증**: `@Valid`, `@NotBlank`, `@Size(min=1, max=1000)` 사용
- **SQL 인젝션 방지**: JPA 파라미터 바인딩 사용 (자동)
- **XSS 방지**: Thymeleaf 자동 이스케이핑 (`th:text`)
- **보안 헤더**: 기존 SecurityConfig에서 이미 적용됨

**상태**: ✅ PASS - 모든 보안 요구사항 충족

### ✅ IV. 환경별 설정 분리 (Environment Separation)

- 기존 application-dev.yml, application-prod.yml 활용
- 추가 설정 불필요 (기존 JPA 설정으로 Comment 엔티티 자동 관리)

**상태**: ✅ PASS - 기존 설정 재사용

### ✅ V. 테스트 가능성 (Testability)

- **의존성 주입**: 생성자 주입 (`@RequiredArgsConstructor`)
- **단위 테스트**: `BoardCommentServiceTest` - 비즈니스 로직 테스트
- **통합 테스트**: `BoardCommentControllerTest` - Controller → Service → Repository 전체 흐름
- **테스트 데이터**: H2 인메모리 DB, `@DataJpaTest`, `@Transactional` 격리

**상태**: ✅ PASS - 테스트 전략 명확

### ✅ VI. 관찰 가능성 (Observability)

- **로깅**: 기존 LoggingInterceptor 활용 (자동 요청/응답 로깅)
- **예외 처리**: 기존 GlobalExceptionHandler 활용, 필요시 댓글 전용 예외 추가
- **모니터링**: 댓글 작성/삭제 이벤트 로깅 추가

**상태**: ✅ PASS - 기존 인프라 활용

### ✅ VII. 코드 컨벤션 준수 (Code Conventions)

- **네이밍**: camelCase (commentId, createdAt), PascalCase (Comment, CommentService)
- **패키지**: `com.apluslife.web.front.comment.{layer}`
- **트랜잭션**: `@Transactional` on Service 메소드
- **권한 검증**: `@PreAuthorize` on Controller 메소드
- **주석**: JavaDoc for public API, 복잡한 로직에 인라인 주석

**상태**: ✅ PASS - 컨벤션 준수 계획

### 🔍 헌장 검증 결과

**전체 상태**: ✅ **PASS** - 모든 원칙 준수, 연구 단계 진행 가능

## Project Structure

### Documentation (this feature)

```
.specify/features/board-comments/
├── spec.md              # ✅ 기능 명세서
├── plan.md              # ✅ 이 파일
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (API 명세)
└── tasks.md             # Phase 2 output (/speckit.tasks 명령)
```

### Source Code (repository root)

## 패키지 관리 구조

### 관리자(Admin) 기능: `manager` 패키지
### 회원(Member) 기능: `web` 패키지

```
src/main/java/com/apluslife/

# 관리자 기능 - manager 패키지
manager/
├── controller/
│   ├── AnnouncementController.java        # 공시자료 관리
│   ├── LifeNewsController.java            # 라이프뉴스 관리
│   └── AdminMainController.java           # 관리자 대시보드, 로그인
└── ...

# 회원/공개 기능 - web 패키지
web/
├── controller/
│   ├── MainController.java                # 공개 페이지 (홈, 회사소개, 게시판)
│   ├── BoardController.java               # 게시판 조회 (회원용)
│   ├── MemberController.java              # 회원 기능
│   └── NewsController.java                # 뉴스 조회 (회원용)
└── ...

# 도메인별 비즈니스 로직 - domain 패키지
domain/
├── announcement/                          # 공시자료
│   ├── entity/
│   ├── dto/
│   ├── service/
│   ├── repository/
│   └── mapper/ (MyBatis)
├── lifenews/                              # 라이프뉴스
│   ├── entity/
│   ├── dto/
│   ├── service/
│   ├── mapper/ (MyBatis)
│   └── ...
├── board/                                 # 게시판
├── member/                                # 회원
└── ...

src/main/resources/
└── templates/
    ├── manager/                           # 관리자 UI
    │   ├── announcements/
    │   ├── lifenews/
    │   ├── admin.html
    │   └── fragments/
    ├── web/                               # 공개/회원 UI
    │   ├── index.html
    │   ├── login.html
    │   └── ...
    └── mybatis/mapper/                    # MyBatis SQL 매핑
        └── *.xml

src/test/java/com/apluslife/
├── manager/
│   └── controller/
│       ├── AnnouncementControllerTest.java
│       └── LifeNewsControllerTest.java
└── domain/
    ├── announcement/
    │   └── service/
    │       └── AnnouncementServiceTest.java
    └── lifenews/
        └── service/
            └── LifeNewsServiceTest.java
```

### 패키지 설계 원칙

**1. 역할 기반 분리 (Role-based Separation)**
- `manager/controller/` - 관리자 기능만 포함
  - `/admin/**` 경로로 매핑
  - ADMIN 권한만 접근 가능
  - AdminMainController는 로그인 페이지 관리

- `web/controller/` - 공개/회원 기능만 포함
  - `/`, `/login`, `/board/**` 등의 경로
  - 공개 또는 USER 권한만 접근 가능
  - MainController는 공개 페이지만 관리

**2. 도메인 중심 설계 (Domain-Driven Design)**
- 비즈니스 로직은 `domain/` 패키지에서 관리
- 각 도메인마다 명확한 계층 구조:
  - `entity/` - JPA 엔티티
  - `dto/` - 요청/응답 DTO
  - `service/` - 비즈니스 로직
  - `repository/` - JPA 리포지토리
  - `mapper/` - MyBatis 매퍼 (필요시)

**3. 지속적인 확장성**
- 새로운 관리자 기능은 `manager/controller/` 에 추가
- 새로운 회원 기능은 `web/controller/` 에 추가
- 새로운 도메인은 `domain/` 에 패키지 추가

**Structure Decision**: Constitution의 도메인 중심 설계 원칙을 따르며, 관리자와 회원 기능을 명확히 분리하여 보안성과 유지보수성을 강화합니다.

## Complexity Tracking

_이 섹션은 Constitution Check 위반 시에만 작성됩니다._

**상태**: N/A - 위반 없음
