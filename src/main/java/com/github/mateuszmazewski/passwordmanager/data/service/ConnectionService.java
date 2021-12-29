package com.github.mateuszmazewski.passwordmanager.data.service;

import com.github.mateuszmazewski.passwordmanager.data.entity.Connection;
import com.github.mateuszmazewski.passwordmanager.data.repository.ConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConnectionService {

    private final ConnectionRepository repository;

    public ConnectionService(@Autowired ConnectionRepository repository) {
        this.repository = repository;
    }

    public Connection findByUserIdAndIp(Integer userId, String ip) {
        return repository.findByUserIdAndIp(userId, ip);
    }

    public List<Connection> findByUserId(Integer userId) {
        return repository.findByUserId(userId);
    }

    public List<Connection> findAll() {
        return repository.findAll();
    }

    public Optional<Connection> get(Integer id) {
        return repository.findById(id);
    }

    public Connection update(Connection entity) {
        return repository.save(entity);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public int count() {
        return (int) repository.count();
    }
}
