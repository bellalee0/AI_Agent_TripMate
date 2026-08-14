package com.example.tripmate.common.entity;

import com.example.tripmate.common.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    private String password;

    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    public User(String email, String name, String nickname, String password, String profileImgUrl, UserRole role) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.profileImgUrl = profileImgUrl;
        this.role = role;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
