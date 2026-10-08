# Pokemon Explorer

Pokemon Explorer is a full-stack Pokemon catalog built for a Java technical interview exercise. It combines a Spring Boot REST API with a React and Vite frontend, integrates with PokeAPI, caches upstream responses in Redis, and stores synchronized Pokemon data plus proprietary local fields in PostgreSQL.

## Features

- Public paginated Pokemon catalog and detail view.
- Live catalog data proxied from PokeAPI with Redis caching.
- PokeAPI integration for Pokemon, species, descriptions, statistics, and evolution chains.
- Local PostgreSQL replication with proprietary fields: Portuguese name, region, internal tags, and notes.
- JWT registration and login.
- Protected synchronization and local editing in PostgreSQL.
- Unit, integration, controller, and frontend component tests.

## Requirements

- Docker Desktop with Compose (PostgreSQL and Redis).
- Java 21 or newer for local backend development.
- Maven 3.9 or newer for local backend development.
- Node.js 22 or newer for local frontend development.

## Run with Docker

```bash
cp .env.example .env
docker compose up --build
```

Open the frontend at `http://localhost:5173` and the API at `http://localhost:8081`.

The API uses host port `8081` by default to avoid conflicts with other local services. Set `VITE_API_URL` in `.env` if you need another port.

Demo credentials:

- Email: `demo@example.com`
- Password: `password`

The public catalog reads Pokémon from PokeAPI through the backend and caches upstream responses in Redis for 10 minutes. PostgreSQL is used for local fields and explicitly synchronized Pokémon. The demo user can synchronize the demo Pokémon from the catalog or synchronize any currently viewed Pokémon from its detail screen before editing local fields.

## Run locally

Start PostgreSQL and Redis:

```bash
docker compose up postgres redis
```

Start the backend:

```bash
cd backend
mvn test
mvn spring-boot:run
```

Start the frontend in a second terminal:

```bash
cd frontend
npm install
npm run dev
```

## Test and build

```bash
cd backend && mvn test
cd frontend && npm test -- --run
cd frontend && npm run build
```

## Architecture

The backend uses MVC with layered separation: controllers handle HTTP, services contain business workflows, repositories handle PostgreSQL, the cache integration handles Redis, and the external integration handles PokeAPI calls. Controllers do not call JPA, Redis, or PokeAPI directly.

```text
                         +------------------+
                         |   PokeAPI        |
                         +--------^---------+
                                  |
                                  | cache miss
+-------------+         +---------+----------+         +------------------+
| React/Vite  +-------->+ Spring Boot API    +-------->+ Redis Cache      |
+-------------+         | Controller/Service |         | 10 min TTL       |
                         +---------+----------+         +------------------+
                                  |
                                  v
                         +------------------+
                         | PostgreSQL       |
                         | local fields and |
                         | explicit sync    |
                         +------------------+
```

Public catalog flow: the frontend calls Spring Boot; the API checks Redis, calls PokeAPI on a cache miss, caches the upstream response, and returns the stable pagination response. PostgreSQL remains the source for local fields and explicit synchronization.

The frontend is a small React/Vite single-page application. It uses a typed API client, an authentication context, and focused catalog, detail, authentication, and editing components.

## Generative AI usage

AI-generated code was treated as a draft. Each suggestion was reviewed against the user stories, validated with tests, checked for authentication and validation gaps, and changed when it introduced weak boundaries, unstable pagination responses, or unsafe error handling. The presentation will include the prompt, representative output, validation steps, corrections, and edge cases.

The separate task-management API exercise is documented in [Generative AI task API exercise](docs/generative-ai-task-api.md). It contains the prompt, representative Java/Spring Boot output, validation approach, corrections, security decisions, and edge cases requested by the interview brief.

## Technical documentation

- [API reference](docs/api.md)
- [Generative AI task API exercise](docs/generative-ai-task-api.md)
- [Presentation outline](docs/presentation-outline.md)
