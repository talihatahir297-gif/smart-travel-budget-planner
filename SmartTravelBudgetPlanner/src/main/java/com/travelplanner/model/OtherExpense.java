package com.travelplanner.model;

import java.util.Date;

// Inheritance: OtherExpense extends Expense
public class OtherExpense extends Expense {
    public OtherExpense(int tripId, String description, double amount, Date date) {
        super(tripId, "Other", description, amount, date);
    }
    public OtherExpense() { super(); }

    @Override
    public String getCategoryIcon() { return "📦"; }

    @Override
    public String getCategoryColor() { return "#8E44AD"; }
}
