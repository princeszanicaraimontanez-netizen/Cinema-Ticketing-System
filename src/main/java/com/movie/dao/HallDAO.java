/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.dao;

import com.movie.model.Hall;
import com.movie.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for halls table.
 * 
 * Architectural Connection in System:
 * - Layer: Data Access Layer (com.movie.dao)
 * - Upstream: Called exclusively by HallService.
 * - Downstream: Executes prepared SQL statements against MySQL via DatabaseConnection.
 * - Object Mapping: Converts SQL ResultSets into Hall entity objects.
 */
public class HallDAO {
    public boolean insert(Hall h) {
        String sql = "INSERT INTO halls (hall_name, total_seats) VALUES (?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setString(1, h.getHallName());
            ps.setInt(2, h.getTotalSeats());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[HallDAO.insert] " + e.getMessage());
            return false;
        }
    }

    public boolean update(Hall h) {
        String sql = "UPDATE halls SET hall_name=?, total_seats=? WHERE hall_id=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setString(1, h.getHallName());
            ps.setInt(2, h.getTotalSeats());
            ps.setInt(3, h.getHallId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[HallDAO.update] " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int hallId) {
        String sql = "DELETE FROM halls WHERE hall_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, hallId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[HallDAO.delete] " + e.getMessage());
            return false;
        }
    }

    public Hall findById(int hallId) {
        String sql = "SELECT * FROM halls WHERE hall_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, hallId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[HallDAO.findById] " + e.getMessage());
        }
        return null;
    }

    public List<Hall> getAll() {
        List<Hall> list = new ArrayList<>();
        String sql = "SELECT * FROM halls ORDER BY hall_name";
        try (Statement st = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[HallDAO.getAll] " + e.getMessage());
        }
        return list;
    }

    private Hall mapRow(ResultSet rs) throws SQLException {
        return new Hall(
                rs.getInt("hall_id"),
                rs.getString("hall_name"),
                rs.getInt("total_seats")
        );
    }  
}
