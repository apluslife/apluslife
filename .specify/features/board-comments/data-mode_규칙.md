# A+Life 데이터베이스 개발 규칙 및 컨벤션

## 목차

1. [테이블 설계 규칙](#테이블-설계-규칙)
2. [컬럼 명명 규칙](#컬럼-명명-규칙)
3. [Entity 설계 규칙](#entity-설계-규칙)
4. [Repository 규칙](#repository-규칙)
5. [DTO 규칙](#dto-규칙)
6. [관계 매핑](#관계-매핑)
7. [주요 테이블 구조](#주요-테이블-구조)
8. [개발 프로세스](#개발-프로세스)

---

## 테이블 설계 규칙

### 1. 테이블 명명 규칙

| 규칙 | 설명 | 예시 |
|------|------|------|
| 형식 | aplus_ + 기능명 | aplus_공시자료, aplus_파일업로드 |
| 언어 | 한글 또는 영문 통일 | aplus_members, aplus_회원정보 |
| 컨벤션 | CamelCase 사용 금지, snake_case 사용 | X aplus_memberInfo -> O aplus_members |

### 2. 컬럼 명명 규칙

#### 기본 컬럼

| 컬럼 | 타입 | 설명 | 필수 |
|------|------|------|------|
| idx | INT | 고유 ID (Primary Key, Auto Increment) | O |
| rDate | DATETIME | 등록일시 (Registration Date) | O |
| uDate | DATETIME | 수정일시 (Update Date) | O |

예시:

```sql
CREATE TABLE aplus_공시자료 (
    idx INT PRIMARY KEY IDENTITY(1,1),
    title VARCHAR(500) NOT NULL,
    fileName VARCHAR(255) NOT NULL,
    filePath VARCHAR(500) NOT NULL,
    rDate DATETIME NOT NULL DEFAULT GETDATE(),
    uDate DATETIME NOT NULL DEFAULT GETDATE()
);
```

#### 컬럼 명명 컨벤션

| 요소 | 규칙 | 예시 |
|------|------|------|
| 외래키 | 관계 테이블명 + _idx | member_idx, board_idx |
| 날짜 | rDate (등록), uDate (수정) | rDate, uDate |
| 카테고리 | categoryIdx, categoryName | categoryIdx: 1, categoryName: '공시자료' |
| 경로 | filePath, imagePath | filePath |
| 상태 | status, isDeleted, isActive | isDeleted BOOLEAN |
| 내용 | title, content, description | 명확한 의미 표현 |

### 3. 데이터 타입 규칙

| 용도 | 권장 타입 | 설명 |
|------|----------|------|
| 텍스트 짧음 | VARCHAR(255) | 파일명, 카테고리명 |
| 텍스트 중간 | VARCHAR(500) | 제목, 경로 |
| 텍스트 길음 | TEXT, LONGTEXT | 본문, 설명 |
| 정수 | INT | ID, 카운트 |
| 날짜시간 | DATETIME | 생성일, 수정일 |
| 부울 | BOOLEAN, BIT | 삭제여부, 활성화 |
| 파일크기 | BIGINT | 파일 크기 (bytes) |

### 4. 제약 조건 (Constraints)

| 제약 조건 | 규칙 | 예시 |
|----------|------|------|
| NOT NULL | 필수 필드는 NOT NULL | title VARCHAR(500) NOT NULL |
| DEFAULT | 날짜는 GETDATE() 기본값 | rDate DATETIME NOT NULL DEFAULT GETDATE() |
| UNIQUE | 중복 불가 필드 설정 | email VARCHAR(255) UNIQUE |
| FOREIGN KEY | 관계 테이블 명시 | FOREIGN KEY (member_idx) REFERENCES aplus_members(idx) |
| PRIMARY KEY | idx를 기본키로 설정 | idx INT PRIMARY KEY IDENTITY(1,1) |

---

## Entity 설계 규칙

### 1. Entity 클래스 구조

```java
@Entity
@Table(name = "aplus_테이블명")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(nullable = false, updatable = false)
    private LocalDateTime rDate;

    @Column(nullable = false)
    private LocalDateTime uDate;

    @PrePersist
    protected void onCreate() {
        rDate = LocalDateTime.now();
        uDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        uDate = LocalDateTime.now();
    }
}
```

### 2. 어노테이션 규칙

| 어노테이션 | 용도 | 필수 여부 |
|----------|------|----------|
| @Entity | JPA Entity 표시 | O |
| @Table(name = "...") | DB 테이블명 명시 | O |
| @Id | Primary Key | O |
| @GeneratedValue(strategy = GenerationType.IDENTITY) | Auto Increment | O |
| @Column(...) | 컬럼 설정 길이, NOT NULL 등 | 권장 |
| @Data | Lombok: Getter, Setter, toString 자동 생성 | O |
| @Builder | Builder 패턴 지원 | O |
| @PrePersist | 저장 전 실행 생성일시 설정 | 권장 |
| @PreUpdate | 수정 전 실행 수정일시 업데이트 | 권장 |

### 3. Entity 필드 명명

- 필드명: 데이터베이스 컬럼명과 동일하게 camelCase
- 외래키: board_idx -> Java에서 boardIdx로 변환

```java
@Column(name = "board_idx", nullable = false)
private Integer boardIdx;  // DB: board_idx
```

### 4. 올바른 Entity 설계 예시

```java
@Entity
@Table(name = "aplus_공시자료")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(length = 500, nullable = false)
    private String title;  // 공시자료 제목

    @Column(length = 255, nullable = false)
    private String fileName;  // 파일명

    @Column(length = 500, nullable = false)
    private String filePath;  // 파일 저장 경로

    @Column(nullable = false, updatable = false)
    private LocalDateTime rDate;  // 등록일

    @Column(nullable = false)
    private LocalDateTime uDate;  // 수정일

    @PrePersist
    protected void onCreate() {
        rDate = LocalDateTime.now();
        uDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        uDate = LocalDateTime.now();
    }
}
```

### 5. 잘못된 Entity 설계 피해야 할 것들

```java
// 문제 1: content 필드가 실제 DB에 없는데 선언함
@Column(columnDefinition = "LONGTEXT", nullable = true)
private String content;  // X 데이터베이스에 없는 필드

// 문제 2: 날짜 자동 업데이트 미설정
// @PrePersist, @PreUpdate 없이 수동으로 처리

// 문제 3: 필드 타입 불일치
private Long idx;  // X Integer 사용해야 함
```

---

## Repository 규칙

### 1. Repository 인터페이스 구조

```java
@Repository
public interface EntityNameRepository extends JpaRepository<EntityName, Integer> {

    /**
     * 특정 조건으로 조회
     */
    List<EntityName> findByCondition(String condition);

    /**
     * 페이징과 함께 조회
     */
    Page<EntityName> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    /**
     * 정렬과 함께 조회
     */
    @Query("SELECT e FROM EntityName e ORDER BY e.rDate DESC")
    List<EntityName> findAllOrderByLatest();

    /**
     * 삭제 쿼리
     */
    void deleteByBoardIdx(Integer boardIdx);
}
```

### 2. 메서드 명명 규칙

| 메서드명 | 설명 | 반환 타입 |
|---------|------|---------|
| findById(ID) | ID로 단건 조회 | Optional<T> |
| findAll() | 전체 조회 | List<T> |
| findAll(Pageable) | 페이징 조회 | Page<T> |
| findBy{Field}(value) | 필드로 조회 | List<T> |
| findBy{Field}IgnoreCase() | 대소문자 무시 조회 | List<T> |
| save(T) | 저장/수정 | T |
| deleteById(ID) | ID로 삭제 | void |
| deleteBy{Field}() | 필드로 삭제 | void |

### 3. 커스텀 쿼리

```java
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {

    // 1. Query Method 자동 생성
    List<Announcement> findByTitleContaining(String keyword);

    // 2. @Query 명시적
    @Query("SELECT a FROM Announcement a ORDER BY a.rDate DESC")
    List<Announcement> findAllOrderByLatest();

    // 3. Native Query SQL 직접 작성
    @Query(value = "SELECT * FROM aplus_공시자료 WHERE idx = ?1", nativeQuery = true)
    Announcement findByIdNative(Integer idx);
}
```

---

## DTO 규칙

### 1. Request DTO 입력

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementRequest {
    private String title;  // 공시자료 제목

    // 검증 어노테이션 사용
    @NotBlank(message = "제목은 필수입니다")
    @Length(min = 1, max = 500)
    private String title;
}
```

### 2. Response DTO 출력

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDto {
    private Integer idx;
    private String title;
    private String fileName;
    private String filePath;
    private LocalDateTime rDate;
    private LocalDateTime uDate;
    private String registrationDate;  // 포맷된 등록일
    private String updateDate;        // 포맷된 수정일

    /**
     * Entity에서 DTO로 변환
     */
    public static AnnouncementDto from(Announcement announcement) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return AnnouncementDto.builder()
                .idx(announcement.getIdx())
                .title(announcement.getTitle())
                .fileName(announcement.getFileName())
                .filePath(announcement.getFilePath())
                .rDate(announcement.getRDate())
                .uDate(announcement.getUDate())
                .registrationDate(announcement.getRDate() != null ?
                        announcement.getRDate().format(formatter) : "")
                .updateDate(announcement.getUDate() != null ?
                        announcement.getUDate().format(formatter) : "")
                .build();
    }
}
```

### 3. DTO 명명 규칙

| 목적 | 명명 규칙 | 예시 |
|------|----------|------|
| Request | EntityNameRequest | AnnouncementRequest |
| Response | EntityNameDto | AnnouncementDto |
| List Response | EntityNameDto List 반환 | List<AnnouncementDto> |
| 페이징 Response | Page<EntityNameDto> | Page<AnnouncementDto> |

---

## 관계 매핑

### 1. 다대다 관계

상황: 게시글과 파일의 관계

```java
// AnnouncementFile Entity
@Entity
@Table(name = "aplus_파일업로드")
public class AnnouncementFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "board_idx", nullable = false)
    private Integer boardIdx;  // 게시글 참조

    @Column(name = "categoryIdx", nullable = false)
    private Integer categoryIdx;

    @Column(name = "categoryName", length = 100, nullable = false)
    private String categoryName;
}
```

### 2. 외래키 구조

| 테이블 | 외래키 | 참조 테이블 | 참조 컬럼 |
|--------|--------|-----------|---------|
| aplus_파일업로드 | board_idx | aplus_공시자료 | idx |
| aplus_파일업로드 | categoryIdx | aplus_카테고리 | categoryIdx |

---

## 주요 테이블 구조

### 1. 공시자료 관련 테이블

#### aplus_공시자료 기간계 DB

```sql
CREATE TABLE aplus_공시자료 (
    idx INT PRIMARY KEY IDENTITY(1,1),
    title VARCHAR(500) NOT NULL,
    fileName VARCHAR(255) NOT NULL,
    filePath VARCHAR(500) NOT NULL,
    rDate DATETIME NOT NULL DEFAULT GETDATE(),
    uDate DATETIME NOT NULL DEFAULT GETDATE()
);
```

필드 설명:
- idx: 고유 ID
- title: 공시자료 제목
- fileName: 업로드 파일명
- filePath: 파일 저장 경로
- rDate: 등록일시
- uDate: 수정일시

#### aplus_파일업로드 파일 관리 테이블

```sql
CREATE TABLE aplus_파일업로드 (
    idx INT PRIMARY KEY IDENTITY(1,1),
    board_idx INT NOT NULL,  -- 게시글 ID
    fileName VARCHAR(255) NOT NULL,
    filePath VARCHAR(500) NOT NULL,
    categoryIdx INT NOT NULL,  -- 카테고리 ID (1: 공시자료)
    categoryName VARCHAR(100) NOT NULL,
    FOREIGN KEY (board_idx) REFERENCES aplus_공시자료(idx)
);
```

필드 설명:
- idx: 파일 고유 ID
- board_idx: 게시글 IDX (aplus_공시자료.idx)
- fileName: 파일명
- filePath: 파일 저장 경로
- categoryIdx: 카테고리 ID (1=공시자료, 2=라이프뉴스 등)
- categoryName: 카테고리명

#### 카테고리 매핑 테이블

| categoryIdx | categoryName | 설명 |
|------------|-------------|------|
| 1 | 공시자료 | 사업보고서, 분기보고서 등 |
| 2 | 라이프뉴스 | 회사 뉴스 및 소식 |
| 3 | 전환서비스 | 전환 서비스 관련 자료 |
| 4 | 제휴업체 | 제휴업체 정보 |
| 5 | 기타 | 기타 자료 |
| 6 | 부고알림 | 부고 알림 |
| 7 | 이용후기 | 사용자 후기 |
| 8 | 자주하는질문 | FAQ |

---

## 개발 프로세스

### 1. 새로운 게시판/메뉴 추가 시 체크리스트

- [ ] DB 테이블 설계
  - 테이블명: aplus_기능명 형식
  - 필수 컬럼: idx (PK), rDate, uDate
  - 외래키 설정 확인

- [ ] Entity 클래스 작성
  - @Entity, @Table 어노테이션 적용
  - @PrePersist, @PreUpdate 메서드 구현
  - 데이터베이스 필드와 완전히 일치 확인

- [ ] Repository 인터페이스 작성
  - JpaRepository<Entity, Integer> 상속
  - 필요한 조회 메서드 작성
  - 메서드명 컨벤션 준수

- [ ] DTO 작성
  - Request DTO: 입력값 처리
  - Response DTO: 출력값 처리
  - from() 메서드로 Entity -> DTO 변환 구현

- [ ] Service 계층 구현
  - CRUD 기본 메서드 구현
  - @Transactional 어노테이션 적용
  - 비즈니스 로직 구현

- [ ] Controller 작성
  - 요청 핸들링
  - 응답 포매팅
  - 검증 로직 추가

- [ ] 문서 업데이트
  - 해당 data-model-[테이블명].md 파일 작성/수정
  - 테이블 구조, Entity, DTO 문서화

### 2. 파일 업로드 기능 추가 시

```
사용자가 파일 업로드
    ↓
FileUtil.saveFile() -> 물리 파일 저장 (C:\apluslife\uploads)
    ↓
게시판 테이블에 fileName, filePath 저장
    ↓
aplus_파일업로드 테이블에 boardIdx, fileName, filePath, categoryIdx, categoryName 저장
```

### 3. 데이터 검증 규칙

```java
@Data
public class AnnouncementRequest {
    @NotBlank(message = "제목은 필수입니다")
    @Length(min = 1, max = 500, message = "제목은 1~500자여야 합니다")
    private String title;

    @NotNull(message = "파일은 필수입니다")
    private MultipartFile file;
}
```

---

## 주요 패턴

### 1. Entity -> DTO 변환 패턴

```java
// 단건 변환
AnnouncementDto dto = AnnouncementDto.from(announcement);

// 리스트 변환
List<AnnouncementDto> dtos = announcements.stream()
    .map(AnnouncementDto::from)
    .collect(Collectors.toList());

// Page 변환
Page<AnnouncementDto> dtoPage = announcements.map(AnnouncementDto::from);
```

### 2. 서비스 계층 패턴

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementService {

    private final AnnouncementRepository repository;

    // 조회는 readOnly = true
    public AnnouncementDto getOne(Integer idx) {
        return AnnouncementDto.from(
            repository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없습니다"))
        );
    }

    // 저장/수정/삭제는 @Transactional (readOnly = false)
    @Transactional
    public Integer create(AnnouncementRequest request) {
        Announcement entity = Announcement.builder()
            .title(request.getTitle())
            .build();
        return repository.save(entity).getIdx();
    }
}
```

### 3. 컨트롤러 패턴

```java
@Controller
@RequestMapping("/admin/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService service;

    @GetMapping("/list")
    public String list(
        @RequestParam(defaultValue = "0") int page,
        Model model
    ) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<AnnouncementDto> announcements = service.getList(pageable);
        model.addAttribute("announcements", announcements);
        return "manager/announcements/list";
    }

    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<Page<AnnouncementDto>> listApi(
        @RequestParam(defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(page, 10);
        return ResponseEntity.ok(service.getList(pageable));
    }
}
```

---

## 정리

### 핵심 원칙

1. Single Source of Truth 단일 진실 공급원
   - 데이터베이스 스키마를 기준으로 Entity 설계
   - data-model-[테이블명].md로 문서화

2. Type Safety 타입 안정성
   - Entity 필드는 DB 컬럼과 정확히 일치
   - NULL 여부, 길이 등 제약 조건 명시

3. Separation of Concerns 관심사의 분리
   - Entity: DB 매핑
   - DTO: API 계약
   - Service: 비즈니스 로직
   - Controller: 요청/응답 처리

4. Documentation 문서화
   - 각 테이블마다 data-model-[테이블명].md 작성
   - Entity, DTO, 사용 예시 포함

---

마지막 업데이트: 2025-10-23
작성자: 개발팀
버전: 1.0
