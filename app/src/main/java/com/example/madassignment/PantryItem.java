package com.example.madassignment;
public class PantryItem {
    private int id; private String name; private double quantity; private String unit;
    public PantryItem(int id, String name, double quantity, String unit) { this.id = id; this.name = name; this.quantity = quantity; this.unit = unit; }
    public int getId() { return id; } public String getName() { return name; } public double getQuantity() { return quantity; } public String getUnit() { return unit; }
}