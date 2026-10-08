# API Reference

Base URL: `http://localhost:8081`

## Authentication

### Register

`POST /api/auth/register`

```json
{"email":"user@example.com","password":"secret123"}
```

### Login

`POST /api/auth/login`

```json
{"email":"demo@example.com","password":"password"}
```

The response contains a JWT. Send it as `Authorization: Bearer <token>` for protected operations.

## Pokemon

- `GET /api/pokemons?page=0&size=20` - public live PokeAPI catalog with Redis caching.
- `GET /api/pokemons/{id}` - public live detail, enriched with local fields when synchronized.
- `POST /api/pokemons/sync` - protected; body `{ "pokeApiIds": [1, 4, 25] }`; persists the external data locally.
- `PATCH /api/pokemons/{id}` - protected; updates `localizedName`, `region`, `internalTags`, and `notes` only after synchronization.
- `DELETE /api/pokemons/{id}` - protected; removes the synchronized local copy without affecting PokeAPI.
- `GET /api/health` - service health.

Pagination responses use the stable shape:

```json
{"content":[],"page":0,"size":20,"totalElements":0,"totalPages":0}
```

PokeAPI responses are cached in Redis for 10 minutes by default. Redis outages are treated as cache misses; PokeAPI failures return `502 Bad Gateway`.

Errors use:

```json
{"status":400,"message":"Payload validation failed","timestamp":"...","path":"/api/pokemons/1","fields":{}}
```

Attempting to update a Pokemon that has not been synchronized returns `409 Conflict`:

```json
{"status":409,"message":"Pokemon must be synchronized before local fields can be edited: 12","timestamp":"...","path":"/api/pokemons/12","fields":{}}
```
