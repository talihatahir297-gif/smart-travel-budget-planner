package com.travelplanner.gui;

import com.travelplanner.dao.UserDAO;
import com.travelplanner.model.User;
import com.travelplanner.util.UIConstants;
import com.travelplanner.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private final UserDAO userDAO = new UserDAO();
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Smart Travel Budget Planner — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIConstants.BG_LIGHT);
        setContentPane(root);

        // Header banner
        JPanel header = new JPanel();
        header.setBackground(UIConstants.PRIMARY);
        header.setPreferredSize(new Dimension(440, 130));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(28, 0, 20, 0));

        JLabel icon = new JLabel("✈️", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLbl = UIHelper.createLabel("Travel Budget Planner", UIConstants.FONT_TITLE, Color.WHITE);
        titleLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subLbl = UIHelper.createLabel("Manage your trips & expenses", UIConstants.FONT_SMALL, new Color(180, 210, 240));
        subLbl.setAlignmentX(CENTER_ALIGNMENT);

        header.add(icon);
        header.add(Box.createVerticalStrut(6));
        header.add(titleLbl);
        header.add(Box.createVerticalStrut(4));
        header.add(subLbl);
        root.add(header, BorderLayout.NORTH);

        // Card
        JPanel card = UIHelper.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setBackground(Color.WHITE);

        JLabel loginTitle = UIHelper.createLabel("Sign In", UIConstants.FONT_HEADING, UIConstants.TEXT_DARK);
        loginTitle.setAlignmentX(CENTER_ALIGNMENT);
        card.add(loginTitle);
        card.add(Box.createVerticalStrut(20));

        // Email
        card.add(UIHelper.createLabel("Email", UIConstants.FONT_BOLD, UIConstants.TEXT_DARK));
        card.add(Box.createVerticalStrut(5));
        emailField = UIHelper.createTextField("Email");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.FIELD_HEIGHT));
        emailField.setAlignmentX(CENTER_ALIGNMENT);
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));

        // Password
        card.add(UIHelper.createLabel("Password", UIConstants.FONT_BOLD, UIConstants.TEXT_DARK));
        card.add(Box.createVerticalStrut(5));
        passwordField = UIHelper.createPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.FIELD_HEIGHT));
        passwordField.setAlignmentX(CENTER_ALIGNMENT);
        card.add(passwordField);
        card.add(Box.createVerticalStrut(22));

        // Login button
        JButton loginBtn = UIHelper.createButton("Login", UIConstants.PRIMARY, Color.WHITE);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.setAlignmentX(CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> handleLogin());
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(16));

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(UIConstants.BORDER);
        card.add(sep);
        card.add(Box.createVerticalStrut(14));

        // Register link
        JPanel regPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        regPanel.setBackground(Color.WHITE);
        regPanel.add(UIHelper.createLabel("Don't have an account?", UIConstants.FONT_SMALL, UIConstants.TEXT_MUTED));
        JButton regBtn = new JButton("Register");
        regBtn.setFont(UIConstants.FONT_SMALL);
        regBtn.setForeground(UIConstants.PRIMARY);
        regBtn.setBorderPainted(false);
        regBtn.setContentAreaFilled(false);
        regBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regBtn.addActionListener(e -> openRegister());
        regPanel.add(regBtn);
        card.add(regPanel);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UIConstants.BG_LIGHT);
        center.setBorder(new EmptyBorder(20, 30, 20, 30));
        center.add(card);
        root.add(center, BorderLayout.CENTER);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            User user = userDAO.login(email, pass);
            if (user != null) {
                dispose();
                SwingUtilities.invokeLater(() -> new DashboardFrame(user).setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegister() {
        dispose();
        SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true));
    }
}
