# Form Login vs HTTP Basic Authentication

Spring Security supports multiple authentication mechanisms. This project demonstrates both.

---

## HTTP Basic Authentication

Credentials are sent in every request via the `Authorization` header:

```
Authorization: Basic <base64(username:password)>
```

### Configuration

```java
.httpBasic(Customizer.withDefaults())
```

### Characteristics

| Property | Value |
|---|---|
| Login UI | Browser native popup |
| Session | None by default (stateless) |
| Credentials sent | Every request |
| Good for | REST APIs, Postman testing |

### Session policy with HTTP Basic

To make it fully stateless (no `JSESSIONID` created):

```java
.sessionManagement(session ->
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
.httpBasic(Customizer.withDefaults())
```

With `STATELESS`, each request is authenticated independently. No session is stored server-side.

---

## Form Login

Spring renders a login page (or you can provide your own). Credentials are submitted once as a form POST. On success, a session is created and a `JSESSIONID` cookie is set in the browser.

### Configuration

```java
.formLogin(form -> form.defaultSuccessUrl("/orders", true))
```

### Characteristics

| Property | Value |
|---|---|
| Login UI | HTML form (Spring's default or custom) |
| Session | Created after successful login (`JSESSIONID` cookie) |
| Credentials sent | Once at login |
| Good for | Browser-based apps |

---

## Session Fixation Protection

**What it is:** An attack where an adversary sets a known session ID on the victim before login. After the victim logs in, the attacker reuses that ID.

**Spring's defence:** After successful login, Spring **invalidates the old session and creates a new one** with a new `JSESSIONID`. This is enabled by default.

**Practical effect:** The `JSESSIONID` you see before login and after login will be different. This is correct and expected — not a bug.

```
Before login:  JSESSIONID = abc123   (pre-auth session)
POST /login    → authentication succeeds
               → old session invalidated
               → new session created
After login:   JSESSIONID = xyz789   (post-auth session, use this)
```

The browser handles this transparently — it receives the new cookie in the login response and sends it on all subsequent requests.

---

## Why did `/orders` show a different JSESSIONID?

When testing in the browser:

1. First request to `/orders` (unauthenticated) → Spring creates an anonymous session
2. Redirect to `/login`
3. Login form submitted → authentication succeeds → **session fixation protection fires** → new session created
4. Redirect to `/orders` with new `JSESSIONID`

The two different IDs are the pre-login anonymous session and the post-login authenticated session. The browser automatically uses the new one after login.

If `/orders` kept asking for login after successful authentication, the root cause was that the form login redirect was going to `/` (undefined), causing an error that broke the session state. Fixed by:

```java
.formLogin(form -> form.defaultSuccessUrl("/orders", true))
```

The `true` parameter forces the redirect to `/orders` even if the user originally requested a different URL.

---

## Switching Between the Two

Only one should be active at a time in this project. In `SecurityConfig.java`:

```java
// For HTTP Basic (REST / Postman testing):
// .formLogin(...)          ← comment out
.httpBasic(Customizer.withDefaults())
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

// For Form Login (browser):
.formLogin(form -> form.defaultSuccessUrl("/orders", true))
// .httpBasic(...)          ← comment out
// .sessionManagement(...)  ← comment out (use default IF_REQUIRED)
```
