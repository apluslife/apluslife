# Tasks: A-Plus Life Community Platform

**Input**: Design documents from `/specs/001-apluslife-platform/`
**Prerequisites**: plan.md, spec.md

**Note**: This task list represents the work already implemented in the project. It serves as documentation of the implementation approach and can be used as a reference for future enhancements.

## Format: `[ID] [P?] [Story] Description`
- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 Create Spring Boot project with Gradle build configuration
- [x] T002 Configure build.gradle with Spring Boot 3.2.1 dependencies (Web, JPA, Security, Thymeleaf)
- [x] T003 [P] Setup multi-environment configuration files (application.yml, application-local.yml, application-prod.yml)
- [x] T004 [P] Configure Lombok for boilerplate code reduction
- [x] T005 Create main application class in src/main/java/com/apluslife/AplusLifeApplication.java

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T006 Configure H2 database for development environment
- [x] T007 Configure SQL Server JDBC driver for production
- [x] T008 Enable JPA auditing for automatic timestamp management
- [x] T009 Setup Spring Security configuration in src/main/java/com/apluslife/config/SecurityConfig.java
- [x] T010 [P] Configure async processing in src/main/java/com/apluslife/config/AsyncConfig.java
- [x] T011 [P] Create file utility class in src/main/java/com/apluslife/common/util/FileUtil.java
- [x] T012 Setup data initializer in src/main/java/com/apluslife/config/DataInitializer.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Member Authentication and Profile Management (Priority: P1) 🎯 MVP

**Goal**: Enable users to register, log in, and access their profiles with role-based access control

**Independent Test**: Create a member account, log in with credentials, verify authentication, and access profile information

### Data Layer for User Story 1

- [x] T013 [P] [US1] Create Member entity in src/main/java/com/apluslife/domain/member/entity/Member.java
  - Fields: id (PK), pw, enpw, name, email, custNo, memberGubun, withdraw, pwdate, manageLevel
  - Methods: isAdmin(), isUser(), isWithdrawn(), isLifeMember(), isNormalMember()
- [x] T014 [P] [US1] Create MemberRepository interface in src/main/java/com/apluslife/domain/member/repository/MemberRepository.java

### DTOs for User Story 1

- [x] T015 [P] [US1] Create LoginRequest DTO in src/main/java/com/apluslife/domain/member/dto/LoginRequest.java
- [x] T016 [P] [US1] Create LoginResponse DTO in src/main/java/com/apluslife/domain/member/dto/LoginResponse.java
- [x] T017 [P] [US1] Create LoginMemberDto in src/main/java/com/apluslife/domain/member/dto/LoginMemberDto.java

### Business Logic for User Story 1

- [x] T018 [US1] Implement CustomUserDetailsService in src/main/java/com/apluslife/domain/member/service/CustomUserDetailsService.java
  - Integrate with Spring Security UserDetailsService
  - Load user by ID and validate credentials
- [x] T019 [US1] Implement MemberService in src/main/java/com/apluslife/domain/member/service/MemberService.java
  - Methods: login(), findById(), authentication logic

### Web Layer for User Story 1

- [x] T020 [US1] Implement MemberController in src/main/java/com/apluslife/web/controller/MemberController.java
  - POST /api/member/login endpoint
  - Login request handling and response
  - Session management
- [x] T021 [US1] Implement MainController in src/main/java/com/apluslife/web/controller/MainController.java
  - GET / endpoint for main page
  - User authentication check

### Security Configuration for User Story 1

- [x] T022 [US1] Configure Spring Security authentication
  - Password encoder configuration
  - Custom authentication provider
  - Session management
  - CSRF protection configuration

**Checkpoint**: User Story 1 complete - Users can register, log in, and access authenticated features

---

## Phase 4: User Story 2 - Board Content Creation and Management (Priority: P2)

**Goal**: Enable users to create, view, edit, and delete board posts with file attachments

**Independent Test**: Log in as a user, create a board post with content, attach files, view the post, edit it, and delete it

### Data Layer for User Story 2

- [x] T023 [P] [US2] Create Board entity in src/main/java/com/apluslife/domain/board/entity/Board.java
  - Fields: idx (PK), boardType, title, content, writer, writerId, viewCount, isNotice, isDeleted, createdDate, modifiedDate
  - Methods: update(), increaseViewCount(), delete(), addFile(), removeFile()
  - OneToMany relationship with BoardFile
- [x] T024 [P] [US2] Create BoardFile entity in src/main/java/com/apluslife/domain/board/entity/BoardFile.java
  - Fields: idx (PK), boardIdx (FK), originalFileName, savedFileName, fileSize, fileType, filePath, fileCategory, uploadDate, isDeleted
  - ManyToOne relationship with Board
- [x] T025 [P] [US2] Create BoardRepository interface in src/main/java/com/apluslife/domain/board/repository/BoardRepository.java
- [x] T026 [P] [US2] Create BoardFileRepository interface in src/main/java/com/apluslife/domain/board/repository/BoardFileRepository.java

### DTOs for User Story 2

- [x] T027 [P] [US2] Create BoardDto in src/main/java/com/apluslife/domain/board/dto/BoardDto.java
- [x] T028 [P] [US2] Create BoardFileDto in src/main/java/com/apluslife/domain/board/dto/BoardFileDto.java
- [x] T029 [P] [US2] Create BoardWriteRequest in src/main/java/com/apluslife/domain/board/dto/BoardWriteRequest.java

### Business Logic for User Story 2

- [x] T030 [US2] Implement BoardService in src/main/java/com/apluslife/domain/board/service/BoardService.java
  - Methods: createBoard(), updateBoard(), deleteBoard(), getBoard(), listBoards(), handleFileUpload()
  - File management integration
  - Soft delete logic
  - View count increment

### Web Layer for User Story 2

- [x] T031 [US2] Implement BoardController in src/main/java/com/apluslife/web/controller/BoardController.java
  - GET /board endpoints for listing and viewing
  - POST /board endpoints for creation
  - PUT /board endpoints for updates
  - DELETE /board endpoints for soft deletion
  - File upload handling
  - Notice management (admin only)

**Checkpoint**: User Story 2 complete - Users can create, view, edit, and delete board posts with file attachments

---

## Phase 5: User Story 3 - User Activity Monitoring and Audit Trail (Priority: P3)

**Goal**: Automatically log all user actions for security auditing and behavior analysis

**Independent Test**: Perform various actions (login, create post, edit post), then verify all actions are logged with complete details

### Data Layer for User Story 3

- [x] T032 [P] [US3] Create UserActionLog entity in src/main/java/com/apluslife/domain/log/entity/UserActionLog.java
  - Fields: idx (PK), userId, userName, actionType, actionTarget, actionDetail, ipAddress, userAgent, actionResult, errorMessage, actionTime
  - Indexes: idx_user_id, idx_action_time, idx_action_type
- [x] T033 [P] [US3] Create UserActionLogRepository interface in src/main/java/com/apluslife/domain/log/repository/UserActionLogRepository.java
  - Query methods for filtering by user, action type, date range

### Business Logic for User Story 3

- [x] T034 [US3] Implement UserActionLogService in src/main/java/com/apluslife/domain/log/service/UserActionLogService.java
  - Methods: logAction(), logSuccess(), logFailure(), queryLogs()
  - Async logging support

### AOP Integration for User Story 3

- [x] T035 [US3] Implement UserActionLogAspect in src/main/java/com/apluslife/common/aspect/UserActionLogAspect.java
  - AOP pointcuts for controller methods
  - Automatic logging of HTTP requests
  - IP address and user agent extraction
  - Success/failure detection
  - Exception handling and error message capture

**Checkpoint**: User Story 3 complete - All user actions are automatically logged with comprehensive audit trails

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Production readiness, security hardening, and performance optimization

- [x] T036 [P] Configure CORS for API security
- [x] T037 [P] Setup character encoding filter for UTF-8 support
- [x] T038 [P] Enable Spring DevTools for development productivity
- [x] T039 Configure HikariCP connection pooling
- [x] T040 [P] Setup proper error handling and validation
- [x] T041 [P] Configure session management and timeout
- [x] T042 Add Spring Boot Actuator health checks (optional)
- [x] T043 Document API endpoints (optional)
- [x] T044 Performance testing for concurrent users (optional)

---

## Summary

### Task Statistics
- **Total Tasks**: 44
- **Phase 1 (Setup)**: 5 tasks
- **Phase 2 (Foundation)**: 7 tasks
- **Phase 3 (User Story 1)**: 10 tasks
- **Phase 4 (User Story 2)**: 9 tasks
- **Phase 5 (User Story 3)**: 4 tasks
- **Phase 6 (Polish)**: 9 tasks

### User Story Coverage
- **US1 (Member Authentication)**: 10 tasks covering entity, DTOs, service, controller, and security
- **US2 (Board Management)**: 9 tasks covering entities, repositories, DTOs, service, and controller
- **US3 (Activity Logging)**: 4 tasks covering entity, repository, service, and AOP aspect

### Parallel Opportunities
- Phases 1-2: Infrastructure tasks can run in parallel after initial setup
- Phase 3: DTOs (T015-T017) can be created in parallel
- Phase 4: Entities and DTOs (T023-T029) can be created in parallel
- Phase 5: Entity and repository (T032-T033) can be created in parallel
- Phase 6: Most polish tasks are independent and can run in parallel

### Implementation Status
All tasks marked as complete [x] indicate the current implemented state of the A-Plus Life platform.

---

## Dependencies

### User Story Dependencies
- **US2 (Board)** depends on **US1 (Member)** for authentication and author information
- **US3 (Logging)** can be implemented independently but integrates with US1 and US2 for comprehensive logging

### Task Dependencies
- T018 (CustomUserDetailsService) depends on T013 (Member entity) and T014 (MemberRepository)
- T030 (BoardService) depends on T023-T024 (Board entities) and T025-T026 (Repositories)
- T035 (UserActionLogAspect) depends on T034 (UserActionLogService) and T032 (UserActionLog entity)

### Technology Dependencies
- Spring Boot 3.2.1 and all core dependencies configured in build.gradle
- H2 database for development, SQL Server for production
- Lombok for code generation
- Spring Security 6 for authentication/authorization

---

## Independent Testing Approach

### User Story 1 Testing
- Create test user accounts with different roles (Admin, User)
- Test login with valid and invalid credentials
- Verify role-based access control
- Test withdrawn member access denial

### User Story 2 Testing
- Create board posts as authenticated user
- Attach files to posts and verify uploads
- Edit posts and verify modifications
- Delete posts and verify soft deletion
- Test notice marking (admin only)
- Verify view count increments

### User Story 3 Testing
- Perform login and verify log creation
- Create/edit/delete content and check logs
- Verify IP address and user agent capture
- Test error scenario logging
- Query logs by user, action type, date range

---

## MVP Scope

The Minimum Viable Product consists of **User Story 1 only** (Member Authentication), which provides:
- User registration and login
- Role-based access control
- Session management
- Basic security features

This MVP delivers immediate value by establishing user identity and access control, which is the foundation for all other features.
