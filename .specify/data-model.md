# A+Life Platform - 종합 데이터 모델

> **중요**: 모든 메뉴 및 모듈 생성 시 이 문서를 참조하여 데이터베이스 스키마를 먼저 정의합니다.
> 메뉴 생성 순서: **Data Model 정의 → Entity 생성 → Repository 생성 → Service 생성 → Controller 생성 → Template 생성**

---

## 📋 메뉴 구조 및 데이터 모델

### 1. 공시자료 (Announcement/Disclosure)

**테이블명**: `aplus_공시자료`

| 컬럼명   | 타입         | 제약조건                    | 설명                        |
| -------- | ------------ | --------------------------- | --------------------------- |
| idx      | INT          | PK, AUTO_INCREMENT          | 고유 ID                     |
| title    | VARCHAR(500) | NOT NULL                    | 공시자료 제목               |
| fileName | VARCHAR(255) | NOT NULL                    | 첨부 파일명 (구분자: \|)    |
| filePath | VARCHAR(500) | NOT NULL                    | 첨부 파일 경로 (구분자: \|) |
| content  | LONGTEXT     | NULLABLE                    | SmartEditor2 상세 설명      |
| rDate    | DATETIME     | NOT NULL, DEFAULT GETDATE() | 등록일시                    |
| uDate    | DATETIME     | NOT NULL, DEFAULT GETDATE() | 수정일시                    |

**파일 형식**: PDF, Word (doc/docx), HWP
**파일 저장**: `uploads/YYYY/MM/DD/UUID.ext`

**Java Entity**:

```java
@Entity
@Table(name = "aplus_공시자료")
public class Announcement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

**주요 기능**:

- SmartEditor2 통합 (HTML 콘텐츠)
- 다중 파일 업로드 (PDF, Word, HWP)
- 검색 (제목 기반)
- 페이지네이션 (10개 항목)
- CRUD 작업

---

### 2. 라이프뉴스 (News)

**테이블명**: `aplus_라이프뉴스`

| 컬럼명    | 타입         | 제약조건                    | 설명               |
| --------- | ------------ | --------------------------- | ------------------ |
| idx       | INT          | PK, AUTO_INCREMENT          | 고유 ID            |
| title     | VARCHAR(500) | NOT NULL                    | 뉴스 제목          |
| content   | LONGTEXT     | NOT NULL                    | 뉴스 본문          |
| summary   | VARCHAR(500) | NULLABLE                    | 요약 정보          |
| thumbnail | VARCHAR(255) | NULLABLE                    | 썸네일 이미지 경로 |
| category  | VARCHAR(50)  | NOT NULL                    | 카테고리           |
| viewCount | INT          | DEFAULT 0                   | 조회수             |
| isNotice  | BOOLEAN      | DEFAULT 0                   | 공지 여부          |
| rDate     | DATETIME     | NOT NULL, DEFAULT GETDATE() | 등록일시           |
| uDate     | DATETIME     | NOT NULL, DEFAULT GETDATE() | 수정일시           |

**카테고리**: 일반뉴스, 업계소식, 이벤트, 공지사항

**Java Entity**:

```java
@Entity
@Table(name = "aplus_라이프뉴스")
public class News {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(length = 500)
    private String summary;

    @Column(length = 255)
    private String thumbnail;

    @Column(length = 50, nullable = false)
    private String category;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false)
    private Boolean isNotice = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime rDate;

    @Column(nullable = false)
    private LocalDateTime uDate;
}
```

**주요 기능**:

- SmartEditor2 통합
- 썸네일 이미지 관리
- 카테고리 분류
- 공지 고정
- 조회수 추적

---

### 3. 제휴업체 (Partners)

**테이블명**: `aplus_제휴업체`

| 컬럼명        | 타입         | 제약조건                    | 설명             |
| ------------- | ------------ | --------------------------- | ---------------- |
| idx           | INT          | PK, AUTO_INCREMENT          | 고유 ID          |
| companyName   | VARCHAR(255) | NOT NULL                    | 회사명           |
| industry      | VARCHAR(100) | NOT NULL                    | 업종             |
| description   | LONGTEXT     | NULLABLE                    | 회사 소개        |
| contactPerson | VARCHAR(100) | NULLABLE                    | 담당자명         |
| phone         | VARCHAR(20)  | NULLABLE                    | 전화번호         |
| email         | VARCHAR(100) | NULLABLE                    | 이메일           |
| address       | VARCHAR(255) | NULLABLE                    | 회사 주소        |
| website       | VARCHAR(255) | NULLABLE                    | 웹사이트         |
| logo          | VARCHAR(255) | NULLABLE                    | 로고 이미지 경로 |
| isActive      | BOOLEAN      | DEFAULT 1                   | 활성화 여부      |
| rDate         | DATETIME     | NOT NULL, DEFAULT GETDATE() | 등록일시         |
| uDate         | DATETIME     | NOT NULL, DEFAULT GETDATE() | 수정일시         |

**Java Entity**:

```java
@Entity
@Table(name = "aplus_제휴업체")
public class Partner {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(length = 255, nullable = false)
    private String companyName;

    @Column(length = 100, nullable = false)
    private String industry;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(length = 100)
    private String contactPerson;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(length = 255)
    private String website;

    @Column(length = 255)
    private String logo;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime rDate;

    @Column(nullable = false)
    private LocalDateTime uDate;
}
```

**주요 기능**:

- 회사 정보 관리
- 로고 이미지 관리
- 연락처 정보
- 활성/비활성 전환
- 업종별 분류

---

### 4. 기타 (Miscellaneous)

**테이블명**: `aplus_기타`

| 컬럼명      | 타입         | 제약조건                    | 설명                        |
| ----------- | ------------ | --------------------------- | --------------------------- |
| idx         | INT          | PK, AUTO_INCREMENT          | 고유 ID                     |
| title       | VARCHAR(500) | NOT NULL                    | 제목                        |
| content     | LONGTEXT     | NOT NULL                    | 본문                        |
| category    | VARCHAR(100) | NOT NULL                    | 카테고리                    |
| attachments | VARCHAR(500) | NULLABLE                    | 첨부 파일 경로 (구분자: \|) |
| rDate       | DATETIME     | NOT NULL, DEFAULT GETDATE() | 등록일시                    |
| uDate       | DATETIME     | NOT NULL, DEFAULT GETDATE() | 수정일시                    |

**Java Entity**:

```java
@Entity
@Table(name = "aplus_기타")
public class Miscellaneous {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(length = 100, nullable = false)
    private String category;

    @Column(length = 500)
    private String attachments;

    @Column(nullable = false, updatable = false)
    private LocalDateTime rDate;

    @Column(nullable = false)
    private LocalDateTime uDate;
}
```

**주요 기능**:

- 자유 형식의 콘텐츠
- 첨부 파일 지원
- 카테고리 분류

---

### 5. 부고알림 (Notice)

**테이블명**: `aplus_부고알림`

| 컬럼명        | 타입         | 제약조건                    | 설명      |
| ------------- | ------------ | --------------------------- | --------- |
| idx           | INT          | PK, AUTO_INCREMENT          | 고유 ID   |
| title         | VARCHAR(500) | NOT NULL                    | 부고 제목 |
| content       | LONGTEXT     | NOT NULL                    | 부고 내용 |
| deceasedName  | VARCHAR(255) | NOT NULL                    | 고인명    |
| deceasedDate  | DATE         | NULLABLE                    | 서거일    |
| funeralDate   | DATE         | NULLABLE                    | 장례일    |
| location      | VARCHAR(255) | NULLABLE                    | 장례 장소 |
| contactPerson | VARCHAR(100) | NULLABLE                    | 신청인    |
| contactPhone  | VARCHAR(20)  | NULLABLE                    | 연락처    |
| priority      | INT          | DEFAULT 0                   | 우선순위  |
| rDate         | DATETIME     | NOT NULL, DEFAULT GETDATE() | 등록일시  |
| uDate         | DATETIME     | NOT NULL, DEFAULT GETDATE() | 수정일시  |

**Java Entity**:

```java
@Entity
@Table(name = "aplus_부고알림")
public class Notice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(length = 255, nullable = false)
    private String deceasedName;

    @Column
    private LocalDate deceasedDate;

    @Column
    private LocalDate funeralDate;

    @Column(length = 255)
    private String location;

    @Column(length = 100)
    private String contactPerson;

    @Column(length = 20)
    private String contactPhone;

    @Column(nullable = false)
    private Integer priority = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime rDate;

    @Column(nullable = false)
    private LocalDateTime uDate;
}
```

**주요 기능**:

- 부고 공지
- 일정 관리 (서거일, 장례일)
- 연락처 관리
- 우선순위 설정

---

### 6. 이용후기 (Review)

**테이블명**: `aplus_이용후기`

| 컬럼명      | 타입         | 제약조건                    | 설명       |
| ----------- | ------------ | --------------------------- | ---------- |
| idx         | INT          | PK, AUTO_INCREMENT          | 고유 ID    |
| title       | VARCHAR(500) | NOT NULL                    | 후기 제목  |
| content     | LONGTEXT     | NOT NULL                    | 후기 내용  |
| author      | VARCHAR(100) | NOT NULL                    | 작성자     |
| rating      | DECIMAL(2,1) | DEFAULT 5.0                 | 별점 (1~5) |
| isPublished | BOOLEAN      | DEFAULT 0                   | 공개 여부  |
| viewCount   | INT          | DEFAULT 0                   | 조회수     |
| rDate       | DATETIME     | NOT NULL, DEFAULT GETDATE() | 등록일시   |
| uDate       | DATETIME     | NOT NULL, DEFAULT GETDATE() | 수정일시   |

**Java Entity**:

```java
@Entity
@Table(name = "aplus_이용후기")
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(length = 100, nullable = false)
    private String author;

    @Column(nullable = false)
    private BigDecimal rating = BigDecimal.valueOf(5.0);

    @Column(nullable = false)
    private Boolean isPublished = false;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime rDate;

    @Column(nullable = false)
    private LocalDateTime uDate;
}
```

**주요 기능**:

- 별점 평가
- 공개/비공개 관리
- 조회수 추적

---

### 7. 자주하는질문 (FAQ)

**테이블명**: `aplus_자주하는질문`

| 컬럼명       | 타입         | 제약조건                    | 설명        |
| ------------ | ------------ | --------------------------- | ----------- |
| idx          | INT          | PK, AUTO_INCREMENT          | 고유 ID     |
| category     | VARCHAR(100) | NOT NULL                    | 카테고리    |
| question     | VARCHAR(500) | NOT NULL                    | 질문        |
| answer       | LONGTEXT     | NOT NULL                    | 답변        |
| displayOrder | INT          | DEFAULT 0                   | 표시 순서   |
| isActive     | BOOLEAN      | DEFAULT 1                   | 활성화 여부 |
| rDate        | DATETIME     | NOT NULL, DEFAULT GETDATE() | 등록일시    |
| uDate        | DATETIME     | NOT NULL, DEFAULT GETDATE() | 수정일시    |

**카테고리**: 일반, 서비스, 결제, 계약, 기타

**Java Entity**:

```java
@Entity
@Table(name = "aplus_자주하는질문")
public class FAQ {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(length = 100, nullable = false)
    private String category;

    @Column(length = 500, nullable = false)
    private String question;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String answer;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime rDate;

    @Column(nullable = false)
    private LocalDateTime uDate;
}
```

**주요 기능**:

- 카테고리별 FAQ
- 순서 관리
- 활성/비활성 전환

---

## 🏗️ 메뉴 생성 규칙 (Template for All Menus)

모든 메뉴 생성 시 아래 순서를 따릅니다:

### Step 1: 데이터 모델 정의 (이 문서에서 정의)

- 테이블 구조 정의
- 칼럼 타입 및 제약조건 명시
- 비즈니스 규칙 기술

### Step 2: Entity 생성

```
위치: src/main/java/com/apluslife/domain/[domain]/entity/[Domain]Entity.java
패턴:
- @Entity, @Table(name="aplus_[한글명]")
- @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
- Lombok: @Data, @NoArgsConstructor, @AllArgsConstructor, @Builder
- @Column: nullable, length, columnDefinition 명시
- @PrePersist/@PreUpdate: 자동 타임스탬프
```

### Step 3: Repository 생성

```
위치: src/main/java/com/apluslife/domain/[domain]/repository/[Domain]Repository.java
패턴:
- extends JpaRepository<[Domain], Integer>
- @Query 메서드로 커스텀 쿼리 정의
- findBy* 메서드로 기본 검색
- Page<T> 반환 타입으로 페이지네이션 지원
```

### Step 4: DTO 생성

```
위치: src/main/java/com/apluslife/domain/[domain]/dto/
파일:
1. [Domain]Request.java - 입력 DTO
2. [Domain]Dto.java - 응답 DTO (Entity::from 메서드)
```

### Step 5: Service 생성

```
위치: src/main/java/com/apluslife/domain/[domain]/service/[Domain]Service.java
패턴:
- @Service, @RequiredArgsConstructor, @Transactional(readOnly = true)
- 메서드: getList, getDetail, create, update, delete, search
- IOException 처리 (파일 업로드 시)
- 트랜잭션 관리: @Transactional (수정 작업)
```

### Step 6: Controller 생성

```
위치: src/main/java/com/apluslife/web/controller/[Domain]Controller.java
패턴:
- @Controller @RequestMapping("/admin/[path]")
- GET /list - 목록 페이지
- GET /create - 등록 폼
- POST /create - 등록 처리
- GET /detail/{id} - 상세 조회
- GET /edit/{id} - 수정 폼
- POST /edit/{id} - 수정 처리
- GET /delete/{id} - 삭제 처리
- @ResponseBody API 엔드포인트
```

### Step 7: Template 생성

```
위치: src/main/resources/templates/manager/[domain]/
파일:
1. [domain]-list.html - 목록 페이지
2. [domain]-create.html - 등록 페이지
3. [domain]-detail.html - 상세 조회 페이지
4. [domain]-edit.html - 수정 페이지
```

### Step 8: Admin 메뉴 업데이트

```
위치: src/main/resources/templates/manager/admin.html
작업:
- 메뉴 항목 추가
- 서브메뉴 생성/목록 링크 추가
- 대시보드 카드 추가
```

---

## 📊 파일 업로드 관리

### 지원되는 파일 형식

```javascript
// 공시자료, 기타 등
PDF, Word (doc/docx), HWP

// 이미지 (썸네일, 로고 등)
JPG, PNG, GIF, BMP, WEBP, SVG

// 문서
XLS, XLSX, PPT, PPTX, TXT, CSV

// 압축
ZIP, RAR, 7Z, TAR, GZ, ALZ, EGG
```

### 저장 경로 구조

```
uploads/
├── 2025/
│   ├── 01/
│   │   ├── 15/
│   │   │   ├── {UUID}.pdf
│   │   │   ├── {UUID}.docx
│   │   │   └── {UUID}.hwp
│   │   └── 16/
│   └── 02/
```

### 파일명 저장 규칙

```
// 단일 파일
uploads/2025/01/15/{UUID}.pdf

// 다중 파일 (구분자 사용)
[PDF]original_name.pdf | [WORD]document.docx | [HWP]file.hwp
```

---

## 🔒 보안 규칙

1. **파일 업로드 검증**

   - 확장자 화이트리스트 검증
   - 파일 크기 제한 (5MB 기본)
   - MIME 타입 검증

2. **접근 제어**

   - 모든 관리 페이지: `/admin/**` 보안
   - 역할 기반: `ROLE_ADMIN` 필수
   - 개인 리소스: 소유자 확인

3. **입력 검증**
   - 필수 필드 확인
   - XSS 방지 (Thymeleaf th:utext)
   - SQL Injection 방지 (JPA 파라미터화)

---

## 📌 SmartEditor2 통합

모든 콘텐츠 편집이 가능한 메뉴 (공시자료, 뉴스, 기타 등)에서 SmartEditor2를 사용합니다.

```html
<!-- Script 임포트 -->
<script
  type="text/javascript"
  th:src="@{/smarteditor/js/HuskyEZCreator.js}"
  charset="UTF-8"
></script>

<!-- Textarea -->
<textarea
  id="content"
  name="content"
  style="width: 100%; min-height: 400px;"
></textarea>

<!-- 초기화 -->
<script>
  let oEditors = [];
  document.addEventListener("DOMContentLoaded", function () {
    nhn.husky.EZCreator.createInIFrame({
      oAppRef: oEditors,
      elPlaceHolder: "content",
      sSkinURI: "/smarteditor/SmartEditor2Skin.html",
      fCreator: "createSEditor2",
      htParams: {
        bUseToolbar: true,
        bUseVerticalResizer: true,
        bUseModeChanger: true,
        fOnBeforeUnload: function () {},
      },
    });
  });

  // 폼 제출 시 콘텐츠 동기화
  form.addEventListener("submit", function (e) {
    if (oEditors.length > 0) {
      oEditors[0].exec("UPDATE_CONTENTS_FIELD", []);
    }
  });
</script>
```

---

## 📝 관리 페이지 스타일 가이드

모든 관리 페이지는 다음 스타일을 따릅니다:

### 헤더

- 배경색: 그래디언트 빨강 (#e74c3c → #c0392b)
- 텍스트: 흰색
- 높이: 60px
- 브레드크럼: 홈 > 메뉴 > 페이지

### 폼 테이블

- 헤더 배경: #f8f9fa
- 행 높이: 50px
- 입력창: 테두리 #ddd, 포커스 시 빨강

### 버튼

- 주요: 초록색 (#27ae60)
- 부가: 회색 (#95a5a6)
- 삭제: 빨강 (#e74c3c)
- 기본: 흰색 (#ffffff)

### 테이블

- 헤더: #f8f9fa
- 호버: #f5f5f5
- 보더: #dee2e6

---

## ✅ 검증 체크리스트

새로운 메뉴 생성 후 확인 사항:

- [ ] data-model.md에서 테이블 구조 정의됨
- [ ] Entity 클래스 생성 완료
- [ ] Repository 인터페이스 생성 완료
- [ ] DTO 클래스 생성 완료
- [ ] Service 클래스 생성 완료 (@Transactional 포함)
- [ ] Controller 클래스 생성 완료 (8개 엔드포인트)
- [ ] 4개 HTML 템플릿 생성 완료
- [ ] admin.html 메뉴 추가 완료
- [ ] build 성공 (`BUILD SUCCESSFUL`)
- [ ] 애플리케이션 시작 성공
- [ ] 목록 페이지 접근 가능
- [ ] 등록 페이지 작동 확인
- [ ] 수정/삭제 기능 확인

---

## 📞 참고 사항

- 모든 테이블은 한글명 사용 (데이터베이스 설정에 맞춤)
- 파일 경로는 전부 forward slash (/) 사용
- 날짜 포맷: `yyyy-MM-dd HH:mm` (목록/상세)
- 페이지네이션: 10개 항목 기본
- 트랜잭션: 조회는 readOnly=true, 수정은 @Transactional

---

## 🎯 관리자 메뉴 구조 (관리자메뉴구성.md 참조)

### 사이드바 메뉴 계층 구조

```
👨‍💼 관리 메뉴
│
├── 📊 경영현황 (그룹)
│   └── 📋 공시자료 관리
│       ├── 목록
│       └── 등록
│
├── 📢 A+Life 소식 (그룹)
│   ├── 📰 라이프뉴스 관리
│   │   ├── 목록
│   │   └── 등록
│   ├── 🔔 부고알림 관리
│   │   ├── 목록
│   │   └── 등록
│   └── 📑 기타 관리
│       ├── 목록
│       └── 등록
│
├── 💬 고객센터 (그룹)
│   ├── ⭐ 이용후기 관리
│   │   ├── 목록
│   │   └── 관리
│   └── ❓ 자주하는질문 관리
│       ├── 목록
│       └── 등록
│
├── 🔄 전환서비스 (그룹)
│   ├── 🏷️ 카테고리 관리
│   │   ├── 목록
│   │   └── 등록
│   └── 📦 상품 관리
│       ├── 목록
│       └── 등록
│
├── ⚙️ 기타 (그룹)
│   ├── 🤝 제휴업체 관리
│   │   ├── 목록
│   │   └── 등록
│   ├── 🎯 팝업 관리
│   │   ├── 목록
│   │   └── 등록
│   ├── 👥 회원 관리
│   │   ├── 목록
│   │   └── 통계
│   └── ⚙️ 시스템 설정
│       ├── 기본 설정
│       └── 보안 설정
│
└── 🚪 로그아웃
```

---

## 📊 구현 현황 대시보드

### 완료된 모듈 (✅ 100%)

| #   | 메뉴       | Entity | Repository | DTO | Service | Controller | Template | 상태          |
| --- | ---------- | ------ | ---------- | --- | ------- | ---------- | -------- | ------------- |
| 1   | 공시자료   | ✅     | ✅         | ✅  | ✅      | ✅         | ✅       | **완료**      |
| 2   | 라이프뉴스 | ✅     | ✅         | ✅  | ✅      | ✅         | 📝       | **거의 완료** |

### 진행 중인 모듈 (🔄 계획됨)

| #   | 메뉴         | 설명                    | 우선순위 | data-model |
| --- | ------------ | ----------------------- | -------- | ---------- |
| 3   | 부고알림     | 부고 공지 + 일정 관리   | 높음     | ✅ 정의됨  |
| 4   | 기타         | 자유 형식 콘텐츠        | 높음     | ✅ 정의됨  |
| 5   | 제휴업체     | 회사 정보 + 로고 관리   | 중간     | ✅ 정의됨  |
| 6   | 이용후기     | 별점 평가 + 관리자 승인 | 중간     | ✅ 정의됨  |
| 7   | 자주하는질문 | 카테고리별 + 순서 관리  | 중간     | ✅ 정의됨  |

### 향후 계획 (📋 예정)

| #   | 메뉴                | 설명           | data-model   |
| --- | ------------------- | -------------- | ------------ |
| 8   | 전환서비스 카테고리 | 카테고리 관리  | ❌ 정의 필요 |
| 9   | 전환서비스 상품     | 상품 CRUD      | ❌ 정의 필요 |
| 10  | 팝업 관리           | 팝업 배너 CRUD | ❌ 정의 필요 |
| 11  | 회원 관리           | 회원 정보 관리 | ❌ 정의 필요 |
| 12  | 시스템 설정         | 시스템 설정    | ❌ 정의 필요 |

---

## 🚀 다음 구현 순서

### Phase 1: 핵심 메뉴 완성 (1주일)

1. **라이프뉴스 템플릿 작성** (news-list/create/detail/edit.html)
2. **부고알림 전체 구현** (Entity ~ Template)
3. **기타 전체 구현** (Entity ~ Template)

### Phase 2: 고객센터 및 제휴업체 (1주일)

4. **제휴업체 전체 구현** (로고 이미지 처리 포함)
5. **이용후기 전체 구현** (별점 기능 추가)
6. **자주하는질문 전체 구현** (카테고리 + 순서)

### Phase 3: 추가 기능 (2주일)

7. **전환서비스 모듈** (data-model 정의 후 구현)
8. **팝업 관리** (이미지 기반)
9. **회원 관리** (조회 및 통계)
10. **시스템 설정** (설정값 관리)

---

## 📂 파일 구조

### 현재 구현된 디렉토리

```
src/main/java/com/apluslife/
├── domain/
│   ├── announcement/
│   │   ├── entity/Announcement.java ✅
│   │   ├── repository/AnnouncementRepository.java ✅
│   │   ├── dto/AnnouncementRequest.java ✅
│   │   ├── dto/AnnouncementDto.java ✅
│   │   └── service/AnnouncementService.java ✅
│   └── news/
│       ├── entity/News.java ✅
│       ├── repository/NewsRepository.java ✅
│       ├── dto/NewsRequest.java ✅
│       ├── dto/NewsDto.java ✅
│       └── service/NewsService.java ✅
│
└── web/controller/
    ├── AnnouncementController.java ✅
    └── NewsController.java ✅

src/main/resources/templates/manager/
├── admin.html ✅ (메뉴 구조 정의)
├── announcements/
│   ├── announcements-list.html ✅
│   ├── announcements-create.html ✅
│   ├── announcements-detail.html ✅
│   └── announcements-edit.html ✅
└── news/
    └── (디렉토리 생성 준비)

.specify/
├── data-model.md ✅ (모든 메뉴의 데이터 모델 정의)
└── features/board-comments/
    └── data-model-aplus_관리자메뉴구성.md ✅ (메뉴 구조)
```

---

## 🔧 빌드 & 배포 현황

### 최신 빌드 정보

```
프로젝트: A+Life Platform
빌드: BUILD SUCCESSFUL
소요시간: 42초
버전: Spring Boot 3.2.1 + Java 21
```

### 구동 환경

```
포트: 8080
데이터베이스: SQL Server (localhost)
스키마: apluslife
보안: Spring Security + JWT (구현 예정)
```

---

## 💡 개발 팁 및 주의사항

### 1. 메뉴 추가 시 체크리스트

새로운 메뉴를 추가할 때는 다음 순서를 반드시 따릅니다:

```
1. ✅ data-model.md에 테이블 구조 추가
2. ✅ Entity 클래스 생성 (@Entity, @Table 포함)
3. ✅ Repository 인터페이스 생성 (JpaRepository 상속)
4. ✅ DTO 클래스 생성 (Request, Dto 2가지)
5. ✅ Service 클래스 생성 (CRUD + 검색 메서드)
6. ✅ Controller 클래스 생성 (8개 기본 엔드포인트)
7. ✅ 4개 HTML 템플릿 생성 (list/create/detail/edit)
8. ✅ admin.html에 메뉴 추가 (사이드바 + 대시보드)
9. ✅ 빌드 테스트 (./gradlew clean build -x test)
10. ✅ 애플리케이션 시작 및 작동 확인
```

### 2. 파일 업로드 처리

```
// 지원 파일 형식
문서: PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, HWP
이미지: JPG, PNG, GIF, BMP, WEBP, SVG
압축: ZIP, RAR, 7Z, TAR, GZ, ALZ, EGG

// 저장 경로
uploads/YYYY/MM/DD/{UUID}.ext

// 사용 클래스
com.apluslife.common.util.FileUtil
```

### 3. SmartEditor2 통합

SmartEditor2를 사용하는 메뉴 (공시자료, 뉴스, 기타 등):

```java
// Template에서
<script th:src="@{/smarteditor/js/HuskyEZCreator.js}"></script>
<textarea id="content" name="content"></textarea>

// JavaScript 초기화 필수
nhn.husky.EZCreator.createInIFrame({
  oAppRef: oEditors,
  elPlaceHolder: "content",
  sSkinURI: "/smarteditor/SmartEditor2Skin.html",
  fCreator: "createSEditor2"
});

// 폼 제출 전 콘텐츠 동기화
oEditors[0].exec("UPDATE_CONTENTS_FIELD", []);
```

### 4. 데이터베이스 스키마 생성

Hibernate가 자동으로 생성하지만, 수동으로 생성할 경우:

```sql
-- 한글 테이블명 지원 확인 (SQL Server)
CREATE TABLE aplus_공시자료 (
    idx INT PRIMARY KEY IDENTITY(1,1),
    title VARCHAR(500) NOT NULL,
    fileName VARCHAR(255) NOT NULL,
    filePath VARCHAR(500) NOT NULL,
    content LONGTEXT,
    rDate DATETIME NOT NULL DEFAULT GETDATE(),
    uDate DATETIME NOT NULL DEFAULT GETDATE()
);
```

### 5. 테스트 시 주의사항

```
- 로그인 필수: /admin/* 모든 페이지는 ROLE_ADMIN 필요
- 테스트 계정: id=admin, password=admin123
- SmartEditor2: 이미지 업로드 시 /smarteditor/upload 경로 확인
- 파일 업로드: multipart/form-data 필수
```

---

## 📚 참고 문서

- **data-model.md**: 모든 메뉴의 데이터 모델 정의
- **data-model-aplus\_관리자메뉴구성.md**: 메뉴 구조 및 분류
- **BoardController.java**: 파일 업로드 구현 참고
- **NewsService.java**: 이미지 처리 및 CRUD 패턴

---

**최종 업데이트**: 2025-10-23
**버전**: 1.1 (구현 현황 추가)
**관리자**: A+Life Development Team
**구현 책임자**: Claude AI Assistant
