/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton database connection manager supporting external configuration
 * via db.properties with fallback to standard default credentials.
 * 
 * Architectural Connection in System:
 * - Layer: Infrastructure / Utility Layer (com.movie.util)
 * - Consumer: Called by all Data Access Objects (UserDAO, MovieDAO, HallDAO, ShowtimeDAO, BookingDAO).
 * - Pattern: Thread-safe Singleton managing the active java.sql.Connection instance to MySQL.
 */
public class DatabaseConnection {

    private static String host = "localhost";
    private static String port = "3306";
    private static String dbName = "movie_ticketing";
    private static String user = "root";
    private static String password = "12345";
    private static String useSSL = "false";
    private static String serverTimezone = "UTC";

    private static DatabaseConnection instance;
    private Connection connection;

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        Properties props = new Properties();
        File[] candidateFiles = new File[]{
            new File("db.properties"),
            new File("MovieTicketing/db.properties"),
            new File("../db.properties")
        };

        boolean loaded = false;
        for (File f : candidateFiles) {
            if (f.exists() && f.isFile()) {
                try (InputStream in = new FileInputStream(f)) {
                    props.load(in);
                    loaded = true;
                    break;
                } catch (Exception ignored) {
                }
            }
        }

        if (!loaded) {
            try (InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (in != null) {
                    props.load(in);
                    loaded = true;
                }
            } catch (Exception ignored) {
            }
        }

        if (loaded) {
            if (props.getProperty("db.host") != null) host = props.getProperty("db.host").trim();
            if (props.getProperty("db.port") != null) port = props.getProperty("db.port").trim();
            if (props.getProperty("db.name") != null) dbName = props.getProperty("db.name").trim();
            if (props.getProperty("db.username") != null) user = props.getProperty("db.username").trim();
            if (props.getProperty("db.password") != null) password = props.getProperty("db.password").trim();
            if (props.getProperty("db.useSSL") != null) useSSL = props.getProperty("db.useSSL").trim();
            if (props.getProperty("db.serverTimezone") != null) serverTimezone = props.getProperty("db.serverTimezone").trim();
        }
    }

    private static String getJdbcUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?useSSL=" + useSSL + "&serverTimezone=" + serverTimezone
                + "&allowPublicKeyRetrieval=true";
    }

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(getJdbcUrl(), user, password);
            System.out.println("[DB] Connected to " + dbName + " at " + host + ":" + port);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found in classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to MySQL database: " + e.getMessage(), e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        try {
            if (instance == null || instance.connection == null || instance.connection.isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(getJdbcUrl(), user, password);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Re-opening connection failed: " + e.getMessage(), e);
        }
        return connection;
    }

    public static boolean testConnection() {
        try {
            return getInstance().getConnection() != null && !getInstance().getConnection().isClosed();
        } catch (Exception e) {
            System.err.println("[DB Test Failed] " + e.getMessage());
            return false;
        }
    }

    public static String getHost() { return host; }
    public static String getPort() { return port; }
    public static String getDbName() { return dbName; }
    public static String getUser() { return user; }
}
