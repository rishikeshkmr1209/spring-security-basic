# Troubleshooting — Issues Encountered and Fixes

A log of real problems hit during this project and how they were resolved.

---

## 1. H2 Console returns 404

**Symptom:** `GET /h2-console` returns `404 Not Found` — whitelabel error page, never reaches the console UI.

**Root cause:** Running Spring Boot 4.1.0 — the H2 console auto-configuration servlet was not registering automatically in this version.

**Fix:** Downgraded to Spring Boot 3.4.1 where H2 console auto-configuration works reliably out of the box.

**Also required in SecurityConfig:**
```java
.requestMatchers("/h2-console/**").permitAll()
.headers(headers -> headers.frameOptions(frame -> frame.disable()))
```
Without `frameOptions().disable()`, the console UI loads but appears blank (browser blocks iframes by default via `X-Frame-Options: DENY`).

---

## 2. Spring Boot 4.x compilation errors after downgrade to 3.x

**Symptom:** Three compile errors after changing parent version from `4.1.0` to `3.4.1`.

### Error 1: `package org.jspecify.annotations does not exist`

`@Nullable` from `org.jspecify` was introduced in Spring Boot 4.x. Not available in 3.x.

**Fix:** Remove the import and the `@Nullable` annotation from `getPassword()`.

### Error 2: `DaoAuthenticationProvider(UserDetailsService)` constructor not found

The single-arg constructor was added in Spring Boot 4.x / Spring Security 7.x.

**Fix:** Use the no-arg constructor with setters (correct for Spring Security 6.x):
```java
DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
provider.setUserDetailsService(userDetailsService);
provider.setPasswordEncoder(passwordEncoder);
```

### Error 3: `spring-boot-starter-security-test` missing version

This artifact doesn't exist in Spring Boot 3.x's BOM.

**Fix:** Replace with the standard Spring Security test dependency:
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 3. Circular reference on startup

**Symptom:**
```
The dependencies of some of the beans in the application context form a cycle:
securityConfig → securityConfig
```

**Root cause:** `SecurityConfig` had a constructor that injected `AuthenticationProvider`, but the same class also defined the `authenticationProvider` `@Bean`. Spring saw a self-referential dependency.

**Fix:** Remove the constructor injection. The `@Bean` method already receives the provider as a method parameter — Spring resolves that correctly without a cycle.

```java
// REMOVE this
private final AuthenticationProvider authenticationProvider;
SecurityConfig(AuthenticationProvider authenticationProvider) { ... }

// KEEP only this — Spring injects via method parameter, not constructor
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authProvider) { ... }
```

---

## 4. Wrong credentials at H2 console

**Symptom:** Trying to log into the H2 console with app user credentials (`sunny` / `1234`) — login fails or is not recognized.

**Explanation:** The H2 console login form authenticates against the **database server** (JDBC), not against Spring Security. They are completely separate systems.

**Correct credentials:**
| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:testdb` |
| Username | `sa` |
| Password | *(leave blank)* |

---

## 5. Different JSESSIONID before and after login

**Symptom:** After form login, hitting `/orders` in the browser showed a different `JSESSIONID` cookie than what was set during login.

**Explanation:** This is **session fixation protection** — a security feature, not a bug. Spring Security deliberately invalidates the pre-login session and creates a new one after authentication to prevent session fixation attacks. The browser receives the new `JSESSIONID` in the login redirect response and uses it automatically.

**Fix:** None needed for the session ID change itself. However, if `/orders` kept redirecting back to login, the real issue was that the default success redirect went to `/` (undefined), corrupting the flow. Fixed by specifying a valid redirect:

```java
.formLogin(form -> form.defaultSuccessUrl("/orders", true))
```

---

## 6. Spring Session JDBC — wrong property names

**Wrong properties (typos):**
```properties
spring.session.store.jdbc=true        # wrong key
spring.server.session.timeout=5m      # wrong prefix
```

**Correct properties:**
```properties
spring.session.store-type=jdbc
server.servlet.session.timeout=5m
```
