package com.travelplanner.gui;

import com.travelplanner.dao.ExpenseDAO;
import com.travelplanner.model.Expense;
import com.travelplanner.model.ExpenseFactory;
import com.travelplanner.model.Trip;
import com.travelplanner.util.UIConstants;
import com.travelplanner.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddExpenseDialog extends JDialog {

    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final Trip trip;

    private JComboBox<String> categoryCombo;
    private JTextField descField, amountField, dateField;

    public AddExpenseDialog(Window parent, Trip trip) {
        super(parent, "Add Expense", ModalityType.APPLICATION_MODAL);
        this.trip = trip;
        setSize(460, 420);
        setMinimumSize(new Dimension(440, 400));
        setLocationRelativeTo(parent);
        setResizable(true);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        // ── Header ──────────────────────────────────────────────
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(new Color(39, 174, 96));
        header.setPreferredSize(new Dimension(460, 68));
        header.add(UIHelper.createLabel(
                "💸  Add New Expense",
                new Font("Segoe UI Emoji", Font.BOLD, 15),
                Color.WHITE));
        root.add(header, BorderLayout.NORTH);

        // ── Form ────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(22, 36, 16, 36));

        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0; lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(0, 0, 4, 0); lc.fill = GridBagConstraints.NONE;

        GridBagConstraints fc = new GridBagConstraints();
        fc.gridx = 0; fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0; fc.insets = new Insets(0, 0, 14, 0);

        // Category
        lc.gridy = 0;
        form.add(UIHelper.createLabel("Category *", UIConstants.FONT_BOLD, UIConstants.TEXT_DARK), lc);
        categoryCombo = UIHelper.createComboBox(ExpenseFactory.getCategories());
        fc.gridy = 1;
        form.add(categoryCombo, fc);

        // Description
        lc.gridy = 2;
        form.add(UIHelper.createLabel("Description *", UIConstants.FONT_BOLD, UIConstants.TEXT_DARK), lc);
        descField = UIHelper.createTextField("");
        fc.gridy = 3;
        form.add(descField, fc);

        // Amount
        lc.gridy = 4;
        form.add(UIHelper.createLabel("Amount (PKR) *", UIConstants.FONT_BOLD, UIConstants.TEXT_DARK), lc);
        amountField = UIHelper.createTextField("");
        fc.gridy = 5;
        form.add(amountField, fc);

        // Date
        lc.gridy = 6;
        form.add(UIHelper.createLabel("Date (YYYY-MM-DD)  — optional", UIConstants.FONT_BOLD, UIConstants.TEXT_DARK), lc);
        dateField = UIHelper.createTextField("e.g. 2025-07-05");
        fc.gridy = 7;
        form.add(dateField, fc);

        // Vertical filler
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridy = 8; filler.weighty = 1.0; filler.fill = GridBagConstraints.VERTICAL;
        form.add(Box.createVerticalGlue(), filler);

        root.add(form, BorderLayout.CENTER);

        // ── Button bar (always visible at bottom) ───────────────
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnBar.setBackground(new Color(245, 248, 252));
        btnBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDER));

        JButton cancelBtn = UIHelper.createButton("Cancel", UIConstants.BORDER, UIConstants.TEXT_DARK);
        cancelBtn.setPreferredSize(new Dimension(100, 38));
        cancelBtn.addActionListener(e -> dispose());
        btnBar.add(cancelBtn);

        JButton saveBtn = UIHelper.createButton("Add Expense", new Color(39, 174, 96), Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(130, 38));
        saveBtn.addActionListener(e -> handleSave());
        btnBar.add(saveBtn);

        root.add(btnBar, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(saveBtn);
    }

    private void handleSave() {
        String category = (String) categoryCombo.getSelectedItem();
        String desc     = descField.getText().trim();
        String amtStr   = amountField.getText().trim();

        if (desc.isEmpty() || amtStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Description and Amount are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amtStr);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid positive amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date expDate = null;
        String dateText = dateField.getText().trim();
        if (!dateText.isEmpty() && !dateText.startsWith("e.g")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                expDate = sdf.parse(dateText);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            Expense expense = ExpenseFactory.createExpense(category, trip.getId(), desc, amount, expDate);
            expenseDAO.addExpense(expense);

            double totalSpent = expenseDAO.getTotalSpentByTrip(trip.getId());
            if (totalSpent > trip.getTotalBudget()) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ Budget exceeded!\n"
                        + "Total spent : PKR " + String.format("%.0f", totalSpent) + "\n"
                        + "Budget      : PKR " + String.format("%.0f", trip.getTotalBudget()),
                        "Budget Alert", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Expense added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            dispose(); // close AFTER the user clicks OK on the popup

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving expense: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
