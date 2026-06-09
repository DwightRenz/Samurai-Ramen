package com.example.cce104_ramen;

import java.sql.*;

public class DataBase {
    private static DataBase instance; // Singleton instance
    private final String url = "jdbc:mysql://localhost:3306/samurairamen";
    private final String user = "root";
    private final String password = "";
    private Connection connection = null;

    private DataBase() {  connect();// Private constructor to prevent instantiation
    }
    public static synchronized DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    public void connect() {
        if (connection == null || isConnectionClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Connection established successfully!");
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        if (connection == null || isConnectionClosed()) {
            connect(); // Ensure connection is established again
        }
        return connection;
    }
    private boolean isConnectionClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to get the latest Order_ID for Transactions
    public int getCurrentOrderID() {
        String query = "SELECT MAX(Order_ID) AS Order_ID FROM Orders";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("Order_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Default to 0 if no orders
    }

    public void insertOrder() {
        String insertOrder = "INSERT INTO orders () VALUES ()"; // Inserting an order without details
        try (PreparedStatement pstmt = connection.prepareStatement(insertOrder)) {
            pstmt.executeUpdate();
            System.out.println("Order inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Insert order failed: " + e.getMessage());
        }
    }

    public void insertTransaction(int totalAmount, int orderId) {
        String insertTransaction = "INSERT INTO Transactions (Order_ID, TotalAmount) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertTransaction)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, totalAmount);
            pstmt.executeUpdate();
            System.out.println("Transaction inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Insert transaction failed: " + e.getMessage());
        }
    }

    public void insertOrderItem(int orderId, String itemName, String category, double price) {
        String insertOrderItem = "INSERT INTO order_items (Order_ID, item_name, category, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertOrderItem)) {
            pstmt.setInt(1, orderId);
            pstmt.setString(2, itemName);
            pstmt.setString(3, category);
            pstmt.setDouble(4, price);
            pstmt.executeUpdate();
            System.out.println("Order item inserted successfully: " + itemName);
        } catch (SQLException e) {
            System.out.println("Insert order item failed: " + e.getMessage());
        }
    }

    public void updateItemQuantity(String itemName, String category, int newQuantity) {
        String query = "UPDATE " + category + " SET quantity = ? WHERE name = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setInt(1, newQuantity);
            pstmt.setString(2, itemName);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Updated quantity for " + itemName + " to " + newQuantity);
            } else {
                System.out.println("Failed to update quantity. Item or category may not exist.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating item quantity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateInventoryCount(String itemName, String category) throws SQLException {
        // Dynamically update the correct table based on the category
        String query = "UPDATE " + category + " SET quantity = quantity - 1 WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, itemName);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Inventory updated successfully for " + itemName + " in " + category);
            } else {
                System.out.println("Failed to update inventory. Item may not exist in category " + category);
            }
        }
    }
    public void decrementItemQuantity(String itemName, String category, int quantityToReduce) {
        String query = "UPDATE " + category + " SET quantity = quantity - ? WHERE name = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setInt(1, quantityToReduce);
            pstmt.setString(2, itemName);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Reduced quantity for " + itemName + " by " + quantityToReduce);
            } else {
                System.out.println("Failed to reduce quantity. Item or category may not exist.");
            }
        } catch (SQLException e) {
            System.out.println("Error reducing item quantity: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
