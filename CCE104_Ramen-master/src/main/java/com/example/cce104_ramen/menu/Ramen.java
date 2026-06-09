package com.example.cce104_ramen.menu;

import com.example.cce104_ramen.Order;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import java.util.List;

public class Ramen extends MenuCategory {
    private Button tonkotsuRamen, misoRamen, shoyuRamen, spicyKimchiRamen, veganRamen;

    public Ramen(ObservableList<Order> orderList, List<String> categoryOrders, IntegerProperty totalPrice,
                 Runnable updateTotalAmountCallback, Button tonkotsuRamen, Button misoRamen,
                 Button shoyuRamen, Button spicyKimchiRamen, Button veganRamen) {
        super(orderList, categoryOrders, totalPrice, updateTotalAmountCallback);
        this.tonkotsuRamen = tonkotsuRamen;
        this.misoRamen = misoRamen;
        this.shoyuRamen = shoyuRamen;
        this.spicyKimchiRamen = spicyKimchiRamen;
        this.veganRamen = veganRamen;

        // Dynamically load quantities from the database
        loadItemQuantitiesFromDatabase("ramen");
    }


    public void setupButtons() {
        addOrder(tonkotsuRamen, "Tonkotsu Ramen", 160, "Ramen");
        addOrder(misoRamen, "Miso Ramen", 160, "Ramen");
        addOrder(shoyuRamen, "Shoyu Ramen", 160, "Ramen");
        addOrder(spicyKimchiRamen, "Spicy Kimchi Ramen", 180, "Ramen");
        addOrder(veganRamen, "Vegan Ramen", 170, "Ramen");
    }


    @Override
    public List<Button> getAllButtons() {
        return List.of(tonkotsuRamen, misoRamen, shoyuRamen, spicyKimchiRamen, veganRamen);
    }
}