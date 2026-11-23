-- =====================================================================
-- Movie Ticketing System - Database Export
-- Target DBMS: MySQL 8.0+
-- Database Name: movie_ticketing
-- Description: Complete schema definitions, relationships, constraints,
--              and fictional demonstration records for final project submission.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `movie_ticketing` 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_0900_ai_ci;

USE `movie_ticketing`;

-- Disable foreign key checks for clean teardown and reload
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `bookings`;
DROP TABLE IF EXISTS `showtimes`;
DROP TABLE IF EXISTS `halls`;
DROP TABLE IF EXISTS `movies`;
DROP TABLE IF EXISTS `users`;

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- Table: users
-- Description: Stores system users (Administrators and Customers)
-- ---------------------------------------------------------------------
CREATE TABLE `users` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `full_name` VARCHAR(100) NOT NULL,
  `role` ENUM('CUSTOMER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uq_users_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- Table: movies
-- Description: Catalog of movies with details, ticket price, and status
-- ---------------------------------------------------------------------
CREATE TABLE `movies` (
  `movie_id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(150) NOT NULL,
  `genre` VARCHAR(50) NOT NULL,
  `duration` INT NOT NULL,
  `rating` VARCHAR(10) NOT NULL,
  `synopsis` TEXT,
  `price` DECIMAL(10,2) NOT NULL,
  `status` ENUM('NOW_SHOWING','COMING_SOON') NOT NULL DEFAULT 'NOW_SHOWING',
  PRIMARY KEY (`movie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- Table: halls
-- Description: Cinema screening halls and total seating capacity
-- ---------------------------------------------------------------------
CREATE TABLE `halls` (
  `hall_id` INT NOT NULL AUTO_INCREMENT,
  `hall_name` VARCHAR(50) NOT NULL,
  `total_seats` INT NOT NULL,
  PRIMARY KEY (`hall_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- Table: showtimes
-- Description: Scheduled movie screenings linking movies and halls
-- ---------------------------------------------------------------------
CREATE TABLE `showtimes` (
  `showtime_id` INT NOT NULL AUTO_INCREMENT,
  `movie_id` INT NOT NULL,
  `hall_id` INT NOT NULL,
  `show_date` DATE NOT NULL,
  `show_time` TIME NOT NULL,
  PRIMARY KEY (`showtime_id`),
  KEY `fk_showtimes_movie_idx` (`movie_id`),
  KEY `fk_showtimes_hall_idx` (`hall_id`),
  CONSTRAINT `fk_showtimes_movie` FOREIGN KEY (`movie_id`) 
      REFERENCES `movies` (`movie_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_showtimes_hall` FOREIGN KEY (`hall_id`) 
      REFERENCES `halls` (`hall_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- Table: bookings
-- Description: Customer ticket reservations and payment status
-- ---------------------------------------------------------------------
CREATE TABLE `bookings` (
  `booking_id` INT NOT NULL AUTO_INCREMENT,
  `booking_code` VARCHAR(20) NOT NULL,
  `user_id` INT NOT NULL,
  `showtime_id` INT NOT NULL,
  `seats` VARCHAR(255) NOT NULL,
  `total_amount` DECIMAL(10,2) NOT NULL,
  `status` ENUM('PENDING','PAID','CANCELLED') NOT NULL DEFAULT 'PENDING',
  `booked_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`booking_id`),
  UNIQUE KEY `uq_bookings_code` (`booking_code`),
  KEY `fk_bookings_user_idx` (`user_id`),
  KEY `fk_bookings_showtime_idx` (`showtime_id`),
  CONSTRAINT `fk_bookings_user` FOREIGN KEY (`user_id`) 
      REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_bookings_showtime` FOREIGN KEY (`showtime_id`) 
      REFERENCES `showtimes` (`showtime_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- Fictional Demonstration Data
-- Passwords hashed using SHA-256 via PasswordUtil:
--   'admin123'    -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
--   'password123' -> ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f
-- =====================================================================

-- Users (1 Admin, 2 Customers)
INSERT INTO `users` (`user_id`, `username`, `password`, `full_name`, `role`) VALUES
(1, 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'System Administrator', 'ADMIN'),
(2, 'johndoe', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'John Doe', 'CUSTOMER'),
(3, 'janedoe', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Jane Doe', 'CUSTOMER');

-- Halls
INSERT INTO `halls` (`hall_id`, `hall_name`, `total_seats`) VALUES
(1, 'Cinema 1 (Standard)', 60),
(2, 'Cinema 2 (IMAX Laser)', 100),
(3, 'Cinema 3 (VIP Premiere)', 40);

-- Movies
INSERT INTO `movies` (`movie_id`, `title`, `genre`, `duration`, `rating`, `synopsis`, `price`, `status`) VALUES
(1, 'Inception', 'Sci-Fi / Action', 148, 'PG-13', 'A thief who steals corporate secrets through dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.', 350.00, 'NOW_SHOWING'),
(2, 'Interstellar', 'Sci-Fi / Adventure', 169, 'PG-13', 'When Earth becomes uninhabitable in the future, a farmer and ex-NASA pilot is tasked to pilot a spacecraft along with a team of researchers.', 380.00, 'NOW_SHOWING'),
(3, 'The Dark Knight', 'Action / Crime', 152, 'PG-13', 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests.', 320.00, 'NOW_SHOWING'),
(4, 'Avatar: The Way of Water', 'Sci-Fi / Fantasy', 192, 'PG-13', 'Jake Sully lives with his newfound family formed on the extrasolar moon Pandora. Once a familiar threat returns to finish what was previously started, Jake must work with Neytiri.', 400.00, 'NOW_SHOWING'),
(5, 'Dune: Part Two', 'Sci-Fi / Adventure', 166, 'PG-13', 'Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family.', 380.00, 'NOW_SHOWING'),
(6, 'Spider-Man: Beyond the Spider-Verse', 'Animation / Action', 140, 'PG', 'Miles Morales catapults across the Multiverse to protect its very existence alongside Gwen Stacy and an elite squad of Spider-Heroes.', 350.00, 'COMING_SOON');

-- Showtimes
INSERT INTO `showtimes` (`showtime_id`, `movie_id`, `hall_id`, `show_date`, `show_time`) VALUES
(1, 1, 1, '2026-11-23', '13:00:00'),
(2, 1, 2, '2026-11-23', '16:30:00'),
(3, 2, 2, '2026-11-23', '19:45:00'),
(4, 3, 1, '2026-11-24', '14:00:00'),
(5, 4, 2, '2026-11-24', '17:30:00'),
(6, 5, 3, '2026-11-24', '20:15:00');

-- Bookings
INSERT INTO `bookings` (`booking_id`, `booking_code`, `user_id`, `showtime_id`, `seats`, `total_amount`, `status`, `booked_at`) VALUES
(1, 'BK-1001', 2, 1, 'A1, A2', 700.00, 'PAID', '2026-11-20 10:15:00'),
(2, 'BK-1002', 2, 3, 'C4, C5', 760.00, 'PAID', '2026-11-21 11:30:00'),
(3, 'BK-1003', 3, 2, 'B3, B4', 700.00, 'PENDING', '2026-11-21 14:45:00'),
(4, 'BK-1004', 3, 5, 'D1', 400.00, 'CANCELLED', '2026-11-21 16:00:00');

-- Reset Auto Increment values to prevent collision
ALTER TABLE `users` AUTO_INCREMENT = 10;
ALTER TABLE `movies` AUTO_INCREMENT = 10;
ALTER TABLE `halls` AUTO_INCREMENT = 10;
ALTER TABLE `showtimes` AUTO_INCREMENT = 10;
ALTER TABLE `bookings` AUTO_INCREMENT = 1005;
