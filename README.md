# Spring Security Basic — Learning Project

A hands-on Spring Boot project exploring Spring Security concepts, progressing from in-memory authentication to database-backed authentication with session persistence.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.4.1 |
| Security | Spring Security 6.x (HTTP Basic Auth) |
| Persistence | Spring Data JPA + Hibernate |
| Database | H2 (in-memory) |
| Session Store | Spring Session JDBC |
| Password Hashing | BCrypt |
| Java | 21 |

---

## Project Structure

```
src/main/java/com/spring/basic/
├── BasicApplication.java
├── config/
│   ├── SecurityConfig.java                    # Security filter chain + auth provider
│   └── UserDetailsServiceAutoConfiguration.java  # BCryptPasswordEncoder bean
├── controller/
│   ├── OrdersController.java                  # CRUD endpoints for orders
│   └── UserAuthEntityController.java          # User registration endpoint
├── entity/
│   └── UserAuthEntity.java                    # JPA entity + UserDetails implementation
├── repo/
│   └── UserAuthEntityRepo.java                # Spring Data JPA repository
└── service/
    └── UserAuthEntityService.java             # UserDetailsService implementation
```

---

## API Endpoints

### Auth (Public)

| Method | Path | Description |
|---|---|---|
| `POST` | `/auth/register` | Register a new user. Accepts JSON body, BCrypt-hashes password, saves to DB. Returns `409` if username already exists. |

**Request body:**
```json
{
  "username": "sunny",
  "password": "1234",
  "role": "ROLE_USER"
}
```

### Orders (Requires `ROLE_USER`)

| Method | Path | Description |
|---|---|---|
| `GET` | `/orders` | Get all orders |
| `GET` | `/orders/{id}` | Get order by ID |
| `POST` | `/orders` | Create a new order |
| `PUT` | `/orders/{id}` | Update an order |
| `DELETE` | `/orders/{id}` | Delete an order |
| `GET` | `/orders/hello` | Health check / greeting |

> Orders are stored in-memory (not persisted to DB). They reset on app restart.

**Order JSON shape:**
```json
{
  "customerName": "Alice",
  "status": "PENDING"
}
```

---

## Security Configuration

- **Auth mechanism:** HTTP Basic Authentication
- **Password encoding:** BCrypt
- **Auth provider:** `DaoAuthenticationProvider` backed by `UserAuthEntityService` (loads users from H2)
- **CSRF:** Disabled (REST API)
- **Frame options:** Disabled (required for H2 console iframe)

### Route Rules

| Path | Rule |
|---|---|
| `/auth/register` | Public |
| `/h2-console/**` | Public |
| `/orders/**` | Requires `ROLE_USER` |
| All others | Authenticated |

---

## Database

H2 in-memory database. Schema is created on startup and dropped on shutdown (`create-drop`).

### `user_auth` Table

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGINT` | Auto-generated primary key |
| `username` | `VARCHAR` | Unique, not null |
| `password` | `VARCHAR` | BCrypt-hashed, not null |
| `role` | `VARCHAR` | e.g. `ROLE_USER`, not null |

### Session Tables (Spring Session JDBC)

Spring Session auto-creates `SPRING_SESSION` and `SPRING_SESSION_ATTRIBUTES` tables to persist HTTP sessions across restarts.

---

## H2 Console

Accessible at `http://localhost:8080/h2-console` while the app is running.

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:testdb` |
| Username | `sa` |
| Password | *(leave empty)* |

> These are the **database** credentials, not your registered app user credentials.

---

## Configuration (`application.properties`)

```properties
# H2 in-memory database
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Spring Session (JDBC)
spring.session.store-type=jdbc
spring.session.jdbc.initialize-schema=always
server.servlet.session.timeout=5m
```

---

## Running the App

```bash
./mvnw spring-boot:run
```

Then register a user and call the orders API:

```bash
# Register
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"sunny","password":"1234","role":"ROLE_USER"}'

# Call protected endpoint (HTTP Basic)
curl -u sunny:1234 http://localhost:8080/orders
```

---

## Learning Progression

This project evolved through the following stages (visible in git history):

1. **Default security** — Spring Boot auto-generated password, single in-memory user
2. **Custom in-memory users** — `InMemoryUserDetailsManager` with hardcoded users (now commented out in `UserDetailsServiceAutoConfiguration.java`)
3. **BCrypt encoding** — Passwords hashed with `BCryptPasswordEncoder`
4. **Database persistence** — Users stored in H2 via JPA; `UserAuthEntityService` implements `UserDetailsService`
5. **Session persistence** — Spring Session JDBC stores sessions in DB with 5-minute timeout
