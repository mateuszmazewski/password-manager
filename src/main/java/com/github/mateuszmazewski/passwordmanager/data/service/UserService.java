package com.github.mateuszmazewski.passwordmanager.data.service;

import com.github.mateuszmazewski.passwordmanager.data.entity.User;

import java.util.Optional;

import com.github.mateuszmazewski.passwordmanager.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(@Autowired UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> get(Integer id) {
        return repository.findById(id);
    }

    public User update(User entity) {
        return repository.save(entity);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public boolean userExists(String username) {
        return repository.findByUsername(username) != null;
    }

    public User findByUsername(String username) {
        return repository.findByUsername(username);
    }

}
