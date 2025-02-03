package com.anjox.Gamebox_api.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Games")
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long userId;

    private String title;

    private String description;

    private String genre;

    private BigDecimal price;

    private String imageUrl;
}
