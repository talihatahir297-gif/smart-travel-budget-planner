package com.travelplanner.model;

import java.util.Date;

public class Trip {
    private int id;
    private int userId;
    private String destination;
    private Date startDate;
    private Date endDate;
    private double totalBudget;
    private double totalSpent; // calculated from expenses

    public Trip() {}

    public Trip(int userId, String destination, Date startDate, Date endDate, double totalBudget) {
        this.userId = userId;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalBudget = totalBudget;
    }

    // Encapsulated getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(double totalBudget) { this.totalBudget = totalBudget; }

    public double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }

    // Business logic (Abstraction)
    public double getRemainingBudget() {
        return totalBudget - totalSpent;
    }

    public boolean isBudgetExceeded() {
        return totalSpent > totalBudget;
    }

    public double getBudgetUsagePercent() {
        if (totalBudget == 0) return 0;
        return (totalSpent / totalBudget) * 100;
    }

    @Override
    public String toString() {
        return destination;
    }
}
