package com.github.mateuszmazewski.passwordmanager.data.service;

import com.github.mateuszmazewski.passwordmanager.data.entity.LoginAttempt;
import com.github.mateuszmazewski.passwordmanager.data.repository.LoginAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginAttemptService {

    private final LoginAttemptRepository repository;
    public final static int MAX_ATTEMPTS = 10;

    public LoginAttemptService(@Autowired LoginAttemptRepository repository) {
        this.repository = repository;
    }

    public void loginSucceeded(String ip) {
        LoginAttempt attempt = repository.findByIp(ip);
        if (attempt != null) {
            repository.deleteById(attempt.getId());
        }
    }

    public void loginFailed(String ip) {
        LoginAttempt attempt = repository.findByIp(ip);
        if (attempt == null) {
            attempt = new LoginAttempt();
            attempt.setIp(ip);
            attempt.setFailedAttempts(1);
            attempt.setBlockedUntil(null);
        } else {
            int failedAttempts = attempt.getFailedAttempts() + 1;
            attempt.setFailedAttempts(failedAttempts);

            if (failedAttempts >= MAX_ATTEMPTS && attempt.getBlockedUntil() == null) {
                attempt.setBlockedUntil(LocalDateTime.now().plusHours(3));
            }
        }

        repository.save(attempt);
    }

    public boolean isBlocked(String ip) {
        LoginAttempt attempt = repository.findByIp(ip);
        if (attempt == null || attempt.getBlockedUntil() == null) {
            return false;
        } else if (LocalDateTime.now().isAfter(attempt.getBlockedUntil())) {
            repository.deleteById(attempt.getId());
            return false;
        }

        return LocalDateTime.now().isBefore(attempt.getBlockedUntil());
    }

    public LoginAttempt findByIp(String ip) {
        return repository.findByIp(ip);
    }
}