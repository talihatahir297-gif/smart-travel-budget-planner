package com.travelplanner.model;

import java.util.Date;

// Factory class using Polymorphism
public class ExpenseFactory {
    public static Expense createExpense(String category, int tripId, String description, double amount, Date date) {
        return switch (category) {
            case "Hotel"     -> new HotelExpense(tripId, description, amount, date);
            case "Food"      -> new FoodExpense(tripId, description, amount, date);
            case "Transport" -> new TransportExpense(tripId, description, amount, date);
            default          -> new OtherExpense(tripId, description, amount, date);
        };
    }

    public static String[] getCategories() {
        return new String[]{"Hotel", "Food", "Transport", "Other"};
    }
}
