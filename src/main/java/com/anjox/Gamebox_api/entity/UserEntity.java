package com.anjox.Gamebox_api.entity;


import com.anjox.Gamebox_api.enums.UserEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "Users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String username;

    private String email;

    private String password;

    private UserEnum type;

}
