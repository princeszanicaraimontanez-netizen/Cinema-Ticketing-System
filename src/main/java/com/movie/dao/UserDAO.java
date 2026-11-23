/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.dao;

import com.movie.model.User;
import com.movie.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for users table.
 * 
 * Architectural Connection in System:
 * - Layer: Data Access Layer (com.movie.dao)
 * - Upstream: Called exclusively by UserService.
 * - Downstream: Executes prepared SQL statements against MySQL via DatabaseConnection.
 * - Object Mapping: Converts SQL ResultSets into User entity objects.
 */
public class UserDAO {

    /** Insert a new user. Returns true if successful. */
    public boolean insert(User user) {
        String sql = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.insert] " + e.getMessage());
        }
        return false;
    }

    /** Find user by username. Returns null if not found. */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.findByUsername] " + e.getMessage());
        }
        return null;
    }

    /** Find user by primary key ID. */
    public User findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.findById] " + e.getMessage());
        }
        return null;
    }

    /** Get all users ordered by user_id. */
    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";
        try (Statement st = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.getAll] " + e.getMessage());
        }
        return list;
    }

    /** Update full name and role of a user. */
    public boolean update(User user) {
        String sql = "UPDATE users SET full_name = ?, role = ? WHERE user_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getRole());
            ps.setInt(3, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO.update] " + e.getMessage());
            return false;
        }
    }

    /** Update role of a user. */
    public boolean updateRole(int userId, String newRole) {
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setString(1, newRole);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO.updateRole] " + e.getMessage());
            return false;
        }
    }

    /** Update password of a user (SHA-256 hash). */
    public boolean updatePassword(int userId, String newHashedPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setString(1, newHashedPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO.updatePassword] " + e.getMessage());
            return false;
        }
    }

    /** Delete user by id. */
    public boolean delete(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO.delete] " + e.getMessage());
            return false;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getString("role")
        );
    }
}
