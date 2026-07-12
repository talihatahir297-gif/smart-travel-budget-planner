package com.travelplanner.model;

import java.util.Date;

// Abstract base class (Abstraction + Inheritance)
public abstract class Expense {
    private int id;
    private int tripId;
    private String category;
    private String description;
    private double amount;
    private Date expenseDate;

    public Expense() {}

    public Expense(int tripId, String category, String description, double amount, Date expenseDate) {
        this.tripId = tripId;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.expenseDate = expenseDate;
    }

    // Abstract method - Polymorphism
    public abstract String getCategoryIcon();
    public abstract String getCategoryColor();

    // Getters and Setters (Encapsulation)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Date getExpenseDate() { return expenseDate; }
    public void setExpenseDate(Date expenseDate) { this.expenseDate = expenseDate; }

    @Override
    public String toString() {
        return category + ": " + description + " - PKR " + String.format("%.2f", amount);
    }
}
