/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.controller;

import com.movie.model.Booking;
import com.movie.model.Hall;
import com.movie.model.Movie;
import com.movie.model.Showtime;
import com.movie.model.User;
import com.movie.service.BookingService;
import com.movie.service.HallService;
import com.movie.service.MovieService;
import com.movie.service.ShowtimeService;
import com.movie.service.UserService;
import com.movie.view.AdminDashboardView;
import com.movie.view.LoginView;
import com.movie.view.ReceiptFrame;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Controller managing the Administrator portal: Movies CRUD, Halls CRUD,
 * Showtimes scheduling, Bookings & Financial reports, and Users roles.
 * 
 * Architectural Connection in System:
 * - Layer: Controller Layer (com.movie.controller)
 * - Upstream: Binds UI events from AdminDashboardView (forms, table clicks, buttons).
 * - Downstream: Coordinates five Service Layer components:
 *   - MovieService (movie catalog management)
 *   - HallService (cinema halls and seating capacity)
 *   - ShowtimeService (movie screening schedules)
 *   - BookingService (reservation lifecycle, financial sales, status updates)
 *   - UserService (administrative roles and account administration)
 * - Views: Refreshes AdminDashboardView tables and opens ReceiptFrame for ticket printing.
 */
public class AdminDashboardController implements ActionListener {

    private final AdminDashboardView view;
    private final MovieService movieService;
    private final HallService hallService;
    private final ShowtimeService showtimeService;
    private final BookingService bookingService;
    private final UserService userService;

    public AdminDashboardController(AdminDashboardView view) {
        this.view = view;
        this.movieService = new MovieService();
        this.hallService = new HallService();
        this.showtimeService = new ShowtimeService();
        this.bookingService = new BookingService();
        this.userService = new UserService();

        initListeners();
        loadAllData();
    }

    private void initListeners() {
        // Top header
        view.getBtnLogout().addActionListener(this);

        // Movie buttons
        view.getBtnAddMovie().addActionListener(this);
        view.getBtnUpdateMovie().addActionListener(this);
        view.getBtnDeleteMovie().addActionListener(this);
        view.getBtnClearMovie().addActionListener(this);
        view.getBtnSearchMovie().addActionListener(this);
        view.getBtnRefreshMovies().addActionListener(this);

        // Hall buttons
        view.getBtnAddHall().addActionListener(this);
        view.getBtnUpdateHall().addActionListener(this);
        view.getBtnDeleteHall().addActionListener(this);
        view.getBtnClearHall().addActionListener(this);
        view.getBtnRefreshHalls().addActionListener(this);

        // Showtime buttons
        view.getBtnAddShowtime().addActionListener(this);
        view.getBtnUpdateShowtime().addActionListener(this);
        view.getBtnDeleteShowtime().addActionListener(this);
        view.getBtnClearShowtime().addActionListener(this);

        // Booking buttons
        view.getBtnFilterBookings().addActionListener(this);
        view.getBtnShowAllBookings().addActionListener(this);
        view.getBtnMarkPaid().addActionListener(this);
        view.getBtnCancelBooking().addActionListener(this);
        view.getBtnDeleteBooking().addActionListener(this);
        view.getBtnPrintBookingReceipt().addActionListener(this);

        // User buttons
        view.getBtnPromoteAdmin().addActionListener(this);
        view.getBtnDemoteCustomer().addActionListener(this);
        view.getBtnDeleteUser().addActionListener(this);
        view.getBtnRefreshUsers().addActionListener(this);

        // Table selection listeners for form population
        view.getTblMovies().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateMovieForm();
        });
        view.getTblHalls().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateHallForm();
        });
        view.getTblShowtimes().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateShowtimeForm();
        });
    }

    private void loadAllData() {
        if (view.getCmbMovieRating().getItemCount() == 0) {
            view.getCmbMovieRating().setModel(new DefaultComboBoxModel<>(new String[]{"G", "PG", "PG-13", "R-13", "R-16", "R-18", "R", "NC-17"}));
        }
        if (view.getCmbMovieStatus().getItemCount() == 0) {
            view.getCmbMovieStatus().setModel(new DefaultComboBoxModel<>(new String[]{"NOW_SHOWING", "COMING_SOON"}));
        }
        refreshMoviesTable(movieService.getAllMovies());
        refreshHallsTable();
        refreshShowtimesTable();
        loadShowtimeDropdowns();
        refreshBookingsTable(bookingService.getAllBookings());
        refreshUsersTable();
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

        // Movie Actions
        if (src == view.getBtnAddMovie()) handleAddMovie();
        else if (src == view.getBtnUpdateMovie()) handleUpdateMovie();
        else if (src == view.getBtnDeleteMovie()) handleDeleteMovie();
        else if (src == view.getBtnClearMovie()) view.clearMovieFields();
        else if (src == view.getBtnSearchMovie()) handleSearchMovies();
        else if (src == view.getBtnRefreshMovies()) refreshMoviesTable(movieService.getAllMovies());

        // Hall Actions
        else if (src == view.getBtnAddHall()) handleAddHall();
        else if (src == view.getBtnUpdateHall()) handleUpdateHall();
        else if (src == view.getBtnDeleteHall()) handleDeleteHall();
        else if (src == view.getBtnClearHall()) view.clearHallFields();
        else if (src == view.getBtnRefreshHalls()) refreshHallsTable();

        // Showtime Actions
        else if (src == view.getBtnAddShowtime()) handleAddShowtime();
        else if (src == view.getBtnUpdateShowtime()) handleUpdateShowtime();
        else if (src == view.getBtnDeleteShowtime()) handleDeleteShowtime();
        else if (src == view.getBtnClearShowtime()) view.clearShowtimeFields();

        // Booking Actions
        else if (src == view.getBtnFilterBookings()) handleFilterBookings();
        else if (src == view.getBtnShowAllBookings()) refreshBookingsTable(bookingService.getAllBookings());
        else if (src == view.getBtnMarkPaid()) handleUpdateBookingStatus("PAID");
        else if (src == view.getBtnCancelBooking()) handleUpdateBookingStatus("CANCELLED");
        else if (src == view.getBtnDeleteBooking()) handleDeleteBooking();
        else if (src == view.getBtnPrintBookingReceipt()) handlePrintReceipt();

        // User Actions
        else if (src == view.getBtnPromoteAdmin()) handleUpdateUserRole("ADMIN");
        else if (src == view.getBtnDemoteCustomer()) handleUpdateUserRole("CUSTOMER");
        else if (src == view.getBtnDeleteUser()) handleDeleteUser();
        else if (src == view.getBtnRefreshUsers()) refreshUsersTable();
    }

    // =========================================================================
    // MOVIES CRUD
    // =========================================================================

    private void handleAddMovie() {
        try {
            String title = view.getTxtMovieTitle().getText();
            String genre = view.getTxtMovieGenre().getText();
            int duration = Integer.parseInt(view.getTxtMovieDuration().getText().trim());
            String rating = (String) view.getCmbMovieRating().getSelectedItem();
            double price = Double.parseDouble(view.getTxtMoviePrice().getText().trim());
            String status = (String) view.getCmbMovieStatus().getSelectedItem();
            String synopsis = view.getTxtMovieSynopsis().getText();

            if (movieService.addMovie(title, genre, duration, rating, synopsis, price, status)) {
                view.showMessage("Movie added successfully!");
                refreshMoviesTable(movieService.getAllMovies());
                loadShowtimeDropdowns();
                view.clearMovieFields();
            } else {
                view.showMessage("Failed to insert movie into database.");
            }
        } catch (NumberFormatException ex) {
            view.showMessage("Validation error: Duration must be integer and Price must be numeric.");
        } catch (Exception ex) {
            view.showMessage("Validation error: " + ex.getMessage());
        }
    }

    private void handleUpdateMovie() {
        int row = view.getTblMovies().getSelectedRow();
        if (row < 0) {
            view.showMessage("Please select a movie from the table to update.");
            return;
        }
        try {
            int movieId = (int) view.getTblMovies().getValueAt(row, 0);
            String title = view.getTxtMovieTitle().getText();
            String genre = view.getTxtMovieGenre().getText();
            int duration = Integer.parseInt(view.getTxtMovieDuration().getText().trim());
            String rating = (String) view.getCmbMovieRating().getSelectedItem();
            double price = Double.parseDouble(view.getTxtMoviePrice().getText().trim());
            String status = (String) view.getCmbMovieStatus().getSelectedItem();
            String synopsis = view.getTxtMovieSynopsis().getText();

            if (movieService.updateMovie(movieId, title, genre, duration, rating, synopsis, price, status)) {
                view.showMessage("Movie updated successfully!");
                refreshMoviesTable(movieService.getAllMovies());
                loadShowtimeDropdowns();
            } else {
                view.showMessage("Failed to update movie in database.");
            }
        } catch (NumberFormatException ex) {
            view.showMessage("Validation error: Duration must be integer and Price must be numeric.");
        } catch (Exception ex) {
            view.showMessage("Validation error: " + ex.getMessage());
        }
    }

    private void handleDeleteMovie() {
        int row = view.getTblMovies().getSelectedRow();
        if (row < 0) {
            view.showMessage("Please select a movie to delete.");
            return;
        }
        int movieId = (int) view.getTblMovies().getValueAt(row, 0);
        String title = (String) view.getTblMovies().getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete movie '" + title + "' and its scheduled showtimes?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (movieService.deleteMovie(movieId)) {
                view.showMessage("Movie deleted successfully.");
                refreshMoviesTable(movieService.getAllMovies());
                refreshShowtimesTable();
                loadShowtimeDropdowns();
                view.clearMovieFields();
            } else {
                view.showMessage("Failed to delete movie.");
            }
        }
    }

    private void handleSearchMovies() {
        String keyword = view.getTxtSearchMovie().getText().trim();
        refreshMoviesTable(movieService.searchMovies(keyword));
    }

    private void populateMovieForm() {
        int row = view.getTblMovies().getSelectedRow();
        if (row >= 0) {
            int movieId = (int) view.getTblMovies().getValueAt(row, 0);
            Movie m = movieService.getMovieById(movieId);
            if (m != null) {
                view.getTxtMovieTitle().setText(m.getTitle());
                view.getTxtMovieGenre().setText(m.getGenre());
                view.getTxtMovieDuration().setText(String.valueOf(m.getDuration()));
                view.getCmbMovieRating().setSelectedItem(m.getRating());
                view.getTxtMoviePrice().setText(String.format("%.2f", m.getPrice()));
                view.getCmbMovieStatus().setSelectedItem(m.getStatus());
                view.getTxtMovieSynopsis().setText(m.getSynopsis());
            }
        }
    }

    private void refreshMoviesTable(List<Movie> list) {
        DefaultTableModel model = (DefaultTableModel) view.getTblMovies().getModel();
        model.setRowCount(0);
        for (Movie m : list) {
            model.addRow(new Object[]{
                    m.getMovieId(),
                    m.getTitle(),
                    m.getGenre(),
                    m.getDuration(),
                    m.getRating(),
                    m.getPrice(),
                    m.getStatus()
            });
        }
    }

    // =========================================================================
    // CINEMA HALLS CRUD
    // =========================================================================

    private void handleAddHall() {
        try {
            String name = view.getTxtHallName().getText();
            int seats = Integer.parseInt(view.getTxtTotalSeats().getText().trim());

            if (hallService.addHall(name, seats)) {
                view.showMessage("Cinema Hall added successfully!");
                refreshHallsTable();
                loadShowtimeDropdowns();
                view.clearHallFields();
            } else {
                view.showMessage("Failed to add hall.");
            }
        } catch (NumberFormatException ex) {
            view.showMessage("Validation error: Total seats must be an integer.");
        } catch (Exception ex) {
            view.showMessage("Validation error: " + ex.getMessage());
        }
    }

    private void handleUpdateHall() {
        int row = view.getTblHalls().getSelectedRow();
        if (row < 0) {
            view.showMessage("Please select a hall from the table to update.");
            return;
        }
        try {
            int hallId = (int) view.getTblHalls().getValueAt(row, 0);
            String name = view.getTxtHallName().getText();
            int seats = Integer.parseInt(view.getTxtTotalSeats().getText().trim());

            if (hallService.updateHall(hallId, name, seats)) {
                view.showMessage("Cinema Hall updated successfully!");
                refreshHallsTable();
                loadShowtimeDropdowns();
            } else {
                view.showMessage("Failed to update hall.");
            }
        } catch (NumberFormatException ex) {
            view.showMessage("Validation error: Total seats must be an integer.");
        } catch (Exception ex) {
            view.showMessage("Validation error: " + ex.getMessage());
        }
    }

    private void handleDeleteHall() {
        int row = view.getTblHalls().getSelectedRow();
        if (row < 0) {
            view.showMessage("Please select a hall to delete.");
            return;
        }
        int hallId = (int) view.getTblHalls().getValueAt(row, 0);
        String name = (String) view.getTblHalls().getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(view,
                "Delete cinema hall '" + name + "' and related showtimes?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (hallService.deleteHall(hallId)) {
                view.showMessage("Hall deleted successfully.");
                refreshHallsTable();
                refreshShowtimesTable();
                loadShowtimeDropdowns();
                view.clearHallFields();
            } else {
                view.showMessage("Failed to delete hall.");
            }
        }
    }

    private void populateHallForm() {
        int row = view.getTblHalls().getSelectedRow();
        if (row >= 0) {
            view.getTxtHallName().setText((String) view.getTblHalls().getValueAt(row, 1));
            view.getTxtTotalSeats().setText(String.valueOf(view.getTblHalls().getValueAt(row, 2)));
        }
    }

    private void refreshHallsTable() {
        DefaultTableModel model = (DefaultTableModel) view.getTblHalls().getModel();
        model.setRowCount(0);
        for (Hall h : hallService.getAllHalls()) {
            model.addRow(new Object[]{h.getHallId(), h.getHallName(), h.getTotalSeats()});
        }
    }

    // =========================================================================
    // SHOWTIMES CRUD
    // =========================================================================

    private void handleAddShowtime() {
        try {
            Movie selectedMovie = (Movie) view.getCmbShowtimeMovie().getSelectedItem();
            Hall selectedHall = (Hall) view.getCmbShowtimeHall().getSelectedItem();
            String date = view.getTxtShowDate().getText();
            String time = view.getTxtShowTime().getText();

            if (selectedMovie == null || selectedHall == null) {
                view.showMessage("Please select both a movie and a cinema hall.");
                return;
            }

            if (showtimeService.addShowtime(selectedMovie.getMovieId(), selectedHall.getHallId(), date, time)) {
                view.showMessage("Showtime scheduled successfully!");
                refreshShowtimesTable();
                view.clearShowtimeFields();
            } else {
                view.showMessage("Failed to schedule showtime.");
            }
        } catch (Exception ex) {
            view.showMessage("Validation error: " + ex.getMessage());
        }
    }

    private void handleUpdateShowtime() {
        int row = view.getTblShowtimes().getSelectedRow();
        if (row < 0) {
            view.showMessage("Please select a showtime from the table to update.");
            return;
        }
        try {
            int showtimeId = (int) view.getTblShowtimes().getValueAt(row, 0);
            Movie selectedMovie = (Movie) view.getCmbShowtimeMovie().getSelectedItem();
            Hall selectedHall = (Hall) view.getCmbShowtimeHall().getSelectedItem();
            String date = view.getTxtShowDate().getText();
            String time = view.getTxtShowTime().getText();

            if (selectedMovie == null || selectedHall == null) {
                view.showMessage("Please select both a movie and a cinema hall.");
                return;
            }

            if (showtimeService.updateShowtime(showtimeId, selectedMovie.getMovieId(), selectedHall.getHallId(), date, time)) {
                view.showMessage("Showtime updated successfully!");
                refreshShowtimesTable();
            } else {
                view.showMessage("Failed to update showtime.");
            }
        } catch (Exception ex) {
            view.showMessage("Validation error: " + ex.getMessage());
        }
    }

    private void handleDeleteShowtime() {
        int row = view.getTblShowtimes().getSelectedRow();
        if (row < 0) {
            view.showMessage("Please select a showtime to delete.");
            return;
        }
        int showtimeId = (int) view.getTblShowtimes().getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(view,
                "Delete showtime #" + showtimeId + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (showtimeService.deleteShowtime(showtimeId)) {
                view.showMessage("Showtime deleted successfully.");
                refreshShowtimesTable();
                view.clearShowtimeFields();
            } else {
                view.showMessage("Failed to delete showtime.");
            }
        }
    }

    private void populateShowtimeForm() {
        int row = view.getTblShowtimes().getSelectedRow();
        if (row >= 0) {
            int showtimeId = (int) view.getTblShowtimes().getValueAt(row, 0);
            Showtime s = showtimeService.getShowtimeById(showtimeId);
            if (s != null) {
                view.getTxtShowDate().setText(s.getShowDate());
                view.getTxtShowTime().setText(s.getShowTime());

                // Select matching Movie in combo
                for (int i = 0; i < view.getCmbShowtimeMovie().getItemCount(); i++) {
                    Object item = view.getCmbShowtimeMovie().getItemAt(i);
                    if (item instanceof Movie && ((Movie) item).getMovieId() == s.getMovieId()) {
                        view.getCmbShowtimeMovie().setSelectedIndex(i);
                        break;
                    }
                }
                // Select matching Hall in combo
                for (int i = 0; i < view.getCmbShowtimeHall().getItemCount(); i++) {
                    Object item = view.getCmbShowtimeHall().getItemAt(i);
                    if (item instanceof Hall && ((Hall) item).getHallId() == s.getHallId()) {
                        view.getCmbShowtimeHall().setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void loadShowtimeDropdowns() {
        DefaultComboBoxModel<Object> movieModel = new DefaultComboBoxModel<>();
        for (Movie m : movieService.getAllMovies()) {
            movieModel.addElement(m);
        }
        view.getCmbShowtimeMovie().setModel(movieModel);

        DefaultComboBoxModel<Object> hallModel = new DefaultComboBoxModel<>();
        for (Hall h : hallService.getAllHalls()) {
            hallModel.addElement(h);
        }
        view.getCmbShowtimeHall().setModel(hallModel);
    }

    private void refreshShowtimesTable() {
        DefaultTableModel model = (DefaultTableModel) view.getTblShowtimes().getModel();
        model.setRowCount(0);
        for (Showtime s : showtimeService.getAllShowtimes()) {
            model.addRow(new Object[]{
                    s.getShowtimeId(),
                    s.getMovieTitle(),
                    s.getHallName(),
                    s.getShowDate(),
                    s.getShowTime(),
                    s.getMoviePrice()
            });
        }
    }

    // =========================================================================
    // BOOKINGS & SALES REPORTS
    // =========================================================================

    private void handleFilterBookings() {
        String from = view.getTxtDateFrom().getText().trim();
        String to = view.getTxtDateTo().getText().trim();
        if (from.isEmpty() || to.isEmpty()) {
            view.showMessage("Please provide both From and To dates in YYYY-MM-DD format.");
            return;
        }
        refreshBookingsTable(bookingService.getBookingsByDateRange(from, to));
        double sales = bookingService.getTotalSales(from, to);
        view.getLblTotalSales().setText(String.format("Total Sales: ₱%,.2f", sales));
    }

    private void handleUpdateBookingStatus(String status) {
        int row = view.getTblBookings().getSelectedRow();
        if (row < 0) {
            view.showMessage("Please select a booking from the table.");
            return;
        }
        int bookingId = (int) view.getTblBookings().getValueAt(row, 0);
        if (bookingService.updateStatus(bookingId, status)) {
            view.showMessage("Booking status updated to " + status + ".");
            refreshBookingsTable(bookingService.getAllBookings());
        } else {
            view.showMessage("Failed to update booking status.");
        }
    }

    private void handleDeleteBooking() {
        int row = view.getTblBookings().getSelectedRow();
        if (row < 0) {
            view.showMessage("Please select a booking to delete.");
            return;
        }
        int bookingId = (int) view.getTblBookings().getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(view,
                "Delete booking record #" + bookingId + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (bookingService.deleteBooking(bookingId)) {
                view.showMessage("Booking deleted successfully.");
                refreshBookingsTable(bookingService.getAllBookings());
            } else {
                view.showMessage("Failed to delete booking.");
            }
        }
    }

    private void handlePrintReceipt() {
        int row = view.getTblBookings().getSelectedRow();
        if (row < 0) {
            view.showMessage("Please select a booking from the table to print receipt.");
            return;
        }

        int bookingId = (int) view.getTblBookings().getValueAt(row, 0);
        Booking booking = bookingService.getBookingById(bookingId);

        if (booking == null) {
            booking = new Booking();
            booking.setBookingId(bookingId);
            booking.setBookingCode((String) view.getTblBookings().getValueAt(row, 1));
            booking.setCustomerName((String) view.getTblBookings().getValueAt(row, 2));
            booking.setMovieTitle((String) view.getTblBookings().getValueAt(row, 3));
            booking.setHallName((String) view.getTblBookings().getValueAt(row, 4));
            String dt = (String) view.getTblBookings().getValueAt(row, 5);
            booking.setShowDate(dt);
            booking.setSeats((String) view.getTblBookings().getValueAt(row, 6));
            booking.setTotalAmount((double) view.getTblBookings().getValueAt(row, 7));
            booking.setStatus((String) view.getTblBookings().getValueAt(row, 8));
        }

        ReceiptFrame frame = new ReceiptFrame(booking, null);
        frame.setVisible(true);
    }

    private void refreshBookingsTable(List<Booking> list) {
        DefaultTableModel model = (DefaultTableModel) view.getTblBookings().getModel();
        model.setRowCount(0);
        for (Booking b : list) {
            model.addRow(new Object[]{
                    b.getBookingId(),
                    b.getBookingCode(),
                    b.getCustomerName(),
                    b.getMovieTitle(),
                    b.getHallName(),
                    b.getShowDate() + " " + b.getShowTime(),
                    b.getSeats(),
                    b.getTotalAmount(),
                    b.getStatus()
            });
        }
        double totalSales = bookingService.getAllTimeSales();
        view.getLblTotalSales().setText(String.format("Total Sales: ₱%,.2f", totalSales));
    }

    // =========================================================================
    // USERS MANAGEMENT
    // =========================================================================

    private void handleUpdateUserRole(String newRole) {
        int row = view.getTblUsers().getSelectedRow();
        if (row < 0) {
            view.showMessage("Please select a user from the table.");
            return;
        }
        int userId = (int) view.getTblUsers().getValueAt(row, 0);
        String username = (String) view.getTblUsers().getValueAt(row, 1);

        if (userService.updateRole(userId, newRole)) {
            view.showMessage("User '" + username + "' role updated to " + newRole + ".");
            refreshUsersTable();
        } else {
            view.showMessage("Failed to update user role.");
        }
    }

    private void handleDeleteUser() {
        int row = view.getTblUsers().getSelectedRow();
        if (row < 0) {
            view.showMessage("Please select a user to delete.");
            return;
        }
        int userId = (int) view.getTblUsers().getValueAt(row, 0);
        String username = (String) view.getTblUsers().getValueAt(row, 1);

        if (view.getCurrentUser() != null && view.getCurrentUser().getUserId() == userId) {
            view.showMessage("You cannot delete the currently logged in administrator account!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Delete user account '" + username + "'?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userService.deleteUser(userId)) {
                view.showMessage("User deleted successfully.");
                refreshUsersTable();
            } else {
                view.showMessage("Failed to delete user.");
            }
        }
    }

    private void refreshUsersTable() {
        DefaultTableModel model = (DefaultTableModel) view.getTblUsers().getModel();
        model.setRowCount(0);
        for (User u : userService.getAllUsers()) {
            model.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getFullName(), u.getRole()});
        }
    }
}
