# Cinema Ticketing System — Comprehensive Line-by-Line & Word-by-Word Code Explanation Guide

> Academic Course / Degree Program:** BS Information Technology 
> Academic Section: 2B-NT Irregular
>Project Title: Cinema Ticketing System**  
> Authors & Roles:  
> • Princes Zanica Rai Montanez — Programmer (Lead Developer)  
> • Rachelle Ann Talega — UI/UX Designer  
> • Charmie Amador — Tester (Quality Assurance)  
> • Asiah Leann Greeze Arroyo — Database (Database Architect)  
> • Jhana Rose Espedion — Documentation (Technical Writer)  

Table of Contents

1. [Foundational Java & OOP Glossary (Word-by-Word Meaning of Keywords)](#1-foundational-java--oop-glossary-word-by-word-meaning-of-keywords)
2. [Database Layer Line-by-Line Explanation (`database/project_database.sql`)](#2-database-layer-line-by-line-explanation-databaseproject_databasesql)
3. [Application Bootstrap Entry Point (`MovieTicketing.java`)](#3-application-bootstrap-entry-point-movieticketingjava)
4. [Utility & Infrastructure Layer](#4-utility--infrastructure-layer)
   - [4.1 DatabaseConnection.java](#41-databaseconnectionjava)
   - [4.2 PasswordUtil.java](#42-passwordutiljava)
   - [4.3 UITheme.java](#43-uithemejava)
5. [Model Layer (Domain POJO Entities)](#5-model-layer-domain-pojo-entities)
   - [5.1 User.java](#51-userjava)
   - [5.2 Movie.java](#52-moviejava)
   - [5.3 Hall.java](#53-halljava)
   - [5.4 Showtime.java](#54-showtimejava)
   - [5.5 Booking.java](#55-bookingjava)
6. [Data Access Object (DAO) Layer](#6-data-access-object-dao-layer)
   - [6.1 UserDAO.java](#61-userdaojava)
   - [6.2 MovieDAO.java](#62-moviedaojava)
   - [6.3 HallDAO.java](#63-halldaojava)
   - [6.4 ShowtimeDAO.java](#64-showtimedaojava)
   - [6.5 BookingDAO.java](#65-bookingdaojava)
7. [Service & Business Logic Layer](#7-service--business-logic-layer)
   - [7.1 UserService.java](#71-userservicejava)
   - [7.2 MovieService.java](#72-movieservicejava)
   - [7.3 HallService.java](#73-hallservicejava)
   - [7.4 ShowtimeService.java](#74-showtimeservicejava)
   - [7.5 BookingService.java](#75-bookingservicejava)
8. [Controller Layer](#8-controller-layer)
   - [8.1 LoginController.java](#81-logincontrollerjava)
   - [8.2 RegisterController.java](#82-registercontrollerjava)
   - [8.3 AdminDashboardController.java](#83-admindashboardcontrollerjava)
   - [8.4 CustomerDashboardController.java](#84-customerdashboardcontrollerjava)
9. [Presentation & View Layer (NetBeans GUI Forms)](#9-presentation--view-layer-netbeans-gui-forms)
   - [9.1 LoginView.java & RegisterView.java](#91-loginviewjava--registerviewjava)
   - [9.2 AdminDashboardView.java & CustomerDashboardView.java](#92-admindashboardviewjava--customerdashboardviewjava)
   - [9.3 SeatMapFrame.java (Interactive Visual Seat Matrix)](#93-seatmapframejava-interactive-visual-seat-matrix)
   - [9.4 ReceiptFrame.java (Official E-Ticket & Printing Engine)](#94-receiptframejava-official-e-ticket--printing-engine)
10. [Oral Defense Preparation Guide & Role-Based Questions](#10-oral-defense-preparation-guide--role-based-questions)

---

## 1. Foundational Java & OOP Glossary (Word-by-Word Meaning of Keywords)

To ensure that our teacher and any reader understands every single keyword used in this project, here is an exact word-by-word explanation of Java and SQL fundamentals:

### Java Keywords
- **`package`**: Declares the folder/namespace in which a Java class resides (e.g., `package com.movie.service;`). It organizes code and avoids class naming collisions.
- **`import`**: Instructs the Java compiler to bring in classes from external packages (e.g., `import java.sql.Connection;`) so we can refer to them by their simple name instead of typing the full package path every time.
- **`public`**: An access modifier meaning the class, variable, or method can be accessed from any other class in the entire application.
- **`private`**: An access modifier enforcing **encapsulation**. The variable or method can ONLY be accessed within the exact class where it is declared.
- **`protected`**: An access modifier allowing access only within the same package or subclasses.
- **`class`**: The fundamental blueprint in object-oriented programming defining the fields (data) and methods (behavior) of an object.
- **`interface`**: A contract or blueprint of abstract methods that other classes must implement (e.g., `SeatConfirmListener`).
- **`implements`**: Used when a class agrees to fulfill the contract defined by an interface.
- **`extends`**: Used for inheritance, indicating that a child class inherits all properties and methods of a parent class (e.g., `public class LoginView extends javax.swing.JFrame`).
- **`static`**: Indicates that the member belongs to the **class itself**, rather than to instances (objects) of the class. Only one copy exists in memory (e.g., `public static void main`).
- **`final`**: When applied to a variable, its value cannot be changed once initialized (constant). When applied to a method, it cannot be overridden. When applied to a class, it cannot be subclassed.
- **`void`**: The return type indicating that a method performs actions but does not return any data value.
- **`new`**: A Java operator that allocates heap memory to instantiate a brand-new object of a class (e.g., `new User()`).
- **`this`**: A reference variable pointing to the current object instance within a class method or constructor.
- **`super`**: A reference variable pointing to the immediate parent (superclass) constructor or methods.
- **`return`**: Terminates the execution of a method and passes a value back to the caller.
- **`try`**: Opens a code block that contains statements that might throw an exception.
- **`catch`**: Catches and handles a specific type of exception if one is thrown inside the `try` block.
- **`finally`**: A block that executes regardless of whether an exception occurred or not (used for cleanup).
- **`throw`**: Explicitly causes an exception to be thrown (e.g., `throw new IllegalArgumentException(...)`).
- **`throws`**: Declared in a method signature indicating that the method might propagate an exception to its caller (e.g., `throws SQLException`).
- **`if` / `else`**: Conditional branch statements executing different code blocks depending on whether a boolean expression evaluates to `true` or `false`.
- **`for`**: A loop construct executing a block of statements repeatedly for a specified count or over a collection.
- **`while`**: A loop that repeats statements as long as a condition remains `true`.
- **`null`**: A literal representing the absence of an object reference.
- **`boolean`**: A primitive data type holding only `true` or `false`.
- **`int`**: A 32-bit signed integer primitive data type.
- **`double`**: A 64-bit double-precision floating-point number primitive data type (used for financial prices).
- **`String`**: A built-in Java class representing immutable sequences of characters.

### Key Java APIs & Classes
- **`List` / `ArrayList`**: An ordered collection interface and its resizable array implementation, allowing dynamic addition of objects.
- **`Set` / `HashSet` / `LinkedHashSet`**: A collection of unique elements that does not allow duplicates. `LinkedHashSet` also preserves the insertion order.
- **`Connection`**: Represents an active, established database session with MySQL.
- **`DriverManager`**: A basic service for managing a set of JDBC drivers, used to obtain a `Connection` to the database.
- **`PreparedStatement`**: A precompiled SQL statement object used to execute parameterized SQL queries safely, completely preventing SQL Injection.
- **`ResultSet`**: A table of data representing a database result set, usually generated by executing a statement that queries the database.
- **`SQLException`**: An exception that provides information on a database access error or other database-related errors.
- **`SwingUtilities.invokeLater()`**: Causes a `Runnable` to be executed asynchronously on the Swing Event Dispatch Thread (EDT), ensuring thread-safe GUI updates.
- **`JOptionPane`**: Standard Swing class for displaying modal dialog boxes (error alerts, information messages, confirmation dialogs).
- **`DefaultTableModel`**: Standard table model storing rows and columns for a Swing `JTable`.

---

## 2. Database Layer Line-by-Line Explanation (`database/project_database.sql`)

The database script `project_database.sql` was engineered by **Asiah Leann Greeze Arroyo (Database Architect)**. Here is the line-by-line explanation of every SQL command:

```sql
1:  CREATE DATABASE IF NOT EXISTS `movie_ticketing` 
2:      DEFAULT CHARACTER SET utf8mb4 
3:      COLLATE utf8mb4_0900_ai_ci;
```
- **Line 1:** `CREATE DATABASE IF NOT EXISTS`: Checks if a database named `movie_ticketing` exists; if not, creates it.
- **Line 2:** `DEFAULT CHARACTER SET utf8mb4`: Configures full UTF-8 Unicode support (supporting 4-byte characters, emojis, and international text).
- **Line 3:** `COLLATE utf8mb4_0900_ai_ci`: Specifies the collation (sorting and comparison rules): `0900` refers to Unicode 9.0 rules, `ai` means accent-insensitive, and `ci` means case-insensitive.

```sql
4:  USE `movie_ticketing`;
5:  SET FOREIGN_KEY_CHECKS = 0;
6:  DROP TABLE IF EXISTS `bookings`;
7:  DROP TABLE IF EXISTS `showtimes`;
8:  DROP TABLE IF EXISTS `halls`;
9:  DROP TABLE IF EXISTS `movies`;
10: DROP TABLE IF EXISTS `users`;
11: SET FOREIGN_KEY_CHECKS = 1;
```
- **Line 4:** `USE`: Sets `movie_ticketing` as the default active database for subsequent queries.
- **Line 5:** `SET FOREIGN_KEY_CHECKS = 0;`: Temporarily disables foreign key constraint checks so that existing tables can be cleanly dropped in any order without foreign key violation errors.
- **Lines 6–10:** `DROP TABLE IF EXISTS`: Drops each table if it already exists, ensuring a fresh installation.
- **Line 11:** `SET FOREIGN_KEY_CHECKS = 1;`: Re-enables foreign key enforcement so all subsequent tables enforce relational integrity.

### The `users` Table
```sql
12: CREATE TABLE `users` (
13:   `user_id` INT NOT NULL AUTO_INCREMENT,
14:   `username` VARCHAR(50) NOT NULL,
15:   `password` VARCHAR(255) NOT NULL,
16:   `full_name` VARCHAR(100) NOT NULL,
17:   `role` ENUM('CUSTOMER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
18:   `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
19:   PRIMARY KEY (`user_id`),
20:   UNIQUE KEY `uq_users_username` (`username`)
21: ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```
- **Line 13:** `user_id`: Primary key integer, automatically increments by 1 for every new user.
- **Line 14:** `username`: Unique username up to 50 characters, cannot be null.
- **Line 15:** `password`: Holds the 64-character SHA-256 cryptographic password hash.
- **Line 16:** `full_name`: Real name of the patron or system administrator.
- **Line 17:** `role`: Restricted by an `ENUM` to only two permissible values: `'CUSTOMER'` or `'ADMIN'`. Defaults to `'CUSTOMER'`.
- **Line 18:** `created_at`: Records registration timestamp using MySQL's `CURRENT_TIMESTAMP`.
- **Line 19:** `PRIMARY KEY (`user_id`)`: Enforces entity uniqueness and creates a clustered B-tree index.
- **Line 20:** `UNIQUE KEY `uq_users_username``: Guarantees no two accounts can ever register with the same username.
- **Line 21:** `ENGINE=InnoDB`: Uses MySQL's ACID-compliant transactional storage engine supporting row-level locking.

### The `movies` Table
```sql
22: CREATE TABLE `movies` (
23:   `movie_id` INT NOT NULL AUTO_INCREMENT,
24:   `title` VARCHAR(150) NOT NULL,
25:   `genre` VARCHAR(50) NOT NULL,
26:   `duration` INT NOT NULL,
27:   `rating` VARCHAR(10) NOT NULL,
28:   `synopsis` TEXT,
29:   `price` DECIMAL(10,2) NOT NULL,
30:   `status` ENUM('NOW_SHOWING','COMING_SOON') NOT NULL DEFAULT 'NOW_SHOWING',
31:   PRIMARY KEY (`movie_id`)
32: ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```
- **Line 26:** `duration`: Running time in minutes (e.g., 192 minutes).
- **Line 27:** `rating`: MPAA certification (G, PG, PG-13, R, NC-17).
- **Line 28:** `synopsis`: Full plot overview stored as `TEXT`.
- **Line 29:** `price`: `DECIMAL(10,2)` guarantees exact financial precision without floating-point rounding errors (10 total digits, 2 decimal places).
- **Line 30:** `status`: Distinguishes active films (`'NOW_SHOWING'`) from upcoming titles (`'COMING_SOON'`).

### The `halls` Table
```sql
33: CREATE TABLE `halls` (
34:   `hall_id` INT NOT NULL AUTO_INCREMENT,
35:   `hall_name` VARCHAR(50) NOT NULL,
36:   `total_seats` INT NOT NULL,
37:   PRIMARY KEY (`hall_id`)
38: ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```
- Stores cinema theaters (e.g., "Cinema 1 (Standard)", "Cinema 2 (IMAX Laser)") and their capacity (e.g., 60 seats).

### The `showtimes` Table
```sql
39: CREATE TABLE `showtimes` (
40:   `showtime_id` INT NOT NULL AUTO_INCREMENT,
41:   `movie_id` INT NOT NULL,
42:   `hall_id` INT NOT NULL,
43:   `show_date` DATE NOT NULL,
44:   `show_time` TIME NOT NULL,
45:   PRIMARY KEY (`showtime_id`),
46:   KEY `fk_showtimes_movie_idx` (`movie_id`),
47:   KEY `fk_showtimes_hall_idx` (`hall_id`),
48:   CONSTRAINT `fk_showtimes_movie` FOREIGN KEY (`movie_id`) 
49:       REFERENCES `movies` (`movie_id`) ON DELETE CASCADE ON UPDATE CASCADE,
50:   CONSTRAINT `fk_showtimes_hall` FOREIGN KEY (`hall_id`) 
51:       REFERENCES `halls` (`hall_id`) ON DELETE CASCADE ON UPDATE CASCADE
52: ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```
- **Lines 41–42:** Foreign keys linking to `movies` and `halls`.
- **Lines 48–51:** `ON DELETE CASCADE ON UPDATE CASCADE`: If a movie or hall is deleted, all its scheduled showtimes are automatically deleted by the database, eliminating orphan rows.

### The `bookings` Table
```sql
53: CREATE TABLE `bookings` (
54:   `booking_id` INT NOT NULL AUTO_INCREMENT,
55:   `booking_code` VARCHAR(20) NOT NULL,
56:   `user_id` INT NOT NULL,
57:   `showtime_id` INT NOT NULL,
58:   `seats` VARCHAR(255) NOT NULL,
59:   `total_amount` DECIMAL(10,2) NOT NULL,
60:   `status` ENUM('PENDING','PAID','CANCELLED') NOT NULL DEFAULT 'PENDING',
61:   `booked_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
62:   PRIMARY KEY (`booking_id`),
63:   UNIQUE KEY `uq_bookings_code` (`booking_code`),
64:   CONSTRAINT `fk_bookings_user` FOREIGN KEY (`user_id`) 
65:       REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
66:   CONSTRAINT `fk_bookings_showtime` FOREIGN KEY (`showtime_id`) 
67:       REFERENCES `showtimes` (`showtime_id`) ON DELETE CASCADE ON UPDATE CASCADE
68: ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```
- **Line 55 & 63:** `booking_code`: Unique reservation ticket code (e.g., `BK-10492`).
- **Line 58:** `seats`: Comma-separated list of reserved seats (e.g., `"A1, A2"`).
- **Line 59:** `total_amount`: Total financial payment for this ticket.
- **Line 60:** `status`: Lifecycle of a booking (`PENDING` $\rightarrow$ `PAID` or `CANCELLED`).

---

## 3. Application Bootstrap Entry Point (`MovieTicketing.java`)

Authored by **Princes Zanica Rai Montanez (Programmer)**, this class initializes the system.

```java
1:  package com.movie;
2:  
3:  import com.movie.controller.LoginController;
4:  import com.movie.util.DatabaseConnection;
5:  import com.movie.view.LoginView;
6:  
7:  import javax.swing.JOptionPane;
8:  import javax.swing.SwingUtilities;
9:  import javax.swing.UIManager;
```
- **Lines 1–9:** Package declaration and imports. Imports the controller (`LoginController`), persistence utility (`DatabaseConnection`), GUI form (`LoginView`), and Swing UI components.

```java
11: /**
12:  * Main application entry point for the Cinema Ticketing System.
...
18:  */
19: public class MovieTicketing {
20: 
21:     public static void main(String[] args) {
```
- **Line 19:** `public class MovieTicketing`: The main class of the project.
- **Line 21:** `public static void main(String[] args)`: The standard JVM entry point. `args` receives any command-line parameters.

```java
22:         // Set modern look and feel
23:         try {
24:             for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
25:                 if ("Nimbus".equals(info.getName())) {
26:                     UIManager.setLookAndFeel(info.getClassName());
27:                     break;
28:                 }
29:             }
30:         } catch (Exception ignored) {
31:         }
```
- **Lines 23–31:** Iterates through all LookAndFeels installed in the JRE. If `"Nimbus"` is found, sets it as the active LookAndFeel for clean, modern vector Swing rendering. The `catch (Exception ignored)` prevents crashes if Nimbus is unavailable.

```java
33:         SwingUtilities.invokeLater(() -> {
34:             // Pre-flight database check
35:             if (!DatabaseConnection.testConnection()) {
36:                 JOptionPane.showMessageDialog(null,
37:                         "Warning: Could not connect to MySQL database (" + DatabaseConnection.getDbName()
38:                                 + " at " + DatabaseConnection.getHost() + ":" + DatabaseConnection.getPort() + ").\n"
39:                                 + "Please ensure MySQL Server is running and credentials in 'db.properties' are correct.\n"
40:                                 + "Refer to README.md for instructions.",
41:                         "Cinema Ticketing System - Database Connection Notice",
42:                         JOptionPane.WARNING_MESSAGE);
43:             }
```
- **Line 33:** `SwingUtilities.invokeLater(() -> { ... });`: Uses a lambda expression to schedule the GUI creation on the Swing Event Dispatch Thread (EDT), guaranteeing thread safety.
- **Lines 35–43:** **Pre-Flight Database Check:** Before showing the login screen, calls `DatabaseConnection.testConnection()`. If MySQL is offline, it displays an informative warning dialog without crashing, telling the teacher or user exactly which host and port failed.

```java
45:             // Connection: View instantiated and handed to Controller
46:             LoginView view = new LoginView();
47:             new LoginController(view);
48:             view.setVisible(true);
49:         });
50:     }
51: }
```
- **Line 46:** `LoginView view = new LoginView();`: Instantiates the NetBeans GUI login form.
- **Line 47:** `new LoginController(view);`: Instantiates the controller, which attaches event listeners to the view's buttons.
- **Line 48:** `view.setVisible(true);`: Displays the login window on screen.

---

## 4. Utility & Infrastructure Layer

### 4.1 DatabaseConnection.java
Implements the **Thread-Safe Singleton Pattern** for database connections.

```java
24: public class DatabaseConnection {
25:     private static String host = "localhost";
26:     private static String port = "3306";
27:     private static String dbName = "movie_ticketing";
28:     private static String user = "root";
29:     private static String password = "12345";
...
34:     private static DatabaseConnection instance;
35:     private Connection connection;
```
- **Lines 25–29:** Default database credentials used as fallbacks if no external file is found.
- **Lines 34–35:** `instance` holds the single static instance of `DatabaseConnection`. `connection` holds the active `java.sql.Connection`.

```java
37:     static {
38:         loadConfiguration();
39:     }
```
- **Line 37:** Static initialization block. Runs once when the class is first loaded into memory, reading external configuration.

```java
41:     private static void loadConfiguration() {
42:         Properties props = new Properties();
43:         File[] candidateFiles = new File[]{
44:             new File("db.properties"),
45:             new File("MovieTicketing/db.properties"),
46:             new File("../db.properties")
47:         };
48:         for (File f : candidateFiles) {
49:             if (f.exists() && f.isFile()) {
50:                 try (InputStream in = new FileInputStream(f)) {
51:                     props.load(in);
...
```
- **Lines 41–55:** Scans for `db.properties` in multiple relative directory locations to support running from root, from the subfolder, or from NetBeans.
- Uses `try-with-resources` (`InputStream in = new FileInputStream(f)`) which automatically closes the file stream when finished.

```java
78:     public static synchronized DatabaseConnection getInstance() {
79:         if (instance == null) {
80:             instance = new DatabaseConnection();
81:         }
82:         return instance;
83:     }
```
- **Line 78:** `synchronized`: Ensures thread-safe instantiation so that even if multiple threads request a connection simultaneously, only one instance is ever created.

```java
85:     public Connection getConnection() {
86:         try {
87:             if (connection == null || connection.isClosed()) {
88:                 String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=%s&serverTimezone=%s",
89:                         host, port, dbName, useSSL, serverTimezone);
90:                 connection = DriverManager.getConnection(url, user, password);
91:             }
92:         } catch (SQLException ex) {
93:             System.err.println("[DB Error] " + ex.getMessage());
94:         }
95:         return connection;
96:     }
```
- **Lines 85–96:** Verifies whether `connection` is valid; if closed or null, re-establishes it via `DriverManager.getConnection(url, user, password)`. Reusing the active connection saves network overhead.

### 4.2 PasswordUtil.java
Provides SHA-256 cryptographic password hashing.

```java
15: public class PasswordUtil {
16: 
17:     public static String hash(String plainPassword) {
18:         if (plainPassword == null) return null;
19:         try {
20:             MessageDigest md = MessageDigest.getInstance("SHA-256");
21:             byte[] digest = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
22:             StringBuilder sb = new StringBuilder();
23:             for (byte b : digest) {
24:                 sb.append(String.format("%02x", b));
25:             }
26:             return sb.toString();
27:         } catch (NoSuchAlgorithmException ex) {
28:             throw new RuntimeException("SHA-256 algorithm not available", ex);
29:         }
30:     }
```
- **Line 20:** `MessageDigest.getInstance("SHA-256")`: Obtains Java's SHA-256 cryptographic algorithm instance.
- **Line 21:** `md.digest(...)`: Computes the 32-byte binary digest of the password in UTF-8.
- **Lines 22–25:** Converts each byte into a 2-character hexadecimal representation (`%02x`), yielding a 64-character hash string.

```java
32:     public static boolean matches(String plainPassword, String hashedPassword) {
33:         if (plainPassword == null || hashedPassword == null) return false;
34:         String computed = hash(plainPassword);
35:         return MessageDigest.isEqual(
36:                 computed.getBytes(StandardCharsets.UTF_8),
37:                 hashedPassword.getBytes(StandardCharsets.UTF_8)
38:         );
39:     }
```
- **Lines 35–38:** `MessageDigest.isEqual()`: Performs a constant-time byte array comparison to prevent side-channel **timing attacks**.

---

## 5. Model Layer (Domain POJO Entities)

The Model layer consists of Plain Old Java Objects (POJOs) representing cinema business entities.

### 5.1 User.java
Represents a system user (`user_id`, `username`, `password`, `full_name`, `role`).
- `isAdmin()`: Returns `true` if `role` equals `"ADMIN"`, used by `LoginController` to route users to the appropriate dashboard.
- `toString()`: Returns `fullName` or `username` for display.

### 5.2 Movie.java
Encapsulates film properties (`movie_id`, `title`, `genre`, `duration`, `rating`, `synopsis`, `price`, `status`).
- Provides full getters and setters to maintain encapsulation.

### 5.3 Hall.java
Encapsulates cinema theaters (`hall_id`, `hall_name`, `total_seats`).
- `toString()`: Returns `hall_name` so it displays naturally when added to a `JComboBox`.

### 5.4 Showtime.java
Links a `Movie` to a `Hall` at a specific `show_date` and `show_time`.
- Includes relational helper fields: `movieTitle`, `hallName`, `moviePrice`, and `hallTotalSeats`. These fields allow `ShowtimeDAO` to return rich join data directly to the controller without requiring separate queries.

### 5.5 Booking.java
Encapsulates customer ticket transactions (`booking_id`, `booking_code`, `user_id`, `showtime_id`, `seats`, `total_amount`, `status`, `booked_at`).
- Holds join fields: `customerName`, `movieTitle`, `hallName`, `showDate`, `showTime`.
- Passed directly to `ReceiptFrame` to render printable official receipts.

---

## 6. Data Access Object (DAO) Layer

Authored by **Asiah Leann Greeze Arroyo (Database)** and **Princes Zanica Rai Montanez (Programmer)**, this layer isolates all JDBC database queries.

### 6.1 Parameterized SQL Queries & Security Against SQL Injection
In all DAOs, queries use `PreparedStatement` with `?` placeholders instead of string concatenation.
- **Vulnerable Concatenation (DO NOT USE):** `"SELECT * FROM users WHERE username = '" + user + "'"`
- **Secure Prepared Statement (USED IN OUR SYSTEM):**
  ```java
  String sql = "SELECT * FROM users WHERE username = ?";
  PreparedStatement ps = conn.prepareStatement(sql);
  ps.setString(1, username);
  ```
  The database driver treats the parameter strictly as literal data, preventing malicious SQL code injection.

### 6.2 BookingDAO.java — Critical Methods
```java
public List<String> getBookedSeats(int showtimeId) {
    List<String> list = new ArrayList<>();
    String sql = "SELECT seats FROM bookings WHERE showtime_id = ? AND status != 'CANCELLED'";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, showtimeId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String seatStr = rs.getString("seats");
                if (seatStr != null) {
                    for (String s : seatStr.split("[,; ]+")) {
                        String t = s.trim().toUpperCase();
                        if (!t.isEmpty() && !list.contains(t)) {
                            list.add(t);
                        }
                    }
                }
            }
        }
    } catch (SQLException ex) { ... }
    return list;
}
```
- **Explanation:** Queries all active bookings (`status != 'CANCELLED'`) for a given showtime. Splits comma-separated seat strings (e.g., `"A1, A2"`) into individual tokens and aggregates them. This is called by `BookingService` and `SeatMapFrame` to identify which seats are unavailable.

```java
public double calculateTotalSales(String fromDate, String toDate) {
    String sql = "SELECT SUM(b.total_amount) AS total FROM bookings b " +
                 "JOIN showtimes s ON b.showtime_id = s.showtime_id " +
                 "WHERE b.status = 'PAID'";
    ...
}
```
- **Explanation:** Aggregates real-time financial box-office sales. Computes the sum of all paid bookings, with optional date filtering for daily or weekly managerial revenue audits.

---

## 7. Service & Business Logic Layer

Authored by **Princes Zanica Rai Montanez (Programmer)**, the Service layer encapsulates business constraints, input validations, and error prevention.

### 7.1 BookingService.java — Seat Conflict Prevention Algorithm
```java
42: public Booking createBooking(int userId, int showtimeId, String seatsRaw, double unitPrice) {
...
54:     String[] tokens = seatsRaw.split("[,; ]+");
55:     List<String> cleanSeats = new ArrayList<>();
56:     for (String t : tokens) {
57:         String s = t.trim().toUpperCase();
58:         if (!s.isEmpty()) {
59:             if (!cleanSeats.contains(s)) {
60:                 cleanSeats.add(s);
61:             }
62:         }
63:     }
...
70:     List<String> alreadyBooked = bookingDAO.getBookedSeats(showtimeId);
71:     List<String> conflicting = new ArrayList<>();
72:     for (String seat : cleanSeats) {
73:         if (alreadyBooked.contains(seat)) {
74:             conflicting.add(seat);
75:         }
76:     }
77: 
78:     if (!conflicting.isEmpty()) {
79:         throw new IllegalStateException("Seat(s) already booked: " + String.join(", ", conflicting)
80:                 + ". Please choose different seats.");
81:     }
```
- **Lines 54–63:** Normalizes seat text, splits tokens by commas, semicolons, or spaces, converts to uppercase, and removes duplicate tokens in the user's input.
- **Lines 70–76:** Fetches currently booked seats from `BookingDAO`. Loops through each requested seat and checks `alreadyBooked.contains(seat)`. If any conflict is found, it is added to `conflicting`.
- **Lines 78–81:** If conflicts exist, throws an `IllegalStateException` listing the exact conflicting seats, preventing duplicate booking.

---

## 8. Controller Layer

The Controller layer links the GUI Views to the Services and Models.

### 8.1 LoginController.java
- Listens to the **Login** button click via `ActionListener`.
- Extracts `txtUsername.getText()` and `new String(txtPassword.getPassword())`.
- Validates that neither field is empty.
- Calls `userService.authenticate(username, password)`.
- If authentication succeeds:
  - If `user.isAdmin()`, instantiates `AdminDashboardView` and binds `AdminDashboardController`.
  - Else, instantiates `CustomerDashboardView` and binds `CustomerDashboardController`.
  - Calls `view.dispose()` to close the login window and `dashboard.setVisible(true)` to open the portal.
- If authentication fails, displays `JOptionPane.showMessageDialog(view, "Invalid username or password", ...)` and clears the password field.

### 8.2 AdminDashboardController.java
- Binds action listeners to all Admin buttons across 5 tabs:
  1. **Movies CRUD:** Add Movie, Update Movie, Delete Movie, Search Movies, Clear.
  2. **Halls CRUD:** Add Hall, Update Hall, Delete Hall, Refresh.
  3. **Showtimes CRUD:** Add Showtime, Update Showtime, Delete Showtime.
  4. **Bookings & Reports:** Filter by Date, Mark as Paid, Cancel Booking, Refresh Sales Total.
  5. **Users Management:** Promote to Admin, Demote to Customer, Delete Account.
- Listens to table row clicks via `ListSelectionListener` to populate the input text fields with the selected record's data.

### 8.3 CustomerDashboardController.java
- **Browse Tab:** Shows available movies; typing in the search box filters the list in real-time. Selecting a movie loads its showtimes in the schedule table below.
- **Book Tickets Tab:** Selecting a showtime updates the available seats label. Clicking **"View & Select Seats (Visual Map)"** opens `SeatMapFrame`.
- **My Bookings Tab:** Displays the customer's reservation history. Clicking **"Pay Now"** updates `PENDING` $\rightarrow$ `PAID`. Clicking **"Print / View E-Receipt"** opens `ReceiptFrame`.

---

## 9. Presentation & View Layer (NetBeans GUI Forms)

Designed by **Rachelle Ann Talega (UI/UX Designer)** using Apache NetBeans 25 Matisse GUI Builder.

### 9.1 SeatMapFrame.java — Visual Interactive Cinema Seat Matrix
```java
public class SeatMapFrame extends javax.swing.JFrame {
    private static final Color COLOR_AVAILABLE = UITheme.SEAT_AVAILABLE; // Green
    private static final Color COLOR_BOOKED    = UITheme.SEAT_BOOKED;    // Red
    private static final Color COLOR_SELECTED  = UITheme.SEAT_SELECTED;  // Yellow
```
- **Lines 35–37:** Distinct color states defined in `UITheme`.
- Dynamically iterates over 60 seat buttons (`A1` through `F10`):
  - If `bookedSeats.contains(seatName)`, the button is colored **Red** (using `UITheme.SEAT_BOOKED`), marked unavailable, and prevented from being selected. Clicking an already booked seat displays a clear warning alert that the seat is already reserved.
  - If the seat is available, the button is colored **Green**.
  - Clicking an available seat toggles it to **Yellow (Selected)**, adds it to `selectedSeatSet`, and recalculates the total price live.
  - Clicking **"Confirm Seats"** invokes `SeatConfirmListener.onSeatsConfirmed(csv, count, total)` and closes the picker.

### 9.2 ReceiptFrame.java — Official Printable E-Ticket
- NetBeans GUI form that displays ticket reference code, movie title, hall name, screening date/time, seat numbers, price breakdown, and payment status.
- **Printing Feature:**
  ```java
  PrinterJob job = PrinterJob.getPrinterJob();
  job.setJobName("Cinema Ticket - " + booking.getBookingCode());
  job.setPrintable((graphics, pageFormat, pageIndex) -> {
      if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
      Graphics2D g2d = (Graphics2D) graphics;
      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      getContentPane().paint(g2d);
      return Printable.PAGE_EXISTS;
  });
  if (job.printDialog()) job.print();
  ```
  Uses the standard Java AWT Print API (`PrinterJob`). Opens the native OS printer dialog, allowing the patron to print directly to a physical receipt printer or export to **Microsoft Print to PDF**.
- **Text File Export:** Includes a **"Save Text (.txt)"** button that writes the formatted ticket receipt directly to a `.txt` file using `FileWriter`.

---

## 10. Oral Defense Preparation Guide & Role-Based Questions

To help our group ace our oral defense, here are targeted questions the instructor may ask, along with our prepared answers:

### For Princes Zanica Rai Montanez (Programmer)
- **Q: Why did you separate the code into Model, View, Controller, and Service layers instead of writing everything inside NetBeans form event handlers?**  
  *Answer:* Putting SQL queries or business logic inside GUI event handlers leads to "spaghetti code," making the system fragile, hard to test, and difficult to maintain. By applying MVC, our `View` only handles user display, `Controller` manages user interactions, `Service` enforces business rules and validation, `Model` encapsulates data, and `DAO` handles database access.
- **Q: How does your system prevent two customers from booking the same seat simultaneously?**  
  *Answer:* In `BookingService.createBooking()`, before saving a reservation, we query `BookingDAO.getBookedSeats(showtimeId)` for all active bookings. We compare the user's normalized seat selection against already booked seats. If any overlap is found, we throw an `IllegalStateException` and block the transaction before any SQL insert can occur.

### For Rachelle Ann Talega (UI/UX Designer)
- **Q: How does the interactive Seat Map communicate with the Customer Dashboard?**  
  *Answer:* We implemented the Callback / Observer pattern using a custom Java interface: `SeatConfirmListener`. When the customer clicks "Confirm Seats" in `SeatMapFrame`, the frame invokes `onSeatsConfirmed(selectedSeatsCsv, seatCount, totalAmount)`. `CustomerDashboardController` receives this callback, automatically populates the seat text field, and recalculates the total price.

### For Charmie Amador (Tester)
- **Q: What edge cases and boundary conditions did you test?**  
  *Answer:* We tested non-numeric durations, negative movie ticket prices (`-50.00`), invalid date strings (`2026/11/25` with slashes instead of hyphens), duplicate username registrations, and seat collision tests. All boundary tests are codified in `SystemVerificationTest.java`, where all 9 automated test suites pass with 100% success.

### For Asiah Leann Greeze Arroyo (Database)
- **Q: How do foreign key constraints with `ON DELETE CASCADE` protect data integrity?**  
  *Answer:* In our schema, `showtimes` references `movies(movie_id)` and `halls(hall_id)` with `ON DELETE CASCADE`. If an administrator removes a movie from the catalog, MySQL automatically cleans up all associated showtimes and bookings. This guarantees referential integrity and prevents orphaned records.

### For Jhana Rose Espedion (Documentation)
- **Q: What documentation artifacts did you prepare for this submission?**  
  *Answer:* I compiled the complete project documentation, including the high-level academic README, visual ER diagrams, MVC architecture flowcharts, high-resolution captioned screenshots of all 13 system screens, the 18-point QA test verification matrix, and this line-by-line code explanation handbook.

---

**End of Code Explanation Guide.**  
*Section 2B-NT Irregular — Cinema Ticketing System (2026)*
