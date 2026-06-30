package com.spring.basic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.basic.entity.UserAuthEntity;
import com.spring.basic.service.UserAuthEntityService;


@RestController
@RequestMapping("/auth")
public class UserAuthEntityController {

    @Autowired
    private UserAuthEntityService authEntityService;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping(value = "/register", produces = MediaType.TEXT_HTML_VALUE)
    public String registerForm() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Register</title>
                    <style>
                        body { font-family: sans-serif; display: flex; justify-content: center; padding-top: 80px; background: #f4f4f4; }
                        .card { background: white; padding: 32px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); width: 320px; }
                        h2 { margin-top: 0; }
                        label { display: block; margin-top: 12px; font-size: 14px; color: #555; }
                        input, select { width: 100%; padding: 8px; margin-top: 4px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
                        button { margin-top: 20px; width: 100%; padding: 10px; background: #4a90e2; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 15px; }
                        button:hover { background: #357abd; }
                    </style>
                </head>
                <body>
                <div class="card">
                    <h2>Register</h2>
                    <form method="post" action="/auth/register">
                        <label>Username</label>
                        <input type="text" name="username" required />
                        <label>Password</label>
                        <input type="password" name="password" required />
                        <label>Role</label>
                        <select name="role">
                            <option value="ROLE_USER">USER</option>
                            <option value="ROLE_ADMIN">ADMIN</option>
                        </select>
                        <button type="submit">Register</button>
                    </form>
                </div>
                </body>
                </html>
                """;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    public String registerFromForm(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String role) {
        UserAuthEntity authEntity = new UserAuthEntity();
        authEntity.setUsername(username);
        authEntity.setPassword(encoder.encode(password));
        authEntity.setRole(role);
        try {
            authEntityService.save(authEntity);
        } catch (DataIntegrityViolationException e) {
            return "<p style='color:red;font-family:sans-serif'>Username already exists. <a href='/auth/register'>Try again</a></p>";
        }
        return "<p style='color:green;font-family:sans-serif'>Registered successfully! <a href='/auth/register'>Register another</a></p>";
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@RequestBody UserAuthEntity authEntity) {
        authEntity.setPassword(encoder.encode(authEntity.getPassword()));
        try {
            authEntityService.save(authEntity);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Username already exists");
        }
        return ResponseEntity.ok("Successfully persisted");
    }

}
