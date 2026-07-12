package com.travelplanner.dao;

import com.travelplanner.db.DatabaseConnection;
import com.travelplanner.model.Trip;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripDAO {

    public Trip addTrip(Trip trip) throws SQLException {
        String sql = "INSERT INTO trips (user_id, destination, start_date, end_date, total_budget) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, trip.getUserId());
            pstmt.setString(2, trip.getDestination());
            pstmt.setDate(3, trip.getStartDate() != null ? new java.sql.Date(trip.getStartDate().getTime()) : null);
            pstmt.setDate(4, trip.getEndDate() != null ? new java.sql.Date(trip.getEndDate().getTime()) : null);
            pstmt.setDouble(5, trip.getTotalBudget());
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                trip.setId(keys.getInt(1));
            }
        }
        return trip;
    }

    public List<Trip> getTripsByUser(int userId) throws SQLException {
        List<Trip> trips = new ArrayList<>();
        String sql = """
            SELECT t.*, COALESCE(SUM(e.amount), 0) AS total_spent
            FROM trips t
            LEFT JOIN expenses e ON t.id = e.trip_id
            WHERE t.user_id = ?
            GROUP BY t.id
            ORDER BY t.created_at DESC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Trip trip = new Trip();
                trip.setId(rs.getInt("id"));
                trip.setUserId(rs.getInt("user_id"));
                trip.setDestination(rs.getString("destination"));
                trip.setStartDate(rs.getDate("start_date"));
                trip.setEndDate(rs.getDate("end_date"));
                trip.setTotalBudget(rs.getDouble("total_budget"));
                trip.setTotalSpent(rs.getDouble("total_spent"));
                trips.add(trip);
            }
        }
        return trips;
    }

    public void updateBudget(int tripId, double newBudget) throws SQLException {
        String sql = "UPDATE trips SET total_budget = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBudget);
            pstmt.setInt(2, tripId);
            pstmt.executeUpdate();
        }
    }

    public void deleteTrip(int tripId) throws SQLException {
        String sql = "DELETE FROM trips WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tripId);
            pstmt.executeUpdate();
        }
    }

    public Trip getTripById(int tripId) throws SQLException {
        String sql = """
            SELECT t.*, COALESCE(SUM(e.amount), 0) AS total_spent
            FROM trips t
            LEFT JOIN expenses e ON t.id = e.trip_id
            WHERE t.id = ?
            GROUP BY t.id
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tripId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Trip trip = new Trip();
                trip.setId(rs.getInt("id"));
                trip.setUserId(rs.getInt("user_id"));
                trip.setDestination(rs.getString("destination"));
                trip.setStartDate(rs.getDate("start_date"));
                trip.setEndDate(rs.getDate("end_date"));
                trip.setTotalBudget(rs.getDouble("total_budget"));
                trip.setTotalSpent(rs.getDouble("total_spent"));
                return trip;
            }
        }
        return null;
    }
}
