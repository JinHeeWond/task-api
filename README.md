# Task API

할 일(Task)을 등록·조회·수정·삭제하는 Spring Boot REST CRUD API입니다.  
데이터베이스 없이 Java Collection 기반의 메모리 저장소를 사용했습니다.

- Organization Repository: https://github.com/2026-2-WebService/assign05-c01-22300743
- Personal Repository: https://github.com/JinHeeWond/task-api
- Deployment URL: 배포 후 업데이트 예정

## 1. 프로젝트 소개

관리하는 데이터는 할 일입니다. 각 할 일은 제목, 설명, 마감일, 우선순위, 완료 여부, 카테고리를 가집니다.

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long | 서버가 자동 생성하는 식별자 |
| title | String | 할 일 제목 |
| description | String | 상세 설명 |
| dueDate | LocalDate | 마감일 |
| priority | Integer | 우선순위, 1~5 |
| completed | Boolean | 완료 여부 |
| category | String | 할 일 분류 |

### 프로젝트 구조

```text
Client
  → TaskController
  → TaskService
  → TaskRepository
  → MemoryTaskRepository
  → LinkedHashMap<Long, Task>
```

### 로컬 실행 방법

```bash
./gradlew bootRun
```

서버 실행 후 기본 주소는 `http://localhost:8080`입니다.

### Docker 실행 방법

```bash
docker build -t task-api .
docker run --rm -p 8080:8080 task-api
```

`Dockerfile`에서 Gradle로 JAR 파일을 빌드하고, Java 17 JRE 컨테이너에서 실행하도록 구성했습니다.

## 2. 개발환경 및 Dependency

| 항목 | 사용 내용 |
|---|---|
| IDE | IntelliJ IDEA |
| JDK | Eclipse Temurin 17.0.20.1 |
| Spring Boot | 4.1.1 |
| Build Tool | Gradle 9.7.1 |
| 데이터 저장 | `LinkedHashMap<Long, Task>` |
| 배포 환경 | Docker 및 Render 배포 예정 |

### Dependency

- `spring-boot-starter-web`: REST Controller를 작성하고 HTTP 요청·응답 및 JSON 변환을 처리하기 위해 사용했습니다.
- `spring-boot-starter-test`: 프로젝트 실행 및 테스트 환경을 확인하기 위해 사용했습니다.

## 3. API Endpoint

| Method | URL | 기능 |
|---|---|---|
| POST | `/api/tasks` | 할 일 등록 |
| GET | `/api/tasks` | 전체 조회 |
| GET | `/api/tasks/{id}` | 단건 조회 |
| PUT | `/api/tasks/{id}` | 할 일 수정 |
| DELETE | `/api/tasks/{id}` | 할 일 삭제 |
| GET | `/api/tasks?completed=true` | 완료 여부 필터 조회 |

### 등록 요청 예시

```json
{
  "title": "Spring Boot 과제",
  "description": "REST CRUD API 구현",
  "dueDate": "2026-10-10",
  "priority": 3,
  "completed": false,
  "category": "과제"
}
```

### 등록 응답 예시

```json
{
  "id": 1,
  "title": "Spring Boot 과제",
  "description": "REST CRUD API 구현",
  "dueDate": "2026-10-10",
  "priority": 3,
  "completed": false,
  "category": "과제"
}
```

## 4. Solution 분석

### Q1. POST 요청은 어떤 순서로 처리되는가?

`BookController.create(@RequestBody BookRequest request)`가 요청을 받고 `BookService.create(BookRequest r)`를 호출합니다. 이후 `MemoryBookRepository.save(Book book)`이 데이터를 메모리에 저장합니다. Controller는 HTTP 요청과 응답을 연결하고, Service는 객체 생성과 변환을 담당하며, Repository는 저장을 담당합니다.

### Q2. 새 데이터의 ID는 어디에서 생성되는가?

`MemoryBookRepository.save(Book book)` 메서드에서 `++sequence`로 새 ID를 생성합니다. 생성한 값을 `book.setId(++sequence)`으로 Book 객체에 넣은 뒤 `store.put(book.getId(), book)`으로 저장합니다.

### Q3. BookRequest, Book, BookResponse를 구분하는 이유는 무엇인가?

`BookRequest`는 클라이언트가 등록·수정할 때 보내는 요청 데이터입니다. `Book`은 Repository가 실제로 저장하고 관리하는 Domain 객체입니다. `BookResponse`는 ID를 포함해 클라이언트에게 반환하는 응답 데이터입니다. `BookService.create()`와 `BookService.toResponse()`에서 각 객체의 역할이 구분됩니다.

### Q4. 존재하지 않는 ID에서 404는 어떻게 반환되는가?

`BookService.findById(Long id)`와 `BookService.delete(Long id)`는 내부의 `findBook(Long id)`를 호출합니다. `findBook()`은 `repository.findById(id).orElseThrow(...)`를 사용하며, 데이터가 없으면 `ResponseStatusException(HttpStatus.NOT_FOUND, ...)`을 발생시켜 404 Not Found를 반환합니다.

### Q5. Domain 객체는 어떻게 Response DTO로 변환되는가?

`BookService.toResponse(Book b)` 메서드가 `Book`의 `id`, `title`, `author`, `price`를 사용하여 `new BookResponse(...)`를 생성합니다. 전체 조회에서는 `BookService.findAll()`의 `stream().map(this::toResponse).toList()`가 `List<Book>`을 `List<BookResponse>`로 변환합니다.

### Q6. Service가 Repository 인터페이스 타입을 주입받는 이유는 무엇인가?

`BookService`는 `BookRepository` 타입으로 주입받기 때문에 `MemoryBookRepository` 구현에 직접 의존하지 않습니다. 따라서 나중에 Database Repository로 구현체를 교체해도 Service의 CRUD 로직을 유지할 수 있습니다.

## 5. 개발 과정 요약

1. IntelliJ에서 Gradle, Java 17, Spring Web 기반의 `task-api` 프로젝트를 생성했습니다.
2. `Task`, `TaskRequest`, `TaskResponse`를 작성하여 Domain 객체와 요청·응답 DTO를 분리했습니다.
3. `TaskRepository` 인터페이스와 `MemoryTaskRepository`를 작성하고 `LinkedHashMap`으로 데이터를 저장하도록 구현했습니다.
4. `TaskService`에 CRUD, DTO 변환, ID 미존재 시 404 처리, 입력값 검증을 작성했습니다.
5. `TaskController`에서 `/api/tasks` REST API를 연결하고 curl로 등록·조회·수정·삭제를 테스트했습니다.

## 6. 기능 수정·확장

### A. 잘못된 입력 처리

제목이 비어 있거나 우선순위가 1~5 범위를 벗어나는 데이터가 저장되지 않도록 했습니다.

- 수정 클래스·메서드: `TaskService.validate(TaskRequest request)`
- 빈 제목 요청: `POST /api/tasks`
- 실제 결과: `400 Bad Request`

```json
{
  "title": "",
  "description": "잘못된 입력 테스트",
  "dueDate": "2026-10-20",
  "priority": 3,
  "completed": false,
  "category": "테스트"
}
```

### B. 완료 여부 필터 조회

완료한 할 일과 아직 완료하지 않은 할 일을 구분해서 조회하기 위해 `completed` 조건을 추가했습니다.

- 수정 클래스·메서드: `TaskController.findAll(Boolean completed)`, `TaskService.findAll(Boolean completed)`
- 요청: `GET /api/tasks?completed=true`
- 실제 결과: 완료 상태가 `true`인 데이터만 반환
- 요청: `GET /api/tasks?completed=false`
- 실제 결과: 조건에 맞는 데이터가 없을 때 `[]` 반환

## 7. 로컬 테스트 결과

| 순서 | 요청 | 결과 |
|---|---|---|
| 등록 | POST `/api/tasks` | `201 Created`, id 자동 생성 |
| 전체 조회 | GET `/api/tasks` | 등록한 목록 반환 |
| 단건 조회 | GET `/api/tasks/1` | id 1 데이터 반환 |
| 수정 | PUT `/api/tasks/1` | 제목, 우선순위, 완료 여부 수정 성공 |
| 필터 | GET `/api/tasks?completed=true` | 완료 데이터 반환 |
| 잘못된 입력 | 빈 title로 POST | `400 Bad Request` |
| 삭제 | DELETE `/api/tasks/1` | `204 No Content` |
| 삭제 후 조회 | GET `/api/tasks/1` | `404 Not Found` |

Docker 컨테이너에서도 `POST /api/tasks`를 실행해 `"id": 1` 응답을 확인했습니다.

## 8. 배포 과정 요약

Dockerfile을 작성해 Gradle 빌드와 Java 17 실행 환경을 분리했습니다.  
`application.properties`에는 아래 설정을 추가해 배포 환경의 PORT를 사용하도록 했습니다.

```properties
server.port=${PORT:8080}
```

배포 URL: 배포 후 업데이트 예정

배포 후에는 다음 요청과 실제 응답을 이 항목에 추가할 예정입니다.

```text
GET  [배포 URL]/api/tasks
POST [배포 URL]/api/tasks
GET  [배포 URL]/api/tasks?completed=true
```

메모리 저장소를 사용하므로 서버나 컨테이너를 재시작하면 저장한 데이터가 사라집니다.

## 9. Weekly Report

### Key Learning

1. Controller, Service, Repository 역할을 분리하면 요청 처리 흐름을 명확하게 관리할 수 있음을 배웠습니다.
2. Request DTO와 Response DTO를 분리해 입력 데이터와 반환 데이터를 구분하는 방법을 익혔습니다.
3. `ResponseStatusException`을 이용해 400, 404 같은 HTTP 상태 코드를 반환할 수 있음을 확인했습니다.

### Problem & Solution

처음 실행할 때 8080 포트를 다른 Spring Boot 서버가 사용하고 있어 실행에 실패했습니다. `lsof -i :8080`으로 포트를 점유한 Java 프로세스를 찾고 `kill PID`로 종료한 뒤 정상 실행했습니다.

### Code Review

`TaskService.findTask(Long id)`는 Repository에서 Optional로 받은 결과에 `orElseThrow()`를 사용합니다. 데이터가 없으면 `ResponseStatusException(HttpStatus.NOT_FOUND, ...)`을 발생시키므로 조회·수정·삭제에서 같은 404 처리 코드를 반복하지 않아도 됩니다.

### AI Usage

AI에게 Controller-Service-Repository 계층 구조, curl 테스트 방법, Dockerfile 작성 방법을 질문했습니다. 제 프로젝트에서 `Task` 주제와 필드를 직접 정하고, 코드 실행 결과가 201, 400, 404로 나오는지 직접 확인했습니다.

### Reflection

다음에는 JPA와 데이터베이스를 연결해 서버가 재시작되어도 데이터가 유지되도록 구현해 보고 싶습니다. 또한 `@Valid`와 Bean Validation을 사용한 입력값 검증도 더 학습하고 싶습니다.

### 건의사항

기본 CRUD 예제에 입력 검증과 검색·필터 기능을 단계적으로 확장하는 실습 시간이 추가되면 좋겠습니다.