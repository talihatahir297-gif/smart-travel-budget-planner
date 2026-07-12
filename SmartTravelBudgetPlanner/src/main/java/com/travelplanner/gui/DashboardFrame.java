package com.travelplanner.gui;

import com.travelplanner.dao.TripDAO;
import com.travelplanner.model.Trip;
import com.travelplanner.model.User;
import com.travelplanner.util.UIConstants;
import com.travelplanner.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DashboardFrame extends JFrame {

    private final User currentUser;
    private final TripDAO tripDAO = new TripDAO();
    private JPanel tripListPanel;
    private JLabel welcomeLabel;

    public DashboardFrame(User user) {
        this.currentUser = user;
        setTitle("Dashboard — Travel Budget Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 620);
        setLocationRelativeTo(null);
        initUI();
        loadTrips();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIConstants.BG_LIGHT);
        setContentPane(root);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UIConstants.PRIMARY);
        topBar.setBorder(new EmptyBorder(14, 24, 14, 24));

        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftTop.setOpaque(false);
        JLabel appName = UIHelper.createLabel("✈️  Travel Budget Planner", new Font("Segoe UI Emoji", Font.BOLD, 17), Color.WHITE);
        leftTop.add(appName);
        topBar.add(leftTop, BorderLayout.WEST);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightTop.setOpaque(false);
        welcomeLabel = UIHelper.createLabel("Hello, " + currentUser.getName() + "!", UIConstants.FONT_BODY, new Color(200, 230, 255));
        rightTop.add(welcomeLabel);
        JButton logoutBtn = UIHelper.createButton("Logout", new Color(231, 76, 60), Color.WHITE);
        logoutBtn.addActionListener(e -> logout());
        rightTop.add(logoutBtn);
        topBar.add(rightTop, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(UIConstants.BG_LIGHT);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Action bar
        JPanel actionBar = new JPanel(new BorderLayout());
        actionBar.setBackground(UIConstants.BG_LIGHT);
        JLabel myTrips = UIHelper.createLabel("My Trips", UIConstants.FONT_HEADING, UIConstants.TEXT_DARK);
        actionBar.add(myTrips, BorderLayout.WEST);
        JButton addTripBtn = UIHelper.createButton("+ New Trip", UIConstants.PRIMARY, Color.WHITE);
        addTripBtn.addActionListener(e -> openAddTrip());
        actionBar.add(addTripBtn, BorderLayout.EAST);
        content.add(actionBar, BorderLayout.NORTH);

        // Trip list
        tripListPanel = new JPanel();
        tripListPanel.setLayout(new BoxLayout(tripListPanel, BoxLayout.Y_AXIS));
        tripListPanel.setBackground(UIConstants.BG_LIGHT);
        JScrollPane scroll = new JScrollPane(tripListPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(UIConstants.BG_LIGHT);
        scroll.getViewport().setBackground(UIConstants.BG_LIGHT);
        content.add(scroll, BorderLayout.CENTER);

        root.add(content, BorderLayout.CENTER);
    }

    public void loadTrips() {
        tripListPanel.removeAll();
        try {
            List<Trip> trips = tripDAO.getTripsByUser(currentUser.getId());
            if (trips.isEmpty()) {
                JPanel empty = new JPanel(new GridBagLayout());
                empty.setBackground(UIConstants.BG_LIGHT);
                JPanel emptyBox = UIHelper.createCard();
                emptyBox.setLayout(new BoxLayout(emptyBox, BoxLayout.Y_AXIS));
                emptyBox.setBorder(new EmptyBorder(40, 60, 40, 60));

                JLabel emptyIcon = new JLabel("🗺️", SwingConstants.CENTER);
                emptyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
                emptyIcon.setAlignmentX(CENTER_ALIGNMENT);
                JLabel emptyText = UIHelper.createLabel("No trips yet! Create your first trip.", UIConstants.FONT_BODY, UIConstants.TEXT_MUTED);
                emptyText.setAlignmentX(CENTER_ALIGNMENT);
                emptyBox.add(emptyIcon);
                emptyBox.add(Box.createVerticalStrut(10));
                emptyBox.add(emptyText);
                empty.add(emptyBox);
                tripListPanel.add(empty);
            } else {
                for (Trip trip : trips) {
                    tripListPanel.add(createTripCard(trip));
                    tripListPanel.add(Box.createVerticalStrut(10));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading trips: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        tripListPanel.revalidate();
        tripListPanel.repaint();
    }

    private JPanel createTripCard(Trip trip) {
        JPanel card = UIHelper.createCard();
        card.setLayout(new BorderLayout(12, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Left: destination info
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(Color.WHITE);

        JLabel dest = UIHelper.createLabel("📍 " + trip.getDestination(), UIConstants.FONT_HEADING, UIConstants.TEXT_DARK);
        left.add(dest);
        left.add(Box.createVerticalStrut(4));

        String dateStr = (trip.getStartDate() != null ? trip.getStartDate().toString() : "?") +
                         " → " + (trip.getEndDate() != null ? trip.getEndDate().toString() : "?");
        JLabel dates = UIHelper.createLabel(dateStr, UIConstants.FONT_SMALL, UIConstants.TEXT_MUTED);
        left.add(dates);
        left.add(Box.createVerticalStrut(8));

        // Budget progress bar
        double pct = trip.getBudgetUsagePercent();
        JProgressBar bar = UIHelper.createProgressBar(pct, trip.isBudgetExceeded());
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        left.add(bar);
        left.add(Box.createVerticalStrut(4));

        String budgetInfo = String.format("PKR %.0f spent of PKR %.0f  (%.0f%%)",
                trip.getTotalSpent(), trip.getTotalBudget(), pct);
        JLabel budgetLbl = UIHelper.createLabel(budgetInfo, UIConstants.FONT_SMALL,
                trip.isBudgetExceeded() ? UIConstants.DANGER : UIConstants.TEXT_MUTED);
        left.add(budgetLbl);

        card.add(left, BorderLayout.CENTER);

        // Right: action buttons
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(Color.WHITE);
        right.setBorder(new EmptyBorder(0, 0, 0, 4));

        JButton viewBtn = UIHelper.createButton("View", UIConstants.PRIMARY, Color.WHITE);
        viewBtn.setMaximumSize(new Dimension(90, 32));
        viewBtn.addActionListener(e -> openTripDetail(trip));

        JButton deleteBtn = UIHelper.createButton("Delete", UIConstants.DANGER, Color.WHITE);
        deleteBtn.setMaximumSize(new Dimension(90, 32));
        deleteBtn.addActionListener(e -> deleteTrip(trip));

        right.add(viewBtn);
        right.add(Box.createVerticalStrut(8));
        right.add(deleteBtn);
        card.add(right, BorderLayout.EAST);

        if (trip.isBudgetExceeded()) {
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.DANGER, 2, true),
                new EmptyBorder(14, 14, 14, 14)
            ));
        }

        return card;
    }

    private void openAddTrip() {
        AddTripDialog dialog = new AddTripDialog(this, currentUser);
        dialog.setVisible(true);
        loadTrips();
    }

    private void openTripDetail(Trip trip) {
        new TripDetailFrame(this, trip, currentUser).setVisible(true);
    }

    private void deleteTrip(Trip trip) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete trip to " + trip.getDestination() + "? This will remove all expenses too.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                tripDAO.deleteTrip(trip.getId());
                loadTrips();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}
