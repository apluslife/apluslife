# A+Life Development Standards & Conventions

**Latest Update**: 2025-10-24 | **Version**: 1.1.0

---

## 📋 Quick Reference

### Package 선택 기준
- **관리자 기능** (`/admin/**`): `com.apluslife.manager.controller`
- **공개/회원 기능** (`/`, `/login`, `/board/**`): `com.apluslife.web.controller`
- **비즈니스 로직**: `com.apluslife.domain.{도메인명}`

### 데이터 접근 선택
- **JPA**: 신규 도메인, 간단한 CRUD → `domain/*/repository/`
- **MyBatis**: 복잡한 쿼리, 레거시 매핑 → `domain/*/mapper/` + XML

### 기본 패턴

```java
// 1. Entity (domain/{도메인}/entity/)
@Entity
@Table(name = "db_table_name")
public class Domain { }

// 2. DTO (domain/{도메인}/dto/)
public class DomainDto { }

// 3. Service (domain/{도메인}/service/)
@Service
@RequiredArgsConstructor
public class DomainService {
    @Transactional
    public void create(...) { }
}

// 4. Controller (manager 또는 web package)
@Controller
@RequestMapping("/path")
@PreAuthorize("hasRole('ROLE')")  // 관리자 기능
public class DomainController {
    private final DomainService service;
}
```

---

## 🔒 Security Checklist

- [ ] Controller 경로 및 권한 검증
  - 관리자: `@PreAuthorize("hasRole('ADMIN')")`
  - 회원: `@PreAuthorize("hasRole('USER')")`
  - 공개: 권한 검증 불필요

- [ ] 입력 검증
  ```java
  @Valid
  @NotBlank(message = "...")
  @Size(min=1, max=100)
  ```

- [ ] SQL 인젝션 방지 (필수)
  - JPA: 자동 파라미터 바인딩 ✅
  - MyBatis: `#{paramName}` 사용 ✅
  - 동적 쿼리 금지 ❌

- [ ] XSS 방지
  - Thymeleaf: `th:text` (자동 이스케이핑) ✅
  - HTML 출력: `HtmlUtils.htmlEscape()` ✅

---

## 📁 File Structure Reference

```
.specify/memory/
├── constitution.md                # 프로젝트 핵심 원칙 (v1.1.0)
├── package-structure-guide.md     # 패키지 구조 상세 가이드 ← 이 문서
└── DEVELOPMENT-STANDARDS.md       # 개발 표준 & 체크리스트

.specify/features/board-comments/
├── plan.md                        # 기능 계획 및 구현 설계
├── spec.md                        # 기능 명세서
├── data-model-*.md                # 테이블 정의 & 쿼리
└── data-mode_규칙.md              # DB 개발 규칙

src/main/java/com/apluslife/
├── manager/controller/            # 관리자 기능
├── web/controller/                # 공개/회원 기능
├── domain/{도메인}/               # 비즈니스 로직
├── common/                        # 공통 기능
└── config/                        # 설정

src/main/resources/
├── templates/manager/             # 관리자 UI
├── templates/web/                 # 공개/회원 UI
├── mybatis/mapper/                # MyBatis XML
└── application.yml                # 설정
```

---

## 🚀 Development Workflow

### 새로운 기능 개발 순서

1. **기능 명세** (`spec.md`)
   - 요구사항, 사용자 시나리오, 수용 기준

2. **설계** (`plan.md`, `data-model-*.md`)
   - 아키텍처, 데이터 모델, API 설계

3. **패키지 결정**
   - 관리자? → `manager/controller/`
   - 회원/공개? → `web/controller/`
   - 비즈니스? → `domain/{도메인}/`

4. **구현 순서**
   ```
   1) Entity/POJO (domain/entity/)
   2) DTO (domain/dto/)
   3) Repository/Mapper (domain/repository/ 또는 mapper/)
   4) Service (domain/service/)
   5) Controller (manager 또는 web package)
   6) Template (templates/manager/ 또는 web/)
   ```

5. **테스트**
   - Service 단위 테스트 (80% 이상)
   - Controller 통합 테스트

6. **리뷰**
   - 보안 체크리스트
   - 코드 리뷰
   - 성능 테스트

---

## 📝 Code Style Guide

### Naming Convention

```
변수/메서드:    camelCase
클래스:        PascalCase
상수:          UPPER_SNAKE_CASE
패키지:        lowercase
테이블:        snake_case (aplus_gongsi)
컬럼:          snake_case (user_id, created_at)
```

### Annotation Order

```java
@Controller
@RequestMapping("/path")
@RequiredArgsConstructor  // 생성자 주입
@Slf4j
public class MyController {
    @Autowired
    private final MyService service;

    @GetMapping("/list")
    @PreAuthorize("hasRole('USER')")
    public String list(Model model) {
        // 구현
    }
}
```

### JavaDoc & Comments

```java
/**
 * 공시자료 목록을 조회합니다.
 *
 * @param pageable 페이지 정보
 * @return 공시자료 페이지
 * @throws IllegalArgumentException 잘못된 입력
 */
public Page<AnnouncementDto> getList(Pageable pageable) {
    // 복잡한 로직이 있으면 인라인 주석 추가
    List<Data> results = repository.findAll();  // DB에서 조회
}
```

---

## ⚠️ Common Mistakes & Solutions

### ❌ 1. Bean Name Conflict (같은 이름의 Controller 두 곳)
```
❌ web/controller/AnnouncementController.java
❌ manager/controller/AnnouncementController.java
```
**해결**: 관리자 기능은 항상 `manager/controller/`에만 작성

### ❌ 2. Business Logic in Controller
```java
// ❌ 잘못된 예
@Controller
public class MyController {
    public String create() {
        database.save(...);  // Controller에 로직 ❌
    }
}

// ✅ 올바른 예
@Controller
public class MyController {
    public String create() {
        service.create(...);  // Service에 위임
    }
}
```

### ❌ 3. Missing Authorization Check
```java
// ❌ 잘못된 예
@Controller
@RequestMapping("/admin/announcements")
// @PreAuthorize 없음
public class AnnouncementController { }

// ✅ 올바른 예
@Controller
@RequestMapping("/admin/announcements")
@PreAuthorize("hasRole('ADMIN')")
public class AnnouncementController { }
```

### ❌ 4. SQL Injection via String Concatenation
```java
// ❌ 위험
String sql = "SELECT * FROM users WHERE id = " + userId;  // 위험 ❌

// ✅ 안전 (JPA)
repository.findById(userId);  // 자동 바인딩

// ✅ 안전 (MyBatis)
// <select> SELECT * FROM users WHERE id = #{userId} </select>
```

### ❌ 5. Wrong Template Location
```
❌ templates/announcements/list.html
✅ templates/manager/announcements/list.html (관리자)
✅ templates/web/announcements/list.html (공개/회원)
```

---

## 🔄 Update History

| Version | Date | Changes |
|---------|------|---------|
| 1.1.0 | 2025-10-24 | 역할 기반 Controller 분리, MyBatis 선택 기준 추가 |
| 1.0.0 | 2025-01-21 | 초기 constitution 작성 |

---

## 📚 Related Documents

- **Constitution** (프로젝트 핵심 원칙)
  - 위치: `.specify/memory/constitution.md`
  - 읽어야 할 이유: 7가지 핵심 원칙 이해

- **Package Structure Guide** (패키지 상세 가이드)
  - 위치: `.specify/memory/package-structure-guide.md`
  - 읽어야 할 이유: 새 기능 추가 시 참조

- **Plan** (기능 설계 계획)
  - 위치: `.specify/features/board-comments/plan.md`
  - 읽어야 할 이유: 기능 개발 전 설계 확인

- **Data Model** (테이블 정의)
  - 위치: `.specify/features/board-comments/data-model-*.md`
  - 읽어야 할 이유: Entity 설계 기준

---

## ✅ Pre-Commit Checklist

새로운 기능을 커밋하기 전에 확인하세요:

- [ ] Constitution 원칙 준수
- [ ] 올바른 package 위치
- [ ] Security 체크리스트 완료
- [ ] 입력 검증 구현
- [ ] Service 단위 테스트 작성 (80%+)
- [ ] JavaDoc 작성
- [ ] 코드 스타일 준수
- [ ] 권한 검증 (@PreAuthorize)
- [ ] SQL 인젝션 방지
- [ ] XSS 방지

---

**유지보수**: Development Team
**마지막 업데이트**: 2025-10-24
**질문/개선 제안**: [GitHub Issues](project-url)
