package com.inventario.controllers;

import java.util.List;

import com.inventario.models.Categoria;
import com.inventario.services.CategoriaService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CategoriaController {

    private final CategoriaService categoriaService = new CategoriaService();
    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();

    @FXML
    private TableView<Categoria> tablaCategorias;

    @FXML
    private TableColumn<Categoria, String> columnaNombre;

    @FXML
    private TableColumn<Categoria, String> columnaDescripcion;

    @FXML
    private TextField nombreField;

    @FXML
    private TextArea descripcionField;

    @FXML
    private Button btnGuardarCategoria;

    @FXML
    private Button btnEliminarCategoria;

    /**
     * 🔹 Inicializa la interfaz gráfica cargando las categorías existentes.
     */
    @FXML
    public void initialize() {
        cargarCategorias();

        columnaNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        columnaDescripcion.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());

        tablaCategorias.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> mostrarDetallesCategoria(newValue)
        );
    }

    /**
     * 🔹 Carga todas las categorías en la tabla.
     */
    private void cargarCategorias() {
        List<Categoria> categorias = categoriaService.obtenerCategorias();
        listaCategorias.setAll(categorias);
        tablaCategorias.setItems(listaCategorias);
    }

    /**
     * 🔹 Muestra los detalles de la categoría seleccionada.
     */
    private void mostrarDetallesCategoria(Categoria categoria) {
        if (categoria != null) {
            nombreField.setText(categoria.getNombre());
            descripcionField.setText(categoria.getDescripcion());
        } else {
            nombreField.clear();
            descripcionField.clear();
        }
    }

    /**
     * 🔹 Guarda una nueva categoría o actualiza una existente.
     */
    @FXML
    private void guardarCategoria() {
        String nombre = nombreField.getText().trim();
        String descripcion = descripcionField.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Error", "El nombre de la categoría no puede estar vacío.", Alert.AlertType.ERROR);
            return;
        }

        Categoria categoriaSeleccionada = tablaCategorias.getSelectionModel().getSelectedItem();

        try {
            if (categoriaSeleccionada == null) {
                // ✅ Agregar nueva categoría
                Categoria nuevaCategoria = new Categoria(0, nombre, descripcion);
                categoriaService.agregarCategoria(nuevaCategoria);
                listaCategorias.add(nuevaCategoria);
                mostrarAlerta("Éxito", "Categoría agregada correctamente.", Alert.AlertType.INFORMATION);
            } else {
                // ✅ Actualizar categoría existente
                categoriaSeleccionada.setNombre(nombre);
                categoriaSeleccionada.setDescripcion(descripcion);
                categoriaService.actualizarCategoria(categoriaSeleccionada);
                mostrarAlerta("Éxito", "Categoría actualizada correctamente.", Alert.AlertType.INFORMATION);
            }
            cargarCategorias();
            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * 🔹 Elimina la categoría seleccionada.
     */
    @FXML
    private void eliminarCategoria() {
        Categoria categoriaSeleccionada = tablaCategorias.getSelectionModel().getSelectedItem();
        if (categoriaSeleccionada == null) {
            mostrarAlerta("Error", "Debes seleccionar una categoría para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Estás seguro de que deseas eliminar esta categoría?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    categoriaService.eliminarCategoria(categoriaSeleccionada.getIdCategoria());
                    listaCategorias.remove(categoriaSeleccionada);
                    mostrarAlerta("Éxito", "Categoría eliminada correctamente.", Alert.AlertType.INFORMATION);
                    limpiarCampos();
                } catch (Exception e) {
                    mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    /**
     * 🔹 Limpia los campos de texto después de agregar o actualizar una categoría.
     */
    private void limpiarCampos() {
        nombreField.clear();
        descripcionField.clear();
        tablaCategorias.getSelectionModel().clearSelection();
    }

    /**
     * 🔹 Muestra una alerta en la interfaz.
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * 🔹 Abre la ventana de gestión de categorías.
     */
    public void abrirVentanaCategorias() {
        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle("Gestión de Categorías");

        // 📏 Aquí puedes ajustar el tamaño de la ventana
        VBox layout = new VBox(10);
        layout.getChildren().addAll(tablaCategorias, nombreField, descripcionField, btnGuardarCategoria, btnEliminarCategoria);

        Scene escena = new Scene(layout, 400, 300); // 🔹 Cambia estos valores (ancho, alto) según lo necesites
        ventana.setScene(escena);
        ventana.showAndWait();
    }
}
