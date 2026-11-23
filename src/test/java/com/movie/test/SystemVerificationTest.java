package com.movie.test;

import com.movie.dao.*;
import com.movie.model.*;
import com.movie.service.*;
import com.movie.util.DatabaseConnection;
import com.movie.util.PasswordUtil;
import com.movie.view.ReceiptFrame;

import java.util.List;

/**
 * System Verification Test Suite validating all CRUD operations,
 * database persistence, password hashing, and business validation rules.
 */
public class SystemVerificationTest {

    private static int testsPassed = 0;
    private static int testsTotal = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   CINEMA TICKETING SYSTEM - VERIFICATION SUITE  ");
        System.out.println("=================================================");

        runTest("Database Connection", () -> {
            boolean connected = DatabaseConnection.testConnection();
            assertTrue(connected, "Database connection must be active");
        });

        runTest("Password Hashing & Matching (SHA-256)", () -> {
            String plain = "admin123";
            String hashed = PasswordUtil.hash(plain);
            assertTrue(hashed != null && !hashed.isEmpty(), "Hash must not be empty");
            assertTrue(PasswordUtil.matches("admin123", hashed), "Plain password must match hash");
            assertTrue(!PasswordUtil.matches("wrongPass", hashed), "Wrong password must not match hash");
        });

        runTest("User Service Authentication", () -> {
            UserService us = new UserService();
            User admin = us.authenticate("admin", "admin123");
            assertTrue(admin != null, "Admin authentication should succeed");
            assertTrue(admin.isAdmin(), "Admin user must have ADMIN role");

            User customer = us.authenticate("johndoe", "password123");
            assertTrue(customer != null, "Customer authentication should succeed");
            assertTrue(!customer.isAdmin(), "Customer must have CUSTOMER role");
        });

        runTest("Movie Service - CRUD Operations", () -> {
            MovieService ms = new MovieService();
            String testTitle = "Temp Test Movie " + System.currentTimeMillis();

            // 1. Create
            boolean created = ms.addMovie(testTitle, "TestGenre", 120, "PG-13", "Test Synopsis", 250.0, "NOW_SHOWING");
            assertTrue(created, "Movie creation should return true");

            // 2. Read & Search
            List<Movie> results = ms.searchMovies(testTitle);
            assertTrue(!results.isEmpty(), "Search should find the newly created movie");
            Movie found = results.get(0);
            assertTrue(found.getTitle().equals(testTitle), "Title must match");

            // 3. Update
            boolean updated = ms.updateMovie(found.getMovieId(), testTitle + " (Updated)", "TestGenre", 125, "PG-13", "Updated Synopsis", 275.0, "NOW_SHOWING");
            assertTrue(updated, "Movie update should return true");

            // 4. Delete
            boolean deleted = ms.deleteMovie(found.getMovieId());
            assertTrue(deleted, "Movie deletion should return true");
        });

        runTest("Cinema Hall Service - CRUD Operations", () -> {
            HallService hs = new HallService();
            String testHall = "Temp Hall " + System.currentTimeMillis();

            // 1. Create
            boolean created = hs.addHall(testHall, 80);
            assertTrue(created, "Hall creation should succeed");

            // 2. Read
            List<Hall> halls = hs.getAllHalls();
            Hall found = null;
            for (Hall h : halls) {
                if (h.getHallName().equals(testHall)) {
                    found = h;
                    break;
                }
            }
            assertTrue(found != null, "Hall should be found in list");

            // 3. Update
            boolean updated = hs.updateHall(found.getHallId(), testHall + " (VIP)", 90);
            assertTrue(updated, "Hall update should succeed");

            // 4. Delete
            boolean deleted = hs.deleteHall(found.getHallId());
            assertTrue(deleted, "Hall deletion should succeed");
        });

        runTest("Showtime Service - Validation & Scheduling", () -> {
            ShowtimeService ss = new ShowtimeService();
            MovieService ms = new MovieService();
            HallService hs = new HallService();

            List<Movie> movies = ms.getAllMovies();
            List<Hall> halls = hs.getAllHalls();
            assertTrue(!movies.isEmpty(), "Movies must exist");
            assertTrue(!halls.isEmpty(), "Halls must exist");

            int movieId = movies.get(0).getMovieId();
            int hallId = halls.get(0).getHallId();

            // Invalid date format test
            try {
                ss.addShowtime(movieId, hallId, "2026/11/20", "14:00:00");
                assertTrue(false, "Invalid date format should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException ex) {
                assertTrue(true, "Caught expected date format exception");
            }

            // Valid showtime creation
            boolean created = ss.addShowtime(movieId, hallId, "2026-11-28", "15:30:00");
            assertTrue(created, "Valid showtime insertion should succeed");

            // Read & Delete
            List<Showtime> list = ss.getByMovie(movieId);
            assertTrue(!list.isEmpty(), "Showtimes by movie should not be empty");
            Showtime createdSt = null;
            for (Showtime st : list) {
                if ("2026-11-28".equals(st.getShowDate())) {
                    createdSt = st;
                    break;
                }
            }
            assertTrue(createdSt != null, "Created showtime must exist");
            boolean deleted = ss.deleteShowtime(createdSt.getShowtimeId());
            assertTrue(deleted, "Showtime deletion should succeed");
        });

        runTest("Booking Service - Seat Conflict Prevention & Calculations", () -> {
            BookingService bs = new BookingService();
            UserDAO ud = new UserDAO();
            ShowtimeDAO sd = new ShowtimeDAO();

            User customer = ud.findByUsername("johndoe");
            List<Showtime> showtimes = sd.getAll();
            assertTrue(customer != null && !showtimes.isEmpty(), "Customer and showtime required");

            int showtimeId = showtimes.get(0).getShowtimeId();
            double price = 300.0;
            String uniqueSeat = "Z" + (System.currentTimeMillis() % 1000);

            // 1. First booking of seat
            Booking b1 = bs.createBooking(customer.getUserId(), showtimeId, uniqueSeat, price);
            assertTrue(b1 != null, "Booking should be created");
            assertTrue(b1.getBookingCode().startsWith("BK-"), "Booking code format valid");
            assertTrue(b1.getTotalAmount() == 300.0, "Total amount matches single seat");

            // 2. Attempt double booking of identical seat
            try {
                bs.createBooking(customer.getUserId(), showtimeId, uniqueSeat, price);
                assertTrue(false, "Double booking should have thrown IllegalStateException");
            } catch (IllegalStateException ex) {
                assertTrue(true, "Caught expected seat conflict error: " + ex.getMessage());
            }

            // 3. Mark as Paid
            boolean paid = bs.markAsPaid(b1.getBookingId());
            assertTrue(paid, "Marking booking as PAID should succeed");

            // 4. Cancel booking (releasing seats)
            boolean cancelled = bs.cancelBooking(b1.getBookingId());
            assertTrue(cancelled, "Cancelling booking should succeed");

            // 5. Cleanup
            bs.deleteBooking(b1.getBookingId());
        });

        runTest("Login Welcome Username Format Validation", () -> {
            UserService us = new UserService();
            User admin = us.authenticate("admin", "admin123");
            assertTrue(admin != null, "User must authenticate");
            String welcomeAdmin = "Welcome, " + admin.getUsername() + "!";
            assertTrue("Welcome, admin!".equals(welcomeAdmin), "Message must format as Welcome, admin!");

            User customer = us.authenticate("johndoe", "password123");
            assertTrue(customer != null, "Customer must authenticate");
            String welcomeCust = "Welcome, " + customer.getUsername() + "!";
            assertTrue("Welcome, johndoe!".equals(welcomeCust), "Message must format as Welcome, johndoe!");
        });

        runTest("Printable E-Receipt & Crystal Report Generation", () -> {
            Booking b = new Booking(999, "BK-99999", 1, 1, "A1, A2", 500.0, "PAID", "2026-11-20 14:00:00");
            b.setCustomerName("John Doe");
            b.setMovieTitle("Inception");
            b.setHallName("Cinema 1");
            b.setShowDate("2026-11-25");
            b.setShowTime("18:00:00");

            User u = new User(1, "johndoe", "", "John Doe", "CUSTOMER");

            ReceiptFrame rf = new ReceiptFrame(b, u);
            String text = rf.buildReceiptPlainText();
            assertTrue(text != null && text.contains("BK-99999"), "Text receipt must contain booking code");
            assertTrue(text.contains("500.00"), "Text receipt must contain total amount");
            assertTrue(text.contains("Inception"), "Text receipt must contain movie title");
            assertTrue(text.contains("Cinema 1"), "Text receipt must contain cinema hall");
            assertTrue(text.contains("A1, A2"), "Text receipt must contain seats");
            assertTrue(text.contains("PAID"), "Text receipt must contain status");
            rf.dispose();
        });

        System.out.println("=================================================");
        System.out.println("SUMMARY: " + testsPassed + " / " + testsTotal + " TESTS PASSED ("
                + (testsPassed == testsTotal ? "100% SUCCESS" : "FAILURES DETECTED") + ")");
        System.out.println("=================================================");

        if (testsPassed != testsTotal) {
            System.exit(1);
        }
    }

    private static void runTest(String name, Runnable test) {
        testsTotal++;
        try {
            test.run();
            testsPassed++;
            System.out.println("[PASS] " + name);
        } catch (Throwable t) {
            System.err.println("[FAIL] " + name + " -> " + t.getMessage());
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
