package com.example.cce104_ramen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Home extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
        Parent root = loader.load();

        // Retrieve the HomeController instance
        HomeController homeController = loader.getController();

        // Set the user data for the scene
        Scene scene = new Scene(root);
        scene.setUserData(homeController); // Assign the HomeController to userData

        // Add stylesheets
        scene.getStylesheets().add(getClass().getResource("transitioncolor.css").toExternalForm());

        // Configure stage
        stage.setTitle("Ramen Ordering System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
