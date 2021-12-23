package com.github.mateuszmazewski.passwordmanager.data.repository;

import com.github.mateuszmazewski.passwordmanager.data.entity.VaultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VaultEntityRepository extends JpaRepository<VaultEntity, Integer> {

    @Query("select ve from VaultEntity ve " +
            "where lower(ve.name) like lower(concat('%', :searchName, '%'))")
    List<VaultEntity> search(@Param("searchName") String searchName);

}