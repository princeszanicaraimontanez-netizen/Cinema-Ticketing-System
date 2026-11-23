/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.model;

/**
 * Entity model representing a cinema ticket reservation and transaction.
 * 
 * Architectural Connection in System:
 * - Layer: Model / Domain Entity Layer (com.movie.model)
 * - Mapping: Maps to the `bookings` table in MySQL, with JOIN fields for customerName, movieTitle, and hallName.
 * - Transport: Flowed through BookingDAO -> BookingService -> Customer/Admin Controllers -> Views & ReceiptFrame.
 */
public class Booking {
    private int bookingId;
    private String bookingCode;
    private int userId;
    private int showtimeId;
    private String seats;        // e.g. "A1,A2,A3"
    private double totalAmount;
    private String status;       // PENDING, PAID, CANCELLED
    private String bookedAt;

    // Convenience fields for display (filled by DAO joins)
    private String customerName;
    private String movieTitle;
    private String hallName;
    private String showDate;
    private String showTime;

    public Booking() {}

    public Booking(int bookingId, String bookingCode, int userId, int showtimeId,
                   String seats, double totalAmount, String status, String bookedAt) {
        this.bookingId = bookingId;
        this.bookingCode = bookingCode;
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.seats = seats;
        this.totalAmount = totalAmount;
        this.status = status;
        this.bookedAt = bookedAt;
    }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getShowtimeId() { return showtimeId; }
    public void setShowtimeId(int showtimeId) { this.showtimeId = showtimeId; }

    public String getSeats() { return seats; }
    public void setSeats(String seats) { this.seats = seats; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBookedAt() { return bookedAt; }
    public void setBookedAt(String bookedAt) { this.bookedAt = bookedAt; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getHallName() { return hallName; }
    public void setHallName(String hallName) { this.hallName = hallName; }

    public String getShowDate() { return showDate; }
    public void setShowDate(String showDate) { this.showDate = showDate; }

    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; } 
}
