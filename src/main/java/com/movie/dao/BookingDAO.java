/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.dao;

import com.movie.model.Booking;
import com.movie.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for bookings table with relational JOIN queries.
 * 
 * Architectural Connection in System:
 * - Layer: Data Access Layer (com.movie.dao)
 * - Upstream: Called exclusively by BookingService.
 * - Downstream: Executes prepared SQL statements against MySQL via DatabaseConnection.
 * - Queries: Manages insertion, status updates (PAID, CANCELLED), seat collision queries,
 *   customer history queries, date-range filters, and total revenue aggregation.
 */
public class BookingDAO {

    public boolean insert(Booking b) {
        String sql = "INSERT INTO bookings (booking_code, user_id, showtime_id, seats, total_amount, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getBookingCode());
            ps.setInt(2, b.getUserId());
            ps.setInt(3, b.getShowtimeId());
            ps.setString(4, b.getSeats());
            ps.setDouble(5, b.getTotalAmount());
            ps.setString(6, b.getStatus());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) b.setBookingId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO.insert] " + e.getMessage());
        }
        return false;
    }

    public boolean updateStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookingDAO.updateStatus] " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int bookingId) {
        String sql = "DELETE FROM bookings WHERE booking_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookingDAO.delete] " + e.getMessage());
            return false;
        }
    }

    public Booking findById(int bookingId) {
        String sql = baseSelect() + " WHERE b.booking_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO.findById] " + e.getMessage());
        }
        return null;
    }

    /** All seats already booked for a showtime (any PENDING or PAID status). */
    public List<String> getBookedSeats(int showtimeId) {
        List<String> seats = new ArrayList<>();
        String sql = "SELECT seats FROM bookings WHERE showtime_id = ? " +
                     "AND status IN ('PENDING','PAID')";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String row = rs.getString("seats");
                    if (row != null && !row.isEmpty()) {
                        for (String s : row.split(",")) {
                            seats.add(s.trim().toUpperCase());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO.getBookedSeats] " + e.getMessage());
        }
        return seats;
    }

    /** Full list of bookings (admin reports). */
    public List<Booking> getAll() {
        List<Booking> list = new ArrayList<>();
        String sql = baseSelect() + " ORDER BY b.booked_at DESC";
        try (Statement st = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookingDAO.getAll] " + e.getMessage());
        }
        return list;
    }

    /** Customer bookings by user_id. */
    public List<Booking> getByUserId(int userId) {
        List<Booking> list = new ArrayList<>();
        String sql = baseSelect() + " WHERE b.user_id = ? ORDER BY b.booked_at DESC";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO.getByUserId] " + e.getMessage());
        }
        return list;
    }

    /** Bookings within a date range (inclusive), on booked_at date. */
    public List<Booking> getByDateRange(String from, String to) {
        List<Booking> list = new ArrayList<>();
        String sql = baseSelect() + " WHERE DATE(b.booked_at) BETWEEN ? AND ? ORDER BY b.booked_at DESC";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setString(1, from);
            ps.setString(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO.getByDateRange] " + e.getMessage());
        }
        return list;
    }

    /** Bookings grouped per movie title. */
    public List<Booking> getByMovie() {
        List<Booking> list = new ArrayList<>();
        String sql = baseSelect() + " ORDER BY m.title";
        try (Statement st = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookingDAO.getByMovie] " + e.getMessage());
        }
        return list;
    }

    /** Total sales amount within date range (only PAID bookings). */
    public double getTotalSales(String from, String to) {
        String sql = "SELECT COALESCE(SUM(total_amount),0) AS total FROM bookings " +
                     "WHERE status='PAID' AND DATE(booked_at) BETWEEN ? AND ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setString(1, from);
            ps.setString(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO.getTotalSales] " + e.getMessage());
        }
        return 0.0;
    }

    /** Total all-time sales for PAID bookings. */
    public double getAllTimeSales() {
        String sql = "SELECT COALESCE(SUM(total_amount),0) AS total FROM bookings WHERE status='PAID'";
        try (Statement st = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            System.err.println("[BookingDAO.getAllTimeSales] " + e.getMessage());
        }
        return 0.0;
    }

    private String baseSelect() {
        return "SELECT b.booking_id, b.booking_code, b.user_id, b.showtime_id, b.seats, " +
               "b.total_amount, b.status, b.booked_at, " +
               "u.full_name AS customer_name, " +
               "m.title AS movie_title, " +
               "h.hall_name, st.show_date, st.show_time " +
               "FROM bookings b " +
               "JOIN users u       ON b.user_id = u.user_id " +
               "JOIN showtimes st  ON b.showtime_id = st.showtime_id " +
               "JOIN movies m      ON st.movie_id = m.movie_id " +
               "JOIN halls h       ON st.hall_id = h.hall_id ";
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking(
                rs.getInt("booking_id"),
                rs.getString("booking_code"),
                rs.getInt("user_id"),
                rs.getInt("showtime_id"),
                rs.getString("seats"),
                rs.getDouble("total_amount"),
                rs.getString("status"),
                rs.getString("booked_at")
        );
        b.setCustomerName(rs.getString("customer_name"));
        b.setMovieTitle(rs.getString("movie_title"));
        b.setHallName(rs.getString("hall_name"));
        b.setShowDate(rs.getString("show_date"));
        b.setShowTime(rs.getString("show_time"));
        return b;
    }
}
