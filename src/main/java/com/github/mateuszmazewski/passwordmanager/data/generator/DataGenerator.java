package com.github.mateuszmazewski.passwordmanager.data.generator;

import com.github.mateuszmazewski.passwordmanager.data.Role;
import com.github.mateuszmazewski.passwordmanager.data.entity.User;
import com.github.mateuszmazewski.passwordmanager.data.repository.UserRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder,
                                      UserRepository userRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }

            logger.info("Generating demo data");
            logger.info("... generating 2 User entities...");
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@example.com");
            user.setHashedPassword(passwordEncoder.encode("user"));
            user.setHashedMasterPassword(passwordEncoder.encode("user"));
            user.setActive(true);
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            admin.setHashedMasterPassword(passwordEncoder.encode("admin"));
            admin.setActive(true);
            admin.setRoles(Stream.of(Role.USER, Role.ADMIN).collect(Collectors.toSet()));
            userRepository.save(admin);

            logger.info("Generated demo data");
        };
    }

}