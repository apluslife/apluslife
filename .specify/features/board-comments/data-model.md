# Data Model: 게시판 댓글 기능

**Feature**: 게시판 댓글 기능
**Date**: 2025-01-21
**Status**: Design Complete

## Overview

게시판 댓글 기능의 데이터 모델을 정의합니다. Comment 엔티티를 중심으로 Board, User와의 관계를 설계하고, JPA 매핑 전략과 데이터베이스 스키마를 상세히 기술합니다.

---

## Entity Relationship Diagram (ERD)

```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│     User        │         │    Comment      │         │     Board       │
│   (member)      │         │   (comment)     │         │    (board)      │
├─────────────────┤         ├─────────────────┤         ├─────────────────┤
│ idx (PK)        │◄───────┐│ id (PK)         │┌───────►│ id (PK)         │
│ id              │        ││ board_id (FK)   ││        │ title           │
│ name            │        ││ user_id (FK)    ││        │ content         │
│ email           │        ││ content         ││        │ author          │
│ manage_level    │        ││ version         ││        │ created_at      │
└─────────────────┘        ││ created_at      ││        │ ...             │
                           ││ updated_at      ││        └─────────────────┘
      1                    └┴─────────────────┴┘              1
      │                            N                           │
      │                                                        │
      └────────────── 한 사용자가 여러 댓글 작성 ───────────────┘
                      한 게시글에 여러 댓글 존재
```

---

## 1. Comment Entity (댓글)

### 1.1 Entity Class

```java
package com.apluslife.web.front.comment.entity;

import com.apluslife.web.front.board.entity.Board;
import com.apluslife.web.login.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment", indexes = {
    @Index(name = "idx_comment_board_id", columnList = "board_id"),
    @Index(name = "idx_comment_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 1000)
    @NotBlank(message = "댓글 내용을 입력해주세요")
    @Size(min = 1, max = 1000, message = "댓글은 1~1000자 이내로 작성해주세요")
    private String content;

    @Version
    private Long version;  // 낙관적 잠금

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 비즈니스 메소드
    public void updateContent(String newContent) {
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("댓글 내용을 입력해주세요");
        }
        if (newContent.length() > 1000) {
            throw new IllegalArgumentException("댓글은 1000자 이내로 작성해주세요");
        }
        this.content = newContent;
    }

    public boolean isAuthor(Long userId) {
        return this.user.getId().equals(userId);
    }
}
```

### 1.2 Field Descriptions

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | Long | PK, Auto Increment | 댓글 고유 ID |
| `board` | Board | FK, Not Null, Lazy | 게시글 참조 (N:1) |
| `user` | User | FK, Not Null, Lazy | 작성자 참조 (N:1) |
| `content` | String | Not Null, 1~1000자 | 댓글 내용 (텍스트) |
| `version` | Long | Optimistic Lock | 동시성 제어용 버전 |
| `createdAt` | LocalDateTime | Not Null, Immutable | 작성시간 (자동) |
| `updatedAt` | LocalDateTime | Nullable | 수정시간 (자동) |

### 1.3 Validation Rules

- **content**:
  - `@NotBlank`: 빈 값 불가
  - `@Size(min=1, max=1000)`: 1~1000자 제한
  - XSS 방지: Thymeleaf `th:text` 자동 이스케이핑
- **board**: Not Null (댓글은 반드시 게시글에 종속)
- **user**: Not Null (익명 댓글 불가)

### 1.4 Indexes

- `idx_comment_board_id`: 게시글별 댓글 조회 최적화
- `idx_comment_created_at`: 시간순 정렬 최적화

---

## 2. Board Entity 수정 (기존 엔티티 확장)

### 2.1 Comment 관계 추가

```java
@Entity
public class Board {
    // 기존 필드...

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // 비즈니스 메소드
    public int getCommentCount() {
        return comments.size();
    }
}
```

### 2.2 Cascade 설정
- `CascadeType.REMOVE`: Board 삭제 시 Comment 자동 삭제
- `orphanRemoval = true`: 고아 객체 자동 제거

---

## 3. User Entity (기존 member 테이블 참조)

### 3.1 기존 구조 활용

```java
// 기존 User (member 테이블) 엔티티 활용
// 추가 변경 불필요 (Comment에서 @ManyToOne으로 참조만)
```

**참고**: 기존 `member` 테이블의 `idx` 필드를 Comment의 `user_id` 외래키로 사용

---

## 4. Database Schema (MSSQL)

### 4.1 Comment Table DDL

```sql
CREATE TABLE comment (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    board_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content NVARCHAR(1000) NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL,
    created_at DATETIME2 NOT NULL,
    updated_at DATETIME2,

    CONSTRAINT fk_comment_board
        FOREIGN KEY (board_id)
        REFERENCES board(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_comment_user
        FOREIGN KEY (user_id)
        REFERENCES member(idx)
);

-- Indexes
CREATE INDEX idx_comment_board_id ON comment(board_id);
CREATE INDEX idx_comment_created_at ON comment(created_at);
```

### 4.2 H2 Database (개발 환경)

```sql
-- H2에서는 JPA ddl-auto=update로 자동 생성
-- IDENTITY 전략 자동 지원
-- DATETIME2 → TIMESTAMP로 자동 변환
```

---

## 5. DTO (Data Transfer Object)

### 5.1 CommentRequest (입력)

```java
package com.apluslife.web.front.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "댓글 내용을 입력해주세요")
    @Size(min = 1, max = 1000, message = "댓글은 1~1000자 이내로 작성해주세요")
    private String content;
}
```

### 5.2 CommentResponse (출력)

```java
package com.apluslife.web.front.comment.dto;

import com.apluslife.web.front.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private Long boardId;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isModified;
    private boolean isAuthor;  // 현재 사용자가 작성자인지 여부

    public static CommentResponse from(Comment comment, Long currentUserId) {
        return CommentResponse.builder()
            .id(comment.getId())
            .boardId(comment.getBoard().getId())
            .authorName(comment.getUser().getName())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .isModified(comment.getUpdatedAt() != null)
            .isAuthor(comment.isAuthor(currentUserId))
            .build();
    }
}
```

---

## 6. Repository Interface

### 6.1 BoardCommentRepository

```java
package com.apluslife.web.front.comment.repository;

import com.apluslife.web.front.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCommentRepository extends JpaRepository<Comment, Long> {

    // 게시글별 댓글 목록 조회 (작성시간 오름차순)
    List<Comment> findByBoardIdOrderByCreatedAtAsc(Long boardId);

    // 게시글별 댓글 개수
    long countByBoardId(Long boardId);

    // 게시글과 사용자 정보를 함께 조회 (N+1 방지)
    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.user " +
           "JOIN FETCH c.board " +
           "WHERE c.board.id = :boardId " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findByBoardIdWithUserAndBoard(@Param("boardId") Long boardId);
}
```

---

## 7. State Transitions (상태 전이)

### 7.1 Comment 생명주기

```
[생성 요청]
    ↓
[작성] ─────► [조회 가능]
    ↓              ↓
    │         [수정] ─────► [수정됨 표시]
    ↓              ↓
    └──────► [삭제] ─────► [DB에서 제거]
```

### 7.2 Version 필드 변경

```
댓글 생성: version = 0
댓글 수정: version = 1, 2, 3, ... (자동 증가)
동시 수정 시: OptimisticLockException 발생
```

---

## 8. Data Integrity Rules (데이터 무결성 규칙)

### 8.1 Referential Integrity (참조 무결성)
- Comment.board_id → Board.id (ON DELETE CASCADE)
- Comment.user_id → User.idx (ON DELETE RESTRICT - 사용자 삭제 시 댓글 유지 또는 논리 삭제)

### 8.2 Business Rules
1. **댓글 내용**: 1~1000자 필수
2. **작성자**: 로그인한 사용자만 작성 가능
3. **수정 권한**: 작성자 본인만 가능
4. **삭제 권한**: 작성자 또는 관리자만 가능
5. **게시글 삭제 시**: 모든 댓글 함께 삭제 (Cascade)

---

## 9. Performance Considerations (성능 고려사항)

### 9.1 N+1 쿼리 방지
- **문제**: 댓글 목록 조회 시 각 댓글의 user, board 정보를 개별 쿼리로 조회
- **해결**: `@Query` + `JOIN FETCH` 사용 (`findByBoardIdWithUserAndBoard`)

### 9.2 Lazy Loading
- Comment → Board, Comment → User 관계는 `FetchType.LAZY` 사용
- 필요한 경우에만 명시적으로 FETCH JOIN

### 9.3 Indexing
- `board_id`: 게시글별 댓글 조회 빈번 → 인덱스 생성
- `created_at`: 시간순 정렬 빈번 → 인덱스 생성

---

## 10. Migration Strategy (마이그레이션 전략)

### 10.1 개발 환경 (H2)
```yaml
# application-dev.yml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # Comment 테이블 자동 생성
```

### 10.2 운영 환경 (MSSQL)
```yaml
# application-prod.yml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # 수동 마이그레이션 후 검증만
```

**수동 마이그레이션 스크립트**:
- 파일: `src/main/resources/db/migration/V001__create_comment_table.sql`
- 실행 시점: 배포 전 DBA 검토 후 실행

---

## Summary

Comment 엔티티는 **Board와 User에 종속된 독립 엔티티**로 설계되었으며, JPA 표준 어노테이션과 Spring Data JPA를 활용하여 구현됩니다.

### 핵심 설계 결정
- ✅ **계층형 아키텍처 준수**: Entity, Repository, Service, Controller 분리
- ✅ **보안 우선**: 입력 검증, XSS 방지, 권한 제어
- ✅ **성능 최적화**: 인덱스, FETCH JOIN, Lazy Loading
- ✅ **동시성 제어**: 낙관적 잠금 (@Version)
- ✅ **테스트 가능성**: H2 인메모리 DB, 명확한 의존성

**다음 단계**: API 계약(contracts/) 및 Quickstart 가이드 작성
