# Generative AI Task Management API Exercise

This document addresses the separate "Generative AI tools" exercise from the
technical interview brief. It is an illustrative example and is not part of
the Pokemon Explorer runtime application.

## Scenario

The task is to generate a RESTful API for a task management system. The API
must support creating, reading, updating, and deleting tasks. Each task has a
title, description, status, and due date, and each task belongs to a user.

The example uses the same general approach as the Pokemon project: Java,
Spring Boot, PostgreSQL, JWT authentication, DTOs, separated controller and
application responsibilities, validation, and automated tests.

## Prompt used with GenAI

```text
Act as a senior Java and Spring Boot engineer. Generate a RESTful task
management API scaffold using Java 21, Spring Boot, PostgreSQL, and JWT.

Requirements:
- Support POST, GET, PUT, and DELETE operations for tasks.
- A task has: id, title, description, status, due_date, user_id,
  created_at, and updated_at.
- Status must be one of TODO, IN_PROGRESS, or DONE.
- Tasks belong to an existing User entity.
- Authenticated users may only read, update, or delete their own tasks.
- Validate required fields, title length, status, and due_date.
- Return 201 for creation, 200 for reads and updates, 204 for deletion,
  400 for invalid payloads, 401 for missing/invalid authentication,
  403 for ownership violations, and 404 for missing tasks.
- Use DTOs instead of exposing persistence entities from controllers.
- Keep controllers thin and place business rules in services.
- Use repository interfaces so the application layer does not depend directly
  on JPA details.
- Include representative unit tests for service rules and controller tests for
  HTTP status codes.

First show the proposed package structure and assumptions. Then provide
representative implementation code for the entity, enum, request/response
DTOs, repository, service, controller, exception handler, and tests. Explain
security and ownership decisions separately from the code.
```

## Representative generated output

The following is a reduced sample of the expected scaffold. It is intentionally
representative rather than a second runnable application.

```java
public enum TaskStatus {
    TODO, IN_PROGRESS, DONE
}

public record CreateTaskRequest(
        @NotBlank @Size(max = 160) String title,
        @Size(max = 2000) String description,
        @NotNull TaskStatus status,
        @FutureOrPresent LocalDate dueDate
) {}

@Entity
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(name = "due_date")
    private LocalDate dueDate;
}

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Optional<TaskEntity> findByIdAndUserId(Long taskId, Long userId);
    Page<TaskEntity> findAllByUserId(Long userId, Pageable pageable);
}

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(user.id(), request));
    }

    @GetMapping("/{id}")
    public TaskResponse get(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id) {
        return taskService.getOwnedTask(user.id(), id);
    }
}
```

The full implementation would add update and delete methods using the same
ownership-aware service boundary. The controller must not trust a `user_id`
provided by the client; the authenticated user is the owner.

## How the generated suggestions were validated

The generated output was treated as a draft and checked against the requested
behavior before being accepted. The validation process was:

1. Compare every endpoint and field with the exercise requirements.
2. Compile the application and run unit tests after each meaningful change.
3. Test successful CRUD requests through the HTTP layer.
4. Test invalid payloads, missing records, invalid status values, and invalid
   due dates.
5. Test unauthenticated requests and ownership boundaries with two users.
6. Verify that repository queries always include the authenticated user's ID.
7. Inspect database mappings and confirm that task entities are not returned
   directly by controllers.
8. Verify that error responses are consistent and do not expose stack traces.

## Corrections and improvements made after review

Typical generated-code issues were corrected as follows:

- Replaced direct entity exposure with request and response DTOs.
- Moved business rules out of controllers and into application services.
- Added `findByIdAndUserId` queries to enforce ownership at the persistence
  boundary.
- Added bean validation for title, description, status, and due date.
- Added explicit handling for `400`, `401`, `403`, `404`, and `409` responses.
- Prevented clients from assigning tasks to another user.
- Added pagination to task listing rather than returning an unbounded result.
- Added tests for both successful flows and rejected requests.
- Avoided trusting generated code until it passed compilation, tests, and a
  manual review against the requirements.

## Edge cases and security decisions

- Missing or malformed JWT: return `401 Unauthorized`.
- Existing task owned by another user: return `404` or `403` according to the
  API policy; this project would use `404` to avoid leaking record existence.
- Unknown task ID: return `404 Not Found`.
- Blank title or title above the maximum length: return `400 Bad Request`.
- Unsupported status: return `400 Bad Request`.
- Due date in the past: return `400 Bad Request`.
- Missing user record: reject the operation rather than creating an orphan
  task.
- Concurrent updates: use an `updated_at` or version field if optimistic
  locking is required by the product.
- Error responses contain safe messages and never include stack traces or
  database credentials.

## Example endpoint contract

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/tasks` | Create a task for the authenticated user |
| `GET` | `/api/tasks` | List the authenticated user's tasks |
| `GET` | `/api/tasks/{id}` | Read one owned task |
| `PUT` | `/api/tasks/{id}` | Replace/update one owned task |
| `DELETE` | `/api/tasks/{id}` | Delete one owned task |

This example demonstrates GenAI usage, prompt engineering, validation, and
critical review without expanding the Pokemon application with unrelated task
management features.
