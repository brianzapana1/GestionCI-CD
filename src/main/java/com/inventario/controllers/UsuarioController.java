package com.inventario.controllers;

import java.util.List;
import java.util.regex.Pattern;

import com.inventario.models.Usuario;
import com.inventario.services.UsuarioService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UsuarioController {
    @FXML
    private TextField nombreField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField usuarioField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> rolComboBox;
    @FXML
    private Button agregarUsuarioButton;
    @FXML
    private Label mensajeLabel;

    private final UsuarioService usuarioService;

    public UsuarioController() {
        this.usuarioService = new UsuarioService();
    }

    @FXML
    private void initialize() {
        // 🔹 Obtener roles desde el Service
        List<String> roles = usuarioService.obtenerRoles();
        if (roles != null && !roles.isEmpty()) {
            rolComboBox.getItems().addAll(roles);
        } else {
            mensajeLabel.setText("⚠️ No se encontraron roles en la base de datos.");
        }

        agregarUsuarioButton.setOnAction(event -> agregarUsuario());
    }

    @FXML
    private void agregarUsuario() {
        String nombre = nombreField.getText().trim();
        String email = emailField.getText().trim();
        String usuario = usuarioField.getText().trim();
        String password = passwordField.getText().trim();
        String rolSeleccionado = rolComboBox.getSelectionModel().getSelectedItem();

        // 🔹 Validaciones en el Controller (solo para UX)
        if (nombre.isEmpty() || email.isEmpty() || usuario.isEmpty() || password.isEmpty() || rolSeleccionado == null) {
            mensajeLabel.setText("⚠️ Todos los campos son obligatorios.");
            return;
        }

        if (!validarEmail(email)) {
            mensajeLabel.setText("⚠️ Email inválido. Debe tener un formato válido.");
            return;
        }

        if (password.length() < 6) {
            mensajeLabel.setText("⚠️ La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        // 🔹 Obtener el ID del rol basado en el nombre seleccionado
        int idRol = usuarioService.obtenerIdRol(rolSeleccionado);
        if (idRol == -1) {
            mensajeLabel.setText("❌ Error al obtener el ID del rol.");
            return;
        }

        // 🔹 Crear el objeto Usuario correctamente
        Usuario nuevoUsuario = new Usuario(0, idRol, nombre, email, usuario, password, null);

        // 🔹 Llamar al servicio correctamente con el objeto Usuario
        boolean registrado = usuarioService.registrarUsuario(nuevoUsuario);

        if (registrado) {
            mensajeLabel.setText("✅ Usuario agregado correctamente.");
            limpiarCampos();
        } else {
            mensajeLabel.setText("❌ Error al agregar usuario.");
        }
    }

    private boolean validarEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(regex, email);
    }

    private void limpiarCampos() {
        nombreField.clear();
        emailField.clear();
        usuarioField.clear();
        passwordField.clear();
        rolComboBox.getSelectionModel().clearSelection();
    }
}
