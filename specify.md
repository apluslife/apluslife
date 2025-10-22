# 🏗️ A+Life 홈페이지 S/W 아키텍처

## 📋 아키텍처 개요

**A+Life 홈페이지**는 Spring Boot 기반의 **계층형 아키텍처**를 채택한 웹 애플리케이션입니다.

### 핵심 특징

- **계층형 아키텍처**: Presentation → Business → Data Access → Infrastructure
- **도메인 중심 패키지**: manager, front, login, mypage, common, config
- **역할 기반 접근 제어**: ADMIN, USER 권한 분리
- **환경별 설정**: dev(H2), prod(MySQL) 분리

---

## 🎯 아키텍처 패턴

### 계층 구조

```
┌─────────────────────────────────────┐
│        Presentation Layer          │ ← Controller, Thymeleaf
├─────────────────────────────────────┤
│        Business Layer              │ ← Service, Security
├─────────────────────────────────────┤
│        Data Access Layer           │ ← Repository, Entity
├─────────────────────────────────────┤
│        Infrastructure Layer         │ ← Database, Config
└─────────────────────────────────────┘
```

### 패키지 구조

```
com.apluslife.web/
├── manager/          # 관리자 기능
├── front/           # 공개 페이지
├── login/           # 인증/인가
├── mypage/          # 사용자 페이지
├── common/          # 공통 기능
└── config/          # 설정
```

---

## 🔧 기술 스택

### Backend

- **Spring Boot 3.5.6** (Java 17+)
- **Spring Security 6** (인증/인가)
- **Spring Data JPA** (ORM)
- **MyBatis 3.0.3** (SQL 매핑)
- **Thymeleaf 3** (템플릿 엔진)
- **H2/MSSQL** (데이터베이스)
- **Log4j2** (로깅)

### Frontend

- **AdminLTE 3.3.11** (관리자 UI)
- **Bootstrap 5** (CSS 프레임워크)
- **jQuery** (JavaScript)
- **Thymeleaf Fragments** (템플릿 컴포넌트)

---

## 🎮 계층별 구조

### 1. Presentation Layer (표현 계층)

**Controller 클래스들**:

- `MainController`: 루트 접근 처리
- `LoginController`: 로그인/회원가입
- `AdminController`: 관리자 기능
- `UserController`: 사용자 기능
- `BoardController`: 게시판 기능

**책임**:

- HTTP 요청/응답 처리
- 사용자 입력 검증
- 권한 기반 라우팅

### 2. Business Layer (비즈니스 계층)

**Service 클래스들**:

- `UserService`: 사용자 관리 로직
- `CustomUserDetailsService`: Spring Security 인증
- `BoardService`: 게시판 비즈니스 로직

**책임**:

- 비즈니스 로직 구현
- 트랜잭션 관리
- 데이터 검증 및 변환

### 3. Data Access Layer (데이터 접근 계층)

**Repository & Entity**:

- `UserRepository`: 사용자 데이터 접근
- `User`: 사용자 엔티티
- `BoardRepository`: 게시판 데이터 접근

**책임**:

- 데이터 영속성 관리
- 쿼리 최적화
- 데이터 매핑

### 4. Infrastructure Layer (인프라 계층)

**Configuration 클래스들**:

- `SecurityConfig`: 보안 설정
- `DatabaseConfig`: 데이터베이스 설정
- `DataInitializer`: 초기 데이터 생성

**책임**:

- 시스템 설정 관리
- 보안 정책 적용
- 환경별 설정 분리

---

## 🔐 보안 아키텍처

### Spring Security 구성

```
┌─────────────────────────────────────┐
│        Security Filter Chain       │
├─────────────────────────────────────┤
│  Authentication Manager            │
├─────────────────────────────────────┤
│  Authorization Manager             │
├─────────────────────────────────────┤
│  Password Encoder (BCrypt)         │
├─────────────────────────────────────┤
│  Session Management                │
└─────────────────────────────────────┘
```

### 보안 기능

- **인증**: 폼 기반 로그인, 세션 관리
- **인가**: 역할 기반 접근 제어 (ADMIN, USER)
- **암호화**: BCrypt 비밀번호 해싱 (강도 12)
- **보안 헤더**: CSP, HSTS, X-Content-Type-Options

---

## 📊 데이터 아키텍처

### 데이터베이스 설계

```sql
CREATE TABLE member (
    idx BIGINT PRIMARY KEY AUTO_INCREMENT,
    id VARCHAR(50) UNIQUE NOT NULL,
    pwd VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    manage_level VARCHAR(1) NOT NULL
);
```

CREATE TABLE admin (
idx BIGINT PRIMARY KEY AUTO_INCREMENT,
admin_id VARCHAR(50) UNIQUE NOT NULL,
admin_pwd VARCHAR(255) NOT NULL,
email VARCHAR(100) UNIQUE NOT NULL,
admin_name VARCHAR(100) NOT NULL,
manage_level VARCHAR(1) NOT NULL
);

### 데이터 접근 패턴

- **JPA**: 기본 CRUD 작업
- **MyBatis**: 복잡한 쿼리 및 매핑
- **H2**: 개발 환경 (인메모리)
- **MySQL**: 운영 환경

### 설정 구조 요약

1. application.yml - 공통 설정
   서버 기본 설정 (포트, 인코딩, 세션 등)
   기본 프로파일: dev (19라인)
   Spring, JPA, Thymeleaf, Security 공통 설정
   Management Endpoints 설정
2. application-dev.yml - 개발 환경 설정
   MSSQL 데이터소스 설정 (개발용)
   Connection Pool: 최대 5개, 최소 2개
   JPA: ddl-auto: update (스키마 자동 업데이트)
   로깅: DEBUG 레벨
   Thymeleaf 캐시: 비활성화
   DevTools 활성화
3. application-prod.yml - 운영 환경 설정 (새로 생성)
   MSSQL 데이터소스 설정 (운영용)
   Connection Pool: 최대 10개, 최소 5개
   JPA: ddl-auto: validate (스키마 변경 방지)
   로깅: INFO/WARN 레벨, 파일 로깅 포함
   Thymeleaf 캐시: 활성화 (성능 향상)

### 프로파일 전환 방법

로컬 환경:
java -jar app.jar --spring.profiles.active=local
개발 서버:
java -jar app.jar --spring.profiles.active=dev
운영 환경:
java -jar app.jar --spring.profiles.active=prod

---

## 🚀 배포 아키텍처

### 환경별 설정

- **Development**: H2 인메모리 DB, 포트 8080
- **Production**: MySQL 외부 DB, 포트 8080
- **Testing**: H2 테스트 DB, 포트 8081

### 배포 옵션

- **내장 Tomcat**: 개발/테스트 환경
- **외부 Tomcat**: 운영 환경 (WAR 배포)

### 실행 명령어

```bash
# 개발 서버 실행
./gradlew bootRun --args='--spring.profiles.active=dev'

# 운영 서버 실행
./gradlew bootRun --args='--spring.profiles.active=prod'

# WAR 파일 배포
java -jar build/libs/AplusLife\ Homepage-1.0.0.war
```

---

## 🔍 모니터링 아키텍처

### 로깅 시스템

- **LoggingInterceptor**: 요청/응답 자동 로깅
- **GlobalExceptionHandler**: 전역 예외 처리
- **Log4j2**: 구조화된 로깅

### 모니터링 지표

- **성능**: 응답 시간, 처리량, 에러율
- **보안**: 로그인 실패, 권한 위반
- **비즈니스**: 사용자 활동, 기능 사용률

---

## 📈 확장성 고려사항

### 수평적 확장

- **세션 클러스터링**: Redis 기반 세션 공유
- **로드 밸런싱**: 다중 인스턴스 배포
- **데이터베이스 샤딩**: 사용자별 데이터 분산

### 수직적 확장

- **캐싱 전략**: Redis 기반 데이터 캐싱
- **비동기 처리**: 메시지 큐 기반 작업 처리
- **CDN**: 정적 리소스 최적화

---

## 🔧 개발 가이드라인

### 아키텍처 원칙

1. **단일 책임 원칙**: 각 클래스는 하나의 책임만
2. **의존성 역전**: 인터페이스를 통한 느슨한 결합
3. **개방-폐쇄 원칙**: 확장에는 열려있고 수정에는 닫혀있음

### 코딩 컨벤션

- **패키지**: `com.apluslife.web.{domain}.{layer}`
- **네이밍**: camelCase (변수/메소드), PascalCase (클래스)
- **의존성 주입**: 생성자 주입 사용
- **트랜잭션**: Service 계층에서 `@Transactional`

### 보안 규칙

- **입력 검증**: `@Valid`, `@NotBlank`, `@Pattern` 사용
- **SQL 인젝션 방지**: 파라미터 바인딩 사용
- **XSS 방지**: `HtmlUtils.htmlEscape()` 사용
- **권한 검증**: `@PreAuthorize` 사용

---

## 📌 기본 계정 정보

### 테스트 계정

- **관리자**: `admin` / `admin123`
- **일반사용자**: `user` / `user123`
- **테스트사용자**: `john` / `john123`

### 접속 URL

- **메인 페이지**: http://localhost:8080
- **로그인**: http://localhost:8080/login
- **관리자 대시보드**: http://localhost:8080/manager/dashboard
- **H2 콘솔**: http://localhost:8080/h2-console
- **Actuator**: http://localhost:8080/actuator/health

---

**마지막 업데이트**: 2024년 12월  
**개발팀**: AplusLife Team  
**문의**: hunt0451@finelab.kr

##기존 java 서비스 kill
powershell.exe "taskkill /F /IM java.exe"
