package com.github.leo51645.assetflow.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Setter
    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Setter
    @Column(nullable = false, length = 30)
    private String firstname;

    @Setter
    @Column(nullable = false, length = 50)
    private String lastname;

    @Setter
    @Column(nullable = false)
    private LocalDate birthday;

    @Setter
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
