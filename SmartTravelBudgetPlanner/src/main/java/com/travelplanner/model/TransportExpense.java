package com.travelplanner.model;

import java.util.Date;

// Inheritance: TransportExpense extends Expense
public class TransportExpense extends Expense {
    public TransportExpense(int tripId, String description, double amount, Date date) {
        super(tripId, "Transport", description, amount, date);
    }
    public TransportExpense() { super(); }

    @Override
    public String getCategoryIcon() { return "🚌"; }

    @Override
    public String getCategoryColor() { return "#27AE60"; }
}
