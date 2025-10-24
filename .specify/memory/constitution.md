<!--
Sync Impact Report:
- Version change: (initial) → 1.0.0
- Added sections: All core principles, Security Standards, Development Workflow, Governance
- Templates requiring updates: ✅ All templates validated
- Follow-up TODOs: None
-->

# A+Life 홈페이지 Constitution

## Core Principles

### I. 계층형 아키텍처 (Layered Architecture)

모든 기능은 명확한 계층 구조를 따라야 합니다:
- **Presentation Layer**: HTTP 요청/응답, 사용자 입력 검증만 처리
- **Business Layer**: 비즈니스 로직, 트랜잭션 관리, 데이터 검증 및 변환
- **Data Access Layer**: 데이터 영속성, 쿼리 최적화, 데이터 매핑
- **Infrastructure Layer**: 시스템 설정, 보안 정책, 환경별 설정

**근거**: 명확한 책임 분리로 유지보수성과 테스트 가능성을 보장합니다.

### II. 도메인 중심 설계 (Domain-Driven Design)

패키지는 기술이 아닌 도메인으로 구성되어야 합니다:

**1. 역할 기반 Controller 분리**
- `manager/controller/` - 관리자 기능만 (공시자료, 라이프뉴스, 관리자 대시보드)
  - `/admin/**` 경로
  - `@PreAuthorize("hasRole('ADMIN')")` 필수

- `web/controller/` - 공개/회원 기능 (홈, 회사소개, 게시판, 회원정보)
  - `/`, `/login`, `/board/**`, `/mypage` 등
  - 공개 또는 `@PreAuthorize("hasRole('USER')")`

**2. 도메인별 Business Logic (Domain Package)**
- `domain/announcement/` - 공시자료
- `domain/lifenews/` - 라이프뉴스 (MyBatis 사용)
- `domain/board/` - 게시판
- `domain/member/` - 회원 정보
- `domain/log/` - 로깅
- 등등...

각 도메인은 명확한 계층 구조:
```
domain/{domain-name}/
├── entity/          # JPA 엔티티
├── dto/             # 요청/응답 DTO
├── service/         # 비즈니스 로직
├── repository/      # JPA Repository
└── mapper/          # MyBatis Mapper (선택)
```

**3. 공통 기능**
- `common/` - 유틸리티, 상수, 공통 컴포넌트
- `config/` - 보안, 데이터베이스, 인프라 설정

**근거**:
- 역할별 분리로 보안성 강화
- 도메인 중심 설계로 비즈니스 요구사항 변경 시 영향 범위 최소화
- 팀 간 협업 용이 (관리자 기능 개발자 ↔ 회원 기능 개발자 분리)

### III. 보안 우선 (Security-First) - NON-NEGOTIABLE

모든 기능은 다음 보안 원칙을 준수해야 합니다:
- **인증/인가**: Spring Security 기반 역할 기반 접근 제어 (ADMIN, USER)
- **비밀번호 암호화**: BCrypt 해싱 (강도 12 이상)
- **입력 검증**: `@Valid`, `@NotBlank`, `@Pattern` 사용 필수
- **SQL 인젝션 방지**: 파라미터 바인딩 사용, 동적 쿼리 금지
- **XSS 방지**: `HtmlUtils.htmlEscape()` 또는 Thymeleaf 자동 이스케이핑
- **보안 헤더**: CSP, HSTS, X-Content-Type-Options 적용

**근거**: 사용자 데이터 보호는 타협할 수 없는 최우선 과제입니다.

### IV. 환경별 설정 분리 (Environment Separation)

모든 환경별 설정은 명확히 분리되어야 합니다:
- **개발(dev)**: MSSQL 개발용, Connection Pool 최소, JPA ddl-auto=update, DEBUG 로깅, Thymeleaf 캐시 비활성화
- **운영(prod)**: MSSQL 운영용, Connection Pool 최대, JPA ddl-auto=validate, INFO 로깅, Thymeleaf 캐시 활성화

**근거**: 환경별 차이로 인한 예상치 못한 동작을 방지하고 운영 안정성을 보장합니다.

### V. 테스트 가능성 (Testability)

모든 코드는 테스트 가능하도록 작성되어야 합니다:
- **의존성 주입**: 생성자 주입 사용 (`@RequiredArgsConstructor` 또는 명시적 생성자)
- **단위 테스트**: Service 계층 비즈니스 로직 테스트 필수
- **통합 테스트**: Controller → Service → Repository 전체 흐름 검증
- **테스트 데이터**: H2 인메모리 DB 사용, 테스트 간 격리 보장

**근거**: 테스트 가능한 코드는 버그를 조기에 발견하고 리팩토링을 안전하게 수행할 수 있게 합니다.

### VI. 관찰 가능성 (Observability)

시스템 동작은 항상 추적 가능해야 합니다:
- **구조화된 로깅**: Log4j2 사용, 요청/응답 자동 로깅 (LoggingInterceptor)
- **전역 예외 처리**: GlobalExceptionHandler로 모든 예외 일관성 있게 처리 및 로깅
- **모니터링 지표**: 성능(응답 시간, 처리량), 보안(로그인 실패, 권한 위반), 비즈니스(사용자 활동)
- **Actuator**: /actuator/health 엔드포인트를 통한 헬스 체크

**근거**: 문제 발생 시 신속한 원인 파악과 해결을 가능하게 합니다.

### VII. 코드 컨벤션 준수 (Code Conventions)

일관된 코딩 스타일을 유지해야 합니다:
- **네이밍**: camelCase (변수/메소드), PascalCase (클래스), UPPER_SNAKE_CASE (상수)
- **패키지**: `com.apluslife.web.{domain}.{layer}` 형식
- **트랜잭션**: Service 계층에서만 `@Transactional` 사용
- **권한 검증**: Controller 메소드에 `@PreAuthorize` 사용
- **주석**: JavaDoc으로 public API 문서화, 복잡한 로직은 인라인 주석 추가

**근거**: 팀원 간 코드 이해도를 높이고 온보딩 시간을 단축합니다.

## 기술 스택 표준 (Technology Stack Standards)

### Backend 필수 기술
- **Framework**: Spring Boot 3.2.1 (Java 21)
- **Security**: Spring Security 6
- **ORM/Data Access**:
  - Spring Data JPA (신규 개발, 조회/단순 CRUD)
  - MyBatis 3.0.3 (복잡한 쿼리, 레거시 DB 매핑)
- **Template Engine**: Thymeleaf 3
- **Database**: H2 (dev), MSSQL (prod)
- **Logging**: SLF4J + Logback
- **Build Tool**: Gradle

### Frontend 필수 기술
- **UI Framework**: AdminLTE (관리자), Bootstrap 5 (사용자)
- **JavaScript**: Vanilla JS (jQuery 최소화)
- **Template**: Thymeleaf Fragments
- **CSS**: Inline styles (Fragments 사용 시)

### Database 접근 방식 선택 기준

**Spring Data JPA 사용:**
- 신규 엔티티 설계
- 간단한 CRUD 작업
- 관계형 데이터 (1:N, M:N 매핑)
- 자동 쿼리 생성이 효율적인 경우
- 예: `domain/announcement/` (공시자료)

**MyBatis 사용:**
- 복잡한 SQL 쿼리 (여러 테이블 JOIN)
- 동적 쿼리 필요
- 원본 DB 스키마에 정확히 매핑
- 성능 최적화가 중요한 경우
- 예: `domain/lifenews/` (라이프뉴스)

**변경 시 요구사항**: 주요 기술 스택 변경은 아키텍처 리뷰 및 팀 승인 필요

## 개발 워크플로우 (Development Workflow)

### 기능 개발 프로세스
1. **명세 작성**: 기능 요구사항, 사용자 시나리오, 수용 기준 정의
2. **계획 수립**: 설계 아티팩트 생성 (데이터 모델, API 설계, UI 목업)
3. **작업 분해**: 독립적으로 실행 가능한 작업 목록 생성
4. **구현**: 계층별 순서 (Entity → Repository → Service → Controller → View)
5. **테스트**: 단위 테스트 → 통합 테스트 → 수동 테스트
6. **리뷰**: 코드 리뷰, 보안 검토, 성능 검토
7. **배포**: 개발 환경 배포 → 검증 → 운영 배포

### 품질 게이트 (Quality Gates)
- 모든 비즈니스 로직은 단위 테스트 커버리지 80% 이상
- 보안 체크리스트 100% 통과 필수
- 코드 리뷰 승인 1명 이상 필요
- 성능 회귀 테스트 통과 (응답 시간 기준선 대비 20% 이내)

### 브랜치 전략
- `master`: 운영 배포 브랜치
- `develop`: 개발 통합 브랜치
- `feature/*`: 기능 개발 브랜치
- `hotfix/*`: 긴급 수정 브랜치


**Version**: 1.1.0 | **Ratified**: 2025-01-21 | **Last Amended**: 2025-10-24

### 개정 내역

**v1.1.0 (2025-10-24)**
- 도메인 중심 설계에 역할 기반 Controller 분리 추가
  - `manager/controller/` - 관리자 기능
  - `web/controller/` - 공개/회원 기능
- 기술 스택에 Spring Data JPA vs MyBatis 선택 기준 명시
- Java 버전 업그레이드: 17 → 21
- Spring Boot 버전 명시: 3.2.1
- Gradle 빌드 도구 추가

**v1.0.0 (2025-01-21)**
- 초기 constitution 작성
