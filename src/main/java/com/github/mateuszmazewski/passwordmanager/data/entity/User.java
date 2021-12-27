package com.github.mateuszmazewski.passwordmanager.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.mateuszmazewski.passwordmanager.data.AbstractEntity;
import com.github.mateuszmazewski.passwordmanager.data.Role;

import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
public class User extends AbstractEntity {

    public final static int MIN_PASSWORD_LENGTH = 8;
    @Column(unique = true)
    private String username;
    @JsonIgnore
    @NotBlank
    private String hashedPassword;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String hashedMasterPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedMasterPassword() {
        return hashedMasterPassword;
    }

    public void setHashedMasterPassword(String hashedMasterPassword) {
        this.hashedMasterPassword = hashedMasterPassword;
    }

}
