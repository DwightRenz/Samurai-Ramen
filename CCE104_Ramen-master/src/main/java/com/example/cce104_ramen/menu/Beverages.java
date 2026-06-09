package com.example.cce104_ramen.menu;

import com.example.cce104_ramen.Order;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import java.util.List;

public class Beverages extends MenuCategory {
    private Button matchaLatte, bubbleTea, cocaCola, pepsi, sake, sprite, icedTea, cokeZero;

    public Beverages(ObservableList<Order> orderList, List<String> categoryOrders, IntegerProperty totalPrice,
                     Runnable updateTotalAmountCallback,
                     Button matchaLatte, Button bubbleTea, Button cocaCola, Button pepsi,
                     Button sake, Button sprite, Button icedTea, Button cokeZero) {
        super(orderList, categoryOrders, totalPrice, updateTotalAmountCallback);
        this.matchaLatte = matchaLatte;
        this.bubbleTea = bubbleTea;
        this.cocaCola = cocaCola;
        this.pepsi = pepsi;
        this.sake = sake;
        this.sprite = sprite;
        this.icedTea = icedTea;
        this.cokeZero = cokeZero;
    }

    public void setupButtons() {
        loadItemQuantitiesFromDatabase("beverages");


        addOrder(matchaLatte, "Matcha Latte", 90, "Beverages");
        addOrder(bubbleTea, "Bubble Tea", 110 , "Beverages");
        addOrder(cocaCola, "Coca-Cola", 50 , "Beverages");
        addOrder(pepsi, "Pepsi", 50, "Beverages");
        addOrder(sake, "Sake", 170, "Beverages");
        addOrder(sprite, "Sprite", 50, "Beverages");
        addOrder(icedTea, "Iced Tea", 50, "Beverages");
        addOrder(cokeZero, "Coke Zero", 60, "Beverages");

    }

    @Override
    public List<Button> getAllButtons() {
        return List.of(matchaLatte, bubbleTea, cocaCola, pepsi, sake, sprite, icedTea, cokeZero);
    }



}
