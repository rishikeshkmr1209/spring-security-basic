package com.spring.basic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.basic.entity.UserAuthEntity;
import com.spring.basic.service.UserAuthEntityService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class UserAuthEntityController {

    @Autowired
    private UserAuthEntityService authEntityService;

    @Autowired
    private PasswordEncoder encoder;


    
    
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserAuthEntity authEntity){
        authEntity.setPassword(encoder.encode(authEntity.getPassword()));
        try {
            authEntityService.save(authEntity);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Username already exists");
        }
        return ResponseEntity.status(200).body("Successfully persisted");


    }

}
