package com.example.order_service;

public class Order {
    private String drink;
    private String size;
    private String extra;

    // Default constructor for JSON deserialization
    public Order() {}

    public Order(String drink, String size, String extra) {
        this.drink = drink;
        this.size = size;
        this.extra = extra;
    }

    public String getDrink() { return drink; }
    public void setDrink(String drink) { this.drink = drink; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getExtra() { return extra; }
    public void setExtra(String extra) { this.extra = extra; }
}