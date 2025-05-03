package com.inventario.controllers;

import java.util.List;

import com.inventario.models.TipoComprobante;
import com.inventario.services.TipoComprobanteService;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;




public class TipoComprobanteCRUDController {
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtEstado;
    @FXML private TableView<TipoComprobante> tablaComprobantes;
    @FXML private TableColumn<TipoComprobante, String> colDescripcion;
    @FXML private TableColumn<TipoComprobante, String> colEstado;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Button btnCancelar;
    @FXML private CheckBox chkEstado;
    private Stage stage; // Referencia al Stage


    private final TipoComprobanteService tipoComprobanteService;
    private ObservableList<TipoComprobante> listaComprobantes;
    private TipoComprobante tipoComprobanteSeleccionado;
    private TipoComprobanteController tipoComprobanteController; // 🔹 Referencia al controlador principal

    /**
     * 🔹 Constructor: Inicializa el servicio.
     */
    public TipoComprobanteCRUDController() {
        this.tipoComprobanteService = new TipoComprobanteService();
    }

    /**
     * 🔹 Permite recibir la referencia del `TipoComprobanteController`.
     */
    public void setTipoComprobanteController(TipoComprobanteController tipoComprobanteController) {
        this.tipoComprobanteController = tipoComprobanteController;
    }

    /**
     * 🔹 Inicializa la vista.
     */
    @FXML
    public void initialize() {
        configurarTabla();
        cargarComprobantes();

        // Evento al seleccionar un comprobante en la tabla
        tablaComprobantes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosComprobante(newSelection);
            }
        });

        // 🔹 Esperar a que la interfaz se cargue completamente antes de ajustar el tamaño
        Platform.runLater(() -> ajustarTamanioVentana(400, 450)); 
        ajustarColumnas();
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
    
        // ✅ Convertimos el booleano en una propiedad observable de String
        colEstado.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isEstado() ? "Activo" : "Inactivo")
        );
    
        colEstado.setCellFactory(col -> new TableCell<TipoComprobante, String>() {
            private final CheckBox checkBox = new CheckBox();
    
            {
                checkBox.setDisable(true); // ✅ Solo mostrar, sin permitir cambios en la tabla
            }
    
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected("Activo".equals(item));
                    setGraphic(checkBox);
                }
            }
        });
    
        listaComprobantes = FXCollections.observableArrayList();
        tablaComprobantes.setItems(listaComprobantes);
    }
    
    
    

    /**
     * 🔹 Carga los tipos de comprobantes en la tabla.
     */
    private void cargarComprobantes() {
        List<TipoComprobante> comprobantes = tipoComprobanteService.listarTiposComprobantes();
        listaComprobantes.setAll(comprobantes);
    }

    /**
     * 🔹 Guarda o actualiza un tipo de comprobante.
     */
    @FXML
    private void guardarComprobante() {
        String descripcion = txtDescripcion.getText().trim();
        boolean estado = chkEstado.isSelected(); // ✅ Convertimos CheckBox a booleano

        if (descripcion.isEmpty()) {
            mostrarAlerta("Error", "La descripción es obligatoria.", Alert.AlertType.ERROR);
            return;
        }

        TipoComprobante comprobante = new TipoComprobante(
            tipoComprobanteSeleccionado != null ? tipoComprobanteSeleccionado.getIdTipoComp() : 0,
            descripcion,
            estado
        );

        boolean exito;
        if (tipoComprobanteSeleccionado == null) {
            exito = tipoComprobanteService.registrarTipoComprobante(comprobante);
        } else {
            exito = tipoComprobanteService.actualizarTipoComprobante(comprobante);
        }

        if (exito) {
            mostrarAlerta("Éxito", "Tipo de comprobante guardado correctamente.", Alert.AlertType.INFORMATION);
            cargarComprobantes();
            limpiarCampos();

            if (tipoComprobanteController != null) {
                tipoComprobanteController.cargarComprobantes();
            }
        } else {
            mostrarAlerta("Error", "No se pudo guardar el tipo de comprobante.", Alert.AlertType.ERROR);
        }
    }


    /**
     * 🔹 Elimina un tipo de comprobante seleccionado.
     */
    @FXML
    private void eliminarComprobante() {
        if (tipoComprobanteSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un tipo de comprobante para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        if (tipoComprobanteService.eliminarTipoComprobante(tipoComprobanteSeleccionado.getIdTipoComp())) {
            mostrarAlerta("Éxito", "Tipo de comprobante eliminado correctamente.", Alert.AlertType.INFORMATION);
            cargarComprobantes();
            limpiarCampos();

            // 🔹 Notificar al `TipoComprobanteController` para actualizar la tabla
            if (tipoComprobanteController != null) {
                tipoComprobanteController.cargarComprobantes();
            }
        } else {
            mostrarAlerta("Error", "No se pudo eliminar el tipo de comprobante.", Alert.AlertType.ERROR);
        }
    }

    /**
     * 🔹 Limpia los campos del formulario.
     */
    @FXML
    private void limpiarCampos() {
        txtDescripcion.clear();
        chkEstado.setSelected(false);
        tipoComprobanteSeleccionado = null;
    }


    /**
     * 🔹 Carga los datos del comprobante seleccionado en los campos.
     */
    private void cargarDatosComprobante(TipoComprobante comprobante) {
        txtDescripcion.setText(comprobante.getDescripcion());
        chkEstado.setSelected(comprobante.isEstado()); // ✅ Cargar booleano en CheckBox
        tipoComprobanteSeleccionado = comprobante;
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
     * 🔹 Muestra una alerta con un mensaje.
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
