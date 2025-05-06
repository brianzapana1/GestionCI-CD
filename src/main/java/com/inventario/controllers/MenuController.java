package com.inventario.controllers;

import java.io.IOException;
import java.util.Optional;

import com.inventario.utils.SessionManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

//prueba para el controlador del menu
public class MenuController {
    @FXML private Button inicioButton;
    @FXML private Button productosButton;
    @FXML private Button clientesButton;
    @FXML private Button cajaButton;
    @FXML private Button ventasButton;
    @FXML private Button proveedoresButton;
    @FXML private Button reportesButton;
    @FXML private Button configuracionButton;
    @FXML private Button categoriasButton;
    @FXML private Button usuariosButton;
    @FXML private Button rolesButton;
    @FXML private Button inventarioButton;

    @FXML private ImageView logoImage;
    @FXML private MenuItem cerrarSesionMenuItem;
    @FXML private MenuItem salirMenuItem;

    @FXML
    private void initialize() {
        Platform.runLater(this::configurarVentana);

        if (cerrarSesionMenuItem != null) {
            cerrarSesionMenuItem.setOnAction(event -> cerrarSesion());
        }
        if (salirMenuItem != null) {
            salirMenuItem.setOnAction(event -> confirmarSalida());
        }

        // ✅ Módulo: Dashboard
        inicioButton.setOnAction(event -> mostrarInicio());

        // ✅ Módulo: Inventario
        productosButton.setOnAction(event -> abrirVentana("/views/ProductoView.fxml", "Gestión de Productos"));
        categoriasButton.setOnAction(event -> abrirVentana("/views/CategoriaView.fxml", "Gestión de Categorías"));
        proveedoresButton.setOnAction(event -> abrirVentana("/views/ProveedorView.fxml", "Gestión de Proveedores"));
        cajaButton.setOnAction(event -> abrirVentana("/views/CompraView.fxml", "Registro de Compras"));
        inventarioButton.setOnAction(event -> abrirVentana("/views/InventarioResumenView.fxml", "Resumen de Inventario"));

        // ✅ Módulo: Clientes y Usuarios
        clientesButton.setOnAction(event -> abrirVentana("/views/ClienteView.fxml", "Gestión de Clientes"));
        usuariosButton.setOnAction(event -> abrirVentana("/views/AgregarUsuarioView.fxml", "Gestión de Usuarios")); // Crea si no lo tienes
        rolesButton.setOnAction(event -> abrirVentana("/views/RolesView.fxml", "Gestión de Roles"));

        // ✅ Módulo: Ventas
        ventasButton.setOnAction(event -> abrirVentana("/views/VentaView.fxml", "Registro de Ventas"));
        reportesButton.setOnAction(event -> abrirVentana("/views/ReporteVentaView.fxml", "Reporte de Ventas"));

        // ✅ Módulo: Configuración
        configuracionButton.setOnAction(event -> abrirVentana("/views/TipoComprobanteView.fxml", "Tipos de Comprobante"));
    }

    private void configurarVentana() {
        Stage stage = (Stage) productosButton.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(true);

        stage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();
            confirmarSalida();
        });
    }

    private void abrirVentana(String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Scene scene = new Scene(loader.load());

            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle(titulo);
            newStage.initModality(Modality.NONE);
            newStage.initStyle(StageStyle.DECORATED);
            newStage.setResizable(true);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Error cargando la vista: " + rutaFXML);
        }
    }

    @FXML
    private void mostrarInicio() {
        System.out.println("🔹 Mostrando pantalla de inicio...");
        // Puedes cargar un dashboard gráfico si deseas
    }

    @FXML
    private void confirmarSalida() {
        Alert alerta = new Alert(AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar salida");
        alerta.setHeaderText("¿Está seguro de que desea salir?");
        alerta.setContentText("Presione 'Aceptar' para salir o 'Cancelar' para continuar usando el sistema.");

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    @FXML
    private void cerrarSesion() {
        SessionManager.cerrarSesion();

        Stage stage = (Stage) inicioButton.getScene().getWindow();
        stage.close();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Scene scene = new Scene(loader.load());

            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("Iniciar Sesión");
            loginStage.setResizable(false);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Error cargando la vista de inicio de sesión");
        }
    }
}
