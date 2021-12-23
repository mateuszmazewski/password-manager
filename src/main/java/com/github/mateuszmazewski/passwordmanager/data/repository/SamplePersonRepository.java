package com.github.mateuszmazewski.passwordmanager.data.repository;

import com.github.mateuszmazewski.passwordmanager.data.entity.SamplePerson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SamplePersonRepository extends JpaRepository<SamplePerson, Integer> {

}