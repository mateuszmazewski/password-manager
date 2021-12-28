package com.github.mateuszmazewski.passwordmanager.data.repository;

import com.github.mateuszmazewski.passwordmanager.data.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Integer> {

    // Implemented dynamically based on method name by Spring Data JPA
    LoginAttempt findByIp(String ip);
}
