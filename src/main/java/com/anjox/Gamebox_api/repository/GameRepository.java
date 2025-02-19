package com.anjox.Gamebox_api.repository;

import com.anjox.Gamebox_api.entity.GameEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<GameEntity, Long> {

    GameEntity findByid(Long id);

    Page<GameEntity> findByUserId(Long userId, Pageable pageable);

    List<GameEntity> findByUserId(Long userId);

    boolean existsByUserIdAndTitle(Long userId, String title);

    Page<GameEntity> findAll(Pageable pageable);

    Page<GameEntity> findByGenreAndUserId(String genre, Long userId, Pageable pageable);

}
