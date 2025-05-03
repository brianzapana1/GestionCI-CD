package com.inventario.controllers;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.inventario.services.LoginService;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField usuarioField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label mensajeLabel;
    @FXML
    private ImageView logoImage; 
    @FXML
    private Label tituloLabel; 
    @FXML
    private Button loginButton; 
    @FXML
    private Button salirButton; 

    private final LoginService loginService;
    private Stage currentStage;

    public LoginController() {
        this.loginService = new LoginService();
    }

    @FXML
    private void initialize() {
        // 🔹 Cargar la imagen correctamente con mejor tamaño y proporción
        Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"), 380, 380, true, true);
        if (logo != null) {
            logoImage.setImage(logo);
        } else {
            System.err.println("⚠ No se pudo cargar la imagen del logo.");
        }

        // 🔹 Ajustes de UI
        tituloLabel.setStyle("-fx-font-size: 26px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold;");
        usuarioField.setStyle("-fx-font-size: 16px;");
        passwordField.setStyle("-fx-font-size: 16px;");

        
        // 🔹 Configurar acciones de botones
        loginButton.setOnAction(this::iniciarSesion);
        salirButton.setOnAction(event -> cerrarAplicacion());
        passwordField.setOnAction(event -> iniciarSesion(new ActionEvent(loginButton, null)));


        // 🔹 Centrar la ventana del Login
        Platform.runLater(() -> {
            currentStage = (Stage) usuarioField.getScene().getWindow();
            if (currentStage != null) {
                centrarVentana(currentStage);
            } else {
                System.err.println("⚠ El Stage sigue siendo null en Platform.runLater.");
            }
        });
    }

    /** 🔹 Método para centrar la ventana en la pantalla **/
    private void centrarVentana(Stage stage) {
        if (stage == null) {
            System.err.println("⚠ No se pudo centrar la ventana porque Stage es null.");
            return;
        }

        double anchoVentana = stage.getWidth();
        double altoVentana = stage.getHeight();
        double anchoPantalla = Screen.getPrimary().getVisualBounds().getWidth();
        double altoPantalla = Screen.getPrimary().getVisualBounds().getHeight();

        double posX = (anchoPantalla - anchoVentana) / 2;
        double posY = (altoPantalla - altoVentana) / 2;

        stage.setX(posX);
        stage.setY(posY);
    }

    @FXML
    private void iniciarSesion(ActionEvent event) {
        String usuario = usuarioField.getText().trim();
        String password = passwordField.getText().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            mensajeLabel.setText("⚠️ Usuario y contraseña son obligatorios.");
            return;
        }

        if (loginService.autenticarUsuario(usuario, password)) {
            mensajeLabel.setText("✅ Inicio de sesión exitoso. Redirigiendo...");

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        cambiarVista(stage, "/views/MainMenuView.fxml", "Menú Principal");
                    });
                }
            }, 500);
        } else {
            mensajeLabel.setText("❌ Credenciales incorrectas.");
        }
    }

    /**
     * 🔹 Cambia la vista sin modificar el tamaño de la ventana.
     */
    private void cambiarVista(Stage stage, String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Scene scene = new Scene(loader.load());

            stage.setScene(scene);
            stage.setTitle(titulo);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Error cargando la vista: " + rutaFXML);
        }
    }

    @FXML
    private void cerrarAplicacion() {
        System.out.println("🔹 Cerrando aplicación...");
        Platform.exit();
        System.exit(0);
    }
}
