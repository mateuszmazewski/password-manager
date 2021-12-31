package com.github.mateuszmazewski.passwordmanager.data.repository;

import com.github.mateuszmazewski.passwordmanager.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Implemented dynamically based on method name by Spring Data JPA
    User findByUsername(String username);

    User findByEmailIgnoreCase(String email);
}