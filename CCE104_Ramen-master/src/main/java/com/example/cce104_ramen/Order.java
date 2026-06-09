package com.example.cce104_ramen;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javafx.beans.property.*;

public class Order {
    private final StringProperty orderName = new SimpleStringProperty();
    private final IntegerProperty price = new SimpleIntegerProperty();
    private final StringProperty category = new SimpleStringProperty("");
    private final IntegerProperty quantity = new SimpleIntegerProperty(1); // Default quantity is 1
    private final IntegerProperty amount = new SimpleIntegerProperty(); // Total amount for the order

    public Order(String orderName, int price, String category) {
        this.orderName.set(orderName);
        this.price.set(price);
        this.category.set(category);
        this.amount.bind(this.price.multiply(this.quantity)); // Bind amount to price * quantity
    }
    public String getCategory() {
        return category.get();
    }

    public String getOrderName() {
        return orderName.get();
    }

    public StringProperty orderNameProperty() {
        return orderName;
    }

    public int getPrice() {
        return price.get();
    }



    public int getQuantity() {
        return quantity.get();
    }



    public void setQuantity(int quantity) {
        this.quantity.set(quantity); // Set the quantity property
    }

    public int getAmount() {
        return amount.get();
    }

}