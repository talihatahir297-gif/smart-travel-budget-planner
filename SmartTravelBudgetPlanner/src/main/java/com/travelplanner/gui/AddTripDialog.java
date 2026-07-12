package com.travelplanner.gui;

import com.travelplanner.dao.TripDAO;
import com.travelplanner.model.Trip;
import com.travelplanner.model.User;
import com.travelplanner.util.UIConstants;
import com.travelplanner.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTripDialog extends JDialog {

    private final TripDAO tripDAO = new TripDAO();
    private final User currentUser;

    private JTextField destinationField, budgetField, startField, endField;

    public AddTripDialog(Frame parent, User user) {
        super(parent, "New Trip", true);
        this.currentUser = user;
        setSize(480, 520);
        setMinimumSize(new Dimension(460, 500));
        setLocationRelativeTo(parent);
        setResizable(true);
        initUI();
    }

    private void initUI() {
        // Use BorderLayout for the whole dialog
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        // ── Header ──────────────────────────────────────────────
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(UIConstants.PRIMARY);
        header.setPreferredSize(new Dimension(480, 72));
        JLabel headerLbl = UIHelper.createLabel(
                "🗺️  Plan a New Trip",
                new Font("Segoe UI Emoji", Font.BOLD, 16),
                Color.WHITE);
        header.add(headerLbl);
        root.add(header, BorderLayout.NORTH);

        // ── Form (CENTER — stretches to fill) ───────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(24, 36, 16, 36));

        GridBagConstraints lc = new GridBagConstraints(); // label column
        lc.gridx = 0; lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(0, 0, 4, 0); lc.fill = GridBagConstraints.NONE;

        GridBagConstraints fc = new GridBagConstraints(); // field column
        fc.gridx = 0; fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0; fc.insets = new Insets(0, 0, 16, 0);

        // Destination
        lc.gridy = 0; form.add(UIHelper.createLabel("Destination *", UIConstants.FONT_BOLD, UIConstants.TEXT_DARK), lc);
        destinationField = UIHelper.createTextField("");
        fc.gridy = 1; form.add(destinationField, fc);

        // Budget
        lc.gridy = 2; form.add(UIHelper.createLabel("Total Budget (PKR) *", UIConstants.FONT_BOLD, UIConstants.TEXT_DARK), lc);
        budgetField = UIHelper.createTextField("");
        fc.gridy = 3; form.add(budgetField, fc);

        // Start date
        lc.gridy = 4; form.add(UIHelper.createLabel("Start Date (YYYY-MM-DD)", UIConstants.FONT_BOLD, UIConstants.TEXT_DARK), lc);
        startField = UIHelper.createTextField("e.g. 2025-07-01");
        fc.gridy = 5; form.add(startField, fc);

        // End date
        lc.gridy = 6; form.add(UIHelper.createLabel("End Date (YYYY-MM-DD)", UIConstants.FONT_BOLD, UIConstants.TEXT_DARK), lc);
        endField = UIHelper.createTextField("e.g. 2025-07-10");
        fc.gridy = 7; form.add(endField, fc);

        // Filler to push buttons to bottom
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridy = 8; filler.weighty = 1.0; filler.fill = GridBagConstraints.VERTICAL;
        form.add(Box.createVerticalGlue(), filler);

        root.add(form, BorderLayout.CENTER);

        // ── Button bar (SOUTH — always visible) ─────────────────
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnBar.setBackground(new Color(245, 248, 252));
        btnBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDER));

        JButton cancelBtn = UIHelper.createButton("Cancel", UIConstants.BORDER, UIConstants.TEXT_DARK);
        cancelBtn.setPreferredSize(new Dimension(100, 38));
        cancelBtn.addActionListener(e -> dispose());
        btnBar.add(cancelBtn);

        JButton saveBtn = UIHelper.createButton("Create Trip", UIConstants.PRIMARY, Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(120, 38));
        saveBtn.addActionListener(e -> handleSave());
        btnBar.add(saveBtn);

        root.add(btnBar, BorderLayout.SOUTH);

        // Allow Enter key on saveBtn
        getRootPane().setDefaultButton(saveBtn);
    }

    private void handleSave() {
        String dest   = destinationField.getText().trim();
        String budStr = budgetField.getText().trim();

        if (dest.isEmpty() || budStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Destination and Budget are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double budget;
        try {
            budget = Double.parseDouble(budStr);
            if (budget <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid positive budget.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        Date start = null, end = null;
        try {
            if (!startField.getText().trim().isEmpty())
                start = sdf.parse(startField.getText().trim());
            if (!endField.getText().trim().isEmpty())
                end   = sdf.parse(endField.getText().trim());
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Use YYYY-MM-DD (e.g. 2025-07-01).",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Trip trip = new Trip(currentUser.getId(), dest, start, end, budget);
            tripDAO.addTrip(trip);
            JOptionPane.showMessageDialog(this,
                    "Trip created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving trip: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
