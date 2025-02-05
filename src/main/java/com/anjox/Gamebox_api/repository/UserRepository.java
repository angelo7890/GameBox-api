package com.anjox.Gamebox_api.repository;

import com.anjox.Gamebox_api.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);
    UserEntity findByUsername(String username);
    UserEntity findById(long id);
    Page<UserEntity> findAll(Pageable pageable);

}
