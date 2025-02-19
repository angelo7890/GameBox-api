package com.anjox.Gamebox_api.repository;

import com.anjox.Gamebox_api.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByemail(String email);

    UserEntity findByUsername(String username);

    UserEntity findByid(Long id);

    Page<UserEntity> findAll(Pageable pageable);

    UserEntity findByactivationCode(String activationCode);

    boolean existsByUsername(String username);

}
