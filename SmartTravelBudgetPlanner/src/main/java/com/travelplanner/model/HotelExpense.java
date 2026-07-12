package com.travelplanner.model;

import java.util.Date;

// Inheritance: HotelExpense extends Expense
public class HotelExpense extends Expense {
    public HotelExpense(int tripId, String description, double amount, Date date) {
        super(tripId, "Hotel", description, amount, date);
    }
    public HotelExpense() { super(); }

    @Override
    public String getCategoryIcon() { return "🏨"; }

    @Override
    public String getCategoryColor() { return "#4A90D9"; }
}
