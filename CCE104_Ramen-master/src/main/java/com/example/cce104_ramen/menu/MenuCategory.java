package com.example.cce104_ramen.menu;

import com.example.cce104_ramen.DataBase;
import com.example.cce104_ramen.HomeController;
import com.example.cce104_ramen.Order;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class MenuCategory {
    protected ObservableList<Order> orderList;
    protected List<String> categoryOrders;
    protected IntegerProperty totalPrice;
    private Runnable updateTotalAmountCallback;
    protected Map<String, Integer> itemQuantities; // Map to store item quantities

    public MenuCategory(ObservableList<Order> orderList, List<String> categoryOrders, IntegerProperty totalPrice, Runnable updateTotalAmountCallback) {
        this.orderList = orderList;
        this.categoryOrders = categoryOrders;
        this.totalPrice = totalPrice;
        this.updateTotalAmountCallback = updateTotalAmountCallback;
        this.itemQuantities = new HashMap<>(); // Initialize the map
    }

    protected void addOrder(Button button, String itemName, int itemPrice, String category) {
        button.setOnAction(e -> {
            int currentStock = itemQuantities.getOrDefault(itemName, 0);
            if (currentStock <= 0) {
                button.setDisable(true); // Disable the button
                showOutOfStockMessage(itemName); // Alert user the item is out of stock
                return;
            }

            AnchorPane quantityPopUp = (AnchorPane) button.getScene().lookup("#QuantityPopUp");
            quantityPopUp.setVisible(true);

            Spinner<Integer> quantitySpinner = (Spinner<Integer>) quantityPopUp.lookup(".spinner");
            TextArea itemTextArea = (TextArea) quantityPopUp.lookup(".text-area");

            itemTextArea.setText(itemName);
            int maxQuantity = itemQuantities.getOrDefault(itemName, 0);
            if (maxQuantity > 0) {
                quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxQuantity, 1));
            } else {
                button.setDisable(true); // Disable if out of stock
                showOutOfStockMessage(itemName);
                return;
            }
            Button addToOrderButton = (Button) quantityPopUp.lookup(".button");

            addToOrderButton.setOnAction(event -> {
                int quantity = quantitySpinner.getValue();
                int totalItemPrice = itemPrice * quantity;

                // Check if the item already exists in the order list
                Order existingOrder = null;
                for (Order order : orderList) {
                    if (order.getOrderName().equals(itemName)) {
                        existingOrder = order;
                        break;
                    }
                }

                if (existingOrder != null) {
                    // Update the existing order's quantity and amount
                    int newQuantity = existingOrder.getQuantity() + quantity;
                    int previousAmount = existingOrder.getAmount();
                    int newAmount = existingOrder.getPrice() * newQuantity;

                    existingOrder.setQuantity(newQuantity); // Update quantity

                    // Recalculate totalPrice properly
                    totalPrice.set(totalPrice.get() + (newAmount - previousAmount));

                    // Update UI in HomeController
                    HomeController homeController = (HomeController) button.getScene().getUserData();
                    homeController.updateOrderView(existingOrder);
                } else {
                    // Create a new order and add it
                    Order order = new Order(itemName, itemPrice, category);
                    order.setQuantity(quantity);
                    orderList.add(order);

                    categoryOrders.add(itemName);
                    totalPrice.set(totalPrice.get() + totalItemPrice);

                    // Render new order in UI
                    HomeController homeController = (HomeController) button.getScene().getUserData();
                    homeController.renderOrder(order);
                }

                // Hide QuantityPopUp
                quantityPopUp.setVisible(false);
            });
        });
    }
    public List<Button> getAllButtons() {
        return List.of(/* Add all buttons for this category */);
    }
    public int getItemStock(String itemName) {
        return itemQuantities.getOrDefault(itemName, 0);
    }


    private void showOutOfStockMessage(String itemName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Out of Stock");
        alert.setHeaderText("Item Unavailable");
        alert.setContentText("Sorry, " + itemName + " is out of stock.");
        alert.showAndWait();
    }



    // Add this to MenuCategory
    protected void loadItemQuantitiesFromDatabase(String category) {
        try {
            String query = "SELECT name, quantity FROM " + category; // Table name matches category
            Statement stmt = DataBase.getInstance().getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                setItemStock(name, quantity); // Populate itemQuantities map
            }
            System.out.println("Loaded quantities for category: " + category);
        } catch (SQLException e) {
            System.out.println("Failed to fetch item quantities for category: " + category);
            e.printStackTrace();
        }
    }


    // Utility function to set initial inventory for items
    public void setItemStock(String itemName, int quantity) {
        itemQuantities.put(itemName, quantity);
    }
}