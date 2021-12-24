package com.github.mateuszmazewski.passwordmanager.data.service;

import com.github.mateuszmazewski.passwordmanager.data.entity.VaultEntity;
import com.github.mateuszmazewski.passwordmanager.data.repository.VaultEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VaultEntityService {

    private final VaultEntityRepository repository;

    public VaultEntityService(@Autowired VaultEntityRepository repository) {
        this.repository = repository;
    }

    public List<VaultEntity> find(Integer userId, String filterName) {
        return repository.search(userId, filterName);
    }

    public List<VaultEntity> findAll() {
        return repository.findAll();
    }

    public Optional<VaultEntity> get(Integer id) {
        return repository.findById(id);
    }

    public VaultEntity update(VaultEntity entity) {
        return repository.save(entity);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public int count() {
        return (int) repository.count();
    }
}
