//Cambio de pruebas para este controlador
package com.inventario.controllers;

import java.util.List;
import java.util.Optional;

import com.inventario.models.Role;
import com.inventario.services.RoleService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class RoleController {

    @FXML private TableView<Role> roleTable;
    @FXML private TableColumn<Role, Integer> colId;
    @FXML private TableColumn<Role, String> colTipo;
    @FXML private TextField txtNuevoRol;
    @FXML private TextField txtActualizarRol;
    @FXML private Button btnAgregar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;

    private final RoleService roleService = new RoleService();
    private ObservableList<Role> roleList;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colTipo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipoRol()));

        cargarRoles();
        
        // Manejar selección en la tabla para actualizar
        roleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtActualizarRol.setText(newSelection.getTipoRol());
            }
        });
    }

    // Método para cargar todos los roles en la tabla
    private void cargarRoles() {
        List<Role> roles = roleService.obtenerRoles();
        roleList = FXCollections.observableArrayList(roles);
        roleTable.setItems(roleList);
    }

    // Método para agregar un nuevo rol
    @FXML
    private void agregarRol() {
        String nuevoRol = txtNuevoRol.getText().trim();
        if (!nuevoRol.isEmpty()) {
            if (roleService.agregarRol(nuevoRol)) {
                cargarRoles();
                txtNuevoRol.clear();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Rol agregado correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo agregar el rol.");
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Ingrese un nombre de rol.");
        }
    }

    // Método para actualizar un rol seleccionado
    @FXML
    private void actualizarRol() {
        Role rolSeleccionado = roleTable.getSelectionModel().getSelectedItem();
        String nuevoNombre = txtActualizarRol.getText().trim();

        if (rolSeleccionado != null && !nuevoNombre.isEmpty()) {
            if (roleService.actualizarRol(rolSeleccionado.getId(), nuevoNombre)) {
                cargarRoles();
                txtActualizarRol.clear();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Rol actualizado correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el rol.");
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Seleccione un rol y escriba un nuevo nombre.");
        }
    }

    // Método para eliminar un rol seleccionado
    @FXML
    private void eliminarRol() {
        Role rolSeleccionado = roleTable.getSelectionModel().getSelectedItem();

        if (rolSeleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Seguro que quieres eliminar este rol?");
            confirmacion.setContentText("Esta acción no se puede deshacer.");
            
            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                if (roleService.eliminarRol(rolSeleccionado.getId())) {
                    cargarRoles();
                    txtActualizarRol.clear();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Rol eliminado correctamente.");
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el rol.");
                }
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Seleccione un rol para eliminar.");
        }
    }

    // Método para mostrar alertas
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
