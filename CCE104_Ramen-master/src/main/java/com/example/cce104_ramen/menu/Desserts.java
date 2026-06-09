package com.example.cce104_ramen.menu;

import com.example.cce104_ramen.Order;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import java.util.List;

public class Desserts extends MenuCategory {
    private Button matchaIceCream, matchaTiramisu, mochi, dorayaki, yokan, warabi;

    public Desserts(ObservableList<Order> orderList, List<String> categoryOrders, IntegerProperty totalPrice,
                    Runnable updateTotalAmountCallback,
                    Button matchaIceCream, Button matchaTiramisu, Button mochi,
                    Button dorayaki, Button yokan, Button warabi) {
        super(orderList, categoryOrders, totalPrice, updateTotalAmountCallback);
        this.matchaIceCream = matchaIceCream;
        this.matchaTiramisu = matchaTiramisu;
        this.mochi = mochi;
        this.dorayaki = dorayaki;
        this.yokan = yokan;
        this.warabi = warabi;

        loadItemQuantitiesFromDatabase("desserts"); // Ensure database quantities are loaded
    }
    public void setupButtons() {
        addOrder(matchaIceCream, "Matcha Ice Cream", 140, "Desserts");
        addOrder(matchaTiramisu, "Matcha Tiramisu", 140, "Desserts");
        addOrder(mochi, "Mochi", 120, "Desserts");
        addOrder(dorayaki, "Dorayaki", 140, "Desserts");
        addOrder(yokan, "Yokan", 140, "Desserts");
        addOrder(warabi, "Warabi", 140, "Desserts");
    }

    @Override
    public List<Button> getAllButtons() {
        return List.of(matchaIceCream, matchaTiramisu, mochi, dorayaki, yokan, warabi);
    }


}