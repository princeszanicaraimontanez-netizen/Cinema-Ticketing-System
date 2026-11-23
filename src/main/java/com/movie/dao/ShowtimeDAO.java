/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.dao;

import com.movie.model.Showtime;
import com.movie.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for showtimes table with JOIN views for movie and hall details.
 * 
 * Architectural Connection in System:
 * - Layer: Data Access Layer (com.movie.dao)
 * - Upstream: Called exclusively by ShowtimeService.
 * - Downstream: Executes prepared SQL statements against MySQL via DatabaseConnection.
 * - Object Mapping: Converts SQL ResultSets (including JOIN fields) into Showtime entity objects.
 */
public class ShowtimeDAO {
  public boolean insert(Showtime s) {
        String sql = "INSERT INTO showtimes (movie_id, hall_id, show_date, show_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, s.getMovieId());
            ps.setInt(2, s.getHallId());
            ps.setString(3, s.getShowDate());
            ps.setString(4, s.getShowTime());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ShowtimeDAO.insert] " + e.getMessage());
            return false;
        }
    }

    public boolean update(Showtime s) {
        String sql = "UPDATE showtimes SET movie_id=?, hall_id=?, show_date=?, show_time=? " +
                     "WHERE showtime_id=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, s.getMovieId());
            ps.setInt(2, s.getHallId());
            ps.setString(3, s.getShowDate());
            ps.setString(4, s.getShowTime());
            ps.setInt(5, s.getShowtimeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ShowtimeDAO.update] " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int showtimeId) {
        String sql = "DELETE FROM showtimes WHERE showtime_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ShowtimeDAO.delete] " + e.getMessage());
            return false;
        }
    }

    public Showtime findById(int showtimeId) {
        String sql = baseSelect() + " WHERE st.showtime_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[ShowtimeDAO.findById] " + e.getMessage());
        }
        return null;
    }

    /** All showtimes with movie + hall info for admin listing. */
    public List<Showtime> getAll() {
        List<Showtime> list = new ArrayList<>();
        String sql = baseSelect() + " ORDER BY st.show_date, st.show_time";
        try (Statement st = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ShowtimeDAO.getAll] " + e.getMessage());
        }
        return list;
    }

    /** Showtimes for a specific movie (used by customers). */
    public List<Showtime> getByMovie(int movieId) {
        List<Showtime> list = new ArrayList<>();
        String sql = baseSelect() + " WHERE st.movie_id = ? ORDER BY st.show_date, st.show_time";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, movieId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ShowtimeDAO.getByMovie] " + e.getMessage());
        }
        return list;
    }

    private String baseSelect() {
        return "SELECT st.showtime_id, st.movie_id, st.hall_id, st.show_date, st.show_time, " +
               "m.title AS movie_title, m.price AS movie_price, " +
               "h.hall_name, h.total_seats AS hall_total_seats " +
               "FROM showtimes st " +
               "JOIN movies m ON st.movie_id = m.movie_id " +
               "JOIN halls  h ON st.hall_id  = h.hall_id ";
    }

    private Showtime mapRow(ResultSet rs) throws SQLException {
        Showtime s = new Showtime(
                rs.getInt("showtime_id"),
                rs.getInt("movie_id"),
                rs.getInt("hall_id"),
                rs.getString("show_date"),
                rs.getString("show_time")
        );
        s.setMovieTitle(rs.getString("movie_title"));
        s.setMoviePrice(rs.getDouble("movie_price"));
        s.setHallName(rs.getString("hall_name"));
        s.setHallTotalSeats(rs.getInt("hall_total_seats"));
        return s;
    }
}
