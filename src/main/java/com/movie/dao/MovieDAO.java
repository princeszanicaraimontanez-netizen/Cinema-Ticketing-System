/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.dao;

import com.movie.model.Movie;
import com.movie.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for movies table.
 * 
 * Architectural Connection in System:
 * - Layer: Data Access Layer (com.movie.dao)
 * - Upstream: Called exclusively by MovieService.
 * - Downstream: Executes prepared SQL statements against MySQL via DatabaseConnection.
 * - Object Mapping: Converts SQL ResultSets into Movie entity objects.
 */
public class MovieDAO {
   public boolean insert(Movie m) {
        String sql = "INSERT INTO movies (title, genre, duration, rating, synopsis, price, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getGenre());
            ps.setInt(3, m.getDuration());
            ps.setString(4, m.getRating());
            ps.setString(5, m.getSynopsis());
            ps.setDouble(6, m.getPrice());
            ps.setString(7, m.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MovieDAO.insert] " + e.getMessage());
            return false;
        }
    }

    public boolean update(Movie m) {
        String sql = "UPDATE movies SET title=?, genre=?, duration=?, rating=?, " +
                     "synopsis=?, price=?, status=? WHERE movie_id=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getGenre());
            ps.setInt(3, m.getDuration());
            ps.setString(4, m.getRating());
            ps.setString(5, m.getSynopsis());
            ps.setDouble(6, m.getPrice());
            ps.setString(7, m.getStatus());
            ps.setInt(8, m.getMovieId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MovieDAO.update] " + e.getMessage());
            return false;
        }
    }

    public boolean updatePrice(int movieId, double newPrice) {
        String sql = "UPDATE movies SET price = ? WHERE movie_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setInt(2, movieId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MovieDAO.updatePrice] " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int movieId) {
        String sql = "DELETE FROM movies WHERE movie_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, movieId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MovieDAO.delete] " + e.getMessage());
            return false;
        }
    }

    public Movie findById(int movieId) {
        String sql = "SELECT * FROM movies WHERE movie_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, movieId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[MovieDAO.findById] " + e.getMessage());
        }
        return null;
    }

    public List<Movie> getAll() {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM movies ORDER BY title";
        try (Statement st = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[MovieDAO.getAll] " + e.getMessage());
        }
        return list;
    }

    public List<Movie> getNowShowing() {
        return getByStatus("NOW_SHOWING");
    }

    /** Search NOW_SHOWING movies by title or genre keyword. */
    public List<Movie> search(String keyword) {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM movies WHERE status='NOW_SHOWING' " +
                     "AND (title LIKE ? OR genre LIKE ?) ORDER BY title";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[MovieDAO.search] " + e.getMessage());
        }
        return list;
    }

    private List<Movie> getByStatus(String status) {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM movies WHERE status = ? ORDER BY title";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[MovieDAO.getByStatus] " + e.getMessage());
        }
        return list;
    }

    private Movie mapRow(ResultSet rs) throws SQLException {
        return new Movie(
                rs.getInt("movie_id"),
                rs.getString("title"),
                rs.getString("genre"),
                rs.getInt("duration"),
                rs.getString("rating"),
                rs.getString("synopsis"),
                rs.getDouble("price"),
                rs.getString("status")
        );
    }  
}
