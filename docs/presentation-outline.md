# Interview Presentation Outline

## 1. User stories

Show the paginated catalog, detailed Pokemon view, local synchronization, and protected editing flow.

## 2. Architecture

Explain the monorepo, the MVC layers (`controller`, `service`, `repository`, `integration`, and `exception`), the Redis cache, live PokeAPI proxy, local PostgreSQL data, and the React/Vite client. Show the architecture diagram from the README.

## 3. Live demonstration

1. Open the live catalog and navigate through multiple PokeAPI pages.
2. Open Pikachu details and show statistics/evolution data.
3. Register or sign in with the demo account.
4. Synchronize the currently viewed Pokemon from PokeAPI into PostgreSQL.
5. Edit the Portuguese name, region, tags, and notes.
6. Refresh the detail view and show the local values persisted.
7. Show an invalid payload and a missing ID returning controlled errors.

## 4. Testing

Explain service tests, repository tests, mocked PokeAPI integration tests, controller tests, and frontend component tests. Mention that TDD was used by observing failing tests before implementation.

## 5. Generative AI discussion

Present the task-management API prompt, a representative generated sample, how the code was tested, and corrections made for validation, authentication, error handling, and separation of concerns.

## 6. Trade-offs

The solution keeps the core flow small, uses Redis to reduce repeated PokeAPI calls, uses PostgreSQL for local persistence, and avoids a heavier frontend framework because Vite is sufficient for this SPA.
