package com.anjox.Gamebox_api.repository;

import com.anjox.Gamebox_api.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<GameEntity, Long> {
}
