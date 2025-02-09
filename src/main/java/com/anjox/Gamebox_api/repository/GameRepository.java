package com.anjox.Gamebox_api.repository;

import com.anjox.Gamebox_api.entity.GameEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GameRepository extends JpaRepository<GameEntity, Long> {

    Optional<GameEntity> findById(Long id);
    void deleteAllByUserId(Long userId);
    Page<GameEntity> findByUserId(Long userId, Pageable pageable);
    boolean existsByUserIdAndTitle(Long userId, String title);
    Page<GameEntity> findAll(Pageable pageable);
}
