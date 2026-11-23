package com.movie.test;

import com.movie.controller.AdminDashboardController;
import com.movie.controller.CustomerDashboardController;
import com.movie.controller.LoginController;
import com.movie.controller.RegisterController;
import com.movie.dao.UserDAO;
import com.movie.model.Booking;
import com.movie.model.Showtime;
import com.movie.model.User;
import com.movie.view.AdminDashboardView;
import com.movie.view.CustomerDashboardView;
import com.movie.view.LoginView;
import com.movie.view.ReceiptFrame;
import com.movie.view.RegisterView;
import com.movie.view.SeatMapFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class GenerateArtifacts {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        File dir1 = new File("../readme.files");
        File dir2 = new File("readme.files");
        dir1.mkdirs();
        dir2.mkdirs();

        UserDAO userDAO = new UserDAO();
        User admin = userDAO.findByUsername("admin");
        User customer = userDAO.findByUsername("johndoe");
        if (admin == null) admin = new User(1, "admin", "", "System Administrator", "ADMIN");
        if (customer == null) customer = new User(2, "johndoe", "", "John Doe", "CUSTOMER");

        // 1. Screenshot Login
        LoginView lv = new LoginView();
        new LoginController(lv);
        lv.setSize(440, 360);
        saveComponent(lv, dir1, dir2, "screenshot_login.png");
        lv.dispose();

        // 2. Screenshot Register
        RegisterView rv = new RegisterView();
        new RegisterController(rv);
        rv.setSize(460, 480);
        saveComponent(rv, dir1, dir2, "screenshot_register.png");
        rv.dispose();

        // 3. Admin Movies Tab
        AdminDashboardView av = new AdminDashboardView(admin);
        new AdminDashboardController(av);
        av.setSize(1040, 700);
        av.getTabbedPane().setSelectedIndex(0);
        if (av.getTblMovies().getRowCount() > 0) {
            av.getTblMovies().setRowSelectionInterval(0, 0);
        }
        saveComponent(av, dir1, dir2, "screenshot_admin_movies.png");

        // 4. Admin Halls Tab
        av.getTabbedPane().setSelectedIndex(1);
        if (av.getTblHalls().getRowCount() > 0) {
            av.getTblHalls().setRowSelectionInterval(0, 0);
        }
        saveComponent(av, dir1, dir2, "screenshot_admin_halls.png");

        // 5. Admin Showtimes Tab
        av.getTabbedPane().setSelectedIndex(2);
        if (av.getTblShowtimes().getRowCount() > 0) {
            av.getTblShowtimes().setRowSelectionInterval(0, 0);
        }
        saveComponent(av, dir1, dir2, "screenshot_admin_showtimes.png");

        // 6. Admin Bookings Tab
        av.getTabbedPane().setSelectedIndex(3);
        if (av.getTblBookings().getRowCount() > 0) {
            av.getTblBookings().setRowSelectionInterval(0, 0);
        }
        saveComponent(av, dir1, dir2, "screenshot_admin_bookings.png");

        // 7. Admin Users Tab
        av.getTabbedPane().setSelectedIndex(4);
        if (av.getTblUsers().getRowCount() > 0) {
            av.getTblUsers().setRowSelectionInterval(0, 0);
        }
        saveComponent(av, dir1, dir2, "screenshot_admin_users.png");
        av.dispose();

        // 8. Customer Browse Tab
        CustomerDashboardView cv = new CustomerDashboardView(customer);
        new CustomerDashboardController(cv);
        cv.setSize(1040, 720);
        cv.getTabbedPane().setSelectedIndex(0);
        if (cv.getTblMovies().getRowCount() > 0) {
            cv.getTblMovies().setRowSelectionInterval(0, 0);
        }
        saveComponent(cv, dir1, dir2, "screenshot_customer_browse.png");

        // 9. Customer Book Tab
        cv.getTabbedPane().setSelectedIndex(1);
        cv.getTxtSeats().setText("A1, A2");
        saveComponent(cv, dir1, dir2, "screenshot_customer_book.png");

        // 10. Customer My Bookings Tab
        cv.getTabbedPane().setSelectedIndex(2);
        if (cv.getTblMyBookings().getRowCount() > 0) {
            cv.getTblMyBookings().setRowSelectionInterval(0, 0);
        }
        saveComponent(cv, dir1, dir2, "screenshot_customer_mybookings.png");
        cv.dispose();

        // 10b. Printable E-Receipt JFrame Screenshot
        Booking sampleBooking = new Booking(1, "BK-10492", 2, 1, "A1, A2", 500.0, "PAID", "2026-11-20 19:30:00");
        sampleBooking.setCustomerName("John Doe");
        sampleBooking.setMovieTitle("Avatar: The Way of Water");
        sampleBooking.setHallName("Cinema 1 (Dolby Atmos)");
        sampleBooking.setShowDate("2026-11-25");
        sampleBooking.setShowTime("19:30:00");

        ReceiptFrame rf = new ReceiptFrame(sampleBooking, customer);
        rf.setSize(520, 600);
        saveComponent(rf, dir1, dir2, "screenshot_receipt_report.png");
        rf.dispose();

        // 10c. Visual Seat Map JFrame Screenshot
        Showtime sampleShowtime = new Showtime(1, 1, 1, "2026-11-24", "17:30:00");
        sampleShowtime.setMovieTitle("Avatar: The Way of Water");
        sampleShowtime.setHallName("Cinema 1 (Standard)");
        sampleShowtime.setMoviePrice(350.00);
        sampleShowtime.setHallTotalSeats(60);
        java.util.List<String> bookedSeats = new java.util.ArrayList<>();
        bookedSeats.add("D1");
        bookedSeats.add("D2");
        bookedSeats.add("E3");
        bookedSeats.add("E4");
        SeatMapFrame smf = new SeatMapFrame(sampleShowtime, bookedSeats, "A1, A2", null);
        smf.setSize(680, 560);
        saveComponent(smf, dir1, dir2, "screenshot_seat_selection.png");
        smf.dispose();

        // 11. Validation Dialog Screenshot
        renderValidationDialog(dir1, dir2);

        // 12. ER Diagram Graphic
        renderERDiagram(dir1, dir2);

        // 13. MVC Architecture Diagram Graphic
        renderMVCDiagram(dir1, dir2);

        System.out.println("ALL ARTIFACTS AND SCREENSHOTS GENERATED SUCCESSFULLY!");
        System.exit(0);
    }

    private static void saveComponent(Window frame, File d1, File d2, String filename) {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        try {
            Thread.sleep(300);
        } catch (InterruptedException ignored) {}

        Component compToPaint = frame;
        String windowTitle = "Cinema Ticketing System";
        if (frame instanceof JFrame) {
            JFrame jf = (JFrame) frame;
            compToPaint = jf.getContentPane();
            if (jf.getTitle() != null && !jf.getTitle().trim().isEmpty()) {
                windowTitle = jf.getTitle().trim();
            }
        }

        int contentW = compToPaint.getWidth();
        int contentH = compToPaint.getHeight();
        if (contentW <= 0) contentW = frame.getWidth();
        if (contentH <= 0) contentH = frame.getHeight();

        int titleBarH = 34;
        int borderW = 1;
        int imgW = contentW + (borderW * 2);
        int imgH = contentH + titleBarH + (borderW * 2);

        BufferedImage img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Window background / outer border
        g2.setColor(new Color(241, 245, 249));
        g2.fillRect(0, 0, imgW, imgH);

        // Modern Title Bar
        GradientPaint gp = new GradientPaint(0, 0, new Color(30, 41, 59), 0, titleBarH, new Color(15, 23, 42));
        g2.setPaint(gp);
        g2.fillRect(0, 0, imgW, titleBarH);

        // macOS-style control dots
        g2.setColor(new Color(239, 68, 68)); // Red
        g2.fillOval(14, 11, 12, 12);
        g2.setColor(new Color(245, 158, 11)); // Amber
        g2.fillOval(32, 11, 12, 12);
        g2.setColor(new Color(34, 197, 94)); // Green
        g2.fillOval(50, 11, 12, 12);

        // Window Title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int tx = Math.max(75, (imgW - fm.stringWidth(windowTitle)) / 2);
        g2.drawString(windowTitle, tx, 22);

        // Paint content pane cleanly
        Graphics2D cg2 = (Graphics2D) g2.create(borderW, titleBarH, contentW, contentH);
        compToPaint.paint(cg2);
        cg2.dispose();

        // Border around whole window
        g2.setColor(new Color(148, 163, 184));
        g2.drawRect(0, 0, imgW - 1, imgH - 1);
        // Border under title bar
        g2.setColor(new Color(51, 65, 85));
        g2.drawLine(0, titleBarH, imgW, titleBarH);

        g2.dispose();
        frame.setVisible(false);
        try {
            ImageIO.write(img, "PNG", new File(d1, filename));
            ImageIO.write(img, "PNG", new File(d2, filename));
            System.out.println("Saved: " + filename + " (size=" + new File(d1, filename).length() + ")");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void renderValidationDialog(File d1, File d2) {
        int w = 500, h = 190;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background
        g.setColor(new Color(248, 250, 252));
        g.fillRect(0, 0, w, h);

        // Title bar
        GradientPaint gp = new GradientPaint(0, 0, new Color(30, 41, 59), 0, 32, new Color(15, 23, 42));
        g.setPaint(gp);
        g.fillRect(0, 0, w, 32);

        // Dots
        g.setColor(new Color(239, 68, 68));
        g.fillOval(12, 10, 11, 11);
        g.setColor(new Color(245, 158, 11));
        g.fillOval(28, 10, 11, 11);
        g.setColor(new Color(34, 197, 94));
        g.fillOval(44, 10, 11, 11);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.drawString("Cinema Ticketing System - Validation Warning", 65, 21);

        // Warning Icon
        g.setColor(new Color(234, 88, 12));
        g.fillOval(32, 58, 44, 44);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g.drawString("!", 49, 90);

        // Message
        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g.drawString("Invalid Input Values Detected", 92, 70);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.setColor(new Color(71, 85, 105));
        g.drawString("Validation error: Duration must be integer and Price must be numeric.", 92, 92);
        g.drawString("Please correct the highlighted fields before submitting to database.", 92, 110);

        // Button
        g.setColor(new Color(226, 232, 240));
        g.fillRoundRect(w / 2 - 45, 138, 90, 32, 6, 6);
        g.setColor(new Color(148, 163, 184));
        g.drawRoundRect(w / 2 - 45, 138, 90, 32, 6, 6);
        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.drawString("OK", w / 2 - 9, 159);

        // Outer border
        g.setColor(new Color(148, 163, 184));
        g.drawRect(0, 0, w - 1, h - 1);

        g.dispose();
        try {
            ImageIO.write(img, "PNG", new File(d1, "screenshot_validation_error.png"));
            ImageIO.write(img, "PNG", new File(d2, "screenshot_validation_error.png"));
            System.out.println("Saved: screenshot_validation_error.png");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void renderERDiagram(File d1, File d2) {
        int w = 1100, h = 650;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background
        g.setColor(new Color(250, 252, 255));
        g.fillRect(0, 0, w, h);

        // Title
        g.setColor(new Color(25, 45, 85));
        g.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g.drawString("Cinema Ticketing System - Entity Relationship Diagram (ERD)", 40, 42);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g.setColor(new Color(100, 110, 130));
        g.drawString("Relational schema design with primary keys, foreign keys, and constraints in MySQL 8.0", 40, 66);

        // Entities:
        drawTableBox(g, 40, 120, 240, 210, "users", new String[]{
                "PK  user_id : INT (AI)",
                "UQ  username : VARCHAR(50)",
                "    password : VARCHAR(255)",
                "    full_name : VARCHAR(100)",
                "    role : ENUM(ADMIN, CUSTOMER)",
                "    created_at : TIMESTAMP"
        });

        drawTableBox(g, 380, 120, 260, 240, "movies", new String[]{
                "PK  movie_id : INT (AI)",
                "    title : VARCHAR(150)",
                "    genre : VARCHAR(50)",
                "    duration : INT",
                "    rating : VARCHAR(10)",
                "    synopsis : TEXT",
                "    price : DECIMAL(10,2)",
                "    status : ENUM(NOW_SHOWING,...)"
        });

        drawTableBox(g, 760, 120, 240, 160, "halls", new String[]{
                "PK  hall_id : INT (AI)",
                "    hall_name : VARCHAR(50)",
                "    total_seats : INT"
        });

        drawTableBox(g, 570, 420, 260, 190, "showtimes", new String[]{
                "PK  showtime_id : INT (AI)",
                "FK  movie_id : INT",
                "FK  hall_id : INT",
                "    show_date : DATE",
                "    show_time : TIME"
        });

        drawTableBox(g, 150, 410, 280, 230, "bookings", new String[]{
                "PK  booking_id : INT (AI)",
                "UQ  booking_code : VARCHAR(20)",
                "FK  user_id : INT",
                "FK  showtime_id : INT",
                "    seats : VARCHAR(255)",
                "    total_amount : DECIMAL(10,2)",
                "    status : ENUM(PENDING,PAID,...)",
                "    booked_at : TIMESTAMP"
        });

        // Connectors (Lines & Cardinality)
        g.setColor(new Color(60, 100, 180));
        g.setStroke(new BasicStroke(2.0f));

        // users -> bookings (1 to Many)
        g.drawLine(160, 330, 160, 420);
        drawCardinality(g, 170, 350, "1");
        drawCardinality(g, 170, 405, "N");

        // movies -> showtimes (1 to Many)
        g.drawLine(510, 360, 630, 420);
        drawCardinality(g, 505, 380, "1");
        drawCardinality(g, 610, 410, "N");

        // halls -> showtimes (1 to Many)
        g.drawLine(860, 280, 750, 420);
        drawCardinality(g, 840, 305, "1");
        drawCardinality(g, 765, 410, "N");

        // showtimes -> bookings (1 to Many)
        g.drawLine(570, 510, 430, 510);
        drawCardinality(g, 550, 500, "1");
        drawCardinality(g, 445, 500, "N");

        g.dispose();
        try {
            ImageIO.write(img, "PNG", new File(d1, "er_diagram.png"));
            ImageIO.write(img, "PNG", new File(d2, "er_diagram.png"));
            System.out.println("Saved: er_diagram.png");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void drawTableBox(Graphics2D g, int x, int y, int w, int h, String title, String[] columns) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, w, h, 8, 8);
        g.setColor(new Color(200, 215, 235));
        g.drawRoundRect(x, y, w, h, 8, 8);

        // Header
        g.setColor(new Color(45, 75, 135));
        g.fillRoundRect(x, y, w, 32, 8, 8);
        g.fillRect(x, y + 20, w, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g.drawString(title.toUpperCase(), x + 14, y + 22);

        // Columns
        g.setFont(new Font("Consolas", Font.PLAIN, 12));
        int cy = y + 54;
        for (String col : columns) {
            if (col.startsWith("PK")) {
                g.setColor(new Color(180, 40, 40));
            } else if (col.startsWith("FK")) {
                g.setColor(new Color(30, 110, 180));
            } else if (col.startsWith("UQ")) {
                g.setColor(new Color(40, 140, 50));
            } else {
                g.setColor(new Color(50, 50, 50));
            }
            g.drawString(col, x + 12, cy);
            cy += 21;
        }
    }

    private static void drawCardinality(Graphics2D g, int x, int y, String label) {
        g.setColor(new Color(20, 40, 80));
        g.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g.drawString(label, x, y);
    }

    private static void renderMVCDiagram(File d1, File d2) {
        int w = 1100, h = 600;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(new Color(250, 252, 255));
        g.fillRect(0, 0, w, h);

        g.setColor(new Color(25, 45, 85));
        g.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g.drawString("Cinema Ticketing System - Architecture & Data Flow", 40, 42);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g.setColor(new Color(100, 110, 130));
        g.drawString("Complete separation of responsibilities: View, Controller, Service & Model, DAO, and MySQL", 40, 66);

        // Tier 1: VIEW (GUI .form & .java)
        drawTierBox(g, 60, 120, 260, 360, "VIEW LAYER (NetBeans GUI)", new Color(230, 242, 255), new Color(40, 100, 180), new String[]{
                "• LoginView (.form / .java)",
                "• RegisterView (.form / .java)",
                "• AdminDashboardView (.form / .java)",
                "  - Movies CRUD Tab",
                "  - Halls CRUD Tab",
                "  - Showtimes Tab",
                "  - Bookings & Sales Tab",
                "  - Users Role Tab",
                "• CustomerDashboardView (.form)",
                "  - Browse Movies Tab",
                "  - Book Tickets Tab",
                "  - My Bookings & E-Tickets"
        });

        // Tier 2: CONTROLLER
        drawTierBox(g, 390, 120, 260, 360, "CONTROLLER LAYER", new Color(245, 235, 255), new Color(110, 50, 180), new String[]{
                "• LoginController",
                "  - Listens to actions",
                "  - Authenticates & routes",
                "• RegisterController",
                "  - Handles user creation",
                "• AdminDashboardController",
                "  - Handles CRUD events",
                "  - Table selection binds",
                "  - Filtering & promotions",
                "• CustomerDashboardController",
                "  - Live seat price calculation",
                "  - Booking & ticket printing"
        });

        // Tier 3: MODEL & SERVICE
        drawTierBox(g, 720, 110, 320, 195, "SERVICE & MODEL LAYER", new Color(235, 255, 245), new Color(30, 140, 70), new String[]{
                "• Services (Business Logic & Rules):",
                "  - MovieService, HallService",
                "  - ShowtimeService, BookingService",
                "  - UserService",
                "• Models (Domain Entities):",
                "  - Movie, Hall, Showtime, Booking, User"
        });

        // Tier 4: DAO & DATABASE
        drawTierBox(g, 720, 365, 320, 185, "DAO & PERSISTENCE LAYER", new Color(255, 245, 235), new Color(190, 90, 20), new String[]{
                "• Data Access Objects (JDBC):",
                "  - MovieDAO, HallDAO, ShowtimeDAO",
                "  - BookingDAO, UserDAO",
                "• PreparedStatement & try-with-resources",
                "• DatabaseConnection (db.properties)",
                "• MySQL Server 8.0 (movie_ticketing)"
        });

        // Draw connecting flow arrows
        g.setStroke(new BasicStroke(2.5f));
        g.setColor(new Color(80, 80, 80));

        // View -> Controller
        drawArrow(g, 320, 260, 390, 260, "User Action");
        drawArrow(g, 390, 300, 320, 300, "Update GUI");

        // Controller -> Service
        drawArrow(g, 650, 200, 720, 200, "Call Service");

        // Service -> DAO
        drawArrow(g, 880, 305, 880, 365, "Query Data");

        g.dispose();
        try {
            ImageIO.write(img, "PNG", new File(d1, "mvc_architecture.png"));
            ImageIO.write(img, "PNG", new File(d2, "mvc_architecture.png"));
            System.out.println("Saved: mvc_architecture.png");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void drawTierBox(Graphics2D g, int x, int y, int w, int h, String title, Color bg, Color border, String[] lines) {
        g.setColor(bg);
        g.fillRoundRect(x, y, w, h, 10, 10);
        g.setColor(border);
        g.setStroke(new BasicStroke(1.8f));
        g.drawRoundRect(x, y, w, h, 10, 10);

        // Header
        g.setColor(border);
        g.fillRoundRect(x, y, w, 34, 10, 10);
        g.fillRect(x, y + 20, w, 14);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g.drawString(title, x + 14, y + 22);

        // Content
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.setColor(new Color(40, 40, 40));
        int cy = y + 56;
        for (String line : lines) {
            if (line.endsWith(":")) {
                g.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g.setColor(border);
            } else {
                g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g.setColor(new Color(50, 50, 50));
            }
            g.drawString(line, x + 14, cy);
            cy += 20;
        }
    }

    private static void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2, String label) {
        g.drawLine(x1, y1, x2, y2);
        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx * dx + dy * dy);
        double xm = D - 8, xn = xm, ym = 5, yn = -5, x;
        double sin = dy / D, cos = dx / D;

        x = xm * cos - ym * sin + x1;
        ym = xm * sin + ym * cos + y1;
        xm = x;

        x = xn * cos - yn * sin + x1;
        yn = xn * sin + yn * cos + y1;
        xn = x;

        int[] xpoints = {x2, (int) xm, (int) xn};
        int[] ypoints = {y2, (int) ym, (int) yn};
        g.fillPolygon(xpoints, ypoints, 3);

        if (label != null) {
            g.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g.setColor(new Color(60, 60, 60));
            if (Math.abs(x1 - x2) < 5) {
                g.drawString(label, x1 + 10, (y1 + y2) / 2 + 4);
            } else {
                g.drawString(label, (x1 + x2) / 2 - 25, (y1 + y2) / 2 - 8);
            }
        }
    }
}
