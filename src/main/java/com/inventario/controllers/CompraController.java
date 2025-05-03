package com.inventario.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.inventario.models.Compra;
import com.inventario.models.DetalleCompra;
import com.inventario.models.Producto;
import com.inventario.models.Proveedor;
import com.inventario.models.TipoComprobante;
import com.inventario.services.CompraService;
import com.inventario.services.DetalleCompraService;
import com.inventario.services.ProductoService;
import com.inventario.services.ProveedorService;
import com.inventario.services.TipoComprobanteService;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CompraController {

    @FXML private ComboBox<Proveedor> proveedorComboBox;
    @FXML private ComboBox<TipoComprobante> tipoComprobanteComboBox;
    @FXML private ComboBox<String> tipoPagoComboBox;

    @FXML private TableView<DetalleCompra> tablaDetalles;
    @FXML private TableColumn<DetalleCompra, String> colProducto;
    @FXML private TableColumn<DetalleCompra, Integer> colCantidad;
    @FXML private TableColumn<DetalleCompra, BigDecimal> colPrecio;
    @FXML private TableColumn<DetalleCompra, BigDecimal> colTotal;

    @FXML private Label subtotalLabel;
    @FXML private Label ivaLabel;
    @FXML private Label totalLabel;

    @FXML private TextField numeroCompraField;
    @FXML private Button btnAgregarProducto;
    @FXML private Button btnGuardar;

    @FXML
    private ComboBox<Producto> productoComboBox;
    @FXML
    private Button btnNuevoProducto;


    private final ProveedorService proveedorService = new ProveedorService();
    private final TipoComprobanteService comprobanteService = new TipoComprobanteService();
    private final CompraService compraService = new CompraService();
    private final DetalleCompraService detalleCompraService = new DetalleCompraService();
    private final ProductoService productoService = new ProductoService();

    private final ObservableList<DetalleCompra> detallesCompra = FXCollections.observableArrayList();

    private final BigDecimal IVA = new BigDecimal("0.13");

    @FXML
    public void initialize() {
        cargarCombos();
        configurarTabla();
        tablaDetalles.setItems(detallesCompra);
        calcularTotales();
        asignarNumeroCompraAutomatico();

    }
    

    private void cargarCombos() {
        // Cargar proveedores
        ObservableList<Proveedor> proveedores = FXCollections.observableArrayList(proveedorService.obtenerProveedores());
        proveedorComboBox.setItems(proveedores);
    
        // Mostrar nombre y razón social en el combo de proveedor
        proveedorComboBox.setConverter(new javafx.util.StringConverter<Proveedor>() {
            @Override
            public String toString(Proveedor proveedor) {
                if (proveedor == null) return "";
                return proveedor.getNombre() + " (" + proveedor.getEmpresa() + ")";
            }
    
            @Override
            public Proveedor fromString(String string) {
                return proveedores.stream()
                    .filter(p -> (p.getNombre() + " (" + p.getRazonSocial() + ")").equals(string))
                    .findFirst().orElse(null);
            }
        });
    
        // Cargar tipo comprobante
        tipoComprobanteComboBox.setItems(FXCollections.observableArrayList(comprobanteService.obtenerTodos()));
    
        // Cargar tipo de pago
        tipoPagoComboBox.setItems(FXCollections.observableArrayList("Efectivo", "Transferencia", "QR", "Otro"));
    }
    

    private void configurarTabla() {
        colProducto.setCellValueFactory(cell -> new SimpleStringProperty(
                productoService.obtenerProductoPorId(cell.getValue().getIdProducto()).getNombre()));
        colCantidad.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getCantidad()));
        colPrecio.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getPrecioUnitario()));
        colTotal.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getTotal()));
    }

    @FXML
    private void agregarProducto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CatalogoProductosView.fxml"));
            Parent root = loader.load();
    
            Stage stage = new Stage();
            stage.setTitle("Seleccionar Producto");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
    
            // Obtener el producto seleccionado
            CatalogoProductosController controller = loader.getController();
            Producto productoSeleccionado = controller.getProductoSeleccionado();
    
            if (productoSeleccionado != null) {
                solicitarCantidadYPrecio(productoSeleccionado);
            }
    
        } catch (IOException e) {
            mostrarAlerta("Error", "❌ No se pudo abrir el catálogo de productos.", Alert.AlertType.ERROR);
        }
    }
    
    private void asignarNumeroCompraAutomatico() {
        try {
            int ultimoNumero = compraService.obtenerUltimoNumeroCompra();
            int nuevoNumero = ultimoNumero + 1;
            numeroCompraField.setText(String.valueOf(nuevoNumero));
            numeroCompraField.setEditable(false); // Evita que el usuario lo edite manualmente
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo generar el número de compra automáticamente.", Alert.AlertType.ERROR);
            numeroCompraField.setText("0"); // fallback
        }
    }


    private void solicitarCantidadYPrecio(Producto producto) {
        TextInputDialog cantidadDialog = new TextInputDialog("1");
        cantidadDialog.setTitle("Cantidad");
        cantidadDialog.setHeaderText("Cantidad a comprar");
        cantidadDialog.setContentText("Ingrese la cantidad:");
    
        TextInputDialog precioDialog = new TextInputDialog("0.00");
        precioDialog.setTitle("Precio");
        precioDialog.setHeaderText("Precio de compra unitario");
        precioDialog.setContentText("Ingrese el precio:");
    
        cantidadDialog.showAndWait().ifPresent(cantidadStr -> {
            precioDialog.showAndWait().ifPresent(precioStr -> {
                try {
                    int cantidad = Integer.parseInt(cantidadStr.trim());
                    BigDecimal precio = new BigDecimal(precioStr.trim());
    
                    if (cantidad <= 0 || precio.compareTo(BigDecimal.ZERO) <= 0) {
                        mostrarAlerta("Valores inválidos", "La cantidad y el precio deben ser mayores que cero.", Alert.AlertType.WARNING);
                        return;
                    }
    
                    BigDecimal total = precio.multiply(BigDecimal.valueOf(cantidad));
    
                    DetalleCompra detalle = new DetalleCompra(0, producto.getId(), 0, cantidad, precio, total);
                    detalle.setProducto(producto);
                    detallesCompra.add(detalle);
                    tablaDetalles.refresh();
                    calcularTotales();
    
                } catch (NumberFormatException e) {
                    mostrarAlerta("Entrada inválida", "Cantidad o precio no válidos.", Alert.AlertType.WARNING);
                }
            });
        });
    }
    

    @FXML
    private void abrirFormularioRegistroProducto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RegistrarProductoDesdeCompraView.fxml"));
            Parent root = loader.load();

            // Opcional: puedes pasar info desde aquí al controller hijo
            Stage stage = new Stage();
            stage.setTitle("Registrar Nuevo Producto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // bloquea hasta cerrar
            stage.showAndWait();

            // Después de cerrar, puedes actualizar la lista de productos
            productoComboBox.setItems(FXCollections.observableArrayList(productoService.obtenerProductos()));

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir el formulario de registro.", Alert.AlertType.ERROR);
        }
    }


    private void manejarProductoDesdeInput(String input) {
        Producto producto = productoService.obtenerProductoPorCodigo(input.trim());
    
        if (producto == null) {
            mostrarAlerta("Producto no encontrado", "No se encontró el producto. Debes registrarlo previamente.", Alert.AlertType.WARNING);
            return;
        }
    
        TextInputDialog cantidadDialog = new TextInputDialog("1");
        cantidadDialog.setTitle("Cantidad");
        cantidadDialog.setHeaderText("Cantidad a comprar");
        cantidadDialog.setContentText("Ingrese la cantidad:");
    
        TextInputDialog precioDialog = new TextInputDialog("0.00");
        precioDialog.setTitle("Precio");
        precioDialog.setHeaderText("Precio de compra unitario");
        precioDialog.setContentText("Ingrese el precio:");
    
        cantidadDialog.showAndWait().ifPresent(cantidadStr -> {
            precioDialog.showAndWait().ifPresent(precioStr -> {
                try {
                    int cantidad = Integer.parseInt(cantidadStr.trim());
                    BigDecimal precio = new BigDecimal(precioStr.trim());
    
                    if (cantidad <= 0 || precio.compareTo(BigDecimal.ZERO) <= 0) {
                        mostrarAlerta("Valores inválidos", "La cantidad y el precio deben ser mayores que cero.", Alert.AlertType.WARNING);
                        return;
                    }
    
                    BigDecimal total = precio.multiply(BigDecimal.valueOf(cantidad));
    
                    DetalleCompra detalle = new DetalleCompra(0, producto.getId(), 0, cantidad, precio, total);
                    detalle.setProducto(producto);
                    detallesCompra.add(detalle);
                    tablaDetalles.refresh();
                    calcularTotales();
    
                } catch (NumberFormatException e) {
                    mostrarAlerta("Entrada inválida", "Cantidad o precio no válidos.", Alert.AlertType.WARNING);
                }
            });
        });
    }
    




    private void calcularTotales() {
        BigDecimal total = detallesCompra.stream()
            .map(DetalleCompra::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalLabel.setText("Bs " + total);
    }


    @FXML
    private void guardarCompra() {
        if (proveedorComboBox.getValue() == null || tipoComprobanteComboBox.getValue() == null || tipoPagoComboBox.getValue() == null) {
            mostrarAlerta("Campos obligatorios", "Debes seleccionar proveedor, tipo de comprobante y tipo de pago.", Alert.AlertType.WARNING);
            return;
        }
    
        if (detallesCompra.isEmpty()) {
            mostrarAlerta("Sin productos", "Debe agregar al menos un producto a la compra.", Alert.AlertType.WARNING);
            return;
        }
    
        Compra compra = new Compra();
        compra.setIdProveedor(proveedorComboBox.getValue().getIdProveedor());
        compra.setIdUsuario(1); // Si tienes auth, reemplaza por el ID del usuario logueado
        compra.setIdTipoComprobante(tipoComprobanteComboBox.getValue().getIdTipoComp());
        compra.setNumeroCompra(Integer.parseInt(numeroCompraField.getText()));
        compra.setTipoPago(tipoPagoComboBox.getValue());
        compra.setFecha(LocalDateTime.now());
        compra.setEstado("Activo");
    
        BigDecimal total = detallesCompra.stream()
            .map(DetalleCompra::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        compra.setTotal(total);
    
        try {
            int idCompraGenerado = compraService.registrarCompra(compra);
    
            if (idCompraGenerado > 0) {
                // 🔹 Guardar detalles Y actualizar inventario en cascada desde el service
                boolean exito = detalleCompraService.guardarDetalles(detallesCompra, idCompraGenerado);
    
                if (exito) {
                    mostrarAlerta("Éxito", "✅ Compra registrada exitosamente.", Alert.AlertType.INFORMATION);
                    limpiarTodo();
                    asignarNumeroCompraAutomatico(); // 🆕 asigna el nuevo número siguiente
                } else {
                    mostrarAlerta("Error", "❌ Ocurrió un error al guardar los detalles o inventario.", Alert.AlertType.ERROR);
                }
            } else {
                mostrarAlerta("Error", "❌ No se pudo registrar la compra.", Alert.AlertType.ERROR);
            }
    
        } catch (Exception e) {
            mostrarAlerta("Error", "❌ " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    
    

    private void limpiarTodo() {
        proveedorComboBox.getSelectionModel().clearSelection();
        tipoComprobanteComboBox.getSelectionModel().clearSelection();
        tipoPagoComboBox.getSelectionModel().clearSelection();
        numeroCompraField.clear();
        detallesCompra.clear();
        calcularTotales();
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
