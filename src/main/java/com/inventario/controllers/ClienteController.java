
package com.inventario.controllers;

import java.io.IOException;
import java.util.List;

import com.inventario.models.Cliente;
import com.inventario.services.ClienteService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClienteController {
    @FXML private TextField txtBuscar;
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colEmpresa;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colNIT;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, String> colRazonSocial;
    @FXML private TableColumn<Cliente, String> colDireccion;
    @FXML private Button btnSeleccionar;
    @FXML private Button btnCancelar;
    @FXML private Button btnAgregarCliente;
    @FXML private Button btnCrearCliente;

    private final ClienteService clienteService;
    private ObservableList<Cliente> listaClientes;
    private Cliente clienteSeleccionado;
    private VentaController ventaController;


    public void setVentaController(VentaController ventaController) {
        this.ventaController = ventaController;
    }

    public ClienteController() {
        this.clienteService = new ClienteService();
    }

    @FXML
    public void initialize() {
        configurarTabla();
        cargarClientes();

        // 🔍 Filtro en tiempo real
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> filtrarClientes());
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
        colDireccion.setCellValueFactory(cellData -> cellData.getValue().direccionProperty()); // ✅ Nueva columna
    
        listaClientes = FXCollections.observableArrayList();
        tablaClientes.setItems(listaClientes);
    }

    private void cargarClientes() {
        List<Cliente> clientes = clienteService.listarClientes();
        listaClientes.setAll(clientes);

        // 🔄 Suscribirse a los cambios en la base de datos (escucha eventos desde ClienteService)

    }


    @FXML
    private void seleccionarCliente() {
        clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        
        if (clienteSeleccionado == null) {
            mostrarAlerta("Debe seleccionar un cliente.");
            return;
        }
    
        System.out.println("✅ Cliente seleccionado: " + clienteSeleccionado.getNombre());
    
        // 📌 Enviar los datos al módulo de ventas si está vinculado
        if (ventaController != null) {
            ventaController.actualizarClienteSeleccionado(clienteSeleccionado);
        }
    
        cerrarVentana();
    }
    
    /**
     * 🔹 Filtra clientes según el texto ingresado.
     */
    @FXML
    private void filtrarClientes() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            tablaClientes.setItems(listaClientes);
            return;
        }

        ObservableList<Cliente> clientesFiltrados = FXCollections.observableArrayList();

        for (Cliente cliente : listaClientes) {
            if (cliente.getNombre().toLowerCase().contains(filtro) ||
                cliente.getEmpresa().toLowerCase().contains(filtro) ||
                cliente.getNit().toLowerCase().contains(filtro) ||
                cliente.getRazonSocial().toLowerCase().contains(filtro) || // 🔹 Nueva condición
                cliente.getEmail().toLowerCase().contains(filtro)) {
                clientesFiltrados.add(cliente);
            }
        }

        tablaClientes.setItems(clientesFiltrados);
    }



    @FXML
    public void actualizarTablaClientes() {
        System.out.println("🔄 Actualizando tabla de clientes...");
        List<Cliente> clientesActualizados = clienteService.listarClientes();
        listaClientes.setAll(clientesActualizados);
    }


    /**
     * 🔹 Obtiene el cliente seleccionado y cierra la ventana.
     */


    /**
     * 🔹 Abre la ventana del CRUD de clientes.
     */
    @FXML
    private void abrirCRUDClientes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ClienteCRUDView.fxml"));
            Parent root = loader.load();

            // 🔹 Obtener la instancia del controlador del CRUD
            ClienteCRUDController crudController = loader.getController();

            // 🔹 Pasarle la referencia del ClienteController
            crudController.setClienteController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana anterior hasta que se cierre el CRUD
            stage.showAndWait(); // Esperar hasta que el usuario cierre la ventana

            // 🔹 Actualizar la tabla después de cerrar el CRUD
            actualizarTablaClientes();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Error al abrir la ventana de CRUD de clientes: " + e.getMessage());
        }
    }


    /**
     * 🔹 Muestra una alerta con un mensaje.
     */
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * 🔹 Cierra la ventana.
     */
    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * 🔹 Devuelve el cliente seleccionado.
     */
    public Cliente getClienteSeleccionado() {
        return clienteSeleccionado;
    }
}
