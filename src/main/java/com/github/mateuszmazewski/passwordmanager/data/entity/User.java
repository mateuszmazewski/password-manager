package com.github.mateuszmazewski.passwordmanager.data.entity;

import com.github.mateuszmazewski.passwordmanager.data.AbstractEntity;
import com.github.mateuszmazewski.passwordmanager.data.Messages;
import com.github.mateuszmazewski.passwordmanager.data.Role;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
public class User extends AbstractEntity {

    public final static int MIN_PASSWORD_LENGTH = 8;
    @Column(unique = true)
    private String username;
    @Email(message = Messages.EMAIL_INVALID)
    @NotBlank
    private String email;
    @NotBlank
    private String hashedPassword;
    @NotBlank
    private String hashedMasterPassword;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getHashedMasterPassword() {
        return hashedMasterPassword;
    }

    public void setHashedMasterPassword(String hashedMasterPassword) {
        this.hashedMasterPassword = hashedMasterPassword;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
