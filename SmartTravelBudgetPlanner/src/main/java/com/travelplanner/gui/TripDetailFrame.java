package com.travelplanner.gui;

import com.travelplanner.dao.ExpenseDAO;
import com.travelplanner.dao.TripDAO;
import com.travelplanner.model.Expense;
import com.travelplanner.model.Trip;
import com.travelplanner.model.User;
import com.travelplanner.util.UIConstants;
import com.travelplanner.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TripDetailFrame extends JFrame {

    private final DashboardFrame dashboard;
    private Trip trip;
    private final User currentUser;
    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final TripDAO tripDAO = new TripDAO();

    // Value labels stored directly as fields
    private JLabel totalBudgetVal, totalSpentVal, remainingVal;
    private JProgressBar budgetBar;
    private DefaultTableModel tableModel;
    private JLabel warningLabel;

    public TripDetailFrame(DashboardFrame dashboard, Trip trip, User user) {
        this.dashboard = dashboard;
        this.trip = trip;
        this.currentUser = user;
        setTitle("Trip: " + trip.getDestination());
        setSize(820, 640);
        setLocationRelativeTo(null);
        initUI();
        loadExpenses();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UIConstants.BG_LIGHT);
        setContentPane(root);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UIConstants.PRIMARY);
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        JButton backBtn = UIHelper.createButton("← Back", new Color(255, 255, 255, 50), Color.WHITE);
        backBtn.addActionListener(e -> { dashboard.loadTrips(); dispose(); });
        topBar.add(backBtn, BorderLayout.WEST);

        JLabel titleLbl = UIHelper.createLabel("📍 " + trip.getDestination(),
                new Font("Segoe UI Emoji", Font.BOLD, 17), Color.WHITE);
        topBar.add(titleLbl, BorderLayout.CENTER);
        root.add(topBar, BorderLayout.NORTH);

        // Budget summary card
        JPanel summaryCard = UIHelper.createCard();
        summaryCard.setLayout(new GridLayout(1, 4, 16, 0));
        summaryCard.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Build stat panels and store the value JLabels in fields
        totalBudgetVal = new JLabel("PKR 0");
        totalBudgetVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalBudgetVal.setForeground(UIConstants.TEXT_DARK);
        summaryCard.add(makeStatPanel("Total Budget", totalBudgetVal));

        totalSpentVal = new JLabel("PKR 0");
        totalSpentVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalSpentVal.setForeground(UIConstants.TEXT_DARK);
        summaryCard.add(makeStatPanel("Total Spent", totalSpentVal));

        remainingVal = new JLabel("PKR 0");
        remainingVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        remainingVal.setForeground(UIConstants.ACCENT);
        summaryCard.add(makeStatPanel("Remaining", remainingVal));

        // Budget bar panel
        JPanel barPanel = new JPanel();
        barPanel.setLayout(new BoxLayout(barPanel, BoxLayout.Y_AXIS));
        barPanel.setBackground(Color.WHITE);

        JLabel usageLbl = UIHelper.createLabel("Budget Usage", UIConstants.FONT_SMALL, UIConstants.TEXT_MUTED);
        usageLbl.setAlignmentX(CENTER_ALIGNMENT);

        budgetBar = UIHelper.createProgressBar(0, false);
        budgetBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));

        warningLabel = UIHelper.createLabel("", UIConstants.FONT_SMALL, UIConstants.TEXT_MUTED);
        warningLabel.setAlignmentX(CENTER_ALIGNMENT);

        barPanel.add(usageLbl);
        barPanel.add(Box.createVerticalStrut(6));
        barPanel.add(budgetBar);
        barPanel.add(Box.createVerticalStrut(4));
        barPanel.add(warningLabel);
        summaryCard.add(barPanel);

        // Content area
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(UIConstants.BG_LIGHT);
        content.setBorder(new EmptyBorder(16, 20, 16, 20));
        content.add(summaryCard, BorderLayout.NORTH);

        // Expense table
        String[] cols = {"ID", "Category", "Description", "Date", "Amount (PKR)", "Tip"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(UIConstants.FONT_BODY);
        table.getTableHeader().setFont(UIConstants.FONT_BOLD);
        table.getTableHeader().setBackground(UIConstants.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(210, 230, 250));
        table.setGridColor(UIConstants.BORDER);
        table.setShowGrid(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        content.add(scroll, BorderLayout.CENTER);

        // Bottom bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomBar.setBackground(UIConstants.BG_LIGHT);

        JButton updateBudgetBtn = UIHelper.createButton("Update Budget", new Color(39, 174, 96), Color.WHITE);
        updateBudgetBtn.addActionListener(e -> updateBudget());
        bottomBar.add(updateBudgetBtn);

        JButton addExpenseBtn = UIHelper.createButton("+ Add Expense", UIConstants.PRIMARY, Color.WHITE);
        addExpenseBtn.addActionListener(e -> openAddExpense());
        bottomBar.add(addExpenseBtn);

        content.add(bottomBar, BorderLayout.SOUTH);
        root.add(content, BorderLayout.CENTER);

        // Double-click to delete expense row
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        int expId = (int) tableModel.getValueAt(row, 0);
                        confirmDeleteExpense(expId);
                    }
                }
            }
        });
    }

    /** Creates a stat panel with a caption label and a value label. */
    private JPanel makeStatPanel(String caption, JLabel valueLabel) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);

        JLabel captionLbl = UIHelper.createLabel(caption, UIConstants.FONT_SMALL, UIConstants.TEXT_MUTED);
        captionLbl.setAlignmentX(CENTER_ALIGNMENT);
        valueLabel.setAlignmentX(CENTER_ALIGNMENT);

        p.add(captionLbl);
        p.add(Box.createVerticalStrut(4));
        p.add(valueLabel);
        return p;
    }

    private void loadExpenses() {
        tableModel.setRowCount(0);
        try {
            trip = tripDAO.getTripById(trip.getId());
            List<Expense> expenses = expenseDAO.getExpensesByTrip(trip.getId());
            for (Expense exp : expenses) {
                tableModel.addRow(new Object[]{
                    exp.getId(),
                    exp.getCategory(),
                    exp.getDescription(),
                    exp.getExpenseDate() != null ? exp.getExpenseDate().toString() : "",
                    String.format("%.2f", exp.getAmount()),
                    "Double-click to delete"
                });
            }
            updateSummary();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSummary() {
        totalBudgetVal.setText("PKR " + String.format("%.0f", trip.getTotalBudget()));
        totalSpentVal.setText("PKR " + String.format("%.0f", trip.getTotalSpent()));

        double rem = trip.getRemainingBudget();
        remainingVal.setText("PKR " + String.format("%.0f", Math.abs(rem)));
        remainingVal.setForeground(rem < 0 ? UIConstants.DANGER : UIConstants.ACCENT);

        double pct = trip.getBudgetUsagePercent();
        budgetBar.setValue((int) Math.min(pct, 100));
        budgetBar.setForeground(trip.isBudgetExceeded() ? UIConstants.DANGER
                : (pct > 75 ? UIConstants.WARNING : UIConstants.ACCENT));

        if (trip.isBudgetExceeded()) {
            warningLabel.setText("⚠️ Exceeded by PKR " + String.format("%.0f", -rem));
            warningLabel.setForeground(UIConstants.DANGER);
        } else {
            warningLabel.setText(String.format("%.0f%% used", pct));
            warningLabel.setForeground(pct > 75 ? UIConstants.WARNING : UIConstants.TEXT_MUTED);
        }
    }

    private void openAddExpense() {
        new AddExpenseDialog(this, trip).setVisible(true);
        loadExpenses();
    }

    private void updateBudget() {
        String input = JOptionPane.showInputDialog(this,
                "Enter new budget (PKR):", String.format("%.0f", trip.getTotalBudget()));
        if (input == null) return;
        try {
            double newBudget = Double.parseDouble(input.trim());
            if (newBudget <= 0) throw new NumberFormatException();
            tripDAO.updateBudget(trip.getId(), newBudget);
            loadExpenses();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmDeleteExpense(int expId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this expense?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                expenseDAO.deleteExpense(expId);
                loadExpenses();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
