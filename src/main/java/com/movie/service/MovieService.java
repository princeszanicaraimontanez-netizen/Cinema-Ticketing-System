/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.service;

import com.movie.dao.MovieDAO;
import com.movie.model.Movie;

import java.util.List;

/**
 * Service managing movies catalog and business validation rules.
 * 
 * Architectural Connection in System:
 * - Layer: Service / Business Logic Layer (com.movie.service)
 * - Upstream: Invoked by AdminDashboardController (CRUD) and CustomerDashboardController (browsing & search).
 * - Downstream: Delegates SQL operations to MovieDAO.
 * - Validation: Enforces constraints on duration, ratings, prices, and title completeness.
 */
public class MovieService {

    private final MovieDAO movieDAO;

    public MovieService() {
        this.movieDAO = new MovieDAO();
    }

    public MovieService(MovieDAO movieDAO) {
        this.movieDAO = movieDAO;
    }

    public boolean addMovie(String title, String genre, int duration, String rating,
                            String synopsis, double price, String status) {
        validateMovie(title, genre, duration, rating, price, status);
        Movie m = new Movie(0, title.trim(), genre.trim(), duration, rating.trim(),
                synopsis != null ? synopsis.trim() : "", price, status.trim());
        return movieDAO.insert(m);
    }

    public boolean updateMovie(int movieId, String title, String genre, int duration,
                               String rating, String synopsis, double price, String status) {
        if (movieId <= 0) {
            throw new IllegalArgumentException("Invalid movie ID for update.");
        }
        validateMovie(title, genre, duration, rating, price, status);
        Movie m = new Movie(movieId, title.trim(), genre.trim(), duration, rating.trim(),
                synopsis != null ? synopsis.trim() : "", price, status.trim());
        return movieDAO.update(m);
    }

    public boolean deleteMovie(int movieId) {
        if (movieId <= 0) {
            throw new IllegalArgumentException("Invalid movie ID for deletion.");
        }
        return movieDAO.delete(movieId);
    }

    public Movie getMovieById(int movieId) {
        return movieDAO.findById(movieId);
    }

    public List<Movie> getAllMovies() {
        return movieDAO.getAll();
    }

    public List<Movie> getNowShowingMovies() {
        return movieDAO.getNowShowing();
    }

    public List<Movie> searchMovies(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return movieDAO.getAll();
        }
        return movieDAO.search(keyword.trim());
    }

    private void validateMovie(String title, String genre, int duration, String rating,
                               double price, String status) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Movie title is required.");
        }
        if (genre == null || genre.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre is required.");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be a positive integer in minutes.");
        }
        if (rating == null || rating.trim().isEmpty()) {
            throw new IllegalArgumentException("Rating is required (e.g. G, PG, PG-13, R).");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Ticket price cannot be negative.");
        }
        if (!"NOW_SHOWING".equalsIgnoreCase(status) && !"COMING_SOON".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("Status must be NOW_SHOWING or COMING_SOON.");
        }
    }
}
