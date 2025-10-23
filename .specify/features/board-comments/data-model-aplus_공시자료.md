# 공시자료 관리 시스템

## 데이터베이스 스키마

### 1. aplus\_공시자료 테이블 (기간계 DB)

| 컬럼명    | 데이터타입   | 설명                         | 필수 |
| --------- | ------------ | ---------------------------- | ---- |
| idx       | INT          | 고유 ID (PK, Auto Increment) | O    |
| title     | VARCHAR(500) | 공시자료 제목                | O    |
| title     | VARCHAR(500) | 공시자료 제목                | O    |
| uploadIdx | INT          | 업로드파일idx                | O    |
| fileName  | VARCHAR(255) | 파일명                       | O    |
| filePath  | VARCHAR(500) | 파일 저장 경로               | O    |
| rDate     | DATETIME     | 등록일시                     | O    |
| uDate     | DATETIME     | 수정일시                     | O    |

**주의**: 기간계 DB에는 `content` 필드가 없습니다.

### 2. aplus\_파일업로드 테이블

| 컬럼명       | 데이터타입   | 설명                                  | 필수 |
| ------------ | ------------ | ------------------------------------- | ---- |
| idx          | INT          | 고유 ID (PK, Auto Increment)          | O    |
| fileName     | VARCHAR(255) | 파일명                                | O    |
| filePath     | VARCHAR(500) | 파일 저장 경로                        | O    |
| categoryIdx  | INT          | 카테고리 ID                           | O    |
| categoryName | VARCHAR(100) | 카테고리명 (예: 공시자료, 라이프뉴스) | O    |

board_idx, categoryIdx 키로 해당 게시글을 관리한다.

## 데이터 예시

### aplus\_공시자료

| idx | title                   | fileName                                     | filePath | rDate               | uDate               |
| --- | ----------------------- | -------------------------------------------- | -------- | ------------------- | ------------------- |
| 1   | 2024년 결산보고서       | [에이플러스라이프]사업보고서(2025.03.28).pdf | /upload  | 2025-03-28 06:42:00 | 2025-03-28 06:42:00 |
| 2   | 2025년 1분기 결산보고서 | [에이플러스라이프]분기보고서(2025.05.15).pdf | /upload  | 2025-05-15 10:30:00 | 2025-05-15 10:30:00 |
| 3   | 2025년 반기보고서       | [에이플러스라이프]반기보고서(2025.08.14).pdf | /upload  | 2025-08-14 14:20:00 | 2025-08-14 14:20:00 |

## SQL 쿼리

### 공시자료 목록 조회

```sql
SELECT idx, title, fileName, filePath, rDate, uDate
FROM aplus_공시자료
ORDER BY rDate DESC;
```

### 공시자료 추가

```sql
INSERT INTO aplus_공시자료 (title, fileName, filePath, rDate, uDate)
VALUES (?, ?, ?, GETDATE(), GETDATE());

INSERT INTO aplus_파일업로드 (fileName, filePath, categoryIdx, categoryName)
VALUES (?, ?, 1, '공시자료');
```

### 공시자료 수정

```sql
UPDATE aplus_공시자료
SET title = ?, fileName = ?, filePath = ?, uDate = GETDATE()
WHERE idx = ?;
```

### 공시자료 삭제

```sql
DELETE FROM aplus_공시자료 WHERE idx = ?;
```

### 공시자료 상세 조회

```sql
SELECT idx, title, fileName, filePath, rDate, uDate
FROM aplus_공시자료
WHERE idx = ?;
```

## 주요 기능

### 1. 조회 (Read)

- 공시자료 목록 조회 (최신순 정렬)
- 공시자료 상세 조회
- 파일 다운로드

### 2. 등록 (Create)

- 공시자료 제목 입력
- 파일 업로드 (PDF 등)
- 등록일시 자동 저장

### 3. 수정 (Update)

- 공시자료 제목 수정
- 파일 교체
- 수정일시 자동 업데이트

### 4. 삭제 (Delete)

- 공시자료 삭제
- 관련 파일 삭제

## 파일다운로드 카테고리 매핑

| categoryIdx | categoryName | 설명                      |
| ----------- | ------------ | ------------------------- |
| 1           | 공시자료     | 사업보고서, 분기보고서 등 |
| 2           | 라이프뉴스   | 회사 뉴스 및 소식         |
| 3           | 전환서비스   | 전환 서비스 관련 자료     |
| 4           | 제휴업체     | 제휴업체 정보             |
| 5           | 기타         | 기타 자료                 |
| 6           | 부고알림     | 부고 알림                 |
| 7           | 이용후기     | 사용자 후기               |
| 8           | 자주하는질문 | FAQ                       |

## Entity 정의

### Announcement.java

```java
@Entity
@Table(name = "aplus_공시자료")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(length = 255, nullable = false)
    private String fileName;

    @Column(length = 500, nullable = false)
    private String filePath;

    @Column(nullable = false, updatable = false)
    private LocalDateTime rDate;

    @Column(nullable = false)
    private LocalDateTime uDate;
}
```

## DTOs

### AnnouncementRequest.java

```java
@Data
public class AnnouncementRequest {
    private String title;  // 공시자료 제목
}
```

### AnnouncementDto.java

```java
@Data
public class AnnouncementDto {
    private Integer idx;
    private String title;
    private String fileName;
    private String filePath;
    private LocalDateTime rDate;
    private LocalDateTime uDate;
    private String registrationDate;  // 포맷된 등록일
    private String updateDate;        // 포맷된 수정일
}
```

## 파일 업로드 관리

### AnnouncementFile.java (Entity)

```java
@Entity
@Table(name = "aplus_파일업로드")
public class AnnouncementFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "board_idx", nullable = false)
    private Integer boardIdx;  // 게시글 IDX (공시자료의 idx)

    @Column(length = 255, nullable = false)
    private String fileName;   // 파일명

    @Column(length = 500, nullable = false)
    private String filePath;   // 파일 저장 경로

    @Column(name = "categoryIdx", nullable = false)
    private Integer categoryIdx;  // 카테고리 ID (공시자료: 1)

    @Column(name = "categoryName", length = 100, nullable = false)
    private String categoryName;  // 카테고리명 (공시자료)
}
```

### 파일 업로드 처리 프로세스

1. **파일 업로드 시**:

   - FileUtil을 통해 물리 파일 저장 (C:\apluslife\uploads)
   - aplus\_공시자료 테이블에 fileName, filePath 저장
   - aplus\_파일업로드 테이블에 파일 메타데이터 저장

2. **파일 삭제 시**:
   - aplus\_파일업로드 테이블에서 관련 파일 정보 삭제
   - 물리 파일 삭제 (C:\apluslife\uploads)
