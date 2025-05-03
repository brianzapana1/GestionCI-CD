package com.inventario.controllers;

import java.io.IOException;
import java.util.List;

import com.inventario.models.TipoComprobante;
import com.inventario.services.TipoComprobanteService;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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

public class TipoComprobanteController {
    private Stage stage; // Referencia a la ventana

    @FXML private TextField txtBuscar;
    @FXML private TableView<TipoComprobante> tablaComprobantes;
    @FXML private TableColumn<TipoComprobante, String> colDescripcion;
    @FXML private TableColumn<TipoComprobante, String> colEstado;
    @FXML private Button btnSeleccionar;
    @FXML private Button btnCancelar;
    @FXML private Button btnAgregarComprobante;


    private final TipoComprobanteService tipoComprobanteService;
    private ObservableList<TipoComprobante> listaComprobantes;
    private TipoComprobante comprobanteSeleccionado;
    private VentaController ventaController;

    public TipoComprobante getTipoComprobanteSeleccionado() {
        return comprobanteSeleccionado;
    }

    public void setVentaController(VentaController ventaController) {
        this.ventaController = ventaController;
    }

    public TipoComprobanteController() {
        this.tipoComprobanteService = new TipoComprobanteService();
    }


    @FXML
    public void initialize() {
        configurarTabla();
        cargarComprobantes();
    
        // 🔍 Filtro en tiempo real
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> filtrarComprobantes());
    
        // 🔹 Esperar a que la interfaz se cargue completamente antes de ajustar el tamaño
        Platform.runLater(() -> ajustarTamanioVentana(450, 450));
        ajustarColumnas();
    
        // 👀 Detectar selección en la tabla y verificar si está activo o inactivo
        tablaComprobantes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                verificarEstadoComprobante(newSelection);
            }
        });
    }
    

    /**
     * 🔹 Ajusta el tamaño de las columnas proporcionalmente.
     */
    private void ajustarColumnas() {
        tablaComprobantes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colDescripcion.setMaxWidth(1f * Integer.MAX_VALUE * 50); // 50% de la tabla
        colEstado.setMaxWidth(1f * Integer.MAX_VALUE * 50);      // 50% de la tabla
    }

    /**
     * 🔹 Verifica si el comprobante seleccionado está activo o inactivo.
     */
    private void verificarEstadoComprobante(TipoComprobante comprobante) {
        if (!comprobante.isEstado()) { // Si está inactivo
            btnSeleccionar.setDisable(true); // 🔹 Desactivar el botón
            btnSeleccionar.setStyle("-fx-background-color: #BDBDBD; -fx-text-fill: #757575;"); // Cambia a gris
        } else {
            btnSeleccionar.setDisable(false); // 🔹 Habilitar el botón
            btnSeleccionar.setStyle(""); // Restaura su estilo original
        }
    }


    /**
     * 🔹 Permite cambiar el tamaño de la ventana después de que se haya cargado el FXML.
     */
    public void ajustarTamanioVentana(double ancho, double alto) {
        obtenerStage(); // Intenta obtener el Stage antes de modificarlo
        if (stage != null) {
            stage.setWidth(ancho);
            stage.setHeight(alto);
        } else {
            System.err.println("❌ No se pudo ajustar el tamaño: Stage es null.");
        }
    }

    /**
     * 🔹 Obtiene el `Stage` dinámicamente desde un nodo de la interfaz (btnCancelar).
     */
    private void obtenerStage() {
        if (stage == null && btnCancelar != null) {
            Scene scene = btnCancelar.getScene();
            if (scene != null) {
                stage = (Stage) scene.getWindow();
            }
        }
    }

    /**
     * 🔹 Configura las columnas de la tabla.
     */
    private void configurarTabla() {
        colDescripcion.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());

        // 🔹 Convertimos el booleano de estado en un String "Activo" o "Inactivo"
        colEstado.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isEstado() ? "Activo" : "Inactivo")
        );

        listaComprobantes = FXCollections.observableArrayList();
        tablaComprobantes.setItems(listaComprobantes);
    }


    /**
     * 🔹 Carga los tipos de comprobantes en la tabla.
     */
    public void cargarComprobantes() {
        List<TipoComprobante> comprobantes = tipoComprobanteService.listarTiposComprobantes();
        listaComprobantes.setAll(comprobantes);
    }

    /**
     * 🔹 Filtra los comprobantes según el texto ingresado.
     */
    @FXML
    private void filtrarComprobantes() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            tablaComprobantes.setItems(listaComprobantes);
            return;
        }
    
        ObservableList<TipoComprobante> comprobantesFiltrados = FXCollections.observableArrayList();
    
        for (TipoComprobante comprobante : listaComprobantes) {
            String estadoTexto = comprobante.isEstado() ? "activo" : "inactivo"; // Convertimos boolean a texto
    
            if (comprobante.getDescripcion().toLowerCase().contains(filtro) ||
                estadoTexto.contains(filtro)) {  // Comparar con "activo" o "inactivo"
                comprobantesFiltrados.add(comprobante);
            }
        }
    
        tablaComprobantes.setItems(comprobantesFiltrados);
    }
    

    /**
     * 🔹 Obtiene el comprobante seleccionado y cierra la ventana.
     */
    @FXML
    private void seleccionarComprobante() {
        comprobanteSeleccionado = tablaComprobantes.getSelectionModel().getSelectedItem();
        
        if (comprobanteSeleccionado == null) {
            mostrarAlerta("Debe seleccionar un tipo de comprobante.");
            return;
        }

        System.out.println("✅ Comprobante seleccionado: " + comprobanteSeleccionado.getDescripcion() +
                        " (ID: " + comprobanteSeleccionado.getIdTipoComp() + ")");

        // 🔹 Enviar el comprobante a VentaController
        if (ventaController != null) {
            ventaController.actualizarTipoComprobante(comprobanteSeleccionado);
            System.out.println("📤 Comprobante enviado a VentaController: " + comprobanteSeleccionado.getIdTipoComp());
        } else {
            System.err.println("⚠ No se pudo enviar el comprobante a VentaController.");
        }

        cerrarVentana();
    }


    /**
     * 🔹 Abre la ventana del CRUD de tipo de comprobante.
     */
    @FXML
    private void abrirCRUDComprobante() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TipoComprobanteCRUDView.fxml"));
            Parent root = loader.load();

            // 🔹 Obtener la instancia del controlador del CRUD
            TipoComprobanteCRUDController crudController = loader.getController();

            // 🔹 Pasarle la referencia del TipoComprobanteController
            crudController.setTipoComprobanteController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana anterior hasta que se cierre el CRUD
            stage.showAndWait(); // Esperar hasta que el usuario cierre la ventana

            // 🔹 Actualizar la tabla después de cerrar el CRUD
            cargarComprobantes();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Error al abrir la ventana de CRUD de comprobantes: " + e.getMessage());
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
        obtenerStage(); // Asegura que el Stage esté disponible antes de cerrarlo
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * 🔹 Devuelve el comprobante seleccionado.
     */
    public TipoComprobante getComprobanteSeleccionado() {
        return comprobanteSeleccionado;
    }
}
