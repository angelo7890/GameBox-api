package com.anjox.Gamebox_api.repository;

import com.anjox.Gamebox_api.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<GameEntity, Long> {

    Optional<GameEntity> findById(Long id);
    void deleteAllByUserId(Long userId);
    List<GameEntity> findByUserId(Long userId);
    boolean existsByUserIdAndTitle(Long userId, String title);

}
