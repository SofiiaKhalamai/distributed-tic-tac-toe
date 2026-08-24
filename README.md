# Distributed Tic Tac Toe

Three Spring Boot microservices playing Tic Tac Toe against each other automatically.

```
game-common            - shared enums, ApiError and web error-handling boilerplate (not a Spring Boot app)
game-engine-service   (port 8081) - board state, move validation, win/draw detection
game-session-service  (port 8082) - session lifecycle, automated move generation, calls the engine
game-ui               (port 8083) - static browser UI, drives the session service and renders the game
```

## API

### game-engine-service (`:8081`)

| Method | Path | Purpose |
|---|---|---|
| POST | `/games/{gameId}/move` | Submit a move `{ "symbol": "X", "row": 0, "col": 0 }` - creates the game on the first move for a given `gameId` |
| GET | `/games/{gameId}` | Get current board + status |

A move to an already-occupied cell, an out-of-turn move, or a move on a finished game returns
`422 Unprocessable Entity` with a JSON error body. An unknown `gameId` returns `404`.

### game-session-service (`:8082`)

| Method | Path | Purpose |
|---|---|---|
| POST | `/sessions` | Create a session (the engine's game is created lazily on the first simulated move) |
| POST | `/sessions/{sessionId}/simulate` | Run the automated game to completion |
| GET | `/sessions/{sessionId}` | Get session status, board, and move history |

If the engine is unreachable, the session service returns `502 Bad Gateway`.

### Swagger / OpenAPI

Both backend services expose interactive API docs via [springdoc-openapi](https://springdoc.org/),
generated automatically from the controllers:

| Service | Swagger UI |
|---|---|
| game-engine-service | http://localhost:8081/swagger-ui/index.html |
| game-session-service | http://localhost:8082/swagger-ui/index.html |

## How it works

1. UI calls `POST /sessions` on the session service - a session row is created; the game in the
   engine isn't initialized yet (there's no explicit "create game" endpoint - the engine creates a
   game lazily on its first move, `gameId == sessionId`).
2. UI calls `POST /sessions/{sessionId}/simulate` - the session service:
    - fetches the engine's current board via `GET /games/{gameId}` (empty/404 the first time, since
      the game doesn't exist yet - the session then assumes an empty board),
    - repeatedly picks a random empty cell for whichever player's turn it is
      (`RandomMoveGeneratorService`), submits it via `POST /games/{gameId}/move`, and records the move
      and updated status in the session,
      until the engine reports `X_WON`, `O_WON`, or `DRAW`.
3. The full move history and final board are returned to the UI, which replays the moves with a short
   delay between each to visualize the game as it "was played."
4. `GET /sessions/{sessionId}` can be polled at any time to retrieve session state and move history.

## Architecture

There are four Maven modules: `game-common` (a plain library, not a Spring Boot app) plus the three
services. Both backend services follow the same layered package structure:

```
com.khalamai.tictactoe.<service>/
├── domain/
│   ├── entity/     plain JPA entities (@Getter/@Setter, no business logic - see below)
│   ├── enums/      service-specific enums only (shared ones live in game-common)
│   └── exception/  flat, each extends RuntimeException directly
├── dto/            request/response records, sparse board representation (occupied cells only)
├── repository/     plain Spring Data JpaRepository interfaces, no wrapper classes
├── service/
│   ├── <Feature>Service.java        interface
│   ├── impl/<Feature>ServiceImpl.java
│   └── mapper/                      MapStruct interfaces (entity → response DTO)
└── web/
    ├── <Feature>Controller.java     returns DTOs directly, @ResponseStatus for non-200
    └── error/                       GlobalExceptionHandler extends game-common's AbstractExceptionHandler
```

## Database

Each service owns a separate in-memory H2 database - no shared schema, no cross-service joins. State
resets on every restart (`spring.jpa.hibernate.ddl-auto=create-drop` against a fresh in-memory
instance each time), which is fine given the assignment's in-memory requirement.

### game-engine-service (`jdbc:h2:mem:gameengine`)

```
games                          game_cells
├── game_id        PK          ├── game_id     FK -> games.game_id
├── status                     ├── board_row
├── next_turn                  ├── board_col
└── version        optimistic  └── symbol
    locking (@Version)
```

### game-session-service (`jdbc:h2:mem:gamesession`)

```
game_sessions                  session_moves
├── session_id     PK          ├── id                   PK, auto-increment
├── status                     ├── session_session_id   FK -> game_sessions.session_id
├── game_status                ├── sequence
└── (no board column -         ├── symbol
    see Architecture above)    ├── board_row
                                └── board_col
```

Both H2 consoles are reachable while a service is running: `http://localhost:8081/h2-console` and
`http://localhost:8082/h2-console` (JDBC URL as shown above, user `sa`, empty password).

## Requirements

- Java 21
- Maven 3.9+ (or use your IDE's bundled Maven)

## Build

From the repo root (multi-module Maven build):

```bash
mvn clean verify
```

This compiles all four modules and runs their test suites.

## Run

`game-engine-service` and `game-session-service` both depend on `game-common`. Since it isn't a Spring
Boot app, running a service with `-pl` (module-only) won't build it as part of that command - install
it to your local Maven repo once first:

```bash
mvn -pl game-common -am install -DskipTests
```

Then start each service in its own terminal, in this order (engine -> session -> UI):

```bash
mvn -pl game-engine-service spring-boot:run
mvn -pl game-session-service spring-boot:run
mvn -pl game-ui spring-boot:run
```

(Re-run the `install` step above whenever you change something under `game-common`.)

Then open **http://localhost:8083** and click **Start Simulation**.

### Or via Docker Compose

```bash
docker compose up --build
```

builds and runs all three services together (`game-engine` on `:8081`, `game-session` on `:8082`,
`game-ui` on `:8083`), wired to talk to each other over the Docker network - `game-session`'s
`game-engine.base-url` is overridden to `http://game-engine:8081` via the `GAME_ENGINE_BASE_URL`
environment variable in `docker-compose.yml` (Spring's relaxed property binding maps it). Open
**http://localhost:8083** the same way. `docker compose down` tears it down.

## Testing

```bash
mvn clean test
```

runs the full suite across all modules:

| Test | What it checks |
|---|---|
| `GameServiceIntegrationTest` | Full game flow in `game-engine-service` against the real embedded H2 database |
| `SessionServiceIntegrationTest` | Full session flow in `game-session-service` against the real H2 database (`GameEngineClient` mocked) |
| `GameServiceConcurrencyIntegrationTest` | Real concurrent threads racing on the same game, proving the optimistic-locking retry resolves the conflict instead of losing/duplicating a write |
| `CrossServiceIntegrationTest` | Boots a real `game-engine-service` instance and drives `createSession()` -> `simulate()` against it over actual HTTP - validates the two services genuinely communicate, not just that each works in isolation |
| `GameEngineClientTest` (`@RestClientTest`) | The HTTP client's request/response contract and error handling, via `MockRestServiceServer` |
| `*ControllerTest` (`@WebMvcTest`) | Each controller's web layer in isolation |
| `*ServiceTest` (Mockito) | Business logic of each service, isolated from Spring/the database |
| `game-common`'s tests | Shared enums and the error-handling base class, plain JUnit, no Spring context |