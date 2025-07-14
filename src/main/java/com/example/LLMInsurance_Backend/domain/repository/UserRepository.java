package com.example.LLMInsurance_Backend.domain.repository;

import com.example.LLMInsurance_Backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
