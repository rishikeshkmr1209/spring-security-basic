# Authentication Evolution — From In-Memory to Database

This project evolved through three authentication approaches. Each stage is preserved in git history.

---

## Stage 1 — Spring Boot Default

Out of the box, Spring Boot auto-generates a random password printed in the console:

```
Using generated security password: 3a1b2c3d-...
```

One user, one random password, regenerated every restart. Useful only for initial smoke testing.

---

## Stage 2 — Custom In-Memory Users

Override the default with `InMemoryUserDetailsManager`. Users are hardcoded in config.

```java
@Bean
@Primary
public InMemoryUserDetailsManager inMemoryUserDetailsManager(
        SecurityProperties properties,
        ObjectProvider<PasswordEncoder> passwordEncoder) {

    UserDetails user1 = User.withUsername("rishi")
        .password(new BCryptPasswordEncoder().encode("1234"))
        .roles("USER")
        .build();

    return new InMemoryUserDetailsManager(user1);
}
```

**Limitation:** Users are lost on restart. No way to register new users at runtime.

> The `{noop}` prefix is an alternative to a `PasswordEncoder` bean — it tells Spring to match the password as plain text. Once you define a `PasswordEncoder` bean, remove `{noop}` and pass encoded passwords.

---

## Stage 3 — Database-Backed Authentication (Current)

Users are persisted in H2 via JPA. A `/auth/register` endpoint allows runtime registration.

### How it works

```
POST /auth/register
    → UserAuthEntityController encodes password with BCrypt
    → saves UserAuthEntity to H2 via UserAuthEntityService

GET /orders (with credentials)
    → DaoAuthenticationProvider calls UserAuthEntityService.loadUserByUsername()
    → fetches UserAuthEntity from H2
    → BCryptPasswordEncoder.matches(rawPassword, storedHash)
    → if OK → grants access
```

### Key components

| Component | Role |
|---|---|
| `UserAuthEntity` | JPA entity that also implements `UserDetails` |
| `UserAuthEntityRepo` | Spring Data repo with `findByUsername` |
| `UserAuthEntityService` | Implements `UserDetailsService` |
| `DaoAuthenticationProvider` | Wires service + encoder together |
| `BCryptPasswordEncoder` | Hashes passwords at registration, verifies at login |

---

## Why `UserAuthEntity` implements `UserDetails` directly?

It avoids a wrapper/adapter class. Spring Security calls `loadUserByUsername()` and expects a `UserDetails` back — returning the entity directly satisfies that contract.

Trade-off: the entity is coupled to Spring Security. For larger projects, a separate `UserDetailsAdapter` class is cleaner.

---

## BCrypt Password Encoding

BCrypt is a one-way hashing algorithm designed to be slow (work factor). You never store or compare plain text passwords.

```java
// At registration — encode once
authEntity.setPassword(encoder.encode(rawPassword));

// At login — Spring calls this internally
encoder.matches(rawPassword, storedHash); // true/false
```

The `BCryptPasswordEncoder` bean must be defined in a config class:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

> **Warning:** Do NOT define `PasswordEncoder` inside a class that also implements `UserDetailsService`. Spring Security's auto-configuration will detect both and may try to wire them together incorrectly, causing startup warnings or failures.
