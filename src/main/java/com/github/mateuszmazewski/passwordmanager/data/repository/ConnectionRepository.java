package com.github.mateuszmazewski.passwordmanager.data.repository;

import com.github.mateuszmazewski.passwordmanager.data.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Integer> {

    // Implemented dynamically based on method name by Spring Data JPA
    List<Connection> findByUserId(Integer userId);

    @Query("select c from Connection c " +
            "where c.userId = :searchUserId " +
            "and lower(c.ip) like lower(concat('%', :searchIp, '%'))")
    Connection findByUserIdAndIp(@Param("searchUserId") Integer searchUserId,
                                 @Param("searchIp") String searchIp);
}
