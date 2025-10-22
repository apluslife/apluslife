# Tasks: 게시판 댓글 기능

**Input**: Design documents from `.specify/features/board-comments/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/api-spec.md, quickstart.md

**Tests**: Tests are included as per A+Life Constitution requirement (85% coverage)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`
- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions
- Spring Boot monolithic application structure
- Source: `src/main/java/com/apluslife/web/front/comment/`
- Templates: `src/main/resources/templates/front/board/`
- Tests: `src/test/java/com/apluslife/web/front/comment/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create comment package structure at `src/main/java/com/apluslife/web/front/comment/` with subdirectories: controller, service, repository, entity, dto
- [ ] T002 [P] Create test package structure at `src/test/java/com/apluslife/web/front/comment/` with subdirectories: service, controller, repository
- [ ] T003 [P] Create templates directory at `src/main/resources/templates/front/board/fragments/` for comment UI components
- [ ] T004 [P] Create database migration script at `src/main/resources/db/migration/V001__create_comment_table.sql` for MSSQL production environment

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T005 Create Comment entity at `src/main/java/com/apluslife/web/front/comment/entity/Comment.java` with JPA annotations (@Entity, @ManyToOne, @Version, @CreatedDate, @LastModifiedDate)
- [ ] T006 Update Board entity to add @OneToMany relationship with Comment (cascade delete) and getCommentCount() method
- [ ] T007 [P] Create CommentRequest DTO at `src/main/java/com/apluslife/web/front/comment/dto/CommentRequest.java` with validation annotations (@NotBlank, @Size)
- [ ] T008 [P] Create CommentResponse DTO at `src/main/java/com/apluslife/web/front/comment/dto/CommentResponse.java` with factory method from(Comment, currentUserId)
- [ ] T009 Create BoardCommentRepository interface at `src/main/java/com/apluslife/web/front/comment/repository/BoardCommentRepository.java` with query methods
- [ ] T010 [P] Enable JPA Auditing by adding @EnableJpaAuditing to main application class or config class
- [ ] T011 [P] Verify H2 dev environment creates comment table automatically by running application with dev profile

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - 댓글 작성 및 조회 (Priority: P1) 🎯 MVP

**Goal**: 사용자는 게시글을 읽고 자신의 의견을 댓글로 남길 수 있습니다. 댓글은 게시글 하단에 시간순으로 표시됩니다.

**Independent Test**: 로그인한 사용자가 게시글 상세 페이지에서 댓글을 작성하고 저장하면, 해당 댓글이 즉시 게시글 하단에 표시되는지 확인합니다.

### Tests for User Story 1

**NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [ ] T012 [P] [US1] Create BoardCommentRepositoryTest at `src/test/java/com/apluslife/web/front/comment/repository/BoardCommentRepositoryTest.java` with @DataJpaTest for findByBoardIdOrderByCreatedAtAsc and countByBoardId
- [ ] T013 [P] [US1] Create BoardCommentServiceTest at `src/test/java/com/apluslife/web/front/comment/service/BoardCommentServiceTest.java` with Mockito for createComment and getCommentsByBoardId logic
- [ ] T014 [P] [US1] Create BoardCommentControllerTest at `src/test/java/com/apluslife/web/front/comment/controller/BoardCommentControllerTest.java` with @SpringBootTest and MockMvc for POST /api/boards/{boardId}/comments and GET /api/boards/{boardId}/comments

### Implementation for User Story 1

- [ ] T015 [US1] Implement BoardCommentService.createComment() at `src/main/java/com/apluslife/web/front/comment/service/BoardCommentService.java` with authentication check and business logic
- [ ] T016 [US1] Implement BoardCommentService.getCommentsByBoardId() at `src/main/java/com/apluslife/web/front/comment/service/BoardCommentService.java` using findByBoardIdWithUserAndBoard to prevent N+1
- [ ] T017 [US1] Create BoardCommentController with POST /api/boards/{boardId}/comments endpoint at `src/main/java/com/apluslife/web/front/comment/controller/BoardCommentController.java` with @PreAuthorize("isAuthenticated()")
- [ ] T018 [US1] Create BoardCommentController with GET /api/boards/{boardId}/comments endpoint at `src/main/java/com/apluslife/web/front/comment/controller/BoardCommentController.java` (no authentication required)
- [ ] T019 [US1] Create Thymeleaf comment list fragment at `src/main/resources/templates/front/board/fragments/comment-section.html` with th:each for displaying comments using th:text (XSS prevention)
- [ ] T020 [US1] Create Thymeleaf comment form fragment at `src/main/resources/templates/front/board/fragments/comment-section.html` with sec:authorize="isAuthenticated()" and form submission
- [ ] T021 [US1] Update board detail template to include comment-section fragment with th:replace
- [ ] T022 [US1] Add logging for comment creation and retrieval in BoardCommentService using Log4j2
- [ ] T023 [US1] Run all User Story 1 tests to verify green status

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - 댓글 수정 및 삭제 (Priority: P2)

**Goal**: 사용자는 자신이 작성한 댓글을 수정하거나 삭제할 수 있어야 합니다. 관리자는 모든 댓글을 관리할 수 있습니다.

**Independent Test**: 댓글 작성자 또는 관리자가 댓글의 "수정" 또는 "삭제" 버튼을 클릭하여 댓글을 변경하거나 제거할 수 있는지 확인합니다.

### Tests for User Story 2

- [ ] T024 [P] [US2] Add BoardCommentServiceTest tests for updateComment (owner success, non-owner forbidden) at `src/test/java/com/apluslife/web/front/comment/service/BoardCommentServiceTest.java`
- [ ] T025 [P] [US2] Add BoardCommentServiceTest tests for deleteComment (owner success, admin success, non-owner forbidden) at `src/test/java/com/apluslife/web/front/comment/service/BoardCommentServiceTest.java`
- [ ] T026 [P] [US2] Add BoardCommentControllerTest tests for PUT /api/boards/{boardId}/comments/{id} with authentication scenarios at `src/test/java/com/apluslife/web/front/comment/controller/BoardCommentControllerTest.java`
- [ ] T027 [P] [US2] Add BoardCommentControllerTest tests for DELETE /api/boards/{boardId}/comments/{id} with authentication scenarios at `src/test/java/com/apluslife/web/front/comment/controller/BoardCommentControllerTest.java`
- [ ] T028 [P] [US2] Add optimistic locking test (concurrent updates) in BoardCommentServiceTest to verify OptimisticLockException handling

### Implementation for User Story 2

- [ ] T029 [US2] Implement BoardCommentService.updateComment() at `src/main/java/com/apluslife/web/front/comment/service/BoardCommentService.java` with ownership verification and @Transactional
- [ ] T030 [US2] Implement BoardCommentService.deleteComment() at `src/main/java/com/apluslife/web/front/comment/service/BoardCommentService.java` with ownership or admin role verification
- [ ] T031 [US2] Add PUT /api/boards/{boardId}/comments/{id} endpoint in BoardCommentController with @PreAuthorize("isAuthenticated()")
- [ ] T032 [US2] Add DELETE /api/boards/{boardId}/comments/{id} endpoint in BoardCommentController with @PreAuthorize("isAuthenticated()")
- [ ] T033 [US2] Create custom exceptions (CommentNotFoundException, ForbiddenException) at `src/main/java/com/apluslife/web/front/comment/exception/` package
- [ ] T034 [US2] Add exception handling in GlobalExceptionHandler for OptimisticLockException returning 409 Conflict with user-friendly message
- [ ] T035 [US2] Update comment-section.html fragment to show edit/delete buttons conditionally based on isAuthor flag using Thymeleaf sec:authorize
- [ ] T036 [US2] Add JavaScript for edit/delete button click handlers with AJAX calls to PUT/DELETE endpoints at `src/main/resources/static/js/comment.js`
- [ ] T037 [US2] Add "(수정됨)" indicator display in comment template when updatedAt is not null
- [ ] T038 [US2] Add logging for comment update and delete operations including security violations
- [ ] T039 [US2] Run all User Story 2 tests to verify green status

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - 댓글 개수 표시 및 알림 (Priority: P3)

**Goal**: 게시글 목록에서 각 게시글의 댓글 개수가 표시되어, 사용자가 활발한 토론이 있는 게시글을 쉽게 확인할 수 있습니다.

**Independent Test**: 게시글 목록 페이지에서 각 게시글 옆에 댓글 개수가 표시되고, 댓글이 추가되면 실시간으로 업데이트되는지 확인합니다.

### Tests for User Story 3

- [ ] T040 [P] [US3] Add integration test for board list with comment count display in BoardControllerTest at `src/test/java/com/apluslife/web/front/board/controller/BoardControllerTest.java`
- [ ] T041 [P] [US3] Add test for comment count update after comment creation in BoardCommentServiceTest

### Implementation for User Story 3

- [ ] T042 [US3] Update Board entity getCommentCount() method to use @Formula or derived query if needed for performance
- [ ] T043 [US3] Update BoardService.getBoardList() to include comment count using JOIN FETCH or DTO projection at `src/main/java/com/apluslife/web/front/board/service/BoardService.java`
- [ ] T044 [US3] Update board list template at `src/main/resources/templates/front/board/list.html` to display comment count with icon using th:text
- [ ] T045 [US3] Add CSS styling for comment count badge at `src/main/resources/static/css/board.css`
- [ ] T046 [US3] Verify comment count updates after navigation from detail page back to list page
- [ ] T047 [US3] Run all User Story 3 tests to verify green status

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T048 [P] Add comprehensive JavaDoc comments to all public methods in BoardCommentService and BoardCommentController
- [ ] T049 [P] Code cleanup: Remove unused imports, format code according to A+Life conventions (camelCase, PascalCase)
- [ ] T050 [P] Performance optimization: Verify all comment queries use appropriate indexes (board_id, created_at)
- [ ] T051 [P] Add unit tests for Comment entity business methods (updateContent, isAuthor) at `src/test/java/com/apluslife/web/front/comment/entity/CommentTest.java`
- [ ] T052 Security hardening: Review all endpoints for CSRF token inclusion and XSS prevention with Thymeleaf th:text
- [ ] T053 [P] Create Postman collection for all comment API endpoints at `.specify/features/board-comments/postman/comment-api.json`
- [ ] T054 Run test coverage report with JaCoCo to verify 85% coverage target for comment package
- [ ] T055 Update quickstart.md with actual test results and screenshots if needed
- [ ] T056 Perform manual testing following quickstart.md validation checklist
- [ ] T057 [P] Update main README.md or CHANGELOG.md with comment feature announcement

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-5)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Phase 6)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Builds on US1 service/controller but independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Integrates with Board list but independently testable

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- Entity/DTO before Repository
- Repository before Service
- Service before Controller
- Controller before UI templates
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel (T002, T003, T004)
- All Foundational tasks marked [P] can run in parallel (T007, T008, T010, T011)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task: "Create BoardCommentRepositoryTest at src/test/java/com/apluslife/web/front/comment/repository/BoardCommentRepositoryTest.java"
Task: "Create BoardCommentServiceTest at src/test/java/com/apluslife/web/front/comment/service/BoardCommentServiceTest.java"
Task: "Create BoardCommentControllerTest at src/test/java/com/apluslife/web/front/comment/controller/BoardCommentControllerTest.java"

# These 3 test files can be created in parallel as they don't depend on each other
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (댓글 작성 및 조회)
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy to dev environment and demo

**Value Delivered**: Users can write and read comments on board posts

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP! 댓글 읽기/쓰기 가능)
3. Add User Story 2 → Test independently → Deploy/Demo (댓글 수정/삭제 추가)
4. Add User Story 3 → Test independently → Deploy/Demo (댓글 개수 표시 추가)
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (T012-T023)
   - Developer B: User Story 2 (T024-T039)
   - Developer C: User Story 3 (T040-T047)
3. Stories complete and integrate independently
4. Team collaborates on Phase 6 (Polish)

---

## Task Summary

- **Total Tasks**: 57
- **Setup Phase**: 4 tasks
- **Foundational Phase**: 7 tasks (CRITICAL BLOCKER)
- **User Story 1 (P1)**: 12 tasks (MVP target)
- **User Story 2 (P2)**: 16 tasks
- **User Story 3 (P3)**: 8 tasks
- **Polish Phase**: 10 tasks

### Parallel Opportunities Identified

- Setup phase: 3 parallel tasks (T002, T003, T004)
- Foundational phase: 4 parallel tasks (T007, T008, T010, T011)
- User Story 1 tests: 3 parallel tasks (T012, T013, T014)
- User Story 2 tests: 5 parallel tasks (T024-T028)
- User Story 3 tests: 2 parallel tasks (T040, T041)
- Polish phase: 7 parallel tasks (T048-T051, T053, T055, T057)

### Independent Test Criteria

- **US1**: 사용자가 댓글을 작성하고 게시글 상세 페이지에서 시간순으로 조회 가능
- **US2**: 작성자가 자신의 댓글을 수정/삭제 가능, 관리자가 모든 댓글 삭제 가능
- **US3**: 게시글 목록에서 각 게시글의 댓글 개수 표시

### Suggested MVP Scope

**Phase 1 + Phase 2 + Phase 3 (User Story 1 only)**

This delivers core comment functionality:
- ✅ 댓글 작성
- ✅ 댓글 조회
- ✅ XSS 방지
- ✅ 권한 제어 (로그인 필요)
- ✅ 85% 테스트 커버리지

**Estimated effort**: ~2-3 days for 1 developer

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing (TDD approach per Constitution V)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Follow A+Life Constitution principles: 계층형 아키텍처, 보안 우선, 테스트 가능성, 관찰 가능성
- All file paths use absolute package names for clarity
- Security annotations (@PreAuthorize) are mandatory per Constitution III
- XSS prevention via Thymeleaf th:text is NON-NEGOTIABLE per Constitution III
