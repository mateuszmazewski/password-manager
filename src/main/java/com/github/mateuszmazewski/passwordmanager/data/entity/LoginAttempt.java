package com.github.mateuszmazewski.passwordmanager.data.entity;

import com.github.mateuszmazewski.passwordmanager.data.AbstractEntity;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
public class LoginAttempt extends AbstractEntity {

    @NotBlank
    private String ip;
    private int failedAttempts;
    private LocalDateTime blockedUntil;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(LocalDateTime blockedUntil) {
        this.blockedUntil = blockedUntil;
    }
}
