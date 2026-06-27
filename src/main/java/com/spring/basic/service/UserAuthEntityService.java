package com.spring.basic.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.spring.basic.entity.UserAuthEntity;
import com.spring.basic.repo.UserAuthEntityRepo;

@Service
public class UserAuthEntityService implements UserDetailsService{

    @Autowired
    private UserAuthEntityRepo authEntityRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authEntityRepo.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("username not found exception"));
        

       
    }

    public void save(UserAuthEntity entity){
        authEntityRepo.save(entity);
    }

}
