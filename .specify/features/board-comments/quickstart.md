# Quickstart: 게시판 댓글 기능 개발 가이드

**Feature**: 게시판 댓글 기능
**Date**: 2025-01-21
**Target Audience**: A+Life 개발팀

## Overview

이 문서는 게시판 댓글 기능을 개발하고 테스트하는 방법을 단계별로 안내합니다. 처음 프로젝트에 합류한 개발자도 30분 내에 개발 환경을 구축하고 기능을 이해할 수 있도록 작성되었습니다.

---

## Prerequisites (사전 요구사항)

### 필수 도구
- ✅ Java 17 이상
- ✅ IntelliJ IDEA 또는 Eclipse
- ✅ Git
- ✅ Gradle (프로젝트에 포함됨)

### 권장 도구
- Postman (API 테스트)
- H2 Console (데이터베이스 확인)

### 지식 요구사항
- Spring Boot 기본 개념
- JPA 기본 사용법
- RESTful API 이해

---

## Quick Setup (5분 안에 시작하기)

### 1. 저장소 클론 및 브랜치 체크아웃

```bash
# 저장소 클론 (이미 클론한 경우 생략)
git clone <repository-url>
cd apluslife

# Feature 브랜치 체크아웃
git checkout 001-board-comments

# 최신 상태 확인
git pull origin 001-board-comments
```

### 2. 개발 환경 실행

```bash
# Gradle 빌드 및 실행 (Windows)
gradlew.bat bootRun --args='--spring.profiles.active=dev'

# Gradle 빌드 및 실행 (Mac/Linux)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

**실행 확인**:
- 브라우저에서 http://localhost:8080 접속
- 로그에서 `Started AplusLifeApplication` 메시지 확인

### 3. H2 데이터베이스 콘솔 접속

```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (비워두기)
```

**테이블 확인**:
```sql
-- Comment 테이블이 자동 생성되었는지 확인
SELECT * FROM COMMENT;
```

---

## Project Structure (프로젝트 구조)

```
src/main/java/com/apluslife/web/
└── front/
    └── comment/                           # 새로 추가된 댓글 패키지
        ├── controller/
        │   └── BoardCommentController.java   # REST API 엔드포인트
        ├── service/
        │   └── BoardCommentService.java      # 비즈니스 로직
        ├── repository/
        │   └── BoardCommentRepository.java   # JPA 레포지토리
        ├── entity/
        │   └── Comment.java                  # JPA 엔티티
        └── dto/
            ├── CommentRequest.java           # 입력 DTO
            └── CommentResponse.java          # 출력 DTO

src/main/resources/
└── templates/
    └── front/
        └── board/
            ├── detail.html                    # 게시글 상세 (댓글 섹션 추가)
            └── fragments/
                └── comment-section.html       # 댓글 UI fragment

src/test/java/com/apluslife/web/
└── front/
    └── comment/
        ├── service/
        │   └── BoardCommentServiceTest.java
        └── controller/
            └── BoardCommentControllerTest.java
```

---

## Development Workflow (개발 워크플로우)

### Step 1: Entity 생성

**파일**: `src/main/java/com/apluslife/web/front/comment/entity/Comment.java`

핵심 포인트:
- `@ManyToOne` 관계로 Board, User 참조
- `@Version` 필드로 낙관적 잠금
- `@CreatedDate`, `@LastModifiedDate`로 자동 시간 관리

참고 문서: [data-model.md](./data-model.md)

### Step 2: Repository 생성

**파일**: `src/main/java/com/apluslife/web/front/comment/repository/BoardCommentRepository.java`

핵심 메소드:
- `findByBoardIdOrderByCreatedAtAsc()` - 게시글별 댓글 조회
- `countByBoardId()` - 댓글 개수
- `findByBoardIdWithUserAndBoard()` - N+1 방지용 FETCH JOIN

### Step 3: Service 생성

**파일**: `src/main/java/com/apluslife/web/front/comment/service/BoardCommentService.java`

핵심 비즈니스 로직:
- 댓글 작성: 로그인 사용자 검증
- 댓글 수정: 작성자 본인 확인
- 댓글 삭제: 작성자 또는 관리자 확인

**권한 검증 예시**:
```java
@Transactional
public CommentResponse updateComment(Long commentId, CommentRequest request, Long currentUserId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException(commentId));

    // 작성자 본인 확인
    if (!comment.isAuthor(currentUserId)) {
        throw new ForbiddenException("댓글 수정 권한이 없습니다.");
    }

    comment.updateContent(request.getContent());
    return CommentResponse.from(comment, currentUserId);
}
```

### Step 4: Controller 생성

**파일**: `src/main/java/com/apluslife/web/front/comment/controller/BoardCommentController.java`

핵심 어노테이션:
- `@PreAuthorize("isAuthenticated()")` - 로그인 필수
- `@Valid` - 입력 검증
- `@PathVariable`, `@RequestBody` - 요청 파라미터 매핑

참고 문서: [contracts/api-spec.md](./contracts/api-spec.md)

### Step 5: Thymeleaf 템플릿 생성

**파일**: `src/main/resources/templates/front/board/fragments/comment-section.html`

Fragment 구조:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">

<!-- 댓글 목록 -->
<div th:fragment="comment-list">
    <div th:each="comment : ${comments}" class="comment-item">
        <strong th:text="${comment.authorName}">작성자</strong>
        <span th:text="${comment.createdAt}">작성시간</span>
        <p th:text="${comment.content}">댓글 내용</p>

        <!-- 수정/삭제 버튼 (작성자 본인만 표시) -->
        <div th:if="${comment.isAuthor}">
            <button class="btn-edit">수정</button>
            <button class="btn-delete">삭제</button>
        </div>
    </div>
</div>

<!-- 댓글 작성 폼 (로그인 사용자만 표시) -->
<div th:fragment="comment-form" sec:authorize="isAuthenticated()">
    <form id="comment-form">
        <textarea name="content" placeholder="댓글을 입력하세요" maxlength="1000"></textarea>
        <button type="submit">등록</button>
    </form>
</div>
</html>
```

**XSS 방지**: `th:text` 사용 (자동 이스케이핑)

---

## Testing Guide (테스트 가이드)

### 단위 테스트 실행

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스만 실행
./gradlew test --tests BoardCommentServiceTest

# 특정 테스트 메소드만 실행
./gradlew test --tests BoardCommentServiceTest.testCreateComment
```

### 통합 테스트 예시

**파일**: `src/test/java/com/apluslife/web/front/comment/controller/BoardCommentControllerTest.java`

```java
@SpringBootTest
@AutoConfigureMockMvc
class BoardCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void 댓글_작성_성공() throws Exception {
        String requestBody = "{\"content\":\"테스트 댓글\"}";

        mockMvc.perform(post("/api/boards/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("테스트 댓글"));
    }

    @Test
    void 댓글_작성_실패_로그인_필요() throws Exception {
        String requestBody = "{\"content\":\"테스트 댓글\"}";

        mockMvc.perform(post("/api/boards/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized());
    }
}
```

---

## Manual Testing (수동 테스트)

### 1. 테스트 데이터 준비

**H2 콘솔**에서 실행:
```sql
-- 테스트 사용자 생성 (이미 존재하는 경우 생략)
INSERT INTO member (id, pwd, email, name, manage_level)
VALUES ('testuser', '{bcrypt}...', 'test@test.com', '테스트사용자', 'U');

-- 테스트 게시글 생성
INSERT INTO board (title, content, author, created_at)
VALUES ('테스트 게시글', '내용', 'testuser', NOW());
```

### 2. Postman으로 API 테스트

#### 2.1 로그인
```
POST http://localhost:8080/login
Content-Type: application/x-www-form-urlencoded

username=testuser
password=user123
```

**중요**: 응답 헤더에서 `Set-Cookie: JSESSIONID=...` 값 저장

#### 2.2 댓글 작성
```
POST http://localhost:8080/api/boards/1/comments
Content-Type: application/json
Cookie: JSESSIONID=ABC123...

{
  "content": "테스트 댓글입니다."
}
```

#### 2.3 댓글 목록 조회
```
GET http://localhost:8080/api/boards/1/comments
Accept: application/json
```

### 3. 브라우저에서 UI 테스트

1. http://localhost:8080/login 접속
2. 로그인 (testuser / user123)
3. 게시글 상세 페이지 접속
4. 댓글 작성 폼에서 댓글 입력 후 "등록" 클릭
5. 댓글이 게시글 하단에 표시되는지 확인
6. "수정", "삭제" 버튼 동작 확인

---

## Troubleshooting (문제 해결)

### Q1: Comment 테이블이 생성되지 않음

**원인**: JPA ddl-auto 설정 문제

**해결**:
```yaml
# application-dev.yml 확인
spring:
  jpa:
    hibernate:
      ddl-auto: update  # create 또는 update여야 함
```

### Q2: 댓글 작성 시 401 Unauthorized

**원인**: Spring Security 세션 없음

**해결**:
- Postman에서 로그인 후 JSESSIONID 쿠키 포함 확인
- `@PreAuthorize` 어노테이션 확인

### Q3: N+1 쿼리 문제 발생

**원인**: Lazy Loading으로 인한 개별 쿼리

**해결**:
```java
// Repository에서 FETCH JOIN 사용
@Query("SELECT c FROM Comment c JOIN FETCH c.user JOIN FETCH c.board WHERE c.board.id = :boardId")
List<Comment> findByBoardIdWithUserAndBoard(@Param("boardId") Long boardId);
```

### Q4: XSS 공격이 막히지 않음

**원인**: `th:utext` 사용 (이스케이핑 비활성화)

**해결**:
```html
<!-- 잘못된 예 -->
<p th:utext="${comment.content}"></p>

<!-- 올바른 예 -->
<p th:text="${comment.content}"></p>
```

---

## Performance Tips (성능 최적화 팁)

### 1. 댓글 조회 최적화
- FETCH JOIN으로 N+1 방지
- `@EntityGraph` 사용 고려

### 2. 댓글 개수 캐싱
- 게시글 조회가 빈번한 경우 Board 엔티티에 `commentCount` 필드 추가
- 댓글 작성/삭제 시 증감 로직 추가

### 3. 인덱스 활용
- `board_id`, `created_at` 컬럼에 인덱스 자동 생성됨
- 실행 계획 확인: `EXPLAIN SELECT * FROM comment WHERE board_id = 1`

---

## Next Steps (다음 단계)

### 추가 기능 구현 (선택사항)
- [ ] 대댓글 (답글) 기능
- [ ] 댓글 좋아요 기능
- [ ] 댓글 신고 기능
- [ ] 댓글 알림 기능

### 배포 준비
- [ ] 운영 환경 마이그레이션 스크립트 준비
- [ ] 성능 테스트 (JMeter)
- [ ] 보안 검토 체크리스트 완료
- [ ] 사용자 가이드 문서 작성

---

## Resources (참고 자료)

### 프로젝트 문서
- [spec.md](./spec.md) - 기능 명세서
- [plan.md](./plan.md) - 구현 계획
- [data-model.md](./data-model.md) - 데이터 모델
- [contracts/api-spec.md](./contracts/api-spec.md) - API 명세

### 헌장 및 가이드라인
- [constitution.md](../../memory/constitution.md) - A+Life 프로젝트 헌장

### 외부 문서
- [Spring Data JPA 공식 문서](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security 공식 문서](https://docs.spring.io/spring-security/reference/)
- [Thymeleaf 공식 문서](https://www.thymeleaf.org/documentation.html)

---

## Support (지원)

문제가 발생하거나 질문이 있는 경우:

1. **이슈 생성**: GitHub Issues에 버그 리포트 또는 질문 등록
2. **팀 채널**: Slack #apluslife-dev 채널에서 질문
3. **코드 리뷰**: Pull Request에서 피드백 요청

---

**Happy Coding!** 🚀
