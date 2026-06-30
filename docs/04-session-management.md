# Session Management

## How Sessions Work in Spring Security

After successful authentication, Spring Security stores the `Authentication` object in a `SecurityContext`, which is tied to the HTTP session. On subsequent requests, `SecurityContextPersistenceFilter` reloads the `SecurityContext` from the session so the user doesn't need to re-authenticate.

```
Request → SecurityContextPersistenceFilter
           → loads SecurityContext from session
           → places in SecurityContextHolder
              → controller runs
           → saves SecurityContext back to session
```

---

## Session Creation Policies

Controlled via `SessionCreationPolicy` in `SecurityConfig`:

| Policy | Behaviour | Use case |
|---|---|---|
| `IF_REQUIRED` | Creates session only when needed | **Default.** Form login, browser apps |
| `STATELESS` | Never creates or uses a session | REST APIs, HTTP Basic |
| `ALWAYS` | Always creates a session | Rarely needed |
| `NEVER` | Never creates, but uses if exists | Edge cases |

```java
.sessionManagement(session ->
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

---

## Spring Session JDBC

By default, sessions are stored in server memory — lost on restart. **Spring Session JDBC** persists sessions to the database so they survive restarts and can be shared across multiple server instances.

### Dependency

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-jdbc</artifactId>
</dependency>
```

### Configuration

```properties
spring.session.store-type=jdbc
spring.session.jdbc.initialize-schema=always
server.servlet.session.timeout=5m
```

`initialize-schema=always` tells Spring to auto-create the session tables on startup.

### Tables created

| Table | Purpose |
|---|---|
| `SPRING_SESSION` | One row per active session (ID, creation time, last access, expiry) |
| `SPRING_SESSION_ATTRIBUTES` | Session attributes as key-value pairs |

Both tables are visible in the H2 console at `http://localhost:8080/h2-console`.

### Session timeout

`server.servlet.session.timeout=5m` — sessions expire after 5 minutes of inactivity. After expiry, the next request redirects to the login page.

---

## H2 Console and Sessions

The H2 console is a separate servlet embedded in the app. It uses **iframes** internally, which requires:

```java
.headers(headers -> headers.frameOptions(frame -> frame.disable()))
```

Without this, the browser blocks the iframe and the console appears blank or broken.

### H2 console credentials

The H2 console authenticates against the **database**, not Spring Security.

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:testdb` |
| Username | `sa` |
| Password | *(empty)* |

Your app users (`sunny`, `rishi`) have no meaning here — they exist in the `user_auth` table inside the DB, not as DB users.

---

## Spring Boot Version and H2 Console

The H2 console is auto-configured by Spring Boot via `H2ConsoleAutoConfiguration`. In **Spring Boot 3.x**, this works out of the box with:

```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

In **Spring Boot 4.x** (Spring Framework 7.x), the auto-configuration was refactored and the console servlet may not register automatically — requiring manual `ServletRegistrationBean` setup. This is a compatibility issue specific to the bleeding-edge 4.x line.

> **Lesson:** For learning projects, prefer Spring Boot 3.x (current stable LTS line) over 4.x. The 3.x ecosystem is fully documented and widely supported.
