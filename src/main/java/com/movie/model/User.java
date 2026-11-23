/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.model;

/**
 * Entity model representing an authenticated user account (Admin or Customer).
 * 
 * Architectural Connection in System:
 * - Layer: Model / Domain Entity Layer (com.movie.model)
 * - Mapping: Maps 1:1 to the `users` table in MySQL.
 * - Transport: Passed across DAO, Service, Controller, and View layers to hold session state.
 */
public class User {
      private int userId;
    private String username;
    private String password;   // hashed
    private String fullName;
    private String role;       // "CUSTOMER" or "ADMIN"

    public User() {}

    public User(int userId, String username, String password, String fullName, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }

    @Override
    public String toString() {
        return fullName != null ? fullName : username;
    }
    
}
