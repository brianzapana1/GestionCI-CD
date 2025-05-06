package com.inventario.controllers;

import com.inventario.models.Proveedor;
import com.inventario.services.ProveedorService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class ProveedorController {
//Prueba para proveedor aceptado
    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, String> colEmpresa;
    @FXML private TableColumn<Proveedor, String> colNombre;
    @FXML private TableColumn<Proveedor, String> colRazonSocial;
    @FXML private TableColumn<Proveedor, String> colNit;
    @FXML private TableColumn<Proveedor, String> colEmail;
    @FXML private TableColumn<Proveedor, Integer> colTelefono;
    @FXML private TableColumn<Proveedor, String> colDireccion;

    @FXML private TextField empresaField, nombreField, razonSocialField, nitField, emailField, telefonoField;
    @FXML private TextArea direccionField;

    @FXML private Button agregarBtn, actualizarBtn, eliminarBtn, limpiarBtn;

    private final ProveedorService proveedorService = new ProveedorService();
    private final ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();

    private Proveedor proveedorSeleccionado = null;

@FXML
public void initialize() {
    configurarTabla();
    cargarProveedores();

    // 🔹 Configurar tamaño de la ventana desde el controller
    Platform.runLater(() -> {
        if (tablaProveedores.getScene() != null) {
            Stage stage = (Stage) tablaProveedores.getScene().getWindow();
            if (stage != null) {
                stage.setWidth(950);
                stage.setHeight(600);
                stage.setResizable(false); // opcional: evita redimensionamiento
                stage.centerOnScreen();    // opcional: centra en pantalla
            }
        }
    });

    // 🔹 Tecla ESC para limpiar campos
    tablaProveedores.setOnKeyPressed(event -> {
        if (event.getCode() == KeyCode.ESCAPE) {
            limpiarCampos();
        }
    });

    // 🔹 Selección de fila
    tablaProveedores.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
        proveedorSeleccionado = newVal;
        if (newVal != null) cargarCampos(newVal);
    });
}


    private void configurarTabla() {
        colEmpresa.setCellValueFactory(cell -> cell.getValue().empresaProperty());
        colNombre.setCellValueFactory(cell -> cell.getValue().nombreProperty());
        colRazonSocial.setCellValueFactory(cell -> cell.getValue().razonSocialProperty());
        colNit.setCellValueFactory(cell -> cell.getValue().nitProperty());
        colEmail.setCellValueFactory(cell -> cell.getValue().emailProperty());
        colTelefono.setCellValueFactory(cell -> cell.getValue().telefonoProperty().asObject());
        colDireccion.setCellValueFactory(cell -> cell.getValue().direccionProperty());

        tablaProveedores.setItems(listaProveedores);
    }

    private void cargarProveedores() {
        listaProveedores.setAll(proveedorService.obtenerTodos());
    }

    private void cargarCampos(Proveedor proveedor) {
        empresaField.setText(proveedor.getEmpresa());
        nombreField.setText(proveedor.getNombre());
        razonSocialField.setText(proveedor.getRazonSocial());
        nitField.setText(proveedor.getNit());
        emailField.setText(proveedor.getEmail());
        telefonoField.setText(String.valueOf(proveedor.getTelefono()));
        direccionField.setText(proveedor.getDireccion());
    }

    @FXML
    private void agregarProveedor() {
        try {
            Proveedor nuevo = construirProveedorDesdeCampos(0);
            proveedorService.agregarProveedor(nuevo);
            mostrarAlerta("✅ Proveedor agregado correctamente.", Alert.AlertType.INFORMATION);
            cargarProveedores();
            limpiarCampos();
        } catch (Exception e) {
            mostrarAlerta(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void actualizarProveedor() {
        if (proveedorSeleccionado == null) {
            mostrarAlerta("⚠ Debes seleccionar un proveedor para actualizar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            Proveedor actualizado = construirProveedorDesdeCampos(proveedorSeleccionado.getIdProveedor());
            proveedorService.actualizarProveedor(actualizado);
            mostrarAlerta("✅ Proveedor actualizado correctamente.", Alert.AlertType.INFORMATION);
            cargarProveedores();
            limpiarCampos();
        } catch (Exception e) {
            mostrarAlerta(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarProveedor() {
        if (proveedorSeleccionado == null) {
            mostrarAlerta("⚠ Debes seleccionar un proveedor para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        boolean confirmado = confirmar("¿Estás seguro de eliminar este proveedor?");
        if (confirmado) {
            if (proveedorService.eliminarProveedor(proveedorSeleccionado.getIdProveedor())) {
                mostrarAlerta("✅ Proveedor eliminado.", Alert.AlertType.INFORMATION);
                cargarProveedores();
                limpiarCampos();
            } else {
                mostrarAlerta("❌ Error al eliminar el proveedor.", Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validarProveedorEnPantalla() {
        StringBuilder errores = new StringBuilder();
        boolean esValido = true;
    
        if (razonSocialField.getText().trim().isEmpty()) {
            errores.append("⚠ Razón social obligatoria.\n");
            razonSocialField.setStyle("-fx-border-color: red;");
            esValido = false;
        } else razonSocialField.setStyle(null);
    
        if (nombreField.getText().trim().isEmpty()) {
            errores.append("⚠ Nombre obligatorio.\n");
            nombreField.setStyle("-fx-border-color: red;");
            esValido = false;
        } else nombreField.setStyle(null);
    
        if (nitField.getText().trim().isEmpty()) {
            errores.append("⚠ NIT obligatorio.\n");
            nitField.setStyle("-fx-border-color: red;");
            esValido = false;
        } else nitField.setStyle(null);
    
        if (!emailField.getText().trim().isEmpty() &&
            !emailField.getText().matches("^(.+)@(.+)$")) {
            errores.append("⚠ Email no válido.\n");
            emailField.setStyle("-fx-border-color: red;");
            esValido = false;
        } else emailField.setStyle(null);
    
        try {
            int tel = Integer.parseInt(telefonoField.getText().trim());
            if (tel < 1000000) {
                errores.append("⚠ Teléfono muy corto.\n");
                telefonoField.setStyle("-fx-border-color: red;");
                esValido = false;
            } else telefonoField.setStyle(null);
        } catch (NumberFormatException e) {
            errores.append("⚠ Teléfono inválido.\n");
            telefonoField.setStyle("-fx-border-color: red;");
            esValido = false;
        }
    
        if (!esValido) {
            mostrarAlerta(errores.toString(), Alert.AlertType.WARNING);
        }
    
        return esValido;
    }
    

    @FXML
    private void limpiarCampos() {
        proveedorSeleccionado = null;
        empresaField.clear();
        nombreField.clear();
        razonSocialField.clear();
        nitField.clear();
        emailField.clear();
        telefonoField.clear();
        direccionField.clear();
        tablaProveedores.getSelectionModel().clearSelection();
    }

    private Proveedor construirProveedorDesdeCampos(int id) {
        return new Proveedor(
            id,
            empresaField.getText().trim(),
            nombreField.getText().trim(),
            razonSocialField.getText().trim(),
            nitField.getText().trim(),
            emailField.getText().trim(),
            Integer.parseInt(telefonoField.getText().trim()),
            direccionField.getText().trim()
        );
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private boolean confirmar(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
