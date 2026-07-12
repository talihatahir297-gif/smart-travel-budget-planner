package com.travelplanner.model;

import java.util.Date;

// Inheritance: FoodExpense extends Expense
public class FoodExpense extends Expense {
    public FoodExpense(int tripId, String description, double amount, Date date) {
        super(tripId, "Food", description, amount, date);
    }
    public FoodExpense() { super(); }

    @Override
    public String getCategoryIcon() { return "🍽️"; }

    @Override
    public String getCategoryColor() { return "#E67E22"; }
}
