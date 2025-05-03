package com.inventario.app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Aplicar estilos globales
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());

            

            primaryStage.setTitle("Sistema de Inventario - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
             // 🔹 Centrar la ventana automáticamente en la pantalla
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Error cargando la vista LoginView.fxml");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
