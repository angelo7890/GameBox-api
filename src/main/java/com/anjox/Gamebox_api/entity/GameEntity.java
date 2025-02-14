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

    private String imageId;

    public GameEntity() {
    }

    public GameEntity(Long userId, String title, String description, String genre, BigDecimal price, String imageUrl, String imageId) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.price = price;
        this.imageUrl = imageUrl;
        this.imageId = imageId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
