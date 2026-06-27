# CSRF Learning Notes

## What is CSRF?
CSRF (Cross-Site Request Forgery) is a security vulnerability where an attacker tricks a logged-in user into making an unwanted request to a website.

## Why it matters in Spring Security
Spring Security protects state-changing requests such as POST, PUT, and DELETE using CSRF protection by default.

## What we observed
When we tried to send a POST request to the orders endpoint, Spring Security blocked it with a 401/forbidden-style response because the request was not passing the expected CSRF protection behavior.

## How we fixed it
We disabled CSRF for the local learning/demo setup by configuring Spring Security to allow the orders endpoint requests during development.

Example approach:
```java
http
    .csrf(csrf -> csrf.disable())
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/orders/**").permitAll()
        .anyRequest().authenticated());
```

## Important note
Disabling CSRF is fine for local testing and simple demos, but in real applications you should usually keep CSRF protection enabled and use proper tokens.

## Key takeaway
- CSRF protects state-changing requests.
- Spring Security enables it by default.
- For local testing, you may temporarily disable it.
- For production, prefer a safer CSRF token-based approach.
