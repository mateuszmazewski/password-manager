package com.github.mateuszmazewski.passwordmanager.data.service;

import com.github.mateuszmazewski.passwordmanager.data.entity.Connection;
import com.github.mateuszmazewski.passwordmanager.data.entity.LoginAttempt;
import com.github.mateuszmazewski.passwordmanager.data.entity.User;
import com.github.mateuszmazewski.passwordmanager.data.repository.ConnectionRepository;
import com.github.mateuszmazewski.passwordmanager.data.repository.LoginAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginAttemptService {

    private final LoginAttemptRepository loginAttemptRepository;
    private final ConnectionRepository connectionRepository;
    public final static int MAX_ATTEMPTS = 10;

    public LoginAttemptService(@Autowired LoginAttemptRepository loginAttemptRepository,
                               @Autowired ConnectionRepository connectionRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
        this.connectionRepository = connectionRepository;
    }

    public void loginSucceeded(String ip, User authenticatedUser) {
        LoginAttempt attempt = loginAttemptRepository.findByIp(ip);
        if (attempt != null) {
            loginAttemptRepository.deleteById(attempt.getId());
        }

        if (authenticatedUser != null) {
            Connection connection = connectionRepository.findByUserIdAndIp(authenticatedUser.getId(), ip);
            if (connection == null) {
                Connection newConnection = new Connection();
                newConnection.setUserId(authenticatedUser.getId());
                newConnection.setIp(ip);
                newConnection.setLastConnectionDate(LocalDateTime.now());
                connectionRepository.save(newConnection);
            } else {
                connection.setLastConnectionDate(LocalDateTime.now());
                connectionRepository.save(connection);
            }
        }
    }

    public void loginFailed(String ip) {
        LoginAttempt attempt = loginAttemptRepository.findByIp(ip);
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

        loginAttemptRepository.save(attempt);
    }

    public boolean isBlocked(String ip) {
        LoginAttempt attempt = loginAttemptRepository.findByIp(ip);
        if (attempt == null || attempt.getBlockedUntil() == null) {
            return false;
        } else if (LocalDateTime.now().isAfter(attempt.getBlockedUntil())) {
            loginAttemptRepository.deleteById(attempt.getId());
            return false;
        }

        return LocalDateTime.now().isBefore(attempt.getBlockedUntil());
    }

    public LoginAttempt findByIp(String ip) {
        return loginAttemptRepository.findByIp(ip);
    }
}