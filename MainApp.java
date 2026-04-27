package com.RationShop;

import com.RationShop.dao.DatabaseInitializer;
import com.RationShop.ui.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize database first
        DatabaseInitializer.initialize();

        // OOP CONCEPT: Polymorphism — Application.launch() calls this start() method
        // JavaFX framework calls start() automatically when app launches

        MainController mainController = new MainController();

        BorderPane root = mainController.getView();

        Scene scene = new Scene(root, 900, 600);

        primaryStage.setTitle("Ration Shop Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}