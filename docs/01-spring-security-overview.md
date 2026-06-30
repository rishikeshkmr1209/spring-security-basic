# Spring Security — Core Concepts

## What is Spring Security?

Spring Security is a framework that handles **authentication** (who are you?) and **authorization** (what can you do?) for Spring applications. It works as a chain of servlet filters that intercept every HTTP request before it reaches your controller.

---

## Security Filter Chain

The central concept. Every request passes through a chain of filters in order. You configure this via a `SecurityFilterChain` bean.

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/**").permitAll()
            .anyRequest().authenticated())
        .formLogin(Customizer.withDefaults());
    return http.build();
}
```

Key filters in the chain (in order):
- `SecurityContextPersistenceFilter` — loads/saves SecurityContext from session
- `UsernamePasswordAuthenticationFilter` — handles form login POST
- `BasicAuthenticationFilter` — handles HTTP Basic header
- `ExceptionTranslationFilter` — converts security exceptions to HTTP responses
- `FilterSecurityInterceptor` — enforces authorization rules

---

## Authentication vs Authorization

| Concept | Question | Spring class |
|---|---|---|
| Authentication | Who are you? | `Authentication`, `AuthenticationProvider` |
| Authorization | What can you do? | `GrantedAuthority`, `@PreAuthorize` |

---

## AuthenticationProvider and DaoAuthenticationProvider

`AuthenticationProvider` is the interface that validates credentials.

`DaoAuthenticationProvider` is the standard implementation — it loads a user from a `UserDetailsService`, then checks the password using a `PasswordEncoder`.

```java
@Bean
public AuthenticationProvider authenticationProvider(
        UserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
}
```

> **Note:** In Spring Boot 4.x, `DaoAuthenticationProvider` gained a constructor that accepts `UserDetailsService` directly. In Spring Security 6.x (Spring Boot 3.x), use the no-arg constructor with setters.

---

## UserDetailsService

The interface Spring Security calls to load a user by username.

```java
@Service
public class UserAuthEntityService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("not found"));
    }
}
```

The returned `UserDetails` object provides:
- `getUsername()`
- `getPassword()` — must be encoded
- `getAuthorities()` — list of roles/permissions
- Account status flags (`isEnabled`, `isAccountNonLocked`, etc.)

---

## Circular Reference Pitfall

**Problem:** If `SecurityConfig` injects `AuthenticationProvider` via constructor AND also defines the `authenticationProvider` bean, Spring detects a circular dependency and fails to start.

```
securityConfig → authenticationProvider → securityConfig (CYCLE)
```

**Fix:** Don't inject the bean via constructor if the same class defines it. Use method parameter injection in the `@Bean` method instead — Spring resolves it correctly.

```java
// WRONG — causes circular reference
@Configuration
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider; // injected

    @Bean
    public AuthenticationProvider authenticationProvider(...) { ... } // also defined here
}

// CORRECT — method parameter only
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain chain(HttpSecurity http, AuthenticationProvider authProvider) { ... }

    @Bean
    public AuthenticationProvider authenticationProvider(...) { ... }
}
```
