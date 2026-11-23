/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.service;

import com.movie.dao.ShowtimeDAO;
import com.movie.model.Showtime;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Service managing showtimes scheduling with date and time format validations.
 * 
 * Architectural Connection in System:
 * - Layer: Service / Business Logic Layer (com.movie.service)
 * - Upstream: Invoked by AdminDashboardController (schedule management) and CustomerDashboardController (showtime queries).
 * - Downstream: Delegates SQL operations to ShowtimeDAO.
 * - Validation: Validates ISO 8601 date formats (YYYY-MM-DD) and 24-hour time patterns (HH:MM:SS).
 */
public class ShowtimeService {

    private final ShowtimeDAO showtimeDAO;
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^\\d{2}:\\d{2}(:\\d{2})?$");

    public ShowtimeService() {
        this.showtimeDAO = new ShowtimeDAO();
    }

    public ShowtimeService(ShowtimeDAO showtimeDAO) {
        this.showtimeDAO = showtimeDAO;
    }

    public boolean addShowtime(int movieId, int hallId, String showDate, String showTime) {
        validateShowtime(movieId, hallId, showDate, showTime);
        String formattedTime = normalizeTime(showTime);
        Showtime s = new Showtime(0, movieId, hallId, showDate.trim(), formattedTime);
        return showtimeDAO.insert(s);
    }

    public boolean updateShowtime(int showtimeId, int movieId, int hallId, String showDate, String showTime) {
        if (showtimeId <= 0) {
            throw new IllegalArgumentException("Invalid showtime ID.");
        }
        validateShowtime(movieId, hallId, showDate, showTime);
        String formattedTime = normalizeTime(showTime);
        Showtime s = new Showtime(showtimeId, movieId, hallId, showDate.trim(), formattedTime);
        return showtimeDAO.update(s);
    }

    public boolean deleteShowtime(int showtimeId) {
        if (showtimeId <= 0) {
            throw new IllegalArgumentException("Invalid showtime ID.");
        }
        return showtimeDAO.delete(showtimeId);
    }

    public Showtime getShowtimeById(int showtimeId) {
        return showtimeDAO.findById(showtimeId);
    }

    public List<Showtime> getAllShowtimes() {
        return showtimeDAO.getAll();
    }

    public List<Showtime> getByMovie(int movieId) {
        return showtimeDAO.getByMovie(movieId);
    }

    private void validateShowtime(int movieId, int hallId, String showDate, String showTime) {
        if (movieId <= 0) {
            throw new IllegalArgumentException("Please select a valid movie.");
        }
        if (hallId <= 0) {
            throw new IllegalArgumentException("Please select a valid cinema hall.");
        }
        if (showDate == null || !DATE_PATTERN.matcher(showDate.trim()).matches()) {
            throw new IllegalArgumentException("Show date must be in YYYY-MM-DD format (e.g. 2026-11-25).");
        }
        if (showTime == null || !TIME_PATTERN.matcher(showTime.trim()).matches()) {
            throw new IllegalArgumentException("Show time must be in HH:MM or HH:MM:SS format (e.g. 14:30:00).");
        }
    }

    private String normalizeTime(String time) {
        String t = time.trim();
        if (t.length() == 5) {
            return t + ":00";
        }
        return t;
    }
}
