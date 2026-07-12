package com.travelplanner.gui;

import com.travelplanner.dao.UserDAO;
import com.travelplanner.model.User;
import com.travelplanner.util.UIConstants;
import com.travelplanner.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {

    private final UserDAO userDAO = new UserDAO();
    private JTextField nameField, emailField;
    private JPasswordField passField, confirmField;

    public RegisterFrame() {
        setTitle("Register — Travel Budget Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIConstants.BG_LIGHT);
        setContentPane(root);

        // Header
        JPanel header = new JPanel();
        header.setBackground(UIConstants.ACCENT);
        header.setPreferredSize(new Dimension(440, 100));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(20, 0, 16, 0));

        JLabel icon = new JLabel("🌍", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        icon.setAlignmentX(CENTER_ALIGNMENT);
        JLabel title = UIHelper.createLabel("Create Account", UIConstants.FONT_TITLE, Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);

        header.add(icon);
        header.add(Box.createVerticalStrut(4));
        header.add(title);
        root.add(header, BorderLayout.NORTH);

        // Form card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(28, 40, 28, 40));

        addField(card, "Full Name");
        nameField = UIHelper.createTextField("");
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.FIELD_HEIGHT));
        card.add(nameField);
        card.add(Box.createVerticalStrut(12));

        addField(card, "Email Address");
        emailField = UIHelper.createTextField("");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.FIELD_HEIGHT));
        card.add(emailField);
        card.add(Box.createVerticalStrut(12));

        addField(card, "Password");
        passField = UIHelper.createPasswordField();
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.FIELD_HEIGHT));
        card.add(passField);
        card.add(Box.createVerticalStrut(12));

        addField(card, "Confirm Password");
        confirmField = UIHelper.createPasswordField();
        confirmField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.FIELD_HEIGHT));
        card.add(confirmField);
        card.add(Box.createVerticalStrut(22));

        JButton registerBtn = UIHelper.createButton("Create Account", UIConstants.ACCENT, Color.WHITE);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        registerBtn.addActionListener(e -> handleRegister());
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(14));

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        backPanel.setBackground(Color.WHITE);
        backPanel.add(UIHelper.createLabel("Already have an account?", UIConstants.FONT_SMALL, UIConstants.TEXT_MUTED));
        JButton backBtn = new JButton("Login");
        backBtn.setFont(UIConstants.FONT_SMALL);
        backBtn.setForeground(UIConstants.PRIMARY);
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        backPanel.add(backBtn);
        card.add(backPanel);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UIConstants.BG_LIGHT);
        center.setBorder(new EmptyBorder(16, 30, 16, 30));
        center.add(card);
        root.add(center, BorderLayout.CENTER);
    }

    private void addField(JPanel card, String label) {
        card.add(UIHelper.createLabel(label, UIConstants.FONT_BOLD, UIConstants.TEXT_DARK));
        card.add(Box.createVerticalStrut(4));
    }

    private void handleRegister() {
        String name    = nameField.getText().trim();
        String email   = emailField.getText().trim();
        String pass    = new String(passField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (pass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (userDAO.emailExists(email)) {
                JOptionPane.showMessageDialog(this, "Email already registered.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            User user = userDAO.register(name, email, pass);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Account created! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginFrame().setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
