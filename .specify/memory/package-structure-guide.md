# A+Life Package Structure & Development Guide

**Version**: 1.0.0 | **Last Updated**: 2025-10-24

이 문서는 A+Life 프로젝트의 패키지 구조와 개발 컨벤션을 정의합니다. 모든 개발자는 새로운 기능을 추가할 때 이 가이드를 따라야 합니다.

---

## 패키지 구조 개요

```
src/main/java/com/apluslife/

├── manager/                 # 관리자 기능 (ADMIN 권한 필요)
│   └── controller/
│       ├── AdminMainController.java      # /admin/login, /admin
│       ├── AnnouncementController.java   # /admin/announcements/**
│       └── LifeNewsController.java       # /admin/lifenews/**
│
├── web/                     # 공개/회원 기능 (공개 또는 USER 권한)
│   └── controller/
│       ├── MainController.java           # /, /login, /about, /products, /board
│       ├── BoardController.java          # /board/**
│       ├── MemberController.java         # 회원 기능
│       └── NewsController.java           # 뉴스 조회
│
├── domain/                  # 비즈니스 로직 (JPA/MyBatis)
│   ├── announcement/
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── service/
│   │   ├── repository/
│   │   └── mapper/
│   ├── lifenews/
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── service/
│   │   └── mapper/
│   ├── board/
│   ├── member/
│   └── ...
│
├── common/                  # 공통 기능
│   ├── util/
│   ├── aspect/
│   └── exception/
│
├── config/                  # 설정
│   ├── SecurityConfig.java
│   ├── WebConfig.java
│   └── ...
│
└── AplusLifeApplication.java

src/main/resources/
├── templates/
│   ├── manager/             # 관리자 UI
│   │   ├── announcements/
│   │   ├── lifenews/
│   │   ├── admin.html
│   │   └── fragments/
│   ├── web/                 # 공개/회원 UI
│   │   ├── index.html
│   │   ├── login.html
│   │   └── ...
│   └── fragments/           # 공통 fragments
│
├── mybatis/mapper/          # MyBatis SQL 매핑 XML
│   └── *.xml
│
└── application.yml          # 설정 파일
```

---

## 역할별 Package 선택 기준

### 관리자 기능 → `manager/controller/`

**언제 사용할까?**
- `/admin/**` URL 패턴
- ADMIN 권한이 필요한 기능
- 관리자만 접근 가능한 대시보드, 관리 화면

**예시:**
- 공시자료 관리: `manager.controller.AnnouncementController`
- 라이프뉴스 관리: `manager.controller.LifeNewsController`
- 관리자 로그인: `manager.controller.AdminMainController`

**작성 규칙:**
```java
@Controller
@RequestMapping("/admin/{기능명}")
@PreAuthorize("hasRole('ADMIN')")  // 필수
public class {기능명}Controller {
    // 구현
}
```

### 공개/회원 기능 → `web/controller/`

**언제 사용할까?**
- `/`, `/login`, `/board/**`, `/mypage` 등의 URL 패턴
- 공개 페이지 또는 USER 권한이 필요한 기능
- 일반 사용자가 접근하는 기능

**예시:**
- 홈페이지: `web.controller.MainController.home()`
- 게시판: `web.controller.BoardController`
- 마이페이지: `web.controller.MainController.mypage()`

**작성 규칙:**
```java
@Controller
@RequestMapping("/{경로}")
// @PreAuthorize는 필요시에만 (공개 페이지는 불필요)
public class {기능명}Controller {
    // 구현
}
```

### 비즈니스 로직 → `domain/{도메인명}/`

**구조:**
```
domain/{도메인명}/
├── entity/          # JPA 엔티티 (@Entity)
├── dto/             # 요청/응답 DTO (Request, Response/Dto)
├── service/         # 비즈니스 로직 (@Service, @Transactional)
├── repository/      # JPA Repository (extends JpaRepository)
└── mapper/          # MyBatis Mapper (선택사항)
```

**작성 규칙:**
```java
// Entity
@Entity
@Table(name = "actual_table_name")
public class {도메인명} { }

// Service
@Service
@RequiredArgsConstructor
public class {도메인명}Service {
    private final {도메인명}Repository repository;

    @Transactional
    public void create(...) { }
}

// Repository
@Repository
public interface {도메인명}Repository extends JpaRepository<{도메인명}, Integer> {
    // 커스텀 메소드
}

// MyBatis Mapper (복잡한 쿼리용)
@Mapper
public interface {도메인명}Mapper {
    List<{도메인명}> selectAll();
}
```

---

## 데이터 접근 방식 선택 (JPA vs MyBatis)

### JPA 사용 기준

**언제 사용:**
- 신규 도메인 설계
- 간단한 CRUD 작업
- 관계형 데이터 (1:N, M:N 매핑)
- 자동 쿼리 생성이 효율적

**예시:**
- `domain/announcement/` - 공시자료 (JPA 사용)
- `domain/board/` - 게시판 (JPA 사용)

**파일 구성:**
```
domain/announcement/
├── entity/Announcement.java
├── dto/AnnouncementDto.java
├── service/AnnouncementService.java
└── repository/AnnouncementRepository.java
```

### MyBatis 사용 기준

**언제 사용:**
- 복잡한 SQL 쿼리 (다중 JOIN)
- 동적 쿼리 필요
- 원본 DB 스키마 정확 매핑 필수
- 성능 최적화 중요

**예시:**
- `domain/lifenews/` - 라이프뉴스 (MyBatis 사용)

**파일 구성:**
```
domain/lifenews/
├── entity/LifeNews.java              # 일반 POJO (JPA 아님)
├── dto/LifeNewsDto.java
├── service/LifeNewsService.java
├── mapper/LifeNewsMapper.java         # @Mapper 인터페이스
└── resources/mybatis/mapper/
    └── LifeNewsMapper.xml
```

---

## 개발 체크리스트

새로운 기능을 추가할 때 다음을 확인하세요:

### 1. 패키지 위치 결정
- [ ] 관리자 기능인가? → `manager/controller/`
- [ ] 공개/회원 기능인가? → `web/controller/`
- [ ] 비즈니스 로직인가? → `domain/{도메인명}/`

### 2. Controller 작성
- [ ] 올바른 `@RequestMapping` 경로 사용
- [ ] 관리자 기능에 `@PreAuthorize("hasRole('ADMIN')")` 추가
- [ ] 회원 기능에 필요시 `@PreAuthorize("hasRole('USER')")` 추가
- [ ] 입력 검증 (`@Valid`) 구현

### 3. Service & Repository
- [ ] Service에 `@Transactional` 추가
- [ ] 의존성 주입 (생성자 주입) 사용
- [ ] 비즈니스 로직은 Service에만 작성

### 4. 데이터 접근
- [ ] JPA 또는 MyBatis 선택
- [ ] Entity와 DTO 분리
- [ ] 쿼리 작성 규칙 준수

### 5. UI 템플릿
- [ ] 관리자 UI: `templates/manager/{도메인명}/`
- [ ] 공개/회원 UI: `templates/web/{도메인명}/` 또는 최상위
- [ ] Fragment 사용 (헤더, 사이드바, 푸터 등)

### 6. 보안
- [ ] SQL 인젝션 방지 (파라미터 바인딩)
- [ ] XSS 방지 (Thymeleaf 자동 이스케이핑)
- [ ] 권한 검증 (`@PreAuthorize`)
- [ ] 입력 검증 완료

### 7. 테스트
- [ ] Service 단위 테스트 작성
- [ ] Controller 통합 테스트 (필요시)
- [ ] 테스트 커버리지 80% 이상

### 8. 문서화
- [ ] JavaDoc 작성
- [ ] 복잡한 로직에 인라인 주석
- [ ] API 명세 업데이트 (필요시)

---

## 네이밍 컨벤션

| 대상 | 규칙 | 예시 |
|------|------|------|
| 클래스 | PascalCase | `AnnouncementController`, `LifeNewsService` |
| 메서드/변수 | camelCase | `createAnnouncement()`, `lifeNewsList` |
| 상수 | UPPER_SNAKE_CASE | `DEFAULT_PAGE_SIZE`, `MAX_FILE_SIZE` |
| 패키지 | lowercase | `com.apluslife.domain.announcement` |
| 테이블 | snake_case | `aplus_gongsi`, `aplus_lifenews` |
| 컬럼 | snake_case | `user_id`, `created_at` |

---

## 자주하는 실수 & 해결방법

### ❌ 실수 1: 같은 이름의 Controller 두 곳에 작성
```java
// 잘못된 예시
web/controller/AnnouncementController.java  (회원용 - 잘못됨)
manager/controller/AnnouncementController.java  (관리자용 - 올바름)
```

**해결:** 관리자 기능은 항상 `manager/controller/`에만 작성

### ❌ 실수 2: Controller에 비즈니스 로직 작성
```java
// 잘못된 예시
@Controller
public class AnnouncementController {
    public String create() {
        // DB 저장 로직을 여기에 작성 ❌
        repository.save(...);
    }
}
```

**해결:** Service에 작성하고 Controller에서 호출
```java
@Controller
public class AnnouncementController {
    @Autowired
    private AnnouncementService service;  // Service 사용

    public String create() {
        service.createAnnouncement(...);  // Service 호출
    }
}
```

### ❌ 실수 3: MyBatis XML 없이 복잡한 쿼리 작성
```java
// 잘못된 예시
@Repository
public interface LifeNewsRepository extends JpaRepository<LifeNews, Integer> {
    @Query("SELECT l FROM LifeNews l " +
           "JOIN ... " +
           "WHERE ... ORDER BY ...") // 복잡한 JPA 쿼리 ❌
    List<LifeNews> findComplex();
}
```

**해결:** MyBatis 사용
```java
// LifeNewsMapper.java
@Mapper
public interface LifeNewsMapper {
    List<LifeNews> selectComplex();
}

// LifeNewsMapper.xml
<select id="selectComplex">
    SELECT ... FROM ... JOIN ... WHERE ...
</select>
```

### ❌ 실수 4: 권한 검증 누락
```java
// 잘못된 예시
@Controller
@RequestMapping("/admin/announcements")
// @PreAuthorize 없음 ❌
public class AnnouncementController {
    public String list() { }
}
```

**해결:** 명시적으로 권한 검증 추가
```java
@Controller
@RequestMapping("/admin/announcements")
@PreAuthorize("hasRole('ADMIN')")  // 필수 ✅
public class AnnouncementController {
    public String list() { }
}
```

---

## 참고 자료

- **Constitution**: `.specify/memory/constitution.md` - 프로젝트 원칙
- **Plan**: `.specify/features/board-comments/plan.md` - 기능 설계 계획
- **Data Model**: `.specify/features/board-comments/data-model-*.md` - 테이블 정의
- **Database Rules**: `.specify/features/board-comments/data-mode_규칙.md` - DB 개발 규칙

---

## 문의 & 변경 요청

새로운 컨벤션이 필요하거나 현재 규칙을 개선할 제안이 있으면:
1. 이 파일 수정
2. `constitution.md` 버전 업그레이드
3. 팀 공지

---

**마지막 업데이트**: 2025-10-24
**유지보수**: Development Team
