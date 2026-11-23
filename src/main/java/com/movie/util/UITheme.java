package com.movie.util;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * UI Theme utility — colors stripped for manual customization.
 * Only Cinema Seat Selection Colors are retained (used by SeatMapFrame).
 * Status colors are retained for data-driven status display in ReceiptFrame.
 * All design color constants and helper methods have been removed.
 */
public class UITheme {

    private static final Properties THEME_PROPS = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        File[] candidates = new File[] {
            new File("theme.properties"),
            new File("MovieTicketing/theme.properties"),
            new File("db.properties"),
            new File("MovieTicketing/db.properties")
        };

        for (File f : candidates) {
            if (f.exists() && f.isFile()) {
                try (InputStream in = new FileInputStream(f)) {
                    THEME_PROPS.load(in);
                    break;
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static Color parseColor(String key, Color fallback) {
        String val = THEME_PROPS.getProperty(key);
        if (val == null || val.trim().isEmpty()) {
            return fallback;
        }
        try {
            val = val.trim();
            if (val.startsWith("#")) {
                val = val.substring(1);
            }
            return new Color(Integer.parseInt(val, 16));
        } catch (Exception e) {
            return fallback;
        }
    }

    // --- Cinema Seat Selection Colors (Green, Yellow, Red) ---
    public static final Color SEAT_AVAILABLE = parseColor("theme.seat.available", new Color(46, 125, 50));   // Green (#2E7D32)
    public static final Color SEAT_SELECTED  = parseColor("theme.seat.selected",  new Color(245, 158, 11));  // Yellow/Amber (#F59E0B)
    public static final Color SEAT_BOOKED    = parseColor("theme.seat.booked",    new Color(239, 68, 68));   // Red (#EF4444)
    public static final Color SEAT_SCREEN_BG = parseColor("theme.seat.screen",    new Color(71, 85, 105));   // Slate Dark

    // --- Status Colors (preserved — used by ReceiptFrame.populateData) ---
    public static final Color STATUS_PAID      = parseColor("theme.status.paid",      new Color(16, 185, 129));
    public static final Color STATUS_PENDING   = parseColor("theme.status.pending",   new Color(245, 158, 11));
    public static final Color STATUS_CANCELLED = parseColor("theme.status.cancelled", new Color(239, 68, 68));

    // --- Standard Typography (preserved) ---
    public static final Font FONT_TITLE        = new Font("Segoe UI", Font.BOLD,  18);
    public static final Font FONT_SUBTITLE     = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_HEADER_LABEL = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_BODY         = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON       = new Font("Segoe UI", Font.BOLD,  12);

    /**
     * Resolves the appropriate status color based on booking payment status.
     * Used by ReceiptFrame to display payment state.
     */
    public static Color getStatusColor(String status) {
        if (status == null) {
            return STATUS_PENDING;
        }
        switch (status.trim().toUpperCase()) {
            case "PAID":      return STATUS_PAID;
            case "CANCELLED": return STATUS_CANCELLED;
            case "PENDING":
            default:          return STATUS_PENDING;
        }
    }
}
