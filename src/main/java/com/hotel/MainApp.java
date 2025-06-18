package com.hotel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


import com.hotel.controllers.MainController;

public class MainApp extends Application {

    private static Stage primaryStage; 

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage; // Store the stage reference
        primaryStage.setTitle("Hotel Management System - Login");
        showLoginScreen();
    }

    public static void showLoginScreen() throws IOException {
        // Correct path relative to the classpath (resources folder)
        String fxmlPath = "/com/hotel/fxml/LoginPage.fxml";
        URL fxmlLocation = MainApp.class.getResource(fxmlPath);

        if (fxmlLocation == null) {
            System.err.println("FATAL ERROR: Cannot find FXML file: " + fxmlPath);
            System.err.println("Check if 'src/main/resources' is marked as a resources root in your IDE and included in the build.");
            // Consider showing a critical error dialog before exiting
            // Platform.exit(); // Use javafx.application.Platform
            return;
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Apply CSS
        Scene scene = new Scene(root, 400, 350); // Adjusted size slightly
        String cssPath = "/com/hotel/css/styles.css";
        URL cssUrl = MainApp.class.getResource(cssPath);
        if (cssUrl != null) {
             scene.getStylesheets().add(cssUrl.toExternalForm());
             System.out.println("CSS loaded: " + cssPath);
        } else {
            System.err.println("Warning: Could not load CSS file: " + cssPath);
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to switch to the main interface after successful login
    public static void showMainInterface(String userRole) throws IOException {
        String fxmlPath = "/com/hotel/fxml/MainInterface.fxml";
        URL fxmlLocation = MainApp.class.getResource(fxmlPath);
        if (fxmlLocation == null) {
            System.err.println("FATAL ERROR: Cannot find FXML file: " + fxmlPath);
             // Show error dialog
            return;
        }
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Get the MainController and pass the user role
        MainController controller = loader.getController();
        if (controller != null) {
             controller.setUserRole(userRole);
        } else {
            System.err.println("Warning: MainController not found after loading MainInterface.fxml");
        }

        primaryStage.setTitle("Hotel Management System - [" + userRole.toUpperCase() + "]");
        Scene scene = new Scene(root, 900, 700); // Adjusted size

        // Apply CSS
        String cssPath = "/com/hotel/css/styles.css";
        URL cssUrl = MainApp.class.getResource(cssPath);
        if (cssUrl != null) {
             scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
             System.err.println("Warning: Could not load CSS file: " + cssPath);
        }

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        // primaryStage.setMaximized(true); // Optional: Start maximized
    }


    public static void main(String[] args) {
        // Optional: Add shutdown hook to close DB connection
        // Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        //     System.out.println("Application shutting down. Closing DB connection.");
        //     DatabaseConnection.closeConnection();
        // }));

        launch(args);
    }
} 
