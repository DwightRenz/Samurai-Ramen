package com.example.cce104_ramen;

import com.example.cce104_ramen.menu.*;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import com.itextpdf.kernel.pdf.PdfWriter;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


import java.util.ArrayList;
import java.util.List;



public class HomeController {
    @FXML
    private AnchorPane QuantityPopUp;

    @FXML
    private AnchorPane main_pane, rightPane, Ramen_Menu, Meals_Menu, Dessert_Menu, Beverages_Menu, View_Orders, First_Appearance;

    @FXML
    private Button TonkotsuRamen, MisoRamen, ShoyuRamen, SpicyKimchiRamen, VeganRamen;
    @FXML
    private Button SamuraiSushiSet, TeriyakiRiceBowl, ChickenKatsuCurry, BeefGyudon, SalmonSpecial, UnagiDonburi;
    @FXML
    private Button MatchaIceCream, MatchaTiramisu, Mochi, Dorayaki, Yokan, Warabi;
    @FXML
    private Button MatchaLatte, BubbleTea, CocaCola, Pepsi, Sake, Sprite, IcedTea, CokeZero;




    @FXML
    private Button ConfirmOrder_Button;


    @FXML
    private TextArea TotalAmountTxtArea;

    @FXML
    private Spinner<Integer> QuantitySpinner;

    @FXML
    private TableColumn<Order, Integer> amountColumn; // Add this to FXML

    @FXML
    private VBox OrderVBox;


    private ObservableList<Order> orderList = FXCollections.observableArrayList();
    private IntegerProperty totalPrice = new SimpleIntegerProperty(0);


    // Lists to store items for each category
    private List<String> ramenOrders = new ArrayList<>();
    private List<String> mealOrders = new ArrayList<>();
    private List<String> beverageOrders = new ArrayList<>();
    private List<String> dessertOrders = new ArrayList<>();

    private DataBase database = DataBase.getInstance(); // Database instance

    // Controllers for each menu category
    private Ramen ramenController;
    private Meals meals;
    private Desserts desserts;
    private Beverages beverages;

    public void initialize() {
        try {
            database.connect(); // Establish the database connection
            if (database.getConnection() != null) {
                System.out.println("Database connection verified in HomeController!");
            } else {
                System.out.println("Failed to establish database connection in HomeController.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } // Establish database connection
        TotalAmountTxtArea.textProperty().bind(totalPrice.asString("₱%d"));
        Runnable updateTotalAmountCallback = () -> {
            totalPrice.set(totalPrice.get()); // Force recalculation of binding
        };
        setupCategoryControllers(updateTotalAmountCallback);
        ConfirmOrder_Button.setOnAction(e -> confirmOrder());


        QuantitySpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);

        QuantityPopUp.setVisible(false);
    }

    public void updateOrderView(Order order) {
        // Iterate through the children of the VBox and find the order
        for (int i = 0; i < OrderVBox.getChildren().size(); i++) {
            AnchorPane orderPane = (AnchorPane) OrderVBox.getChildren().get(i);
            TextArea itemNameField = (TextArea) orderPane.lookup(".text-area");
            if (itemNameField != null && itemNameField.getText().equals(order.getOrderName())) {
                // Locate spinner and amount field
                Spinner<Integer> quantitySpinner = (Spinner<Integer>) orderPane.lookup(".spinner");
                TextArea amountField = (TextArea) orderPane.lookup(".amount-field");

                if (quantitySpinner != null) {
                    quantitySpinner.getValueFactory().setValue(order.getQuantity()); // Update quantity
                }

                if (amountField != null) {
                    amountField.setText("₱" + order.getAmount()); // Update amount
                }

                break; // Stop iterating as we've already found and updated the order
            }
        }
        // Always update the total price
    }
    public void renderOrder(Order order) {
        // Create a new AnchorPane
        AnchorPane orderPane = new AnchorPane();
        orderPane.setStyle("-fx-background-color:  #0000; -fx-border-color: #fff; -fx-border-radius: 5; -fx-background-radius: 5;");
        orderPane.setPrefHeight(85);
        orderPane.setPrefWidth(OrderVBox.getPrefWidth());

        // Create a TextArea for the item name
        TextArea itemName = new TextArea(order.getOrderName());
        itemName.setLayoutX(20);
        itemName.setLayoutY(9);
        itemName.setPrefWidth(240);
        itemName.setPrefHeight(35);
        itemName.setEditable(false);
        itemName.setStyle("-fx-focus-color: transparent;");

        // Create a TextArea for the total amount
        TextArea amount = new TextArea("₱" + order.getAmount());
        amount.setLayoutX(270);
        amount.setLayoutY(9);
        amount.setPrefWidth(100);
        amount.setPrefHeight(35);
        amount.setEditable(false);
        amount.setStyle("-fx-focus-color: transparent; -fx-text-fill: gold;");

        // Create a Spinner for the quantity
        Spinner<Integer> quantitySpinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, order.getQuantity());
        quantitySpinner.setValueFactory(valueFactory);
        quantitySpinner.setEditable(true);
        quantitySpinner.setLayoutX(390);
        quantitySpinner.setLayoutY(9);
        quantitySpinner.setPrefWidth(80);
        quantitySpinner.setPrefHeight(35);
        quantitySpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);

        // Handle changes in the quantity Spinner
        quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            MenuCategory categoryController = getCategoryController(order.getCategory());
            int currentStock = categoryController.getItemStock(order.getOrderName());

            if (newVal > currentStock) {
                quantitySpinner.getValueFactory().setValue(oldVal); // Revert to the previous value
                showStockExceededMessage(order.getOrderName(), currentStock);

                return;
            }

            int newAmount = order.getPrice() * newVal;
            int previousAmount = order.getAmount();

            // Update the order's fields
            order.setQuantity(newVal);

            // Update the amount in UI
            amount.setText("₱" + newAmount);

            // Update the total price
            totalPrice.set(totalPrice.get() + (newAmount - previousAmount));
        });

        // Create a Remove Button
        Button removeButton = new Button("Remove");
        removeButton.setLayoutX(490);
        removeButton.setLayoutY(5);
        removeButton.setPrefWidth(100);
        removeButton.setPrefHeight(35);
        removeButton.setStyle("-fx-background-radius: 15; -fx-background-color: red; -fx-text-fill: white;");
        removeButton.setOnAction(e -> {
            orderList.remove(order);
            OrderVBox.getChildren().remove(orderPane);
            totalPrice.set(totalPrice.get() - order.getAmount());
        });

        // Add all components to the AnchorPane
        orderPane.getChildren().addAll(itemName, amount, quantitySpinner, removeButton);

        // Add the AnchorPane to the VBox
        OrderVBox.getChildren().add(orderPane);
    }



    private boolean showingStockExceededMessage = false;
    private long lastPopupTime = 0;

    private void showStockExceededMessage(String itemName, int maxStock) {
        long currentTime = System.currentTimeMillis();

        // Allow a popup only if the previous one was shown more than 1 second ago
        if (showingStockExceededMessage || (currentTime - lastPopupTime) < 1000) {
            return;
        }

        showingStockExceededMessage = true; // Block additional popups
        lastPopupTime = currentTime; // Update the last popup time

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Exceeds Stock");
            alert.setHeaderText("Quantity Exceeded for " + itemName);
            alert.setContentText("You cannot order more than " + maxStock + " units of " + itemName + ".");

            // On close, reset the state after a small delay to avoid triggering loops immediately
            alert.setOnHidden(event -> {
                showingStockExceededMessage = false; // Allow future popups
            });

            alert.showAndWait();
        });
    }




    private void setupCategoryControllers(Runnable updateTotalAmountCallback) {
        ramenController = new Ramen(orderList, ramenOrders, totalPrice, updateTotalAmountCallback, TonkotsuRamen, MisoRamen, ShoyuRamen, SpicyKimchiRamen, VeganRamen);
        meals = new Meals(orderList, mealOrders, totalPrice, updateTotalAmountCallback, SamuraiSushiSet, TeriyakiRiceBowl, ChickenKatsuCurry, BeefGyudon, SalmonSpecial, UnagiDonburi);
        desserts = new Desserts(orderList, dessertOrders, totalPrice, updateTotalAmountCallback, MatchaIceCream, MatchaTiramisu, Mochi, Dorayaki, Yokan, Warabi);
        beverages = new Beverages(orderList, beverageOrders, totalPrice, updateTotalAmountCallback, MatchaLatte, BubbleTea, CocaCola, Pepsi, Sake, Sprite, IcedTea, CokeZero);

        // Initialize button event handlers
        ramenController.setupButtons();
        meals.setupButtons();
        desserts.setupButtons();
        beverages.setupButtons();
    }
    private void confirmOrder() {
        try {
            // Process order saving in database and PDF invoice generation
            database.insertOrder();
            int currentOrderID = database.getCurrentOrderID();

            // Process order items
            for (Order order : orderList) {
                String itemName = order.getOrderName();
                String category = determineCategory(itemName);
                int quantityOrdered = order.getQuantity();

                // Insert into order_items table
                database.insertOrderItem(currentOrderID, itemName, category, order.getPrice());

                // Decrement the inventory count
                database.decrementItemQuantity(itemName, category, quantityOrdered);

                // Update in-memory stock and disable button if appropriate
                MenuCategory categoryController = getCategoryController(category);
                int updatedStock = categoryController.getItemStock(itemName) - quantityOrdered;

                categoryController.setItemStock(itemName, updatedStock);

                if (updatedStock <= 0) {
                    disableItemButton(itemName, categoryController);
                }
            }

            // Insert transaction details
            database.insertTransaction(totalPrice.get(), currentOrderID);

            // Generate Invoice
            String filePath = "Invoice_" + currentOrderID + ".pdf";
            PDFInvoiceGenerator.generateInvoice(filePath, orderList, totalPrice.get());
            System.out.println("Invoice generated: " + filePath);

            // Show success message to the user
            showConfirmationMessage();

            // Reset orders and navigate to the initial screen
            resetOrderData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Utility to get the corresponding MenuCategory controller
    private MenuCategory getCategoryController(String category) {
        switch (category.toLowerCase()) {
            case "ramen":
                return ramenController;
            case "meals":
                return meals;
            case "desserts":
                return desserts;
            case "beverages":
                return beverages;
            default:
                throw new IllegalArgumentException("Invalid category: " + category);
        }
    }

    // Disable the button for an out-of-stock item
    // Disable the button for an out-of-stock item and add "Out of Order" text
    private void disableItemButton(String itemName, MenuCategory categoryController) {
        List<Button> categoryButtons = categoryController.getAllButtons();
        for (Button button : categoryButtons) {
            if (button.getText().equals(itemName)) {
                button.setDisable(true);

                // Access the graphic
                Node graphic = button.getGraphic();
                if (graphic instanceof Pane) {
                    Pane graphicPane = (Pane) graphic;

                    // Check if "Out of Order" label exists
                    boolean labelExists = graphicPane.getChildren().stream()
                            .anyMatch(node -> node instanceof Label && ((Label) node).getText().equals("Out of Order"));
                    if (!labelExists) {
                        Label outOfOrderLabel = new Label("Out of Order");
                        outOfOrderLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14; -fx-font-weight: bold;");

                        // Dynamic position
                        double offsetY = 0;
                        for (Node child : graphicPane.getChildren()) {
                            if (child instanceof Label) {
                                offsetY = Math.max(offsetY, child.getLayoutY() + 20);
                            }
                        }
                        outOfOrderLabel.setLayoutX(25);  // Horizontal alignment
                        outOfOrderLabel.setLayoutY(offsetY);  // Below existing children

                        graphicPane.getChildren().add(outOfOrderLabel);
                    }
                }
                break;  // Exit after modifying the correct button
            }
        }
    }
    // Utility method to determine the category of an item
    private String determineCategory(String itemName) {
        if (ramenOrders.contains(itemName)) {
            return "ramen";
        } else if (mealOrders.contains(itemName)) {
            return "meals";
        } else if (beverageOrders.contains(itemName)) {
            return "beverages";
        } else if (dessertOrders.contains(itemName)) {
            return "desserts";
        }
        return "unknown"; // Fallback if no category is found
    }

    // Show confirmation message in the UI
    private void showConfirmationMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Confirmation");
        alert.setHeaderText("Order Successful");
        alert.setContentText("Your order has been placed successfully!\nPlease proceed to the counter with your receipt.");
        alert.showAndWait();
    }

    private void resetOrderData() {
        orderList.clear();
        ramenOrders.clear();
        mealOrders.clear();
        beverageOrders.clear();
        dessertOrders.clear();

        // Reset total price and UI elements
        totalPrice.set(0);
        OrderVBox.getChildren().clear();
        FirstAppearance();
    }



    // Menu visibility functions
    @FXML
    public void RamenMenu() {
        QuantityPopUp.setVisible(false); // Ensure the pop-up is hidden
        setMenuVisibility(true, false, false, false, false, false);
    }

    @FXML
    public void MealsMenu() {
        QuantityPopUp.setVisible(false); // Ensure the pop-up is hidden
        setMenuVisibility(false, true, false, false, false, false);
    }

    @FXML
    public void DessertsMenu() {
        QuantityPopUp.setVisible(false); // Ensure the pop-up is hidden
        setMenuVisibility(false, false, true, false, false, false);
    }

    @FXML
    public void BeveragesMenu() {
        QuantityPopUp.setVisible(false); // Ensure the pop-up is hidden
        setMenuVisibility(false, false, false, true, false, false);
    }

    @FXML
    public void ViewOrdersMenu() {
        QuantityPopUp.setVisible(false); // Ensure the pop-up is hidden
        setMenuVisibility(false, false, false, false, true, false);
    }

    @FXML
    public void FirstAppearance() {
        QuantityPopUp.setVisible(false); // Ensure the pop-up is hidden
        setMenuVisibility(false, false, false, false, false, true);
    }

    private void setMenuVisibility(boolean ramen, boolean meals, boolean desserts, boolean beverages, boolean viewOrders, boolean firstAppearance) {
        Ramen_Menu.setVisible(ramen);
        Meals_Menu.setVisible(meals);
        Dessert_Menu.setVisible(desserts);
        Beverages_Menu.setVisible(beverages);
        View_Orders.setVisible(viewOrders);
        First_Appearance.setVisible(firstAppearance);
    }


}
