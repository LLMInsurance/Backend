package com.example.LLMInsurance_Backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "app_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UUID")
    private UUID uuid;

    @Column(name = "userId", unique = true, nullable = false)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;

    @Column(name = "birthDate", nullable = false)
    private LocalDate birthDate;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "isMarried", nullable = false)
    private boolean isMarried;

    @Column(name = "job", nullable = false)
    private String job;

    @Column(name = "diseases")
    private String[] diseases;

    @Column(name = "subscriptions")
    private String[] subscriptions;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "modifiedAt", nullable = false)
    private Date modifiedAt;

    @Column(name = "isLogin", nullable = false) @ColumnDefault("false")
    private boolean isLogin;

    @Column(name = "isDeleted", nullable = false) @ColumnDefault("false")
    private boolean isDeleted;
}
