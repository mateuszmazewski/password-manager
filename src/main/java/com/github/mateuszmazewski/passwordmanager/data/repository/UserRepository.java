package com.github.mateuszmazewski.passwordmanager.data.repository;

import com.github.mateuszmazewski.passwordmanager.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);
}