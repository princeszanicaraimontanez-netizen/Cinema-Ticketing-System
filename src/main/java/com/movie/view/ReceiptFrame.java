/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.movie.view;

import com.movie.model.Booking;
import com.movie.model.User;
import com.movie.util.UITheme;

import javax.swing.*;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * NetBeans GUI Builder JFrame Form for the Official Cinema E-Receipt / Ticket.
 * Displays booking details cleanly using standard Swing GUI components.
 * 
 * Architectural Connection in System:
 * - Layer: Presentation / View Layer (com.movie.view)
 * - Displayed By: CustomerDashboardController (after booking/payment) and AdminDashboardController (sales audit).
 * - Domain Models: Receives Booking and User entity instances containing populated transaction data.
 * - Dynamic Styling: Status colors and button accents are resolved via UITheme (no hard-coding).
 */
public class ReceiptFrame extends javax.swing.JFrame {

    private Booking booking;
    private User currentUser;

    /**
     * Creates new form ReceiptFrame
     */
    public ReceiptFrame() {
        initComponents();
        initCustomListeners();
        applyCustomTheme();
        setLocationRelativeTo(null);
    }

    public ReceiptFrame(Booking booking, User user) {
        initComponents();
        this.booking = booking;
        this.currentUser = user;
        initCustomListeners();
        applyCustomTheme();
        populateData();
        setLocationRelativeTo(null);
    }

    /**
     * Applies centralized theme styling (Clean background, Light Blue button, Lavender button)
     * without hard-coding color literals.
     */
    private void applyCustomTheme() {
        // Colors removed — apply manually as needed
    }

    private void initCustomListeners() {
        btnClose.addActionListener(e -> dispose());
        btnSaveText.addActionListener(e -> saveAsText());
        btnPrint.addActionListener(e -> printReceipt());
    }

    public void setBooking(Booking booking, User user) {
        this.booking = booking;
        this.currentUser = user;
        populateData();
    }

    private void populateData() {
        if (booking == null) return;

        lblBookingCode.setText(booking.getBookingCode());
        lblCustomerName.setText(booking.getCustomerName() != null ? booking.getCustomerName()
                : (currentUser != null ? currentUser.getFullName() : "Valued Customer"));
        lblMovieName.setText(booking.getMovieTitle() != null ? booking.getMovieTitle() : "Cinema Screening");
        lblHallName.setText(booking.getHallName() != null ? booking.getHallName() : "Cinema Hall");
        lblShowDateTime.setText(booking.getShowDate() + " " + booking.getShowTime());
        lblSeats.setText(booking.getSeats() != null ? booking.getSeats() : "-");

        int seatCount = calculateSeatCount();
        double pricePerSeat = seatCount > 0 ? (booking.getTotalAmount() / seatCount) : 0.0;
        lblTicketPrice.setText(String.format("₱%,.2f / seat (%d seat%s)",
                pricePerSeat, seatCount, seatCount > 1 ? "s" : ""));

        lblStatus.setText(booking.getStatus() != null ? booking.getStatus().toUpperCase() : "PAID");
        lblStatus.setForeground(UITheme.getStatusColor(booking.getStatus()));

        lblTotalAmount.setText(String.format("₱%,.2f", booking.getTotalAmount()));
    }

    private int calculateSeatCount() {
        if (booking == null || booking.getSeats() == null || booking.getSeats().trim().isEmpty()) {
            return 0;
        }
        return booking.getSeats().trim().split("[,; ]+").length;
    }

    public String buildReceiptPlainText() {
        if (booking == null) return "No booking data available.";
        String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String cust = booking.getCustomerName() != null ? booking.getCustomerName()
                : (currentUser != null ? currentUser.getFullName() : "Valued Customer");

        int count = calculateSeatCount();
        return "========================================================\n"
             + "             CINEMA TICKETING SYSTEM - E-RECEIPT        \n"
             + "========================================================\n"
             + "Issued: " + timeNow + "\n"
             + "Booking Reference: " + booking.getBookingCode() + "\n"
             + "Customer Name:     " + cust + "\n"
             + "Movie:             " + booking.getMovieTitle() + "\n"
             + "Cinema Hall:       " + booking.getHallName() + "\n"
             + "Date & Time:       " + booking.getShowDate() + " " + booking.getShowTime() + "\n"
             + "Reserved Seats:    " + booking.getSeats() + " (" + count + " seats)\n"
             + "Status:            " + booking.getStatus() + "\n"
             + "--------------------------------------------------------\n"
             + "TOTAL AMOUNT:      PHP " + String.format("%,.2f", booking.getTotalAmount()) + "\n"
             + "========================================================\n"
             + "    Thank you for booking with us! Enjoy your movie!   \n"
             + "========================================================\n";
    }

    private void printReceipt() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Ticket Receipt - " + (booking != null ? booking.getBookingCode() : "Print"));
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return java.awt.print.Printable.NO_SUCH_PAGE;
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            double scaleX = pageFormat.getImageableWidth() / getWidth();
            double scaleY = pageFormat.getImageableHeight() / getHeight();
            double scale = Math.min(scaleX, scaleY);
            if (scale < 1.0) g2.scale(scale, scale);
            getContentPane().printAll(g2);
            return java.awt.print.Printable.PAGE_EXISTS;
        });

        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "Receipt sent to printer successfully!", "Print", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Printing failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveAsText() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Ticket_" + (booking != null ? booking.getBookingCode() : "receipt") + ".txt"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try (FileWriter fw = new FileWriter(f)) {
                fw.write(buildReceiptPlainText());
                JOptionPane.showMessageDialog(this, "Receipt saved to " + f.getName() + "!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
        lblReceiptTitle = new javax.swing.JLabel();
        lblReceiptSubtitle = new javax.swing.JLabel();
        sep1 = new javax.swing.JSeparator();
        pnlDetails = new javax.swing.JPanel();
        lblBookingCodeTitle = new javax.swing.JLabel();
        lblBookingCode = new javax.swing.JLabel();
        lblCustomerTitle = new javax.swing.JLabel();
        lblCustomerName = new javax.swing.JLabel();
        lblMovieTitle = new javax.swing.JLabel();
        lblMovieName = new javax.swing.JLabel();
        lblHallTitle = new javax.swing.JLabel();
        lblHallName = new javax.swing.JLabel();
        lblDateTimeTitle = new javax.swing.JLabel();
        lblShowDateTime = new javax.swing.JLabel();
        lblSeatsTitle = new javax.swing.JLabel();
        lblSeats = new javax.swing.JLabel();
        lblPriceTitle = new javax.swing.JLabel();
        lblTicketPrice = new javax.swing.JLabel();
        lblStatusTitle = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        sep2 = new javax.swing.JSeparator();
        pnlTotal = new javax.swing.JPanel();
        lblTotalLabel = new javax.swing.JLabel();
        lblTotalAmount = new javax.swing.JLabel();
        pnlActions = new javax.swing.JPanel();
        btnPrint = new javax.swing.JButton();
        btnSaveText = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cinema Ticketing System - Official E-Receipt");
        setResizable(false);

        lblReceiptTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblReceiptTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblReceiptTitle.setText("CINEMA TICKETING SYSTEM");

        lblReceiptSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        // lblReceiptSubtitle color removed
        lblReceiptSubtitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblReceiptSubtitle.setText("Official Electronic Ticket & Reservation Receipt");

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblReceiptTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
            .addComponent(lblReceiptSubtitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addComponent(lblReceiptTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblReceiptSubtitle))
        );

        lblBookingCodeTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblBookingCodeTitle.setText("Booking Reference:");

        lblBookingCode.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblBookingCode.setText("BK-00000");

        lblCustomerTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblCustomerTitle.setText("Customer Name:");

        lblCustomerName.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblCustomerName.setText("Customer Name");

        lblMovieTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblMovieTitle.setText("Movie Title:");

        lblMovieName.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblMovieName.setText("Movie Title");

        lblHallTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblHallTitle.setText("Cinema Hall:");

        lblHallName.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblHallName.setText("Cinema Hall");

        lblDateTimeTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDateTimeTitle.setText("Show Date & Time:");

        lblShowDateTime.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblShowDateTime.setText("2026-01-01 00:00:00");

        lblSeatsTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSeatsTitle.setText("Reserved Seats:");

        lblSeats.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSeats.setText("A1, A2");

        lblPriceTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPriceTitle.setText("Ticket Price:");

        lblTicketPrice.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblTicketPrice.setText("₱0.00 / seat");

        lblStatusTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblStatusTitle.setText("Payment Status:");

        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblStatus.setText("PAID");

        javax.swing.GroupLayout pnlDetailsLayout = new javax.swing.GroupLayout(pnlDetails);
        pnlDetails.setLayout(pnlDetailsLayout);
        pnlDetailsLayout.setHorizontalGroup(
            pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetailsLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBookingCodeTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCustomerTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMovieTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHallTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDateTimeTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSeatsTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPriceTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatusTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBookingCode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCustomerName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMovieName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblHallName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblShowDateTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblSeats, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTicketPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnlDetailsLayout.setVerticalGroup(
            pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetailsLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBookingCodeTitle)
                    .addComponent(lblBookingCode))
                .addGap(10, 10, 10)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCustomerTitle)
                    .addComponent(lblCustomerName))
                .addGap(10, 10, 10)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMovieTitle)
                    .addComponent(lblMovieName))
                .addGap(10, 10, 10)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHallTitle)
                    .addComponent(lblHallName))
                .addGap(10, 10, 10)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDateTimeTitle)
                    .addComponent(lblShowDateTime))
                .addGap(10, 10, 10)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSeatsTitle)
                    .addComponent(lblSeats))
                .addGap(10, 10, 10)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPriceTitle)
                    .addComponent(lblTicketPrice))
                .addGap(10, 10, 10)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatusTitle)
                    .addComponent(lblStatus))
                .addGap(6, 6, 6))
        );

        pnlTotal.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        lblTotalLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalLabel.setText("TOTAL AMOUNT:");

        lblTotalAmount.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotalAmount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalAmount.setText("₱0.00");

        javax.swing.GroupLayout pnlTotalLayout = new javax.swing.GroupLayout(pnlTotal);
        pnlTotal.setLayout(pnlTotalLayout);
        pnlTotalLayout.setHorizontalGroup(
            pnlTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTotalLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblTotalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(lblTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        pnlTotalLayout.setVerticalGroup(
            pnlTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTotalLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalLabel)
                    .addComponent(lblTotalAmount))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        btnPrint.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPrint.setText("Print / Save PDF");
        btnPrint.setPreferredSize(new java.awt.Dimension(140, 32));
        pnlActions.add(btnPrint);

        btnSaveText.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        btnSaveText.setText("Save Text (.txt)");
        btnSaveText.setPreferredSize(new java.awt.Dimension(130, 32));
        pnlActions.add(btnSaveText);

        btnClose.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        btnClose.setText("Close");
        btnClose.setPreferredSize(new java.awt.Dimension(90, 32));
        pnlActions.add(btnClose);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sep1)
                    .addComponent(pnlDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sep2)
                    .addComponent(pnlTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(sep1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(pnlDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(sep2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(pnlTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSaveText;
    private javax.swing.JLabel lblBookingCode;
    private javax.swing.JLabel lblBookingCodeTitle;
    private javax.swing.JLabel lblCustomerName;
    private javax.swing.JLabel lblCustomerTitle;
    private javax.swing.JLabel lblDateTimeTitle;
    private javax.swing.JLabel lblHallName;
    private javax.swing.JLabel lblHallTitle;
    private javax.swing.JLabel lblMovieName;
    private javax.swing.JLabel lblMovieTitle;
    private javax.swing.JLabel lblPriceTitle;
    private javax.swing.JLabel lblReceiptSubtitle;
    private javax.swing.JLabel lblReceiptTitle;
    private javax.swing.JLabel lblSeats;
    private javax.swing.JLabel lblSeatsTitle;
    private javax.swing.JLabel lblShowDateTime;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStatusTitle;
    private javax.swing.JLabel lblTicketPrice;
    private javax.swing.JLabel lblTotalAmount;
    private javax.swing.JLabel lblTotalLabel;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlDetails;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlTotal;
    private javax.swing.JSeparator sep1;
    private javax.swing.JSeparator sep2;
    // End of variables declaration//GEN-END:variables
}
