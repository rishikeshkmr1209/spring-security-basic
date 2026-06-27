package com.spring.basic.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.basic.entity.UserAuthEntity;

@Repository
public interface UserAuthEntityRepo  extends JpaRepository<UserAuthEntity,Long>{


    Optional<UserAuthEntity> findByUsername(String username);
    


}
