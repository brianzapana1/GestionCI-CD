//Actualizacion catalogo productos
package com.inventario.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import com.inventario.models.Producto;
import com.inventario.services.ProductoService;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CatalogoProductosController {
    @FXML private TilePane tileProductos;
    @FXML private Button btnCerrar;
    @FXML private TextField txtBuscar;

    private final ProductoService productoService;
    private Producto productoSeleccionado;
    private List<Producto> listaProductos;

    public CatalogoProductosController() {
        this.productoService = new ProductoService();
    }

    @FXML
    public void initialize() {
        cargarProductos();

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarProductos();
        });

        Platform.runLater(() -> {
            Stage stage = (Stage) tileProductos.getScene().getWindow();
            stage.setWidth(900);
            stage.setHeight(650);
            stage.setResizable(true);
        });
    }

    private void cargarProductos() {
        listaProductos = productoService.obtenerProductos();
        actualizarVista(listaProductos);
    }

    @FXML
    private void filtrarProductos() {
        String filtro = txtBuscar.getText().toLowerCase();
        List<Producto> productosFiltrados = listaProductos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(filtro))
                .collect(Collectors.toList());
        actualizarVista(productosFiltrados);
    }

    private void actualizarVista(List<Producto> productos) {
        tileProductos.getChildren().clear();
        for (Producto producto : productos) {
            tileProductos.getChildren().add(crearTarjetaProducto(producto));
        }
    }

    private VBox crearTarjetaProducto(Producto producto) {
        VBox tarjeta = new VBox(10);
        tarjeta.setPadding(new Insets(10));
        tarjeta.setStyle("-fx-background-color: #FFC0CB; -fx-border-radius: 10; -fx-alignment: center; -fx-padding: 15px;");
    
        String rutaImagen = "resources/images/productos/" + producto.getImagenUrl();
        ImageView imagenView = new ImageView();
    
        try {
            URL imageUrl = getClass().getClassLoader().getResource("images/productos/" + producto.getImagenUrl());
    
            if (imageUrl != null) {
                imagenView.setImage(new Image(imageUrl.toExternalForm()));
            } else {
                File file = new File(rutaImagen);
                if (file.exists()) {
                    imagenView.setImage(new Image(file.toURI().toString()));
                } else {
                    throw new IllegalArgumentException("❌ Imagen NO encontrada: " + rutaImagen);
                }
            }
    
            imagenView.setFitWidth(200);
            imagenView.setFitHeight(150);
            imagenView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("⚠ No se pudo cargar la imagen: " + rutaImagen + " → " + e.getMessage());
            imagenView = new ImageView();
        }
    
        Label nombre = new Label(producto.getNombre());
        nombre.setStyle("-fx-font-weight: bold; -fx-text-fill: #880E4F; -fx-font-size: 14px;");
    
        Label codigo = new Label("Código: " + producto.getCodigo());
        codigo.setStyle("-fx-text-fill: #C2185B; -fx-font-size: 13px;");
    
        Button btnAgregar = new Button("Agregar");
        btnAgregar.setStyle("-fx-background-color: #D81B60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-font-size: 13px;");
    
        btnAgregar.setOnAction(e -> {
            System.out.println("✅ Producto agregado: " + producto.getNombre() + " (Código: " + producto.getCodigo() + ")");
            productoSeleccionado = producto;
            System.out.println("🔍 Código enviado a UI para la venta: " + productoSeleccionado.getCodigo());
            ((Stage) btnAgregar.getScene().getWindow()).close();
        });
    
        tarjeta.getChildren().addAll(imagenView, nombre, codigo, btnAgregar);
        return tarjeta;
    }
    

    @FXML
    private void abrirFormularioNuevoProducto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ProductoView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Registrar Nuevo Producto");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Espera que el usuario termine de registrar el producto

            // 🔄 Recargar productos tras cierre del formulario
            cargarProductos();
        } catch (IOException e) {
            System.err.println("❌ Error al abrir el formulario de nuevo producto: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public Producto getProductoSeleccionado() {
        return productoSeleccionado;
    }

    @FXML
    private void cerrarCatalogo() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}
