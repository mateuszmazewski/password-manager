package com.github.mateuszmazewski.passwordmanager.data.generator;

import com.github.mateuszmazewski.passwordmanager.data.repository.UserRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder,
                                      UserRepository userRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
/*
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                logger.info("App is ready");
                return;
            }
            logger.info("Generating demo data");
            logger.info("... generating 2 User entities...");
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@example.com");
            user.setHashedPassword(passwordEncoder.encode("user"));
            user.setHashedMasterPassword(passwordEncoder.encode("user"));
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            admin.setHashedMasterPassword(passwordEncoder.encode("admin"));
            admin.setRoles(Stream.of(Role.USER, Role.ADMIN).collect(Collectors.toSet()));
            userRepository.save(admin);

            logger.info("Generated demo data");
 */
            logger.info("App is ready");
            logger.info("Go to https://localhost:8443 or http://localhost:8080 (it will redirect you to https anyway)");
        };
    }

}