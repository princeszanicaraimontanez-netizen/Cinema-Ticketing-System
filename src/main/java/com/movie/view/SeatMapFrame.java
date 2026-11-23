/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.movie.view;

import com.movie.model.Showtime;
import com.movie.util.UITheme;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * NetBeans GUI Builder JFrame Form for Visual Cinema Seat Selection.
 * Allows the customer to interactively click and pick available seats.
 * All cinema seat buttons are visually defined in NetBeans GUI Builder.
 * 
 * Architectural Connection in System:
 * - Layer: Presentation / View Layer (com.movie.view)
 * - Triggered By: CustomerDashboardController when the customer initiates seat selection.
 * - Callbacks: Notifies CustomerDashboardController via SeatConfirmListener on confirmation.
 * - Dynamic Styling: Seat colors (Available = Light Blue, Selected = Lavender, Booked = Soft Red)
 *   are dynamically supplied via UITheme and theme.properties without hard-coding.
 */
public class SeatMapFrame extends javax.swing.JFrame {

    // Distinct seat colors dynamically managed by UITheme (no hard-coding)
    private static final Color COLOR_AVAILABLE = UITheme.SEAT_AVAILABLE;
    private static final Color COLOR_BOOKED    = UITheme.SEAT_BOOKED;
    private static final Color COLOR_SELECTED  = UITheme.SEAT_SELECTED;

    private Showtime showtime;
    private final Set<String> bookedSeatSet = new HashSet<>();
    private final Set<String> selectedSeatSet = new LinkedHashSet<>();
    private JButton[] allSeatButtons;
    private SeatConfirmListener confirmListener;

    public interface SeatConfirmListener {
        void onSeatsConfirmed(String selectedSeatsCsv, int seatCount, double totalAmount);
    }

    /**
     * Creates new form SeatMapFrame
     */
    public SeatMapFrame() {
        initComponents();
        initSeatButtons();
        initCustomEvents();
        applyCustomTheme();
        setLocationRelativeTo(null);
    }

    public SeatMapFrame(Showtime showtime, List<String> bookedSeats, String initialSelected, SeatConfirmListener listener) {
        initComponents();
        this.showtime = showtime;
        this.confirmListener = listener;

        if (bookedSeats != null) {
            for (String s : bookedSeats) {
                if (s != null && !s.trim().isEmpty()) {
                    bookedSeatSet.add(s.trim().toUpperCase());
                }
            }
        }

        if (initialSelected != null && !initialSelected.trim().isEmpty()) {
            String[] tokens = initialSelected.split("[,; ]+");
            for (String t : tokens) {
                String id = t.trim().toUpperCase();
                if (!id.isEmpty() && !bookedSeatSet.contains(id)) {
                    selectedSeatSet.add(id);
                }
            }
        }

        initSeatButtons();
        initCustomEvents();
        applyCustomTheme();
        loadShowtimeInfo();
        applySeatStatus();
        updateSummary();
        setLocationRelativeTo(null);
    }

    /**
     * Applies centralized theme styling (Clean, Light Blue, Lavender)
     * without hard-coding color literals.
     */
    private void applyCustomTheme() {
        // Seat map colors (Green, Yellow, Red)
        pnlScreen.setBackground(UITheme.SEAT_SCREEN_BG);
        lblLegendAvailable.setForeground(UITheme.SEAT_AVAILABLE);
        lblLegendAvailable.setText("■ Available (Green)");
        lblLegendSelected.setForeground(UITheme.SEAT_SELECTED);
        lblLegendSelected.setText("■ Selected (Yellow)");
        lblLegendBooked.setForeground(UITheme.SEAT_BOOKED);
        lblLegendBooked.setText("■ Booked (Red)");
    }

    private void initSeatButtons() {
        allSeatButtons = new JButton[]{
            btnA1, btnA2, btnA3, btnA4, btnA5, btnA6, btnA7, btnA8, btnA9, btnA10, 
            btnB1, btnB2, btnB3, btnB4, btnB5, btnB6, btnB7, btnB8, btnB9, btnB10, 
            btnC1, btnC2, btnC3, btnC4, btnC5, btnC6, btnC7, btnC8, btnC9, btnC10, 
            btnD1, btnD2, btnD3, btnD4, btnD5, btnD6, btnD7, btnD8, btnD9, btnD10, 
            btnE1, btnE2, btnE3, btnE4, btnE5, btnE6, btnE7, btnE8, btnE9, btnE10, 
            btnF1, btnF2, btnF3, btnF4, btnF5, btnF6, btnF7, btnF8, btnF9, btnF10
        };

        for (JButton btn : allSeatButtons) {
            String seatId = btn.getText();
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener((ActionEvent e) -> toggleSeat(btn, seatId));
        }
    }

    private void initCustomEvents() {
        btnCancel.addActionListener(e -> dispose());
        btnConfirmSeats.addActionListener(e -> handleConfirm());
    }

    private void loadShowtimeInfo() {
        if (showtime != null) {
            lblMovieInfo.setText(String.format("Movie: %s  |  Hall: %s  |  Date: %s %s  |  Ticket: ₱%.2f",
                    showtime.getMovieTitle(), showtime.getHallName(),
                    showtime.getShowDate(), showtime.getShowTime(),
                    showtime.getMoviePrice()));
        }
    }

    private void applySeatStatus() {
        int totalSeats = (showtime != null && showtime.getHallTotalSeats() > 0)
                ? showtime.getHallTotalSeats() : 60;

        for (int i = 0; i < allSeatButtons.length; i++) {
            JButton btn = allSeatButtons[i];
            String seatId = btn.getText();

            // If hall capacity is less than 60, hide the excess buttons
            if (i >= totalSeats) {
                btn.setVisible(false);
                continue;
            }
            btn.setVisible(true);

            if (bookedSeatSet.contains(seatId)) {
                btn.setBackground(COLOR_BOOKED);
                btn.setForeground(Color.WHITE);
                btn.setEnabled(true);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                btn.setToolTipText(seatId + " - Already Booked (Unavailable)");
            } else if (selectedSeatSet.contains(seatId)) {
                btn.setBackground(COLOR_SELECTED);
                btn.setForeground(Color.WHITE);
                btn.setEnabled(true);
                btn.setToolTipText(seatId + " - Selected (Click to deselect)");
            } else {
                btn.setBackground(COLOR_AVAILABLE);
                btn.setForeground(Color.WHITE);
                btn.setEnabled(true);
                btn.setToolTipText(seatId + " - Available (Click to select)");
            }
        }
    }

    private void toggleSeat(JButton btn, String seatId) {
        if (bookedSeatSet.contains(seatId)) {
            JOptionPane.showMessageDialog(this,
                    "Seat " + seatId + " is already booked and unavailable.",
                    "Seat Unavailable", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedSeatSet.contains(seatId)) {
            selectedSeatSet.remove(seatId);
            btn.setBackground(COLOR_AVAILABLE);
            btn.setForeground(Color.WHITE);
            btn.setToolTipText(seatId + " - Available (Click to select)");
        } else {
            selectedSeatSet.add(seatId);
            btn.setBackground(COLOR_SELECTED);
            btn.setForeground(Color.WHITE);
            btn.setToolTipText(seatId + " - Selected (Click to deselect)");
        }
        updateSummary();
    }

    private void updateSummary() {
        int count = selectedSeatSet.size();
        String seats = String.join(", ", selectedSeatSet);
        lblSelectedSeats.setText("Seats Selected (" + count + "): " + (count == 0 ? "None" : seats));

        double price = showtime != null ? showtime.getMoviePrice() : 0.0;
        double total = count * price;
        lblTotalPrice.setText(String.format("Total Amount: ₱%,.2f", total));
    }

    private void handleConfirm() {
        if (selectedSeatSet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one green seat before confirming.",
                    "No Seats Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int count = selectedSeatSet.size();
        String seats = String.join(", ", selectedSeatSet);
        double price = showtime != null ? showtime.getMoviePrice() : 0.0;
        double total = count * price;

        if (confirmListener != null) {
            confirmListener.onSeatsConfirmed(seats, count, total);
        }
        dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblMovieInfo = new javax.swing.JLabel();
        pnlScreen = new javax.swing.JPanel();
        lblScreen = new javax.swing.JLabel();
        scrollSeats = new javax.swing.JScrollPane();
        pnlSeatsGrid = new javax.swing.JPanel();
        btnA1 = new javax.swing.JButton();
        btnA2 = new javax.swing.JButton();
        btnA3 = new javax.swing.JButton();
        btnA4 = new javax.swing.JButton();
        btnA5 = new javax.swing.JButton();
        btnA6 = new javax.swing.JButton();
        btnA7 = new javax.swing.JButton();
        btnA8 = new javax.swing.JButton();
        btnA9 = new javax.swing.JButton();
        btnA10 = new javax.swing.JButton();
        btnB1 = new javax.swing.JButton();
        btnB2 = new javax.swing.JButton();
        btnB3 = new javax.swing.JButton();
        btnB4 = new javax.swing.JButton();
        btnB5 = new javax.swing.JButton();
        btnB6 = new javax.swing.JButton();
        btnB7 = new javax.swing.JButton();
        btnB8 = new javax.swing.JButton();
        btnB9 = new javax.swing.JButton();
        btnB10 = new javax.swing.JButton();
        btnC1 = new javax.swing.JButton();
        btnC2 = new javax.swing.JButton();
        btnC3 = new javax.swing.JButton();
        btnC4 = new javax.swing.JButton();
        btnC5 = new javax.swing.JButton();
        btnC6 = new javax.swing.JButton();
        btnC7 = new javax.swing.JButton();
        btnC8 = new javax.swing.JButton();
        btnC9 = new javax.swing.JButton();
        btnC10 = new javax.swing.JButton();
        btnD1 = new javax.swing.JButton();
        btnD2 = new javax.swing.JButton();
        btnD3 = new javax.swing.JButton();
        btnD4 = new javax.swing.JButton();
        btnD5 = new javax.swing.JButton();
        btnD6 = new javax.swing.JButton();
        btnD7 = new javax.swing.JButton();
        btnD8 = new javax.swing.JButton();
        btnD9 = new javax.swing.JButton();
        btnD10 = new javax.swing.JButton();
        btnE1 = new javax.swing.JButton();
        btnE2 = new javax.swing.JButton();
        btnE3 = new javax.swing.JButton();
        btnE4 = new javax.swing.JButton();
        btnE5 = new javax.swing.JButton();
        btnE6 = new javax.swing.JButton();
        btnE7 = new javax.swing.JButton();
        btnE8 = new javax.swing.JButton();
        btnE9 = new javax.swing.JButton();
        btnE10 = new javax.swing.JButton();
        btnF1 = new javax.swing.JButton();
        btnF2 = new javax.swing.JButton();
        btnF3 = new javax.swing.JButton();
        btnF4 = new javax.swing.JButton();
        btnF5 = new javax.swing.JButton();
        btnF6 = new javax.swing.JButton();
        btnF7 = new javax.swing.JButton();
        btnF8 = new javax.swing.JButton();
        btnF9 = new javax.swing.JButton();
        btnF10 = new javax.swing.JButton();
        pnlLegend = new javax.swing.JPanel();
        lblLegendAvailable = new javax.swing.JLabel();
        lblLegendSelected = new javax.swing.JLabel();
        lblLegendBooked = new javax.swing.JLabel();
        pnlBottom = new javax.swing.JPanel();
        lblSelectedSeats = new javax.swing.JLabel();
        lblTotalPrice = new javax.swing.JLabel();
        btnConfirmSeats = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cinema Ticketing System - Visual Seat Selection");
        setResizable(false);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("CINEMA SEAT SELECTION");

        lblMovieInfo.setForeground(new java.awt.Color(102, 102, 102));
        lblMovieInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMovieInfo.setText("Movie: - | Hall: - | Showtime: - | Price: ₱0.00");

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
            .addComponent(lblMovieInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMovieInfo))
        );

        pnlScreen.setBackground(new java.awt.Color(55, 71, 79));
        pnlScreen.setLayout(new java.awt.GridBagLayout());

        lblScreen.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblScreen.setForeground(new java.awt.Color(220, 235, 245));
        lblScreen.setText("--- C I N E M A   S C R E E N ---");
        pnlScreen.add(lblScreen, new java.awt.GridBagConstraints());

        scrollSeats.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        pnlSeatsGrid.setBackground(new java.awt.Color(26, 32, 44));
        pnlSeatsGrid.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlSeatsGrid.setLayout(new java.awt.GridLayout(6, 10, 6, 6));

        btnA1.setBackground(new java.awt.Color(46, 125, 50));
        btnA1.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA1.setForeground(new java.awt.Color(255, 255, 255));
        btnA1.setText("A1");
        btnA1.setFocusPainted(false);
        btnA1.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnA1.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnA1);

        btnA2.setBackground(new java.awt.Color(46, 125, 50));
        btnA2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA2.setForeground(new java.awt.Color(255, 255, 255));
        btnA2.setText("A2");
        btnA2.setFocusPainted(false);
        btnA2.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnA2.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnA2);

        btnA3.setBackground(new java.awt.Color(46, 125, 50));
        btnA3.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA3.setForeground(new java.awt.Color(255, 255, 255));
        btnA3.setText("A3");
        btnA3.setFocusPainted(false);
        btnA3.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnA3.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnA3);

        btnA4.setBackground(new java.awt.Color(46, 125, 50));
        btnA4.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA4.setForeground(new java.awt.Color(255, 255, 255));
        btnA4.setText("A4");
        btnA4.setFocusPainted(false);
        btnA4.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnA4.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnA4);

        btnA5.setBackground(new java.awt.Color(46, 125, 50));
        btnA5.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA5.setForeground(new java.awt.Color(255, 255, 255));
        btnA5.setText("A5");
        btnA5.setFocusPainted(false);
        btnA5.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnA5.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnA5);

        btnA6.setBackground(new java.awt.Color(46, 125, 50));
        btnA6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA6.setForeground(new java.awt.Color(255, 255, 255));
        btnA6.setText("A6");
        btnA6.setFocusPainted(false);
        btnA6.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnA6.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnA6);

        btnA7.setBackground(new java.awt.Color(46, 125, 50));
        btnA7.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA7.setForeground(new java.awt.Color(255, 255, 255));
        btnA7.setText("A7");
        btnA7.setFocusPainted(false);
        btnA7.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnA7.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnA7);

        btnA8.setBackground(new java.awt.Color(46, 125, 50));
        btnA8.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA8.setForeground(new java.awt.Color(255, 255, 255));
        btnA8.setText("A8");
        btnA8.setFocusPainted(false);
        btnA8.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnA8.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnA8);

        btnA9.setBackground(new java.awt.Color(46, 125, 50));
        btnA9.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA9.setForeground(new java.awt.Color(255, 255, 255));
        btnA9.setText("A9");
        btnA9.setFocusPainted(false);
        btnA9.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnA9.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnA9);

        btnA10.setBackground(new java.awt.Color(46, 125, 50));
        btnA10.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnA10.setForeground(new java.awt.Color(255, 255, 255));
        btnA10.setText("A10");
        btnA10.setFocusPainted(false);
        btnA10.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnA10.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnA10);

        btnB1.setBackground(new java.awt.Color(46, 125, 50));
        btnB1.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB1.setForeground(new java.awt.Color(255, 255, 255));
        btnB1.setText("B1");
        btnB1.setFocusPainted(false);
        btnB1.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnB1.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnB1);

        btnB2.setBackground(new java.awt.Color(46, 125, 50));
        btnB2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB2.setForeground(new java.awt.Color(255, 255, 255));
        btnB2.setText("B2");
        btnB2.setFocusPainted(false);
        btnB2.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnB2.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnB2);

        btnB3.setBackground(new java.awt.Color(46, 125, 50));
        btnB3.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB3.setForeground(new java.awt.Color(255, 255, 255));
        btnB3.setText("B3");
        btnB3.setFocusPainted(false);
        btnB3.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnB3.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnB3);

        btnB4.setBackground(new java.awt.Color(46, 125, 50));
        btnB4.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB4.setForeground(new java.awt.Color(255, 255, 255));
        btnB4.setText("B4");
        btnB4.setFocusPainted(false);
        btnB4.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnB4.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnB4);

        btnB5.setBackground(new java.awt.Color(46, 125, 50));
        btnB5.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB5.setForeground(new java.awt.Color(255, 255, 255));
        btnB5.setText("B5");
        btnB5.setFocusPainted(false);
        btnB5.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnB5.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnB5);

        btnB6.setBackground(new java.awt.Color(46, 125, 50));
        btnB6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB6.setForeground(new java.awt.Color(255, 255, 255));
        btnB6.setText("B6");
        btnB6.setFocusPainted(false);
        btnB6.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnB6.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnB6);

        btnB7.setBackground(new java.awt.Color(46, 125, 50));
        btnB7.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB7.setForeground(new java.awt.Color(255, 255, 255));
        btnB7.setText("B7");
        btnB7.setFocusPainted(false);
        btnB7.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnB7.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnB7);

        btnB8.setBackground(new java.awt.Color(46, 125, 50));
        btnB8.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB8.setForeground(new java.awt.Color(255, 255, 255));
        btnB8.setText("B8");
        btnB8.setFocusPainted(false);
        btnB8.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnB8.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnB8);

        btnB9.setBackground(new java.awt.Color(46, 125, 50));
        btnB9.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB9.setForeground(new java.awt.Color(255, 255, 255));
        btnB9.setText("B9");
        btnB9.setFocusPainted(false);
        btnB9.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnB9.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnB9);

        btnB10.setBackground(new java.awt.Color(46, 125, 50));
        btnB10.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnB10.setForeground(new java.awt.Color(255, 255, 255));
        btnB10.setText("B10");
        btnB10.setFocusPainted(false);
        btnB10.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnB10.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnB10);

        btnC1.setBackground(new java.awt.Color(46, 125, 50));
        btnC1.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC1.setForeground(new java.awt.Color(255, 255, 255));
        btnC1.setText("C1");
        btnC1.setFocusPainted(false);
        btnC1.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnC1.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnC1);

        btnC2.setBackground(new java.awt.Color(46, 125, 50));
        btnC2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC2.setForeground(new java.awt.Color(255, 255, 255));
        btnC2.setText("C2");
        btnC2.setFocusPainted(false);
        btnC2.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnC2.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnC2);

        btnC3.setBackground(new java.awt.Color(46, 125, 50));
        btnC3.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC3.setForeground(new java.awt.Color(255, 255, 255));
        btnC3.setText("C3");
        btnC3.setFocusPainted(false);
        btnC3.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnC3.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnC3);

        btnC4.setBackground(new java.awt.Color(46, 125, 50));
        btnC4.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC4.setForeground(new java.awt.Color(255, 255, 255));
        btnC4.setText("C4");
        btnC4.setFocusPainted(false);
        btnC4.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnC4.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnC4);

        btnC5.setBackground(new java.awt.Color(46, 125, 50));
        btnC5.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC5.setForeground(new java.awt.Color(255, 255, 255));
        btnC5.setText("C5");
        btnC5.setFocusPainted(false);
        btnC5.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnC5.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnC5);

        btnC6.setBackground(new java.awt.Color(46, 125, 50));
        btnC6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC6.setForeground(new java.awt.Color(255, 255, 255));
        btnC6.setText("C6");
        btnC6.setFocusPainted(false);
        btnC6.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnC6.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnC6);

        btnC7.setBackground(new java.awt.Color(46, 125, 50));
        btnC7.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC7.setForeground(new java.awt.Color(255, 255, 255));
        btnC7.setText("C7");
        btnC7.setFocusPainted(false);
        btnC7.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnC7.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnC7);

        btnC8.setBackground(new java.awt.Color(46, 125, 50));
        btnC8.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC8.setForeground(new java.awt.Color(255, 255, 255));
        btnC8.setText("C8");
        btnC8.setFocusPainted(false);
        btnC8.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnC8.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnC8);

        btnC9.setBackground(new java.awt.Color(46, 125, 50));
        btnC9.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC9.setForeground(new java.awt.Color(255, 255, 255));
        btnC9.setText("C9");
        btnC9.setFocusPainted(false);
        btnC9.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnC9.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnC9);

        btnC10.setBackground(new java.awt.Color(46, 125, 50));
        btnC10.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnC10.setForeground(new java.awt.Color(255, 255, 255));
        btnC10.setText("C10");
        btnC10.setFocusPainted(false);
        btnC10.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnC10.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnC10);

        btnD1.setBackground(new java.awt.Color(46, 125, 50));
        btnD1.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD1.setForeground(new java.awt.Color(255, 255, 255));
        btnD1.setText("D1");
        btnD1.setFocusPainted(false);
        btnD1.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD1.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnD1);

        btnD2.setBackground(new java.awt.Color(46, 125, 50));
        btnD2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD2.setForeground(new java.awt.Color(255, 255, 255));
        btnD2.setText("D2");
        btnD2.setFocusPainted(false);
        btnD2.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD2.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnD2);

        btnD3.setBackground(new java.awt.Color(46, 125, 50));
        btnD3.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD3.setForeground(new java.awt.Color(255, 255, 255));
        btnD3.setText("D3");
        btnD3.setFocusPainted(false);
        btnD3.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD3.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnD3);

        btnD4.setBackground(new java.awt.Color(46, 125, 50));
        btnD4.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD4.setForeground(new java.awt.Color(255, 255, 255));
        btnD4.setText("D4");
        btnD4.setFocusPainted(false);
        btnD4.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD4.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnD4);

        btnD5.setBackground(new java.awt.Color(46, 125, 50));
        btnD5.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD5.setForeground(new java.awt.Color(255, 255, 255));
        btnD5.setText("D5");
        btnD5.setFocusPainted(false);
        btnD5.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD5.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnD5);

        btnD6.setBackground(new java.awt.Color(46, 125, 50));
        btnD6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD6.setForeground(new java.awt.Color(255, 255, 255));
        btnD6.setText("D6");
        btnD6.setFocusPainted(false);
        btnD6.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD6.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnD6);

        btnD7.setBackground(new java.awt.Color(46, 125, 50));
        btnD7.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD7.setForeground(new java.awt.Color(255, 255, 255));
        btnD7.setText("D7");
        btnD7.setFocusPainted(false);
        btnD7.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD7.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnD7);

        btnD8.setBackground(new java.awt.Color(46, 125, 50));
        btnD8.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD8.setForeground(new java.awt.Color(255, 255, 255));
        btnD8.setText("D8");
        btnD8.setFocusPainted(false);
        btnD8.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD8.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnD8);

        btnD9.setBackground(new java.awt.Color(46, 125, 50));
        btnD9.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD9.setForeground(new java.awt.Color(255, 255, 255));
        btnD9.setText("D9");
        btnD9.setFocusPainted(false);
        btnD9.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD9.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnD9);

        btnD10.setBackground(new java.awt.Color(46, 125, 50));
        btnD10.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnD10.setForeground(new java.awt.Color(255, 255, 255));
        btnD10.setText("D10");
        btnD10.setFocusPainted(false);
        btnD10.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD10.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnD10);

        btnE1.setBackground(new java.awt.Color(46, 125, 50));
        btnE1.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE1.setForeground(new java.awt.Color(255, 255, 255));
        btnE1.setText("E1");
        btnE1.setFocusPainted(false);
        btnE1.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnE1.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnE1);

        btnE2.setBackground(new java.awt.Color(46, 125, 50));
        btnE2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE2.setForeground(new java.awt.Color(255, 255, 255));
        btnE2.setText("E2");
        btnE2.setFocusPainted(false);
        btnE2.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnE2.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnE2);

        btnE3.setBackground(new java.awt.Color(46, 125, 50));
        btnE3.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE3.setForeground(new java.awt.Color(255, 255, 255));
        btnE3.setText("E3");
        btnE3.setFocusPainted(false);
        btnE3.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnE3.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnE3);

        btnE4.setBackground(new java.awt.Color(46, 125, 50));
        btnE4.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE4.setForeground(new java.awt.Color(255, 255, 255));
        btnE4.setText("E4");
        btnE4.setFocusPainted(false);
        btnE4.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnE4.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnE4);

        btnE5.setBackground(new java.awt.Color(46, 125, 50));
        btnE5.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE5.setForeground(new java.awt.Color(255, 255, 255));
        btnE5.setText("E5");
        btnE5.setFocusPainted(false);
        btnE5.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnE5.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnE5);

        btnE6.setBackground(new java.awt.Color(46, 125, 50));
        btnE6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE6.setForeground(new java.awt.Color(255, 255, 255));
        btnE6.setText("E6");
        btnE6.setFocusPainted(false);
        btnE6.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnE6.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnE6);

        btnE7.setBackground(new java.awt.Color(46, 125, 50));
        btnE7.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE7.setForeground(new java.awt.Color(255, 255, 255));
        btnE7.setText("E7");
        btnE7.setFocusPainted(false);
        btnE7.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnE7.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnE7);

        btnE8.setBackground(new java.awt.Color(46, 125, 50));
        btnE8.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE8.setForeground(new java.awt.Color(255, 255, 255));
        btnE8.setText("E8");
        btnE8.setFocusPainted(false);
        btnE8.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnE8.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnE8);

        btnE9.setBackground(new java.awt.Color(46, 125, 50));
        btnE9.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE9.setForeground(new java.awt.Color(255, 255, 255));
        btnE9.setText("E9");
        btnE9.setFocusPainted(false);
        btnE9.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnE9.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnE9);

        btnE10.setBackground(new java.awt.Color(46, 125, 50));
        btnE10.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnE10.setForeground(new java.awt.Color(255, 255, 255));
        btnE10.setText("E10");
        btnE10.setFocusPainted(false);
        btnE10.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnE10.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnE10);

        btnF1.setBackground(new java.awt.Color(46, 125, 50));
        btnF1.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF1.setForeground(new java.awt.Color(255, 255, 255));
        btnF1.setText("F1");
        btnF1.setFocusPainted(false);
        btnF1.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnF1.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnF1);

        btnF2.setBackground(new java.awt.Color(46, 125, 50));
        btnF2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF2.setForeground(new java.awt.Color(255, 255, 255));
        btnF2.setText("F2");
        btnF2.setFocusPainted(false);
        btnF2.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnF2.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnF2);

        btnF3.setBackground(new java.awt.Color(46, 125, 50));
        btnF3.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF3.setForeground(new java.awt.Color(255, 255, 255));
        btnF3.setText("F3");
        btnF3.setFocusPainted(false);
        btnF3.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnF3.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnF3);

        btnF4.setBackground(new java.awt.Color(46, 125, 50));
        btnF4.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF4.setForeground(new java.awt.Color(255, 255, 255));
        btnF4.setText("F4");
        btnF4.setFocusPainted(false);
        btnF4.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnF4.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnF4);

        btnF5.setBackground(new java.awt.Color(46, 125, 50));
        btnF5.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF5.setForeground(new java.awt.Color(255, 255, 255));
        btnF5.setText("F5");
        btnF5.setFocusPainted(false);
        btnF5.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnF5.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnF5);

        btnF6.setBackground(new java.awt.Color(46, 125, 50));
        btnF6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF6.setForeground(new java.awt.Color(255, 255, 255));
        btnF6.setText("F6");
        btnF6.setFocusPainted(false);
        btnF6.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnF6.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnF6);

        btnF7.setBackground(new java.awt.Color(46, 125, 50));
        btnF7.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF7.setForeground(new java.awt.Color(255, 255, 255));
        btnF7.setText("F7");
        btnF7.setFocusPainted(false);
        btnF7.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnF7.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnF7);

        btnF8.setBackground(new java.awt.Color(46, 125, 50));
        btnF8.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF8.setForeground(new java.awt.Color(255, 255, 255));
        btnF8.setText("F8");
        btnF8.setFocusPainted(false);
        btnF8.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnF8.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnF8);

        btnF9.setBackground(new java.awt.Color(46, 125, 50));
        btnF9.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF9.setForeground(new java.awt.Color(255, 255, 255));
        btnF9.setText("F9");
        btnF9.setFocusPainted(false);
        btnF9.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnF9.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnF9);

        btnF10.setBackground(new java.awt.Color(46, 125, 50));
        btnF10.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnF10.setForeground(new java.awt.Color(255, 255, 255));
        btnF10.setText("F10");
        btnF10.setFocusPainted(false);
        btnF10.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnF10.setPreferredSize(new java.awt.Dimension(48, 28));
        pnlSeatsGrid.add(btnF10);

        scrollSeats.setViewportView(pnlSeatsGrid);

        lblLegendAvailable.setForeground(new java.awt.Color(46, 125, 50));
        lblLegendAvailable.setText("■ Available (Green)");
        pnlLegend.add(lblLegendAvailable);

        lblLegendSelected.setForeground(new java.awt.Color(245, 158, 11));
        lblLegendSelected.setText("■ Selected (Yellow)");
        pnlLegend.add(lblLegendSelected);

        lblLegendBooked.setForeground(new java.awt.Color(239, 68, 68));
        lblLegendBooked.setText("■ Booked (Red)");
        pnlLegend.add(lblLegendBooked);

        lblSelectedSeats.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSelectedSeats.setText("Seats Selected: None");

        lblTotalPrice.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalPrice.setForeground(new java.awt.Color(0, 102, 0));
        lblTotalPrice.setText("Total Amount: ₱0.00");

        btnConfirmSeats.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnConfirmSeats.setText("Confirm Seats");

        btnCancel.setText("Cancel");

        javax.swing.GroupLayout pnlBottomLayout = new javax.swing.GroupLayout(pnlBottom);
        pnlBottom.setLayout(pnlBottomLayout);
        pnlBottomLayout.setHorizontalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBottomLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSelectedSeats, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(btnConfirmSeats, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );
        pnlBottomLayout.setVerticalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBottomLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBottomLayout.createSequentialGroup()
                        .addComponent(lblSelectedSeats)
                        .addGap(4, 4, 4)
                        .addComponent(lblTotalPrice))
                    .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnConfirmSeats, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlScreen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollSeats, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                    .addComponent(pnlLegend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlBottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(pnlScreen, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(scrollSeats, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(pnlLegend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(pnlBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnA1;
    private javax.swing.JButton btnA2;
    private javax.swing.JButton btnA3;
    private javax.swing.JButton btnA4;
    private javax.swing.JButton btnA5;
    private javax.swing.JButton btnA6;
    private javax.swing.JButton btnA7;
    private javax.swing.JButton btnA8;
    private javax.swing.JButton btnA9;
    private javax.swing.JButton btnA10;
    private javax.swing.JButton btnB1;
    private javax.swing.JButton btnB2;
    private javax.swing.JButton btnB3;
    private javax.swing.JButton btnB4;
    private javax.swing.JButton btnB5;
    private javax.swing.JButton btnB6;
    private javax.swing.JButton btnB7;
    private javax.swing.JButton btnB8;
    private javax.swing.JButton btnB9;
    private javax.swing.JButton btnB10;
    private javax.swing.JButton btnC1;
    private javax.swing.JButton btnC2;
    private javax.swing.JButton btnC3;
    private javax.swing.JButton btnC4;
    private javax.swing.JButton btnC5;
    private javax.swing.JButton btnC6;
    private javax.swing.JButton btnC7;
    private javax.swing.JButton btnC8;
    private javax.swing.JButton btnC9;
    private javax.swing.JButton btnC10;
    private javax.swing.JButton btnD1;
    private javax.swing.JButton btnD2;
    private javax.swing.JButton btnD3;
    private javax.swing.JButton btnD4;
    private javax.swing.JButton btnD5;
    private javax.swing.JButton btnD6;
    private javax.swing.JButton btnD7;
    private javax.swing.JButton btnD8;
    private javax.swing.JButton btnD9;
    private javax.swing.JButton btnD10;
    private javax.swing.JButton btnE1;
    private javax.swing.JButton btnE2;
    private javax.swing.JButton btnE3;
    private javax.swing.JButton btnE4;
    private javax.swing.JButton btnE5;
    private javax.swing.JButton btnE6;
    private javax.swing.JButton btnE7;
    private javax.swing.JButton btnE8;
    private javax.swing.JButton btnE9;
    private javax.swing.JButton btnE10;
    private javax.swing.JButton btnF1;
    private javax.swing.JButton btnF2;
    private javax.swing.JButton btnF3;
    private javax.swing.JButton btnF4;
    private javax.swing.JButton btnF5;
    private javax.swing.JButton btnF6;
    private javax.swing.JButton btnF7;
    private javax.swing.JButton btnF8;
    private javax.swing.JButton btnF9;
    private javax.swing.JButton btnF10;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnConfirmSeats;
    private javax.swing.JLabel lblLegendAvailable;
    private javax.swing.JLabel lblLegendBooked;
    private javax.swing.JLabel lblLegendSelected;
    private javax.swing.JLabel lblMovieInfo;
    private javax.swing.JLabel lblScreen;
    private javax.swing.JLabel lblSelectedSeats;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalPrice;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlLegend;
    private javax.swing.JPanel pnlScreen;
    private javax.swing.JPanel pnlSeatsGrid;
    private javax.swing.JScrollPane scrollSeats;
    // End of variables declaration//GEN-END:variables
}
