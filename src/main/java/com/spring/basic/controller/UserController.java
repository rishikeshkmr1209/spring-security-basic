package com.spring.basic.controller;

import com.spring.basic.configprops.UserConfigurations;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController("/user")
public class UserController {

    
    @Autowired
    private UserConfigurations userConfiguration;

    

    @GetMapping
    public String getMethodName(@RequestParam String param) {
        return  userConfiguration.getName();
    }
    

}
