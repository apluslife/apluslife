# A+Life Platform - 개발 가이드

개발자는 주니어 등급으로 모두 한글로 해줘

> 📌 **이 문서는 지속적으로 참조되는 개발 매뉴얼입니다**
> 새로운 기능 추가 시, 메뉴 생성 시 **반드시 이 문서의 체크리스트를 따릅니다**

---

## 🎯 핵심 원칙

### 1. 모든 개발은 data-model.md를 기반으로 시작

```
새 기능 추가 → data-model.md에 정의 → 코드 구현
```

### 2. 메뉴 추가 시 정해진 순서 준수

```
데이터 모델 → Entity → Repository → DTO → Service → Controller → Template → 메뉴 추가
```

### 3. 일관된 패턴 유지

```
모든 메뉴는 동일한 구조와 패턴을 따릅니다
(공시자료, 라이프뉴스와 동일하게 구현)
```

---

## 📋 메뉴 추가 체크리스트 (10단계)

새로운 메뉴를 추가할 때마다 이 체크리스트를 완료합니다:

### ✅ Step 1: data-model.md에 데이터 모델 정의

- [ ] 테이블명 정의 (aplus\_[한글명])
- [ ] 컬럼 정의 (이름, 타입, 제약조건)
- [ ] 설명 작성
- [ ] 주요 기능 명시
- [ ] Entity 클래스 코드 예시 추가

**예시**:

````markdown
### N. [메뉴명] ([영문명])

**테이블명**: `aplus_[한글명]`

| 컬럼명 | 타입 | 제약조건 | 설명    |
| ------ | ---- | -------- | ------- |
| idx    | INT  | PK       | 고유 ID |
| ...    | ...  | ...      | ...     |

**Java Entity**:

````java
// Entity 코드
```java
````
````

---

### ✅ Step 2: Entity 클래스 생성

**위치**: `src/main/java/com/apluslife/domain/[domain]/entity/[Domain].java`

**기본 구조**:

```java
@Entity
@Table(name = "aplus_[한글명]")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class [Domain] {
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

**체크 항목**:

- [ ] @Entity, @Table 어노테이션 포함
- [ ] @Data, @NoArgsConstructor, @AllArgsConstructor, @Builder 포함
- [ ] @Id @GeneratedValue 포함
- [ ] idx, rDate, uDate 기본 필드 포함
- [ ] @PrePersist, @PreUpdate 메서드 포함

---

### ✅ Step 3: Repository 인터페이스 생성

**위치**: `src/main/java/com/apluslife/domain/[domain]/repository/[Domain]Repository.java`

**기본 구조**:

```java
@Repository
public interface [Domain]Repository extends JpaRepository<[Domain], Integer> {

    @Query("SELECT d FROM [Domain] d ORDER BY d.rDate DESC")
    Page<[Domain]> findAllOrderByLatest(Pageable pageable);

    Page<[Domain]> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}
```

**체크 항목**:

- [ ] @Repository 어노테이션 포함
- [ ] JpaRepository 상속
- [ ] 최신순 조회 메서드 포함
- [ ] 검색 메서드 포함

---

### ✅ Step 4: DTO 클래스 생성 (2개)

**파일 1**: `[Domain]Request.java` - 입력 DTO

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class [Domain]Request {
    private String title;
    private String content;
    // 필요한 필드만 포함 (저장/수정 시 필요한 것들)
}
```

**파일 2**: `[Domain]Dto.java` - 응답 DTO

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class [Domain]Dto {
    private Integer idx;
    private String title;
    private String content;
    private LocalDateTime rDate;
    private LocalDateTime uDate;
    private String rDateFormatted;
    private String uDateFormatted;

    public static [Domain]Dto from([Domain] entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return [Domain]Dto.builder()
                .idx(entity.getIdx())
                .title(entity.getTitle())
                .content(entity.getContent())
                .rDate(entity.getRDate())
                .uDate(entity.getUDate())
                .rDateFormatted(entity.getRDate() != null ?
                    entity.getRDate().format(formatter) : "")
                .uDateFormatted(entity.getUDate() != null ?
                    entity.getUDate().format(formatter) : "")
                .build();
    }
}
```

**체크 항목**:

- [ ] Request DTO 생성
- [ ] Dto DTO 생성
- [ ] Dto.from() static 메서드 포함
- [ ] 날짜 포맷 메서드 포함

---

### ✅ Step 5: Service 클래스 생성

**위치**: `src/main/java/com/apluslife/domain/[domain]/service/[Domain]Service.java`

**기본 메서드**:

```java
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class [Domain]Service {

    private final [Domain]Repository [domain]Repository;
    private final FileUtil fileUtil; // 파일 업로드 시에만

    // 1. 목록 조회 (페이징)
    public Page<[Domain]Dto> get[Domain]List(Pageable pageable) {
        Page<[Domain]> items = [domain]Repository.findAllOrderByLatest(pageable);
        return items.map([Domain]Dto::from);
    }

    // 2. 상세 조회
    public [Domain]Dto get[Domain](Integer idx) {
        [Domain] item = [domain]Repository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("[메뉴명]을 찾을 수 없습니다."));
        return [Domain]Dto.from(item);
    }

    // 3. 생성 (트랜잭션)
    @Transactional
    public Integer create[Domain]([Domain]Request request) throws IOException {
        [Domain] item = [Domain].builder()
                .title(request.getTitle())
                .content(request.getContent())
                .rDate(LocalDateTime.now())
                .uDate(LocalDateTime.now())
                .build();

        [Domain] saved = [domain]Repository.save(item);
        return saved.getIdx();
    }

    // 4. 수정 (트랜잭션)
    @Transactional
    public void update[Domain](Integer idx, [Domain]Request request) throws IOException {
        [Domain] item = [domain]Repository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("[메뉴명]을 찾을 수 없습니다."));

        item.setTitle(request.getTitle());
        item.setContent(request.getContent());
        item.setUDate(LocalDateTime.now());

        [domain]Repository.save(item);
    }

    // 5. 삭제 (트랜잭션)
    @Transactional
    public void delete[Domain](Integer idx) {
        [domain]Repository.deleteById(idx);
    }

    // 6. 검색
    public Page<[Domain]Dto> search[Domain]s(String keyword, Pageable pageable) {
        Page<[Domain]> items = [domain]Repository.findByTitleContainingIgnoreCase(keyword, pageable);
        return items.map([Domain]Dto::from);
    }
}
```

**체크 항목**:

- [ ] @Service, @RequiredArgsConstructor, @Slf4j 포함
- [ ] 기본 readOnly=true 설정
- [ ] 6가지 기본 메서드 포함 (목록, 상세, 생성, 수정, 삭제, 검색)
- [ ] 수정 메서드에 @Transactional 포함

---

### ✅ Step 6: Controller 클래스 생성

**위치**: `src/main/java/com/apluslife/web/controller/[Domain]Controller.java`

**8개 기본 엔드포인트**:

```java
@Slf4j
@Controller
@RequestMapping("/admin/[path]")
@RequiredArgsConstructor
public class [Domain]Controller {

    private final [Domain]Service [domain]Service;

    // 1. 목록 페이지
    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(required = false) String keyword,
                      Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<[Domain]Dto> items;

        if (keyword != null && !keyword.isEmpty()) {
            items = [domain]Service.search[Domain]s(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            items = [domain]Service.get[Domain]List(pageable);
        }

        model.addAttribute("[domain]s", items);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", items.getTotalPages());

        return "manager/[path]/[domain]-list";
    }

    // 2. 등록 폼
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("[domain]Request", new [Domain]Request());
        return "manager/[path]/[domain]-create";
    }

    // 3. 등록 처리
    @PostMapping("/create")
    public String create(@ModelAttribute [Domain]Request request,
                        RedirectAttributes redirectAttributes) {
        try {
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "[메뉴명] 제목을 입력하세요.");
                return "redirect:/admin/[path]/create";
            }

            Integer idx = [domain]Service.create[Domain](request);
            redirectAttributes.addFlashAttribute("message", "[메뉴명]이 성공적으로 등록되었습니다.");
            return "redirect:/admin/[path]/detail/" + idx;

        } catch (Exception e) {
            log.error("[메뉴명] 등록 실패", e);
            redirectAttributes.addFlashAttribute("error", "[메뉴명] 등록에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/[path]/create";
        }
    }

    // 4. 상세 조회
    @GetMapping("/detail/{idx}")
    public String detail(@PathVariable Integer idx, Model model) {
        try {
            [Domain]Dto item = [domain]Service.get[Domain](idx);
            model.addAttribute("[domain]", item);
            return "manager/[path]/[domain]-detail";
        } catch (Exception e) {
            log.error("[메뉴명] 상세 조회 실패", e);
            return "redirect:/admin/[path]/list";
        }
    }

    // 5. 수정 폼
    @GetMapping("/edit/{idx}")
    public String editForm(@PathVariable Integer idx, Model model) {
        try {
            [Domain]Dto item = [domain]Service.get[Domain](idx);
            model.addAttribute("[domain]", item);
            model.addAttribute("[domain]Request", [Domain]Request.builder()
                    .title(item.getTitle())
                    .content(item.getContent())
                    .build());
            return "manager/[path]/[domain]-edit";
        } catch (Exception e) {
            log.error("[메뉴명] 수정 페이지 로드 실패", e);
            return "redirect:/admin/[path]/list";
        }
    }

    // 6. 수정 처리
    @PostMapping("/edit/{idx}")
    public String edit(@PathVariable Integer idx,
                      @ModelAttribute [Domain]Request request,
                      RedirectAttributes redirectAttributes) {
        try {
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "[메뉴명] 제목을 입력하세요.");
                return "redirect:/admin/[path]/edit/" + idx;
            }

            [domain]Service.update[Domain](idx, request);
            redirectAttributes.addFlashAttribute("message", "[메뉴명]이 성공적으로 수정되었습니다.");
            return "redirect:/admin/[path]/detail/" + idx;

        } catch (Exception e) {
            log.error("[메뉴명] 수정 실패", e);
            redirectAttributes.addFlashAttribute("error", "[메뉴명] 수정에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/[path]/edit/" + idx;
        }
    }

    // 7. 삭제 처리
    @GetMapping("/delete/{idx}")
    public String delete(@PathVariable Integer idx,
                        RedirectAttributes redirectAttributes) {
        try {
            [domain]Service.delete[Domain](idx);
            redirectAttributes.addFlashAttribute("message", "[메뉴명]이 성공적으로 삭제되었습니다.");
            return "redirect:/admin/[path]/list";
        } catch (Exception e) {
            log.error("[메뉴명] 삭제 실패", e);
            redirectAttributes.addFlashAttribute("error", "[메뉴명] 삭제에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/[path]/list";
        }
    }

    // 8. API - JSON 조회
    @GetMapping("/api/{idx}")
    @ResponseBody
    public ResponseEntity<[Domain]Dto> get[Domain]Api(@PathVariable Integer idx) {
        try {
            [Domain]Dto item = [domain]Service.get[Domain](idx);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
```

**체크 항목**:

- [ ] @Controller, @RequestMapping, @RequiredArgsConstructor 포함
- [ ] 8개 기본 엔드포인트 모두 구현
- [ ] 입력값 검증 포함
- [ ] try-catch로 예외 처리
- [ ] RedirectAttributes로 메시지 전달

---

### ✅ Step 7: HTML 템플릿 생성 (4개)

**위치**: `src/main/resources/templates/manager/[path]/`

4개의 HTML 파일을 생성합니다:

1. **[domain]-list.html** - 목록 페이지 (검색, 페이지네이션, 삭제 버튼)
2. **[domain]-create.html** - 등록 페이지 (폼, SmartEditor2)
3. **[domain]-detail.html** - 상세 조회 페이지 (내용 표시, 수정/삭제 버튼)
4. **[domain]-edit.html** - 수정 페이지 (기존 값 표시, 폼)

**공통 스타일**:

```html
<!-- 헤더 -->
<div class="header">
  배경: #e74c3c → #c0392b 그래디언트 높이: 60px 텍스트: 흰색
</div>

<!-- 폼 테이블 -->
<table class="form-table">
  <th>헤더 배경: #f8f9fa</th>
</table>

<!-- 버튼 -->
주요(초록): #27ae60 부가(회색): #95a5a6 삭제(빨강): #e74c3c
```

**체크 항목**:

- [ ] list.html: 검색, 페이지네이션, 삭제 버튼
- [ ] create.html: 유효성 검사, SmartEditor2 (콘텐츠 있는 경우)
- [ ] detail.html: 상세 정보 표시, 수정/삭제 버튼
- [ ] edit.html: 기존 값 표시, 수정 버튼
- [ ] 모두 Thymeleaf 문법 사용
- [ ] Responsive 디자인 적용

---

### ✅ Step 8: admin.html에 메뉴 추가

**파일**: `src/main/resources/templates/manager/admin.html`

**추가 항목**:

1. **사이드바 메뉴** (좌측)

   - 메뉴 그룹 헤더 추가
   - 메뉴 아이템 추가
   - 서브메뉴 링크 추가

2. **대시보드 메뉴 카드** (메인)
   - 섹션 헤더 추가
   - 메뉴 카드 추가
   - 상태 배지 표시

**체크 항목**:

- [ ] 사이드바에 메뉴 그룹 추가
- [ ] 사이드바에 메뉴 아이템 추가
- [ ] 서브메뉴 (목록/등록) 추가
- [ ] 대시보드에 해당 섹션 추가
- [ ] 메뉴 카드 추가
- [ ] 상태 배지 설정 (✓ 활성화 / 준비중)

---

### ✅ Step 9: 빌드 및 컴파일 확인

```bash
# 빌드 실행
./gradlew clean build -x test

# 기대 결과
BUILD SUCCESSFUL in XX초
컴파일 오류: 0
컴파일 경고: 0 (또는 무시 가능한 수준)
```

**체크 항목**:

- [ ] 빌드 성공
- [ ] 컴파일 오류 없음
- [ ] 모든 클래스 import 됨

---

### ✅ Step 10: 애플리케이션 시작 및 작동 확인

```bash
# 애플리케이션 시작
./gradlew bootRun

# 또는 IDE에서 실행
# 포트: 8080
# URL: http://localhost:8080/admin/[path]/list
```

**테스트 항목**:

- [ ] 애플리케이션 시작 성공
- [ ] 목록 페이지 접근 가능 (/admin/[path]/list)
- [ ] 등록 페이지 작동 (/admin/[path]/create)
- [ ] 데이터 등록 가능
- [ ] 목록에서 데이터 확인
- [ ] 수정 페이지 작동
- [ ] 삭제 기능 작동
- [ ] 메뉴가 admin 페이지에 표시됨

---

## 💾 예시: 참조 모듈

이미 구현된 모듈을 참고합니다:

### 공시자료 (Announcement)

- **Entity**: `domain/announcement/entity/Announcement.java`
- **Service**: `domain/announcement/service/AnnouncementService.java`
- **Controller**: `web/controller/AnnouncementController.java`
- **Template**: `templates/manager/announcements/*`

### 라이프뉴스 (News)

- **Entity**: `domain/news/entity/News.java`
- **Service**: `domain/news/service/NewsService.java`
- **Controller**: `web/controller/NewsController.java`

---

## 🚨 자주 하는 실수 및 해결책

### 1. data-model.md 생략

❌ 안함: 코드 구현 후 db 스키마 고민
✅ 해야함: data-model.md에 먼저 정의 후 코드 작성

### 2. 체크리스트 불완전

❌ 안함: 몇 개 단계만 완료
✅ 해야함: 10단계 모두 완료

### 3. SmartEditor2 콘텐츠 동기화 누락

❌ 안함: textarea에만 작성
✅ 해야함: 폼 제출 전 `oEditors[0].exec("UPDATE_CONTENTS_FIELD", [])`

### 4. 파일명 컨벤션 무시

❌ 안함: [domain]-list.html, [Domain]Dto.java 등 일관성 없음
✅ 해야함: 공시자료, 뉴스와 동일한 패턴 사용

### 5. 메뉴 추가 누락

❌ 안함: admin.html 업데이트 안함
✅ 해야함: 사이드바 + 대시보드 카드 모두 추가

### 6. 검색 기능 미구현

❌ 안함: SearchRepository 메서드 없음
✅ 해야함: `findByTitleContainingIgnoreCase()` 메서드 추가

### 7. 페이지네이션 10개 아님

❌ 안함: PageRequest.of(page, 20)
✅ 해야함: PageRequest.of(page, 10) - 기본 10개

---

## 🔗 빠른 링크

| 항목          | 파일                                                                  |
| ------------- | --------------------------------------------------------------------- |
| 데이터 모델   | `.specify/data-model.md`                                              |
| 메뉴 구조     | `.specify/features/board-comments/data-model-aplus_관리자메뉴구성.md` |
| 구현 현황     | 이 파일의 "구현 현황 대시보드" 섹션                                   |
| 공시자료 참고 | `src/main/java/com/apluslife/domain/announcement/`                    |
| 뉴스 참고     | `src/main/java/com/apluslife/domain/news/`                            |
| 파일 업로드   | `src/main/java/com/apluslife/common/util/FileUtil.java`               |

---

## 📞 지원 및 문의

- 데이터 모델 관련: `data-model.md` 참조
- 메뉴 구조 관련: `data-model-aplus_관리자메뉴구성.md` 참조
- 코드 패턴 관련: `AnnouncementController.java`, `NewsService.java` 참조

---

**최종 업데이트**: 2025-10-23
**버전**: 1.0
**작성자**: Claude AI Assistant
**용도**: 지속적인 개발 참조 문서

**다음 개발자에게**:
이 문서를 정독하고 체크리스트를 따르면, 일관되고 안정적인 코드를 작성할 수 있습니다.
질문이 있으면 먼저 data-model.md와 이 문서를 확인하세요!
