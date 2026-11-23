/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.model;

/**
 * Entity model representing a scheduled screening session of a movie in a specific hall.
 * 
 * Architectural Connection in System:
 * - Layer: Model / Domain Entity Layer (com.movie.model)
 * - Mapping: Maps to the `showtimes` table in MySQL, with JOIN fields for movieTitle, hallName, and price.
 * - Transport: Passed from ShowtimeDAO -> ShowtimeService -> Controllers -> Dashboard Views & SeatMapFrame.
 */
public class Showtime {
   private int showtimeId;
    private int movieId;
    private int hallId;
    private String showDate;   // "yyyy-MM-dd"
    private String showTime;   // "HH:mm:ss"

    // Convenience fields for display (filled by DAO joins)
    private String movieTitle;
    private String hallName;
    private double moviePrice;
    private int hallTotalSeats;

    public Showtime() {}

    public Showtime(int showtimeId, int movieId, int hallId, String showDate, String showTime) {
        this.showtimeId = showtimeId;
        this.movieId = movieId;
        this.hallId = hallId;
        this.showDate = showDate;
        this.showTime = showTime;
    }

    public int getShowtimeId() { return showtimeId; }
    public void setShowtimeId(int showtimeId) { this.showtimeId = showtimeId; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public int getHallId() { return hallId; }
    public void setHallId(int hallId) { this.hallId = hallId; }

    public String getShowDate() { return showDate; }
    public void setShowDate(String showDate) { this.showDate = showDate; }

    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getHallName() { return hallName; }
    public void setHallName(String hallName) { this.hallName = hallName; }

    public double getMoviePrice() { return moviePrice; }
    public void setMoviePrice(double moviePrice) { this.moviePrice = moviePrice; }

    public int getHallTotalSeats() { return hallTotalSeats; }
    public void setHallTotalSeats(int hallTotalSeats) { this.hallTotalSeats = hallTotalSeats; }

    @Override
    public String toString() {
        String timeStr = (showTime != null && showTime.length() >= 5) ? showTime.substring(0, 5) : showTime;
        return hallName + " | " + showDate + " " + timeStr + " (₱" + String.format("%.2f", moviePrice) + ")";
    }
}
