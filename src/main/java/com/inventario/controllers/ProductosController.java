package com.inventario.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.inventario.models.Categoria;
import com.inventario.models.Producto;
import com.inventario.services.CategoriaService;
import com.inventario.services.ProductoService; // ✅ Importación para manejar categorías

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty; // ✅ Importación para el servicio de categorías
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView; // ✅ Importación para ComboBox de categorías
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ProductosController {
    @FXML
    private Button seleccionarImagenBtn;
    @FXML
    private Label imagenPathLabel;
    @FXML
    private ImageView imagenProducto;
    @FXML
    private TextField codigoField;
    @FXML
    private TextField nombreField;
    @FXML
    private TextArea descripcionField;
    @FXML
    private Label descripcionCounter;

    @FXML
    private ComboBox<Categoria> categoriaComboBox; // ✅ Reemplazo de TextField por ComboBox para categorías
    @FXML
    private Button agregarCategoriaBtn; // ✅ Botón para agregar nuevas categorías

    @FXML
    private TextField precioField;
    @FXML
    private TextField costoField; // ✅ Nuevo campo para costo
    @FXML
    private TextField stockField;
    @FXML
    private CheckBox estadoCheckbox; // ✅ Nuevo campo para estado
    @FXML
    private TextField imagenUrlField;
    @FXML
    private DatePicker fechaVencField; // ✅ Campo para fecha de vencimiento
    @FXML
    private DatePicker fechaCompraField; // ✅ Nuevo campo para fecha de compra

    @FXML
    private TableView<Producto> productosTable;
    @FXML
    private TableColumn<Producto, String> codigoColumn;
    @FXML
    private TableColumn<Producto, String> nombreColumn;
    @FXML
    private TableColumn<Producto, String> descripcionColumn;
    @FXML
    private TableColumn<Producto, String> categoriaColumn;
    @FXML
    private TableColumn<Producto, Double> precioColumn;
    @FXML
    private TableColumn<Producto, Double> costoColumn; // ✅ Nueva columna para costo
    @FXML
    private TableColumn<Producto, Integer> stockColumn;
    @FXML
    private TableColumn<Producto, String> creacionColumn;
    @FXML
    private TableColumn<Producto, String> fechaVencColumn; // ✅ Columna para fecha de vencimiento
    @FXML
    private TableColumn<Producto, String> fechaCompraColumn; // ✅ Nueva columna para fecha de compra
    @FXML
    private TableColumn<Producto, String> imagenUrlColumn;
    @FXML
    private TableColumn<Producto, String> estadoColumn; // ✅ Columna para estado

    private final ProductoService productoService;
    private final CategoriaService categoriaService; // ✅ Servicio de categorías
    private final ObservableList<Producto> productosLista;
    private final ObservableList<Categoria> categoriasLista; // ✅ Lista de categorías

    private String rutaImagenSeleccionada = null; // ✅ Guardará la ruta de la imagen seleccionada


    private static final int MAX_DESC_LENGTH = 200;
    private static final int MAX_CATEGORIA_LENGTH = 100;



    public ProductosController() {
        this.productoService = new ProductoService();
        this.categoriaService = new CategoriaService(); // ✅ Inicialización del servicio de categorías
        this.productosLista = FXCollections.observableArrayList();
        this.categoriasLista = FXCollections.observableArrayList(); // ✅ Inicialización de la lista de categorías
    }


    @FXML
    private void initialize() {
        Platform.runLater(this::configurarVentana);
    
        // 🔹 Columnas básicas
        codigoColumn.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        nombreColumn.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        descripcionColumn.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
    
        // 🔹 Columna de categoría (nombre)
        categoriaColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getCategoria() != null
                    ? cellData.getValue().getCategoria().getNombre()
                    : "Sin categoría"
            )
        );
    
        // 🔹 Columna de estado ("Activo" o "Inactivo")
        estadoColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().isEstado() ? "Activo" : "Inactivo")
        );
    
        // 🔹 Columna de fecha de vencimiento
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        fechaVencColumn.setCellValueFactory(cellData -> {
            LocalDate fechaVenc = cellData.getValue().getFechaVenc();
            return new SimpleStringProperty(fechaVenc != null ? fechaVenc.format(dateFormatter) : "N/A");
        });
    
        // ✅ Tabla
        productosTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cargarProductos();
        cargarCategorias();
    
        categoriaComboBox.setItems(categoriasLista);
    
        productosTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                seleccionarProducto();
            }
        });
    
        imagenProducto.setImage(null);
    
        productosTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                limpiarSeleccion();
            }
        });
    }
    
    
       
    
    private void configurarVentana() {
        Stage stage = (Stage) productosTable.getScene().getWindow();
        double anchoVentana = 1200;
        double altoVentana = 650;

        double anchoPantalla = Screen.getPrimary().getVisualBounds().getWidth();
        double altoPantalla = Screen.getPrimary().getVisualBounds().getHeight();

        double posX = (anchoPantalla - anchoVentana) / 2;
        double posY = (altoPantalla - altoVentana) / 2;

        stage.setWidth(anchoVentana);
        stage.setHeight(altoVentana);
        stage.setX(posX);
        stage.setY(posY);
        stage.setResizable(false);
    }

    @FXML
    private void cargarProductos() {
        try {
            productosLista.clear();
            List<Producto> productos = productoService.obtenerProductos();

            if (productos == null || productos.isEmpty()) {
                System.out.println("⚠ No hay productos en la base de datos.");
            } else {
                productosLista.addAll(productos);
            }

            productosTable.setItems(productosLista);
        } catch (Exception e) {
            System.err.println("❌ Error al cargar productos: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar los productos. Intenta nuevamente.", Alert.AlertType.ERROR);
        }
    }

    /**
     * 🔹 Carga las categorías disponibles en el ComboBox.
     */
    @FXML
    private void cargarCategorias() {
        try {
            categoriasLista.clear(); // ✅ Limpiar la lista antes de cargar nuevas categorías
            List<Categoria> categorias = categoriaService.obtenerCategorias(); // ✅ Obtener desde el servicio
            
            if (categorias == null || categorias.isEmpty()) {
                System.out.println("⚠ No hay categorías disponibles.");
            } else {
                categoriasLista.addAll(categorias);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al cargar categorías: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar las categorías. Intenta nuevamente.", Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void abrirGestionCategorias() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CategoriaView.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Gestión de Categorías");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // ✅ Recargar las categorías después de cerrar la ventana
            cargarCategorias();
        } catch (IOException e) {
            System.err.println("❌ Error al abrir la ventana de categorías: " + e.getMessage());
        }
    }
   

    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");

        // Permitir solo formatos de imagen
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File archivoSeleccionado = fileChooser.showOpenDialog(null);
        if (archivoSeleccionado != null) {
            // ✅ Solo guardar la ruta sin copiar aún
            rutaImagenSeleccionada = archivoSeleccionado.getAbsolutePath();

            // ✅ Mostrar solo el nombre del archivo en la UI
            imagenPathLabel.setText(archivoSeleccionado.getName());

            // ✅ Mostrar la imagen en la vista previa
            imagenProducto.setImage(new Image(archivoSeleccionado.toURI().toString()));
        }
    }


    /**
     * 🔹 Método para actualizar los campos al seleccionar un producto en la tabla.
     */
    @FXML
    private void seleccionarProducto() {
        Producto seleccionado = productosTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            codigoField.setText(seleccionado.getCodigo());
            nombreField.setText(seleccionado.getNombre());
            descripcionField.setText(seleccionado.getDescripcion());
    
            // Categoría
            if (categoriaComboBox != null) {
                if (seleccionado.getCategoria() != null) {
                    categoriaComboBox.getSelectionModel().select(seleccionado.getCategoria());
                } else {
                    categoriaComboBox.getSelectionModel().clearSelection();
                }
            }
    
            // Fecha de vencimiento
            if (fechaVencField != null) {
                fechaVencField.setValue(seleccionado.getFechaVenc());
            }
    
            // Estado
            estadoCheckbox.setSelected(seleccionado.isEstado());
    
            // Imagen
            if (imagenProducto != null && imagenPathLabel != null) {
                if (seleccionado.getImagenUrl() != null && !seleccionado.getImagenUrl().isEmpty()) {
                    String rutaImagen = System.getProperty("user.dir") + "/resources/images/productos/" + seleccionado.getImagenUrl();
                    File archivoImagen = new File(rutaImagen);
    
                    if (archivoImagen.exists()) {
                        imagenProducto.setImage(new Image(archivoImagen.toURI().toString()));
                        imagenPathLabel.setText(seleccionado.getImagenUrl());
                    } else {
                        imagenProducto.setImage(null);
                        imagenPathLabel.setText("No seleccionada");
                        System.out.println("⚠ No se pudo encontrar la imagen: " + rutaImagen);
                    }
                } else {
                    imagenProducto.setImage(null);
                    imagenPathLabel.setText("No seleccionada");
                }
            }
        }
    }
    
    
    


    @FXML
    private void configurarEventos() {
        // ✅ Validación para la descripción (máximo 200 caracteres)
        descripcionField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.length() > MAX_DESC_LENGTH) {
                descripcionField.setText(newValue.substring(0, MAX_DESC_LENGTH));
            }
            descripcionCounter.setText(newValue.length() + "/" + MAX_DESC_LENGTH);
        });
    
        // ✅ Validación para stock (solo números enteros positivos y no demasiado altos)
        stockField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                stockField.setText(oldValue);
            } else if (!newValue.isEmpty()) {
                int stockValue = Integer.parseInt(newValue);
                if (stockValue < 0) {
                    stockField.setText("0");
                } else if (stockValue > 100000) {
                    stockField.setText(oldValue);
                }
            }
        });
    
        // ✅ Validación para precio de compra (antes llamado "costo")
        if (costoField != null) {
            costoField.textProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue.matches("\\d*[.,]?\\d*")) {
                    costoField.setText(oldValue);
                } else {
                    costoField.setText(newValue.replace(",", "."));
                }
            });
        }
    
        // ✅ Validación de nombre de imagen (si imagenUrlField sigue existiendo)
        if (imagenUrlField != null) {
            imagenUrlField.textProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue.trim().isEmpty() && !newValue.matches("^.+\\.(jpg|jpeg|png|gif)$")) {
                    mostrarAlerta("⚠ Error", "La URL de la imagen debe ser un archivo válido (jpg, png, gif).", Alert.AlertType.WARNING);
                }
            });
        }
    }
    


    private boolean validarEntrada() {
        StringBuilder mensajeError = new StringBuilder();
        boolean esValido = true;
    
        // ✅ Código
        String codigo = codigoField.getText().trim();
        if (codigo.isEmpty()) {
            mensajeError.append("⚠ Código no puede estar vacío.\n");
            esValido = false;
        } else if (codigo.length() > 50) {
            mensajeError.append("⚠ Código no puede tener más de 50 caracteres.\n");
            esValido = false;
        } else if (!codigo.matches("^[a-zA-Z0-9-_]+$")) {
            mensajeError.append("⚠ Código solo puede contener letras, números, guiones y guiones bajos.\n");
            esValido = false;
        }
    
        // ✅ Nombre
        String nombre = nombreField.getText().trim();
        if (nombre.isEmpty()) {
            mensajeError.append("⚠ Nombre no puede estar vacío.\n");
            esValido = false;
        } else if (!nombre.matches("^[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ'\\- ]+$")) {
            mensajeError.append("⚠ Nombre solo puede contener letras, números, espacios, apóstrofes y guiones.\n");
            esValido = false;
        }
    
        // ✅ Descripción
        String descripcion = descripcionField.getText().trim();
        if (descripcion.isEmpty()) {
            mensajeError.append("⚠ Descripción no puede estar vacía.\n");
            esValido = false;
        } else if (descripcion.length() > MAX_DESC_LENGTH) {
            mensajeError.append("⚠ Descripción no puede exceder " + MAX_DESC_LENGTH + " caracteres.\n");
            esValido = false;
        }
    
        // ✅ Categoría
        Categoria categoriaSeleccionada = categoriaComboBox.getSelectionModel().getSelectedItem();
        if (categoriaSeleccionada == null) {
            mensajeError.append("⚠ Debes seleccionar una categoría válida.\n");
            esValido = false;
        }
    
        // ✅ Imagen (opcional)
        if (imagenUrlField != null && !imagenUrlField.getText().trim().isEmpty()) {
            String imagen = imagenUrlField.getText().trim();
            if (!imagen.matches("^.+\\.(jpg|jpeg|png|gif)$")) {
                mensajeError.append("⚠ La imagen debe ser un archivo válido (jpg, png, gif).\n");
                esValido = false;
            }
        }
    
        // ✅ Fecha de vencimiento
        if (fechaVencField.getValue() == null) {
            mensajeError.append("⚠ Debes seleccionar una fecha de vencimiento.\n");
            esValido = false;
        }
    
        if (!esValido) {
            mostrarAlerta("Error en los datos", mensajeError.toString(), Alert.AlertType.WARNING);
        }
    
        return esValido;
    }
    
    


    @FXML
    private void agregarProducto() {
        if (!validarEntrada()) return;

        LocalDateTime fechaCreacion = LocalDateTime.now();

        LocalDate fechaVencimiento = fechaVencField.getValue();
        if (fechaVencimiento == null) {
            mostrarAlerta("Error", "⚠ Debes seleccionar una fecha de vencimiento.", Alert.AlertType.WARNING);
            return;
        }

        boolean estado = estadoCheckbox.isSelected();

        Categoria categoriaSeleccionada = categoriaComboBox.getSelectionModel().getSelectedItem();
        if (categoriaSeleccionada == null) {
            mostrarAlerta("Error", "⚠ Debes seleccionar una categoría.", Alert.AlertType.WARNING);
            return;
        }

        String nombreImagen = "default.png";

        if (rutaImagenSeleccionada != null && !rutaImagenSeleccionada.isEmpty()) {
            File archivoOriginal = new File(rutaImagenSeleccionada);
            if (archivoOriginal.exists()) {
                String extension = "";
                int lastDot = archivoOriginal.getName().lastIndexOf(".");
                if (lastDot > 0) {
                    extension = archivoOriginal.getName().substring(lastDot);
                }

                String codigo = codigoField.getText().trim().replace(" ", "_");
                String nombre = nombreField.getText().trim().replace(" ", "_");
                nombreImagen = codigo + "_" + nombre + extension;

                String rutaDestino = System.getProperty("user.dir") + "/resources/images/productos/" + nombreImagen;
                File archivoDestino = new File(rutaDestino);

                try {
                    Files.copy(archivoOriginal.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    mostrarAlerta("Error", "❌ No se pudo guardar la imagen del producto. Se usará la imagen por defecto.", Alert.AlertType.WARNING);
                    nombreImagen = "default.png";
                }
            }
        } else {
            // ⚠ Si no hay imagen seleccionada, nos aseguramos de que default.png esté en /resources/images/productos/
            String rutaDestino = System.getProperty("user.dir") + "/resources/images/productos/default.png";
            File archivoDestino = new File(rutaDestino);

            if (!archivoDestino.exists()) {
                try (InputStream defaultStream = getClass().getResourceAsStream("/images/default.png")) {
                    if (defaultStream != null) {
                        Files.copy(defaultStream, archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        System.err.println("❌ No se encontró /images/default.png dentro del classpath.");
                    }
                } catch (IOException e) {
                    System.err.println("❌ Error al copiar imagen por defecto: " + e.getMessage());
                }
            }
        }

        Producto nuevoProducto = new Producto(
            0,
            categoriaSeleccionada.getIdCategoria(),
            categoriaSeleccionada,
            codigoField.getText().trim(),
            nombreField.getText().trim(),
            descripcionField.getText().trim(),
            fechaCreacion,
            nombreImagen,
            estado,
            fechaVencimiento
        );

        try {
            productoService.agregarProducto(nuevoProducto);
            productosLista.add(nuevoProducto);
            mostrarAlerta("Éxito", "✅ Producto agregado correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarProductos();
        } catch (Exception e) {
            if (e.getMessage().contains("Ya existe la llave")) {
                mostrarAlerta("Error", "❌ El código del producto ya existe. Ingresa un código único.", Alert.AlertType.ERROR);
            } else {
                mostrarAlerta("Error", "❌ " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    
    


    @FXML
    private void editarProducto() {
        if (!validarEntrada()) return;
    
        Producto seleccionado = productosTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Error", "⚠ Debes seleccionar un producto para editar.", Alert.AlertType.WARNING);
            return;
        }
    
        LocalDate fechaVenc = fechaVencField.getValue();
        if (fechaVenc == null) {
            mostrarAlerta("Error", "⚠ Debes seleccionar una fecha de vencimiento.", Alert.AlertType.WARNING);
            return;
        }
    
        boolean estado = estadoCheckbox.isSelected();
        Categoria categoriaSeleccionada = categoriaComboBox.getSelectionModel().getSelectedItem();
        if (categoriaSeleccionada == null) {
            mostrarAlerta("Error", "⚠ Debes seleccionar una categoría.", Alert.AlertType.WARNING);
            return;
        }
    
        String nuevoCodigo = codigoField.getText().trim().replace(" ", "_");
        String nuevoNombre = nombreField.getText().trim().replace(" ", "_");
    
        String carpetaImagenes = System.getProperty("user.dir") + "/resources/images/productos/";
        File imagenActual = new File(carpetaImagenes + seleccionado.getImagenUrl());
    
        boolean imagenCambiada = rutaImagenSeleccionada != null && !rutaImagenSeleccionada.isEmpty();
        boolean nombreCambiado = !nuevoCodigo.equals(seleccionado.getCodigo()) || !nuevoNombre.equals(seleccionado.getNombre());
    
        String imagenFinal = seleccionado.getImagenUrl();
    
        if (imagenCambiada) {
            if (imagenActual.exists() && !seleccionado.getImagenUrl().equals("default.png")) {
                imagenActual.delete();
            }
        
            File archivoOriginal = new File(rutaImagenSeleccionada);
            if (archivoOriginal.exists()) {
                String extension = archivoOriginal.getName().substring(archivoOriginal.getName().lastIndexOf("."));
                imagenFinal = nuevoCodigo + "_" + nuevoNombre + extension;
        
                File archivoDestino = new File(carpetaImagenes + imagenFinal);
                try {
                    Files.copy(archivoOriginal.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    mostrarAlerta("Error", "❌ No se pudo guardar la nueva imagen. Se mantendrá la anterior.", Alert.AlertType.WARNING);
                    imagenFinal = seleccionado.getImagenUrl(); // En caso de fallo
                }
            }
        } else if (nombreCambiado) {
            if (imagenActual.exists() && !seleccionado.getImagenUrl().equals("default.png")) {
                String extension = imagenActual.getName().substring(imagenActual.getName().lastIndexOf("."));
                String nuevoNombreImagen = nuevoCodigo + "_" + nuevoNombre + extension;
                File nuevaImagen = new File(carpetaImagenes + nuevoNombreImagen);
        
                if (imagenActual.renameTo(nuevaImagen)) {
                    imagenFinal = nuevoNombreImagen;
                } else {
                    mostrarAlerta("Error", "❌ No se pudo renombrar la imagen existente. Se mantendrá la anterior.", Alert.AlertType.WARNING);
                    imagenFinal = seleccionado.getImagenUrl();
                }
            }
        }
        
    
        Producto productoEditado = new Producto(
            seleccionado.getId(),
            categoriaSeleccionada.getIdCategoria(),
            categoriaSeleccionada,
            nuevoCodigo,
            nuevoNombre,
            descripcionField.getText().trim(),
            LocalDateTime.now(),
            imagenFinal,
            estado,
            fechaVenc
        );
    
        try {
            productoService.actualizarProducto(productoEditado);
            cargarProductos();
            mostrarAlerta("Éxito", "✅ Producto actualizado correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
        } catch (Exception e) {
            if (e.getMessage().contains("Ya existe la llave")) {
                mostrarAlerta("Error", "❌ El código del producto ya existe. Usa un código diferente.", Alert.AlertType.ERROR);
            } else {
                mostrarAlerta("Error", "❌ " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    



    @FXML
    private void eliminarProducto() {
        Producto seleccionado = productosTable.getSelectionModel().getSelectedItem();
        
        if (seleccionado == null) {
            mostrarAlerta("Error", "⚠ Selecciona un producto para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        // ✅ Confirmación antes de eliminar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación");
        confirmacion.setHeaderText("Eliminar Producto");
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar el producto '" + seleccionado.getNombre() + "'?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean eliminado = productoService.eliminarProducto(seleccionado.getId());
                if (eliminado) {
                    productosLista.remove(seleccionado); // ✅ Remueve solo si se eliminó correctamente
                    productosTable.refresh(); // ✅ Refresca la tabla después de eliminar

                    // ✅ Eliminación de la imagen del producto si existe
                    if (seleccionado.getImagenUrl() != null && !seleccionado.getImagenUrl().isEmpty()) {
                        String rutaImagen = System.getProperty("user.dir") + "/resources/images/productos/" + seleccionado.getImagenUrl();
                        File archivoImagen = new File(rutaImagen);

                        if (archivoImagen.exists()) {
                            if (archivoImagen.delete()) {
                                System.out.println("✅ Imagen eliminada: " + seleccionado.getImagenUrl());
                            } else {
                                System.err.println("⚠ No se pudo eliminar la imagen: " + seleccionado.getImagenUrl());
                            }
                        }
                    }

                    // ✅ Limpia los campos del formulario después de eliminar
                    limpiarCampos();

                    mostrarAlerta("Éxito", "✅ Producto eliminado correctamente.", Alert.AlertType.INFORMATION);
                } else {
                    mostrarAlerta("Error", "❌ No se pudo eliminar el producto. Verifica si tiene dependencias.", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                mostrarAlerta("Error", "❌ Ocurrió un error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }



    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        if (mensaje == null || mensaje.trim().isEmpty()) return; // Evita alertas vacías

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        
        // ✅ Evita encabezados vacíos para una mejor presentación
        if (tipo == Alert.AlertType.ERROR) {
            alerta.setHeaderText("❌ Error");
        } else if (tipo == Alert.AlertType.WARNING) {
            alerta.setHeaderText("⚠ Advertencia");
        } else if (tipo == Alert.AlertType.INFORMATION) {
            alerta.setHeaderText("✅ Información");
        } else {
            alerta.setHeaderText(null);
        }
        
        alerta.setContentText(mensaje);
        
        // ✅ Si es un error o advertencia, el usuario debe confirmar. Si es información, solo se muestra
        if (tipo == Alert.AlertType.ERROR || tipo == Alert.AlertType.WARNING) {
            alerta.showAndWait(); // Espera a que el usuario la cierre
        } else {
            alerta.show(); // Muestra la alerta sin bloquear la ejecución
        }
    }


    /**
     * 🔹 Método para limpiar todos los campos del formulario de productos.
     */
    private void limpiarCampos() {
        // ✅ Limpiar todos los campos de texto
        if (codigoField != null) codigoField.clear();
        if (nombreField != null) nombreField.clear();
        if (descripcionField != null) descripcionField.clear(); // ✅ Asegurar limpieza correcta de TextArea
        if (precioField != null) precioField.clear();
        if (costoField != null) costoField.clear();
        if (stockField != null) stockField.clear();

        // ✅ Resetear la selección de la categoría
        if (categoriaComboBox != null) categoriaComboBox.getSelectionModel().clearSelection();

        // ✅ Resetear la fecha de vencimiento
        if (fechaVencField != null) fechaVencField.setValue(null);

        // ✅ Resetear la fecha de compra
        if (fechaCompraField != null) fechaCompraField.setValue(null);

        // ✅ Resetear imagen
        if (imagenUrlField != null) imagenUrlField.clear();
        if (imagenPathLabel != null) imagenPathLabel.setText("No seleccionada");
        if (imagenProducto != null) imagenProducto.setImage(null);

        // ✅ Desmarcar estado
        if (estadoCheckbox != null) estadoCheckbox.setSelected(false);

        // ✅ Forzar el refresco de los campos (previene errores visuales en JavaFX)
        Platform.runLater(() -> {
            if (codigoField != null) codigoField.requestFocus();
        });

        // ✅ Mensaje de depuración (opcional)
        System.out.println("✔ Campos del formulario limpiados correctamente.");
    }


    /**
     * 🔹 Método para limpiar la selección de la tabla y los campos del formulario.
     */
    @FXML
    private void limpiarSeleccion() {
        if (productosTable != null) {
            productosTable.getSelectionModel().clearSelection(); // ✅ Deselecciona cualquier producto seleccionado
        }
        limpiarCampos(); // ✅ Llama al método que limpia todos los campos del formulario

        // ✅ Mensaje opcional de depuración
        System.out.println("✔ Selección limpiada y formulario reseteado.");
    }

}
