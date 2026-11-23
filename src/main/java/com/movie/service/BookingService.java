/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.service;

import com.movie.dao.BookingDAO;
import com.movie.model.Booking;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Service enforcing business rules for seat booking, avoiding double booking,
 * code generation, and financial sales calculations.
 * 
 * Architectural Connection in System:
 * - Layer: Service / Business Logic Layer (com.movie.service)
 * - Upstream: Invoked by CustomerDashboardController (booking, payments, cancellations)
 *   and AdminDashboardController (auditing, filters, financial revenue reporting).
 * - Downstream: Coordinates transactional operations with BookingDAO.
 * - Concurrency & Business Rules: Enforces double-booking prevention across seat tokens
 *   and generates unique booking codes (BK-XXXXX).
 */
public class BookingService {

    private final BookingDAO bookingDAO;
    private final Random random = new Random();

    public BookingService() {
        this.bookingDAO = new BookingDAO();
    }

    public BookingService(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    /**
     * Creates a new booking with validation preventing seat conflicts.
     */
    public Booking createBooking(int userId, int showtimeId, String seatsRaw, double unitPrice) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User session invalid. Please log in again.");
        }
        if (showtimeId <= 0) {
            throw new IllegalArgumentException("Please select a showtime.");
        }
        if (seatsRaw == null || seatsRaw.trim().isEmpty()) {
            throw new IllegalArgumentException("Please specify at least one seat.");
        }

        // Clean and tokenize seats
        String[] tokens = seatsRaw.split("[,; ]+");
        List<String> cleanSeats = new ArrayList<>();
        for (String t : tokens) {
            String s = t.trim().toUpperCase();
            if (!s.isEmpty()) {
                if (!cleanSeats.contains(s)) {
                    cleanSeats.add(s);
                }
            }
        }

        if (cleanSeats.isEmpty()) {
            throw new IllegalArgumentException("Please select or enter valid seat numbers.");
        }

        // Check if any seat is already booked for this showtime
        List<String> alreadyBooked = bookingDAO.getBookedSeats(showtimeId);
        List<String> conflicting = new ArrayList<>();
        for (String seat : cleanSeats) {
            if (alreadyBooked.contains(seat)) {
                conflicting.add(seat);
            }
        }

        if (!conflicting.isEmpty()) {
            throw new IllegalStateException("Seat(s) already booked: " + String.join(", ", conflicting)
                    + ". Please choose different seats.");
        }

        String seatList = String.join(", ", cleanSeats);
        double totalAmount = cleanSeats.size() * unitPrice;
        String bookingCode = "BK-" + (10000 + random.nextInt(90000));

        Booking booking = new Booking();
        booking.setBookingCode(bookingCode);
        booking.setUserId(userId);
        booking.setShowtimeId(showtimeId);
        booking.setSeats(seatList);
        booking.setTotalAmount(totalAmount);
        booking.setStatus("PENDING");

        boolean inserted = bookingDAO.insert(booking);
        if (!inserted) {
            throw new RuntimeException("Database error: Could not complete booking. Please try again.");
        }

        return booking;
    }

    public Booking getBookingById(int bookingId) {
        if (bookingId <= 0) {
            throw new IllegalArgumentException("Invalid booking ID.");
        }
        return bookingDAO.findById(bookingId);
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.getAll();
    }

    public List<Booking> getCustomerBookings(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
        return bookingDAO.getByUserId(userId);
    }

    public List<Booking> getBookingsByDateRange(String from, String to) {
        if (from == null || from.trim().isEmpty() || to == null || to.trim().isEmpty()) {
            return bookingDAO.getAll();
        }
        return bookingDAO.getByDateRange(from.trim(), to.trim());
    }

    public boolean updateStatus(int bookingId, String status) {
        if (bookingId <= 0) {
            throw new IllegalArgumentException("Invalid booking ID.");
        }
        return bookingDAO.updateStatus(bookingId, status);
    }

    public boolean markAsPaid(int bookingId) {
        if (bookingId <= 0) {
            throw new IllegalArgumentException("Invalid booking ID.");
        }
        return bookingDAO.updateStatus(bookingId, "PAID");
    }

    public boolean cancelBooking(int bookingId) {
        if (bookingId <= 0) {
            throw new IllegalArgumentException("Invalid booking ID.");
        }
        return bookingDAO.updateStatus(bookingId, "CANCELLED");
    }

    public boolean deleteBooking(int bookingId) {
        if (bookingId <= 0) {
            throw new IllegalArgumentException("Invalid booking ID.");
        }
        return bookingDAO.delete(bookingId);
    }

    public List<String> getBookedSeats(int showtimeId) {
        return bookingDAO.getBookedSeats(showtimeId);
    }

    public double getTotalSales(String from, String to) {
        if (from == null || from.trim().isEmpty() || to == null || to.trim().isEmpty()) {
            return bookingDAO.getAllTimeSales();
        }
        return bookingDAO.getTotalSales(from.trim(), to.trim());
    }

    public double getAllTimeSales() {
        return bookingDAO.getAllTimeSales();
    }
}
