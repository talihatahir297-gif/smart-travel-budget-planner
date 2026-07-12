package com.travelplanner.dao;

import com.travelplanner.db.DatabaseConnection;
import com.travelplanner.model.Expense;
import com.travelplanner.model.ExpenseFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    public Expense addExpense(Expense expense) throws SQLException {
        String sql = "INSERT INTO expenses (trip_id, category, description, amount, expense_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, expense.getTripId());
            pstmt.setString(2, expense.getCategory());
            pstmt.setString(3, expense.getDescription());
            pstmt.setDouble(4, expense.getAmount());
            pstmt.setDate(5, expense.getExpenseDate() != null
                    ? new java.sql.Date(expense.getExpenseDate().getTime()) : null);
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) expense.setId(keys.getInt(1));
        }
        return expense;
    }

    public List<Expense> getExpensesByTrip(int tripId) throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE trip_id = ? ORDER BY expense_date DESC, created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tripId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String category = rs.getString("category");
                Expense expense = ExpenseFactory.createExpense(
                    category,
                    rs.getInt("trip_id"),
                    rs.getString("description"),
                    rs.getDouble("amount"),
                    rs.getDate("expense_date")
                );
                expense.setId(rs.getInt("id"));
                expenses.add(expense);
            }
        }
        return expenses;
    }

    public void deleteExpense(int expenseId) throws SQLException {
        String sql = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, expenseId);
            pstmt.executeUpdate();
        }
    }

    public double getTotalSpentByTrip(int tripId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE trip_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tripId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }
}
