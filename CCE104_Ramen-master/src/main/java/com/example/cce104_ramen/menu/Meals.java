package com.example.cce104_ramen.menu;

import com.example.cce104_ramen.Order;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import java.util.List;

public class Meals extends MenuCategory {
    private Button samuraiSushiSet, teriyakiRiceBowl, chickenKatsuCurry, beefGyudon, salmonSpecial, unagiDonburi;

    public Meals(ObservableList<Order> orderList, List<String> categoryOrders, IntegerProperty totalPrice,
                 Runnable updateTotalAmountCallback,
                 Button samuraiSushiSet, Button teriyakiRiceBowl, Button chickenKatsuCurry,
                 Button beefGyudon, Button salmonSpecial, Button unagiDonburi) {
        super(orderList, categoryOrders, totalPrice, updateTotalAmountCallback);
        this.samuraiSushiSet = samuraiSushiSet;
        this.teriyakiRiceBowl = teriyakiRiceBowl;
        this.chickenKatsuCurry = chickenKatsuCurry;
        this.beefGyudon = beefGyudon;
        this.salmonSpecial = salmonSpecial;
        this.unagiDonburi = unagiDonburi;

        loadItemQuantitiesFromDatabase("meals");
    }

    public void setupButtons() {

        addOrder(samuraiSushiSet, "Samurai Sushi Set", 160, "Meals");
        addOrder(teriyakiRiceBowl, "Teriyaki Rice Bowl", 160, "Meals");
        addOrder(chickenKatsuCurry, "Chicken Katsu Curry", 170, "Meals");
        addOrder(beefGyudon, "Beef Gyudon", 160, "Meals");
        addOrder(salmonSpecial, "Samurai Salmon Special", 180, "Meals");
        addOrder(unagiDonburi, "Unagi Donburi", 170, "Meals");
    }

    @Override
    public List<Button> getAllButtons() {
        return List.of(samuraiSushiSet, teriyakiRiceBowl, chickenKatsuCurry, beefGyudon, salmonSpecial, unagiDonburi);
    }

}
