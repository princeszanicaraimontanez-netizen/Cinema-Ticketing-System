package com.movie;

import com.movie.controller.LoginController;
import com.movie.util.DatabaseConnection;
import com.movie.view.LoginView;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application entry point for the Cinema Ticketing System.
 * 
 * Connection in System:
 * - Bootstraps the application on the Swing Event Dispatch Thread (EDT).
 * - Verifies database connectivity via DatabaseConnection (DAO/Persistence check).
 * - Instantiates LoginView (View layer) and binds it to LoginController (Controller layer).
 */
public class MovieTicketing {

    public static void main(String[] args) {
        // Set modern look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            // Pre-flight database check
            if (!DatabaseConnection.testConnection()) {
                JOptionPane.showMessageDialog(null,
                        "Warning: Could not connect to MySQL database (" + DatabaseConnection.getDbName()
                                + " at " + DatabaseConnection.getHost() + ":" + DatabaseConnection.getPort() + ").\n"
                                + "Please ensure MySQL Server is running and credentials in 'db.properties' are correct.\n"
                                + "Refer to README.md for instructions.",
                        "Cinema Ticketing System - Database Connection Notice",
                        JOptionPane.WARNING_MESSAGE);
            }

            // Connection: View instantiated and handed to Controller
            LoginView view = new LoginView();
            new LoginController(view);
            view.setVisible(true);
        });
    }
}
