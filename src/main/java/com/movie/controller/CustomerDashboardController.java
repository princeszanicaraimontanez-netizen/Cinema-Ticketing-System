/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.controller;

import com.movie.model.Booking;
import com.movie.model.Movie;
import com.movie.model.Showtime;
import com.movie.model.User;
import com.movie.service.BookingService;
import com.movie.service.MovieService;
import com.movie.service.ShowtimeService;
import com.movie.view.CustomerDashboardView;
import com.movie.view.LoginView;
import com.movie.view.ReceiptFrame;
import com.movie.view.SeatMapFrame;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller managing customer operations: browsing movies, inspecting showtimes,
 * selecting seats visually via SeatMapFrame, booking tickets, paying for reservations,
 * and viewing E-Tickets via ReceiptFrame.
 * 
 * Architectural Connection in System:
 * - Layer: Controller Layer (com.movie.controller)
 * - Upstream: Binds UI events from CustomerDashboardView (search, selection, booking buttons).
 * - Downstream: Coordinates Service Layer components:
 *   - MovieService (fetches active movie catalogue)
 *   - ShowtimeService (queries schedules and seat availability)
 *   - BookingService (creates reservations, computes prices, executes payments)
 * - View Navigation: Spawns interactive SeatMapFrame for seat picking and ReceiptFrame for ticket printing.
 */
public class CustomerDashboardController implements ActionListener {

    private final CustomerDashboardView view;
    private final User currentUser;
    private final MovieService movieService;
    private final ShowtimeService showtimeService;
    private final BookingService bookingService;

    public CustomerDashboardController(CustomerDashboardView view) {
        this.view = view;
        this.currentUser = view.getCurrentUser();
        this.movieService = new MovieService();
        this.showtimeService = new ShowtimeService();
        this.bookingService = new BookingService();

        initListeners();
        loadInitialData();
    }

    private void initListeners() {
        // Logout
        view.getBtnLogout().addActionListener(this);

        // Browse Movies tab
        view.getBtnSearchMovie().addActionListener(this);
        view.getBtnResetSearch().addActionListener(this);
        view.getBtnProceedToBook().addActionListener(this);

        // Movie row selection listener
        view.getTblMovies().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) handleMovieSelection();
        });

        // Book Tickets tab
        view.getCmbBookMovie().addActionListener(e -> handleBookMovieChange());
        view.getCmbBookShowtime().addActionListener(e -> handleBookShowtimeChange());
        view.getBtnSelectSeats().addActionListener(e -> handleOpenSeatMap());
        view.getBtnConfirmBooking().addActionListener(this);
        view.getBtnClearBooking().addActionListener(this);

        // Live calculation of seats & total price on text change
        view.getTxtSeats().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateBookingTotals(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateBookingTotals(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateBookingTotals(); }
        });

        // My Bookings tab
        view.getBtnPayBooking().addActionListener(this);
        view.getBtnCancelMyBooking().addActionListener(this);
        view.getBtnViewReceipt().addActionListener(this);
        view.getBtnRefreshMyBookings().addActionListener(this);
    }

    private void loadInitialData() {
        refreshBrowseMoviesTable(movieService.getNowShowingMovies());
        loadBookMovieDropdown();
        refreshMyBookingsTable();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        // Logout
        if (src == view.getBtnLogout()) {
            view.dispose();
            LoginView lv = new LoginView();
            new LoginController(lv);
            lv.setVisible(true);
            return;
        }

        // Browse Actions
        if (src == view.getBtnSearchMovie()) handleSearchMovies();
        else if (src == view.getBtnResetSearch()) refreshBrowseMoviesTable(movieService.getNowShowingMovies());
        else if (src == view.getBtnProceedToBook()) handleProceedToBook();

        // Book Actions
        else if (src == view.getBtnConfirmBooking()) handleConfirmBooking();
        else if (src == view.getBtnClearBooking()) clearBookingFields();

        // My Bookings Actions
        else if (src == view.getBtnPayBooking()) handlePayBooking();
        else if (src == view.getBtnCancelMyBooking()) handleCancelBooking();
        else if (src == view.getBtnViewReceipt()) handleViewReceipt();
        else if (src == view.getBtnRefreshMyBookings()) refreshMyBookingsTable();
    }

    // =========================================================================
    // BROWSE MOVIES TAB
    // =========================================================================

    private void handleSearchMovies() {
        String keyword = view.getTxtSearchMovie().getText().trim();
        refreshBrowseMoviesTable(movieService.searchMovies(keyword));
    }

    private void refreshBrowseMoviesTable(List<Movie> list) {
        DefaultTableModel model = (DefaultTableModel) view.getTblMovies().getModel();
        model.setRowCount(0);
        for (Movie m : list) {
            model.addRow(new Object[]{
                    m.getMovieId(),
                    m.getTitle(),
                    m.getGenre(),
                    m.getDuration(),
                    m.getRating(),
                    m.getPrice()
            });
        }
    }

    private void handleMovieSelection() {
        int row = view.getTblMovies().getSelectedRow();
        if (row >= 0) {
            int movieId = (int) view.getTblMovies().getValueAt(row, 0);
            Movie m = movieService.getMovieById(movieId);
            if (m != null) {
                view.getTxaSynopsis().setText(m.getSynopsis());
            }

            // Load showtimes for this movie
            DefaultTableModel stModel = (DefaultTableModel) view.getTblMovieShowtimes().getModel();
            stModel.setRowCount(0);
            List<Showtime> showtimes = showtimeService.getByMovie(movieId);
            for (Showtime s : showtimes) {
                stModel.addRow(new Object[]{
                        s.getShowtimeId(),
                        s.getHallName(),
                        s.getShowDate(),
                        s.getShowTime(),
                        s.getMoviePrice()
                });
            }
        }
    }

    private void handleProceedToBook() {
        int movieRow = view.getTblMovies().getSelectedRow();
        if (movieRow < 0) {
            view.showMessage("Please select a movie first.");
            return;
        }
        int movieId = (int) view.getTblMovies().getValueAt(movieRow, 0);

        int stRow = view.getTblMovieShowtimes().getSelectedRow();
        Integer showtimeId = null;
        if (stRow >= 0) {
            showtimeId = (int) view.getTblMovieShowtimes().getValueAt(stRow, 0);
        }

        // Switch to Book Tickets tab
        view.getTabbedPane().setSelectedIndex(1);

        // Select movie in combo
        for (int i = 0; i < view.getCmbBookMovie().getItemCount(); i++) {
            Object obj = view.getCmbBookMovie().getItemAt(i);
            if (obj instanceof Movie && ((Movie) obj).getMovieId() == movieId) {
                view.getCmbBookMovie().setSelectedIndex(i);
                break;
            }
        }

        // If showtime was selected, select it in showtime combo
        if (showtimeId != null) {
            for (int i = 0; i < view.getCmbBookShowtime().getItemCount(); i++) {
                Object obj = view.getCmbBookShowtime().getItemAt(i);
                if (obj instanceof Showtime && ((Showtime) obj).getShowtimeId() == showtimeId) {
                    view.getCmbBookShowtime().setSelectedIndex(i);
                    break;
                }
            }
        }

        // Open visual seat selection frame for immediate selection
        handleOpenSeatMap();
    }

    // =========================================================================
    // BOOK TICKETS TAB
    // =========================================================================

    private void loadBookMovieDropdown() {
        DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>();
        for (Movie m : movieService.getNowShowingMovies()) {
            model.addElement(m);
        }
        view.getCmbBookMovie().setModel(model);
        if (model.getSize() > 0) {
            handleBookMovieChange();
        }
    }

    private void handleBookMovieChange() {
        Object item = view.getCmbBookMovie().getSelectedItem();
        DefaultComboBoxModel<Object> stModel = new DefaultComboBoxModel<>();
        if (item instanceof Movie) {
            Movie m = (Movie) item;
            List<Showtime> showtimes = showtimeService.getByMovie(m.getMovieId());
            for (Showtime s : showtimes) {
                stModel.addElement(s);
            }
        }
        view.getCmbBookShowtime().setModel(stModel);
        handleBookShowtimeChange();
    }

    private void handleBookShowtimeChange() {
        Object item = view.getCmbBookShowtime().getSelectedItem();
        if (item instanceof Showtime) {
            Showtime s = (Showtime) item;
            view.getLblShowtimeDetails().setText(
                    "Hall: " + s.getHallName() + "  |  Date: " + s.getShowDate() + " " + s.getShowTime()
                    + "  |  Ticket Price: ₱" + String.format("%.2f", s.getMoviePrice())
            );

            // Fetch already booked seats
            List<String> booked = bookingService.getBookedSeats(s.getShowtimeId());
            int totalSeats = s.getHallTotalSeats() > 0 ? s.getHallTotalSeats() : 60;
            int availableCount = Math.max(0, totalSeats - booked.size());

            view.getLblBookedSeats().setText(String.format("Available: %d / %d seats (%d booked)",
                    availableCount, totalSeats, booked.size()));
        } else {
            view.getLblShowtimeDetails().setText("Hall: -  |  Ticket Price: ₱0.00");
            view.getLblBookedSeats().setText("Available Seats: -");
        }
        view.getTxtSeats().setText("");
        updateBookingTotals();
    }

    /**
     * Opens the dedicated visual seat selection JFrame.
     */
    private void handleOpenSeatMap() {
        Object item = view.getCmbBookShowtime().getSelectedItem();
        if (!(item instanceof Showtime)) {
            view.showMessage("Please select a movie and screening showtime first.");
            return;
        }
        Showtime s = (Showtime) item;
        List<String> booked = bookingService.getBookedSeats(s.getShowtimeId());
        String currentSeats = view.getTxtSeats().getText().trim();

        SeatMapFrame frame = new SeatMapFrame(s, booked, currentSeats, (selectedSeatsCsv, seatCount, totalAmount) -> {
            view.getTxtSeats().setText(selectedSeatsCsv);
            updateBookingTotals();
        });
        frame.setVisible(true);
    }

    private void updateBookingTotals() {
        String raw = view.getTxtSeats().getText().trim();
        int count = 0;
        String seatsDisplay = "";
        if (!raw.isEmpty()) {
            String[] tokens = raw.split("[,; ]+");
            List<String> valid = new ArrayList<>();
            for (String t : tokens) {
                if (!t.trim().isEmpty() && !valid.contains(t.trim().toUpperCase())) {
                    valid.add(t.trim().toUpperCase());
                }
            }
            count = valid.size();
            seatsDisplay = String.join(", ", valid);
        }

        view.getLblSeatCount().setText("Seats Selected (" + count + "): " + (count == 0 ? "None" : seatsDisplay));

        double price = 0.0;
        Object obj = view.getCmbBookShowtime().getSelectedItem();
        if (obj instanceof Showtime) {
            price = ((Showtime) obj).getMoviePrice();
        }
        double total = count * price;
        view.getLblTotalAmount().setText(String.format("Total Amount: ₱%,.2f", total));
    }

    private void handleConfirmBooking() {
        if (currentUser == null) {
            view.showMessage("Session expired. Please log in again.");
            return;
        }

        Object obj = view.getCmbBookShowtime().getSelectedItem();
        if (!(obj instanceof Showtime)) {
            view.showMessage("Please select a screening showtime.");
            return;
        }
        Showtime s = (Showtime) obj;
        String seats = view.getTxtSeats().getText().trim();

        if (seats.isEmpty()) {
            view.showMessage("Please select seats to reserve (click 'View & Select Seats (Visual Map)' or enter seats).");
            return;
        }

        try {
            Booking b = bookingService.createBooking(
                    currentUser.getUserId(),
                    s.getShowtimeId(),
                    seats,
                    s.getMoviePrice()
            );

            // Populate display fields on booking for e-receipt
            b.setCustomerName(currentUser.getFullName());
            b.setMovieTitle(s.getMovieTitle());
            b.setHallName(s.getHallName());
            b.setShowDate(s.getShowDate());
            b.setShowTime(s.getShowTime());

            clearBookingFields();
            handleBookShowtimeChange();
            refreshMyBookingsTable();

            // Switch to My Bookings tab
            view.getTabbedPane().setSelectedIndex(2);

            // Offer to view and print official E-Receipt immediately via ReceiptFrame
            int choice = JOptionPane.showOptionDialog(view,
                    "Ticket reservation completed successfully!\n\n"
                            + "Booking Reference: " + b.getBookingCode() + "\n"
                            + "Movie: " + s.getMovieTitle() + "\n"
                            + "Seats: " + b.getSeats() + "\n"
                            + "Total: ₱" + String.format("%,.2f", b.getTotalAmount()) + " (" + b.getStatus() + ")\n\n"
                            + "Would you like to view and print your Official E-Receipt now?",
                    "Booking Confirmed",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{"View & Print E-Receipt", "View Later"},
                    "View & Print E-Receipt");

            if (choice == JOptionPane.YES_OPTION) {
                ReceiptFrame receiptFrame = new ReceiptFrame(b, currentUser);
                receiptFrame.setVisible(true);
            }

        } catch (IllegalStateException ex) {
            view.showMessage(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            view.showMessage("Validation error: " + ex.getMessage());
        } catch (Exception ex) {
            view.showMessage("Error completing booking: " + ex.getMessage());
        }
    }

    private void clearBookingFields() {
        view.getTxtSeats().setText("");
        updateBookingTotals();
    }

    // =========================================================================
    // MY BOOKINGS TAB
    // =========================================================================

    private void refreshMyBookingsTable() {
        if (currentUser == null) return;
        DefaultTableModel model = (DefaultTableModel) view.getTblMyBookings().getModel();
        model.setRowCount(0);
        List<Booking> bookings = bookingService.getCustomerBookings(currentUser.getUserId());
        for (Booking b : bookings) {
            model.addRow(new Object[]{
                    b.getBookingId(),
                    b.getBookingCode(),
                    b.getMovieTitle(),
                    b.getHallName(),
                    b.getShowDate() + " " + b.getShowTime(),
                    b.getSeats(),
                    b.getTotalAmount(),
                    b.getStatus()
            });
        }
    }

    private void handlePayBooking() {
        int row = view.getTblMyBookings().getSelectedRow();
        if (row < 0) {
            view.showMessage("Please select a booking from the table to pay.");
            return;
        }
        int bookingId = (int) view.getTblMyBookings().getValueAt(row, 0);
        String status = (String) view.getTblMyBookings().getValueAt(row, 7);
        if ("PAID".equalsIgnoreCase(status)) {
            view.showMessage("This booking is already PAID!");
            return;
        }
        if ("CANCELLED".equalsIgnoreCase(status)) {
            view.showMessage("Cannot pay for a CANCELLED booking.");
            return;
        }

        double amount = (double) view.getTblMyBookings().getValueAt(row, 6);
        int confirm = JOptionPane.showConfirmDialog(view,
                "Process payment of ₱" + String.format("%,.2f", amount) + " for booking #" + bookingId + "?",
                "Confirm Payment", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (bookingService.markAsPaid(bookingId)) {
                view.showMessage("Payment successful! Status updated to PAID.");
                refreshMyBookingsTable();
            } else {
                view.showMessage("Failed to process payment.");
            }
        }
    }

    private void handleCancelBooking() {
        int row = view.getTblMyBookings().getSelectedRow();
        if (row < 0) {
            view.showMessage("Please select a booking from the table to cancel.");
            return;
        }
        int bookingId = (int) view.getTblMyBookings().getValueAt(row, 0);
        String status = (String) view.getTblMyBookings().getValueAt(row, 7);
        if ("CANCELLED".equalsIgnoreCase(status)) {
            view.showMessage("This booking is already cancelled.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to cancel booking #" + bookingId + "? The reserved seats will be released.",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (bookingService.cancelBooking(bookingId)) {
                view.showMessage("Booking cancelled successfully.");
                refreshMyBookingsTable();
                handleBookShowtimeChange(); // update available seats
            } else {
                view.showMessage("Failed to cancel booking.");
            }
        }
    }

    private void handleViewReceipt() {
        int row = view.getTblMyBookings().getSelectedRow();
        if (row < 0) {
            view.showMessage("Please select a booking from the table to view receipt / e-ticket.");
            return;
        }

        int bookingId = (int) view.getTblMyBookings().getValueAt(row, 0);
        Booking booking = bookingService.getBookingById(bookingId);

        // Fallback reconstruction if database query is unavailable
        if (booking == null) {
            booking = new Booking();
            booking.setBookingId(bookingId);
            booking.setBookingCode((String) view.getTblMyBookings().getValueAt(row, 1));
            booking.setMovieTitle((String) view.getTblMyBookings().getValueAt(row, 2));
            booking.setHallName((String) view.getTblMyBookings().getValueAt(row, 3));
            String dt = (String) view.getTblMyBookings().getValueAt(row, 4);
            booking.setShowDate(dt);
            booking.setSeats((String) view.getTblMyBookings().getValueAt(row, 5));
            booking.setTotalAmount((double) view.getTblMyBookings().getValueAt(row, 6));
            booking.setStatus((String) view.getTblMyBookings().getValueAt(row, 7));
            booking.setCustomerName(currentUser != null ? currentUser.getFullName() : "Valued Customer");
        }

        ReceiptFrame frame = new ReceiptFrame(booking, currentUser);
        frame.setVisible(true);
    }
}
