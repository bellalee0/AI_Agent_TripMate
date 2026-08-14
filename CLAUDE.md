# 코드 컨벤션

> 이 문서는 Claude Code Review / GitHub Actions가 이 저장소의 PR을 검토할 때 따라야 하는 규칙입니다.

## 목차

1. [패키지 구조](#패키지-구조)
2. [명명 규칙](#명명-규칙)
3. [로직 작성 규칙](#로직-작성-규칙)
4. [테스트 코드 작성 규칙](#테스트-코드-작성-규칙)
5. [Git 브랜치 전략](#git-브랜치-전략)
6. [Git 커밋 메시지 컨벤션](#git-커밋-메시지-컨벤션)
7. [Pull Request 규칙](#pull-request-규칙)

---

## 패키지 구조

### common

여러 도메인에서 공통으로 사용하는 요소를 모아둔다.

- `entity` : 전체 엔티티
- `jwt` : 인증/인가 설정 및 필터
- `exception` : 공통 예외, 예외 코드, 전역 예외 핸들러
- `dto` : 공통 응답 객체 및 프로젝트 전역에서 사용할 dto
- `utils` : 프로젝트 내에서 사용할 자체 메서드
- `config` : 설정 클래스
- `enums` : 프로젝트 전역에서 사용하는 enum 관리
- `constants` : 프로젝트 전역에서 사용하는 상수 관리

### domain

큰 도메인 단위로 패키지를 분류하고(`user`, `auth`, `product` 등), 각 도메인 하위는 역할별로 동일하게 분리한다.

- `controller`
- `service`
- `repository`
- `dto`

```plain text
com.example.tripmate
├── common
│   ├── entity
│   ├── jwt
│   ├── exception
│   ├── utils
│   ├── dto
│   ├── enums
│   ├── constants
│   └── config
└── domain
    ├── user
    │   ├── controller
    │   ├── service
    │   ├── repository
    │   └── dto
    ├── auth
    │   ├── controller
    │   ├── service
    │   ├── repository
    │   └── dto
    └── product
        ├── controller
        ├── service
        ├── repository
        └── dto
```

새 도메인을 추가할 때는 위 하위 구조(controller/service/repository/dto)를 그대로 따른다.

---

## 명명 규칙

### DTO 명명 규칙

도메인 + 행위 + Request/Response

```
example: UserCreateRequest / MeetingUpdateRequest
```

### Collection 명명 규칙

변수명 + Collection 타입(List/Page)

```
example: keywordList / meetingPage
```

### 메서드명 규칙

- 컨트롤러, 서비스의 메서드명은 반드시 동일하게 맞춘다 (같은 API를 처리하는 Controller/Service 메서드명 일치)
- 동사 + 명사

```
example: searchTodo / getTodoPage / getTodo
```

```java
// Controller
@GetMapping("/todos/{todoId}")
public ResponseEntity<CommonResponse<TodoResponse>> getTodo(@PathVariable Long todoId) {

    TodoResponse response = todoService.getTodo(todoId);

    return ResponseEntity.ok(CommonResponse.success(TODO_GET_SUCCESS, response));
}

// Service - Controller와 동일한 메서드명 사용
@Transactional(readOnly = true)
public TodoResponse getTodo(Long todoId) {

    Todo todo = todoRepository.findTodoById(todoId);

    return TodoResponse.from(todo);
}
```

### 정적 팩토리 메서드 네이밍

- Entity → DTO 등 객체 생성 메서드는 `from` 으로 통일
- 파라미터 개수가 3개 이상인 경우 `from` 사용 (`of`, `create` 등 혼용 금지)
- 파라미터 타입이 달라 가독성이 떨어질 경우 사용 X

```java
public record TodoResponse(
        Long id,
        String title,
        String contents
) {

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents()
        );
    }
}
```

---

## 로직 작성 규칙

### 1. 공통 응답 객체 사용

모든 API 응답은 공통 응답 객체를 통해 반환하며, 공통 응답 객체는 Controller에서 생성한다.

```java
// X
@PostMapping("/users")
public ResponseEntity<CommonResponse<UserCreateResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {

    CommonResponse<UserCreateResponse> response = userService.createUser(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

// O
@PostMapping("/users")
public ResponseEntity<CommonResponse<UserCreateResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {

    UserCreateResponse response = userService.createUser(request);

    return ResponseEntity.status(HttpStatus.CREATED)
            // 공통 응답 객체 생성하여 반환
            .body(CommonResponse.success(USER_CREATE_SUCCESS, response));
}
```

### 2. 공통 예외 처리 객체 사용

예외 처리는 공통 예외 객체 + 전역 예외 처리 핸들러를 통해 응답한다. 예외 메시지와 상태 코드는 일관성을 유지한다.

### 3. 주석

- 메서드/클래스 전체에 대한 설명: `/** */` 사용
- 기타 설명: `//` 사용 (단, 로직의 동작 순서 등은 작성 X)

```java
/**
 * 공연 예매 로직
 */
@Transactional
public BookingCreateResponse createBooking(Long userId, BookingCreateRequest request) {
    ...
}
```

### 4. 가독성을 위한 개행

- 서비스단 메서드 시그니처 아래 한 줄 개행
- 논리적으로 다른 블록은 개행으로 구분

```java
public TodoResponse getTodo(long todoId) {

    Todo todo = todoRepository.findByIdWithUser(todoId)
            .orElseThrow(() -> new InvalidRequestException("Todo not found"));

    User user = todo.getUser();

    return new TodoResponse(
            todo.getId(),
            todo.getTitle(),
            todo.getContents(),
            todo.getWeather(),
            new UserResponse(user.getId(), user.getEmail()),
            todo.getCreatedAt(),
            todo.getModifiedAt()
    );
}
```

### 5. 중복 로직은 추출(extract)

비즈니스 로직에서 중복되는 코드는 메서드로 추출하고, 추출된 메서드는 클래스 맨 밑에 둔다.

### 6. Controller 파라미터 가독성 규칙

메서드 파라미터는 3개 이상일 경우 한 줄에 하나씩 개행한다.

```java
@GetMapping("/todos")
public ResponseEntity<Page<TodoResponse>> getTodos(
        @RequestParam(required = false) String weather,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        @PageableDefault(
                sort = "modifiedAt",
                direction = Sort.Direction.DESC
        )
        Pageable pageable
) {
    return ResponseEntity.ok(todoService.getTodos(pageable, weather, startDate, endDate));
}
```

### 7. Pageable 처리

Pageable 객체는 컨트롤러에서 처리한다 (서비스에서 Pageable 객체를 직접 생성하지 않는다). `@PageableDefault`를 사용한다.

```java
@GetMapping("/todos")
public Page<TodoResponse> getTodos(
        @PageableDefault Pageable pageable
) {
    return todoService.getTodos(pageable);
}
```

### 8. 중간 변수 사용

가독성을 위해 중간 변수를 사용해서 return 한다.

```java
// X
@PostMapping("/users")
public ResponseEntity<CommonResponse<UserCreateResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {

    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
}

// O
@PostMapping("/users")
public ResponseEntity<CommonResponse<UserCreateResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {

    UserCreateResponse response = userService.createUser(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

### 9. 리포지토리 default 메서드 사용

Optional로 반환되어 반복적으로 예외 처리가 필요한 경우, 예외 처리는 리포지토리에서 진행한다. 각 리포지토리 하단에 default 메서드를 생성한다.

```java
// X
public class UserService {

    @Transactional(readOnly = true)
    public UserGetResponse getUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        return UserGetResponse.from(user);
    }
}

// O
public interface UserRepository extends JpaRepository<User, Long> {

    default User findUserById(Long userId) {
        return findById(userId)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }
}

public class UserService {

    @Transactional(readOnly = true)
    public UserGetResponse getUser(Long userId) {

        User user = userRepository.findUserById(userId);

        return UserGetResponse.from(user);
    }
}
```

---

## 테스트 코드 작성 규칙

### Given-When-Then 구조 사용

테스트 메서드 내부를 given(Given) / when(When) / then(Then) 세 블록으로 명확히 구분하고 주석으로 표시한다.

```java
@Test
void getTodo_성공() {

    // given(Given)
    Long todoId = 1L;
    Todo todo = TodoFixture.create(todoId);
    given(todoRepository.findTodoById(todoId)).willReturn(todo);

    // when(When)
    TodoResponse response = todoService.getTodo(todoId);

    // then(Then)
    assertThat(response.id()).isEqualTo(todoId);
}
```

### API 생성 시 테스트코드 즉시 작성

- 컨트롤러/서비스 API 구현 직후, 같은 PR 안에서 테스트 코드까지 함께 작성한다.
- 테스트는 Service 내 메서드를 기준으로 단위 테스트로 진행한다.

---

## Git 브랜치 전략

### 브랜치 관리 구조

```plain text
main
 └─ dev
     ├─ v1
     │   └─ 도메인, 기능별
     └─ v2
 └─ prod
```

### 브랜치 명명 규칙

```
/(commit-type)/(domain)-(http-method)

example: /feat/user-create
```

- 기능 추가: `feat/`
- 문서 관리: `docs/`
- 리팩토링: `refactor/`
- 테스트 코드: `test/`

---

## Git 커밋 메시지 컨벤션

### 커밋 유형

| 커밋 유형 | 의미 |
| --- | --- |
| `feat` | 새로운 기능 추가 |
| `refactor` | 코드 리팩토링 |
| `fix` | 버그 수정 |
| `chore` | 빌드 관련 파일 수정 등 |
| `docs` | 문서 수정 |
| `test` | 테스트 코드, 리팩토링 테스트 코드 추가 |
| `release` | 릴리즈 |

### 제목과 본문 분리

```
feat: 제목

- 변경사항 1
- 변경사항 2
```

- 제목: 커밋 컨벤션 + 요약
- 본문: 변경한 내용과 이유 설명
- 여러 항목이 있다면 글머리 기호로 가독성 높이기
- 커밋은 최소 단위로 진행하기

---

## Pull Request 규칙

### PR 템플릿

```plain text
# [ ]PR 제목

## ✨ PR 요약
> 이 PR이 해결하는 문제를 한 줄로 설명해주세요

## 📋 변경 내용
-
-

## 🧱 설계 / 구조 변경
- 변경된 패키지 구조
- 도메인 / 레이어 변경 여부

## 🧪 테스트 결과
- 테스트 종류:
  - [ ] 단위 테스트
  - [ ] 통합 테스트
- 테스트 결과 요약:

## 🚨 Breaking Change
- [ ] 있음
- [ ] 없음

> 있다면 상세 설명
```

### PR 단위

최소 단위로 PR을 생성한다. API 단위가 이상적이며, 불가능하다면 리뷰어(Claude)를 고려해서 최소 단위로 올린다.

### 리뷰/병합 규칙 (1인 개발 + AI 리뷰어 기준)

이 저장소는 1인 개발 체제이며, Claude Code Review(GitHub Actions)를 리뷰어로 사용한다.

- PR은 Claude Code Review를 거친다. (PR 댓글에 `@claude`라고 멘션)
- 🔴 Important 등급 findings는 병합 전 반드시 해결한다.
- 🟡 Nit 등급은 우선순위에 따라 선택적으로 반영한다.
- 리뷰에서 지적된 컨벤션 위반 사항은 재리뷰(`@claude review`) 후 병합한다.