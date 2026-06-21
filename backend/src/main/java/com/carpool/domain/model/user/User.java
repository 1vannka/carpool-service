package com.carpool.domain.model.user;

public class User {
    private Long id;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String telegramAlias;
    private String vkAlias;
    private String role;

    public User() {
    }

    public User(Long id, String email, String passwordHash, String firstName, String lastName, String telegramAlias, String vkAlias, String role) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.telegramAlias = telegramAlias;
        this.vkAlias = vkAlias;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTelegramAlias() {
        return telegramAlias;
    }

    public void setTelegramAlias(String telegramAlias) {
        this.telegramAlias = telegramAlias;
    }

    public String getVkAlias() {
        return vkAlias;
    }

    public void setVkAlias(String vkAlias) {
        this.vkAlias = vkAlias;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
