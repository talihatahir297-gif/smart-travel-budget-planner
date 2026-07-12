package com.travelplanner;

import com.travelplanner.db.DatabaseConnection;
import com.travelplanner.gui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Initialize DB tables on startup
        DatabaseConnection.initializeDatabase();

        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
