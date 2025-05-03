package com.inventario.controllers;

import java.util.List;

import com.inventario.models.Cliente;
import com.inventario.services.ClienteService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ClienteCRUDController {
    @FXML private TextField txtEmpresa, txtNombre, txtRazonSocial, txtNIT, txtTelefono, txtEmail, txtDireccion;
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colEmpresa;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colNIT;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, String> colRazonSocial;
    @FXML private TableColumn<Cliente, String> colDireccion;
    @FXML private Button btnGuardar, btnEliminar, btnLimpiar, btnCancelar;

    private final ClienteService clienteService;
    private ObservableList<Cliente> listaClientes;
    private Cliente clienteSeleccionado;
    private ClienteController clienteController;

    public void setClienteController(ClienteController clienteController) {
        this.clienteController = clienteController;
    }

    public ClienteCRUDController() {
        this.clienteService = new ClienteService();
    }


    @FXML
    public void initialize() {
        configurarTabla();
        cargarClientes();
    
        // 📌 Ajustar tamaño de la ventana al contenido de la tabla
        Platform.runLater(() -> {
            Stage stage = (Stage) tablaClientes.getScene().getWindow();
            stage.setHeight(650); // Altura fija
            stage.setResizable(false); // Bloquear redimensionado manual
    
            // 🔹 Ajustar ancho dinámicamente al tamaño de la tabla
            double anchoTabla = tablaClientes.getBoundsInLocal().getWidth();
            stage.setWidth(anchoTabla + 20); // Margen extra para evitar recortes
        });
    
        // 📌 Evento al seleccionar un cliente en la tabla
        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosCliente(newSelection);
            }
        });
    }
    
    /**
     * 🔹 Configura las columnas de la tabla.
     */
    private void configurarTabla() {
        colEmpresa.setCellValueFactory(cellData -> cellData.getValue().empresaProperty());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colRazonSocial.setCellValueFactory(cellData -> cellData.getValue().razonSocialProperty()); // ✅ Nueva columna
        colNIT.setCellValueFactory(cellData -> cellData.getValue().nitProperty());
        colTelefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty().asString());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colDireccion.setCellValueFactory(cellData -> cellData.getValue().direccionProperty());
    
        listaClientes = FXCollections.observableArrayList();
        tablaClientes.setItems(listaClientes);
    }
    

    /**
     * 🔹 Carga los clientes en la tabla.
     */
    private void cargarClientes() {
        List<Cliente> clientes = clienteService.listarClientes();
        listaClientes.setAll(clientes);
    }

    /**
     * 🔹 Agrega o actualiza un cliente.
     */
    @FXML
    private void guardarCliente() {
        if (txtRazonSocial.getText().trim().isEmpty() || txtNIT.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "La Razon social y el NIT son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        int telefono;
        try {
            telefono = txtTelefono.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtTelefono.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El teléfono debe ser un número válido.", Alert.AlertType.ERROR);
            return;
        }

        Cliente cliente = new Cliente(
            clienteSeleccionado != null ? clienteSeleccionado.getIdCliente() : 0, 
            txtEmpresa.getText().trim(),
            txtNombre.getText().trim(),
            txtRazonSocial.getText().trim(),
            txtNIT.getText().trim(),
            telefono,
            txtEmail.getText().trim(),
            txtDireccion.getText().trim()
        );

        boolean exito;
        if (clienteSeleccionado == null) {
            exito = clienteService.registrarCliente(cliente);
        } else {
            exito = clienteService.actualizarCliente(cliente);
        }

        if (exito) {
            mostrarAlerta("Éxito", "Cliente guardado correctamente.", Alert.AlertType.INFORMATION);
            cargarClientes();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo guardar el cliente.", Alert.AlertType.ERROR);
        }
    }

    /**
     * 🔹 Elimina un cliente seleccionado.
     */
    @FXML
    private void eliminarCliente() {
        if (clienteSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un cliente para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        if (clienteService.eliminarCliente(clienteSeleccionado.getIdCliente())) {
            mostrarAlerta("Éxito", "Cliente eliminado correctamente.", Alert.AlertType.INFORMATION);
            cargarClientes();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo eliminar el cliente.", Alert.AlertType.ERROR);
        }
    }

    /**
     * 🔹 Limpia los campos del formulario.
     */
    @FXML
    private void limpiarCampos() {
        txtEmpresa.clear();
        txtNombre.clear();
        txtRazonSocial.clear();
        txtNIT.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        clienteSeleccionado = null;
    }

    /**
     * 🔹 Carga los datos del cliente en los campos de texto.
     */
    private void cargarDatosCliente(Cliente cliente) {
        txtEmpresa.setText(cliente.getEmpresa());
        txtNombre.setText(cliente.getNombre());
        txtRazonSocial.setText(cliente.getRazonSocial());
        txtNIT.setText(cliente.getNit());
        txtTelefono.setText(String.valueOf(cliente.getTelefono()));
        txtEmail.setText(cliente.getEmail());
        txtDireccion.setText(cliente.getDireccion());
        clienteSeleccionado = cliente;
    }

    /**
     * 🔹 Muestra una alerta con un mensaje.
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * 🔹 Cierra la ventana.
     */
    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    
        // 🔹 Llamar a actualizar tabla en ClienteController después de cerrar la ventana
        if (clienteController != null) {
            System.out.println("🔄 Se cerró el CRUD, actualizando tabla en ClienteController...");
            clienteController.actualizarTablaClientes();
        } else {
            System.err.println("⚠ No se pudo actualizar la tabla porque clienteController es null.");
        }
    }
    
    

}
