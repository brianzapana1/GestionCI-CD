package com.inventario.controllers;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import com.inventario.dao.InventarioDAO;
import com.inventario.models.Cliente;
import com.inventario.models.DetalleImpuesto;
import com.inventario.models.DetalleVenta;
import com.inventario.models.Inventario;
import com.inventario.models.Producto;
import com.inventario.models.TipoComprobante;
import com.inventario.models.Venta;
import com.inventario.services.DetalleImpuestoService;
import com.inventario.services.InventarioService;
import com.inventario.services.ProductoService;
import com.inventario.services.VentaService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import javafx.application.Platform;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.BigDecimalStringConverter;

public class VentaController {
    @FXML private TextField txtCliente;
    @FXML private TextField txtNIT;
    @FXML private TextField txtProducto;
    @FXML private TextField txtStock;
    // @FXML private TextField txtPrecio;
    @FXML private TextField txtCantidad;
    // @FXML private TextField txtTotal;
    @FXML private Label lblFecha;
    @FXML private TextField txtDescuento;
    // @FXML private TextField txtIVA;
    @FXML private TextField txtTotalPagar;
    @FXML private TextField txtSubtotal;
    @FXML private TableView<DetalleVenta> tblDetalleVenta;
    @FXML private Button btnAgregar;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnNuevo;
    @FXML private Button btnBuscarCliente; // Asegúrate de tener este botón en el FXML
    @FXML private TextField txtComprobante;
    @FXML private Button btnSeleccionarComprobante; // Agregar referencia al botón
    @FXML private CheckBox chkEditarIVA;
    @FXML private CheckBox chkEditarDescuento;
    @FXML private Label lblStockDisponible;


    
    private final VentaService ventaService;
    private final ProductoService productoService;
    private final InventarioService inventarioService = new InventarioService();

    private ObservableList<DetalleVenta> detallesVenta;
    private Venta ventaActual;

    public VentaController() {
        this.ventaService = new VentaService();
        this.productoService = new ProductoService();
        this.detallesVenta = FXCollections.observableArrayList();
        this.ventaActual = new Venta();
    }

    @FXML
    public void initialize() {
        if (lblFecha != null) {
            lblFecha.setText(LocalDateTime.now().toString());
        } else {
            System.err.println("⚠ Error: lblFecha no ha sido inicializado en el FXML.");
        }

        tblDetalleVenta.setItems(detallesVenta);
        configurarTabla();

        btnBuscarCliente.setOnAction(event -> abrirSeleccionarCliente());
        configurarDescuento();

        // Validación para cantidad (solo enteros)
        txtCantidad.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                newValue = newValue.replaceAll(" ", "");
                if (!newValue.matches("\\d*")) {
                    txtCantidad.setText(oldValue);
                }
            }
        });

        // Ajuste de tamaño desde el controlador
        Platform.runLater(() -> {
            Stage stage = (Stage) lblFecha.getScene().getWindow();
            stage.setWidth(1100);  // 🟥 Ajusta el ancho deseado
            stage.setHeight(750);  // 🟥 Ajusta el alto deseado
            stage.setResizable(false); // Opcional: evitar redimensionamiento
        });

        tblDetalleVenta.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }


    /**
     * 🔹 Configura las columnas de la tabla de ventas.
     */
    private void configurarTabla() {
        tblDetalleVenta.setEditable(true);
    
        TableColumn<DetalleVenta, String> colProducto = new TableColumn<>("Producto");
        colProducto.setCellValueFactory(cellData -> cellData.getValue().nombreProductoProperty());
    
        TableColumn<DetalleVenta, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());
    
        TableColumn<DetalleVenta, BigDecimal> colCostoCompra = new TableColumn<>("Precio Compra");
        colCostoCompra.setCellValueFactory(cellData -> cellData.getValue().costoProperty());
    
        TableColumn<DetalleVenta, BigDecimal> colPrecioVenta = new TableColumn<>("Precio Venta");
        colPrecioVenta.setCellValueFactory(cellData -> cellData.getValue().precioVentaProperty());
    
        // 🟣 Celda editable con guardado al perder foco
        colPrecioVenta.setCellFactory(column -> {
            TextFieldTableCell<DetalleVenta, BigDecimal> cell = new TextFieldTableCell<>(new BigDecimalStringConverter()) {
                @Override
                public void updateItem(BigDecimal item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.compareTo(BigDecimal.ZERO) == 0) {
                        setText(""); // Mostrar campo vacío
                    } else {
                        setText(item.toPlainString());
                    }
                }
            };
    
            // 🔄 Guardar valor al salir de la celda
            cell.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused && cell.isEditing()) {
                    try {
                        String texto = cell.getText().replace(",", ".").trim();
                        if (!texto.matches("[0-9]*\\.?[0-9]+")) {
                            throw new NumberFormatException("Caracteres inválidos");
                        }
                        cell.commitEdit(new BigDecimal(texto));
                    } catch (Exception e) {
                        cell.cancelEdit();
                    }
                }
            });
    
            return cell;
        });
    
        // 🧠 Validación al editar
        colPrecioVenta.setOnEditCommit(event -> {
            DetalleVenta detalle = event.getRowValue();
            String input = event.getNewValue() != null ? event.getNewValue().toString().replace(",", ".") : "";
    
            try {
                if (!input.matches("[0-9]*\\.?[0-9]+")) {
                    throw new NumberFormatException("Caracteres inválidos");
                }
    
                BigDecimal nuevoPrecio = new BigDecimal(input);
    
                if (nuevoPrecio.compareTo(BigDecimal.ZERO) <= 0) {
                    mostrarAlerta("❌ El precio de venta debe ser mayor a cero.");
                    detalle.setPrecioVenta(BigDecimal.ZERO);
                    detalle.setTotal(BigDecimal.ZERO);
                } else if (nuevoPrecio.compareTo(detalle.getCosto()) < 0) {
                    mostrarAlerta("❌ El precio de venta no puede ser menor al precio de compra.");
                    detalle.setPrecioVenta(BigDecimal.ZERO);
                    detalle.setTotal(BigDecimal.ZERO);
                } else {
                    detalle.setPrecioVenta(nuevoPrecio);
                    detalle.setTotal(nuevoPrecio.multiply(BigDecimal.valueOf(detalle.getCantidad())));
                }
    
            } catch (NumberFormatException e) {
                mostrarAlerta("❌ Solo se permiten números. Use punto (.) como separador decimal.");
                detalle.setPrecioVenta(BigDecimal.ZERO);
                detalle.setTotal(BigDecimal.ZERO);
            }
    
            tblDetalleVenta.refresh();
            calcularTotales();
        });
    
        TableColumn<DetalleVenta, BigDecimal> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(cellData -> cellData.getValue().totalProperty());
    
        tblDetalleVenta.getColumns().clear();
        tblDetalleVenta.getColumns().addAll(colProducto, colCantidad, colCostoCompra, colPrecioVenta, colTotal);
    }
    
    
    
    
    

    @FXML
    private void abrirSeleccionarCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ClienteView.fxml"));
            Parent root = loader.load();
            ClienteController clienteController = loader.getController();
            
            // 🔹 Enlazar con el módulo de ventas
            clienteController.setVentaController(this);
    
            Stage stage = new Stage();
            stage.setTitle("Seleccionar Cliente");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
    
            // 🔹 Obtener cliente seleccionado y actualizar campos
            Cliente clienteSeleccionado = clienteController.getClienteSeleccionado();
            if (clienteSeleccionado != null) {
                txtCliente.setText(clienteSeleccionado.getRazonSocial());
                txtNIT.setText(clienteSeleccionado.getNit());
            }
        } catch (IOException e) {
            System.err.println("❌ Error al abrir la ventana de selección de clientes: " + e.getMessage());
        }
    }
    


    public void actualizarClienteSeleccionado(Cliente cliente) {
    if (cliente != null) {
        txtCliente.setText(cliente.getRazonSocial());
        txtNIT.setText(cliente.getNit());
    }
    }


    /**
     * 🔹 Abre el catálogo de productos para seleccionar uno.
     */
    @FXML
    public void abrirCatalogoProductos() {
        Producto productoSeleccionado = mostrarCatalogoProductos();
    
        if (productoSeleccionado != null) {
            txtProducto.setText(productoSeleccionado.getNombre());
    
            // 🔻 Eliminamos uso de stock y precio porque ya no están en Producto
            int stockTotal = inventarioService.obtenerStockTotalPorProducto(productoSeleccionado.getId());
            txtStock.setText(String.valueOf(stockTotal));
                        // txtPrecio.setText("?");
            
            // 📝 Puedes reemplazar "?" por un mensaje como "Consultar" o dejarlo vacío si prefieres:
            // txtStock.setText("Consultar");
            // txtPrecio.setText("N/A");
        }
    }
    
    

    /**
     * 🔹 Abre el catálogo de productos y permite seleccionar uno.
     */
    private Producto mostrarCatalogoProductos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CatalogoProductosView.fxml"));
            Parent root = loader.load();
            CatalogoProductosController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Seleccionar Producto");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            return controller.getProductoSeleccionado();
        } catch (Exception e) {
            System.err.println("❌ Error al abrir catálogo: " + e.getMessage());
            return null;
        }
    }

    // private void configurarIVA() {
    //     // ✅ Establecer el IVA predeterminado en 13%
    //     txtIVA.setText("13");
    //     txtIVA.setEditable(false); // Deshabilitar edición
    //     txtIVA.setStyle("-fx-background-color: lightgray;");
    
    //     // ✅ Configurar el CheckBox para habilitar la edición del IVA
    //     chkEditarIVA.setOnAction(event -> {
    //         boolean editable = chkEditarIVA.isSelected();
    //         txtIVA.setEditable(editable);
    //         if (editable) {
    //             txtIVA.setStyle(""); // Quitar fondo gris si se activa la edición
    //             txtIVA.clear();
    //         } else {
    //             txtIVA.setText("13"); // Restaurar valor por defecto
    //             txtIVA.setStyle("-fx-background-color: lightgray;");
    //         }
    //         calcularTotales();
    //     });
    
    //     txtIVA.textProperty().addListener((observable, oldValue, newValue) -> {
    //         if (!newValue.matches("\\d*\\.?\\d*")) {
    //             txtIVA.setText(oldValue); // ✅ Solo permitir números y punto decimal
    //         }
    //         calcularTotales();
    //     });
    // }

    
    private void configurarDescuento() {
        // ✅ Establecer el descuento predeterminado en 0%
        txtDescuento.setText("0");
        txtDescuento.setEditable(false); // Deshabilitar edición por defecto
        txtDescuento.setStyle("-fx-background-color: lightgray;");
    
        // ✅ Configurar el CheckBox para habilitar la edición del descuento
        chkEditarDescuento.setOnAction(event -> {
            boolean editable = chkEditarDescuento.isSelected();
            txtDescuento.setEditable(editable);
            if (editable) {
                txtDescuento.setStyle(""); // Quitar fondo gris si se activa la edición
                txtDescuento.clear();
            } else {
                txtDescuento.setText("0"); // Restaurar valor por defecto
                txtDescuento.setStyle("-fx-background-color: lightgray;");
            }
            calcularTotales(); // Recalcular cuando se active/desactive
        });
    
        // ✅ Validar entrada de números en txtDescuento en tiempo real
        txtDescuento.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                txtDescuento.setText(oldValue); // Solo permitir números y punto decimal
            }
            calcularTotales(); // Recalcular cuando se escriba un nuevo valor
        });
    }
    

    @FXML
private void abrirSeleccionarComprobante() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TipoComprobanteView.fxml"));
        Parent root = loader.load();
        TipoComprobanteController tipoComprobanteController = loader.getController();

        // 🔹 Enlazar con el módulo de ventas
        tipoComprobanteController.setVentaController(this);

        Stage stage = new Stage();
        stage.setTitle("Seleccionar Tipo de Comprobante");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        // 🔹 Obtener el tipo de comprobante seleccionado y actualizar campo
        TipoComprobante comprobanteSeleccionado = tipoComprobanteController.getTipoComprobanteSeleccionado();
        if (comprobanteSeleccionado != null) {
            txtComprobante.setText(comprobanteSeleccionado.getDescripcion()); // ✅ Se muestra la descripción en UI
            ventaActual.setIdTipoComprobante(comprobanteSeleccionado.getIdTipoComp()); // ✅ Se asigna el ID
            System.out.println("✅ Tipo de comprobante guardado en ventaActual: " + comprobanteSeleccionado.getIdTipoComp());
        } else {
            System.err.println("⚠ No se seleccionó ningún tipo de comprobante.");
        }
    } catch (IOException e) {
        System.err.println("❌ Error al abrir la ventana de selección de comprobantes: " + e.getMessage());
    }
}

    /**
     * 🔹 Recibe el tipo de comprobante seleccionado desde `TipoComprobanteController`.
     */
    public void actualizarTipoComprobante(TipoComprobante tipoComprobante) {
        if (tipoComprobante != null) {
            txtComprobante.setText(tipoComprobante.getDescripcion());
            ventaActual.setIdTipoComprobante(tipoComprobante.getIdTipoComp());
    
            System.out.println("✅ Tipo de comprobante actualizado en la venta:");
            System.out.println("   - ID: " + ventaActual.getIdTipoComprobante());
            System.out.println("   - Descripción: " + tipoComprobante.getDescripcion());
        } else {
            System.err.println("⚠ No se recibió un tipo de comprobante válido.");
        }
    }
    

    /**
     * 🔹 Agrega un producto a la lista de la venta.
     */
    @FXML
    public void agregarProductoAVenta() {
        if (txtProducto.getText().isEmpty() || txtCantidad.getText().isEmpty()) {
            mostrarAlerta("Debe seleccionar un producto y definir la cantidad.");
            return;
        }
    
        String nombreIngresado = txtProducto.getText().trim();
        List<Producto> productos = productoService.obtenerProductos();
    
        Producto producto = productos.stream()
            .filter(p -> p.getNombre().equalsIgnoreCase(nombreIngresado) || p.getCodigo().equalsIgnoreCase(nombreIngresado))
            .findFirst()
            .orElse(null);
    
        if (producto == null) {
            mostrarAlerta("Producto no encontrado. Verifique que el nombre o código es correcto.");
            return;
        }
    
        int cantidadSolicitada;
        try {
            cantidadSolicitada = Integer.parseInt(txtCantidad.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Ingrese una cantidad válida.");
            return;
        }
    
        if (cantidadSolicitada <= 0) {
            mostrarAlerta("La cantidad debe ser mayor a 0.");
            return;
        }
    
        InventarioDAO inventarioDAO = new InventarioDAO();
        List<Inventario> inventarios = inventarioDAO.obtenerInventarioDisponiblePorProductoPEPS(producto.getId());
    
        int cantidadRestante = cantidadSolicitada;
        int totalDisponible = 0;
    
        for (Inventario inv : inventarios) {
            // 🔍 Validar cuánto ya se está usando en vista por ese detallecomp
            int yaUsadoEnVista = detallesVenta.stream()
                .filter(d -> d.getIdProducto() == producto.getId() && d.getIdDetalleCompra() == inv.getIdDetalleCompra())
                .mapToInt(DetalleVenta::getCantidad)
                .sum();
    
            int disponibleLote = inv.getCantidadDisponible() - yaUsadoEnVista;
            if (disponibleLote <= 0) continue;
    
            totalDisponible += disponibleLote;
    
            int cantidadUsada = Math.min(disponibleLote, cantidadRestante);
            BigDecimal costoUnitario = productoService.obtenerCostoUnitarioPorDetalleCompra(inv.getIdDetalleCompra());
    
            DetalleVenta detalle = new DetalleVenta(
                0,
                0,
                producto.getId(),
                producto.getNombre(),
                cantidadUsada,
                costoUnitario,
                null, // A definir luego
                BigDecimal.ZERO
            );
            detalle.setIdDetalleCompra(inv.getIdDetalleCompra()); // ✅ Para identificar lote
            detallesVenta.add(detalle);
            cantidadRestante -= cantidadUsada;
    
            if (cantidadRestante <= 0) break;
        }
    
        // 🔴 Mostrar errores si no hay suficiente stock o faltó cubrir
        if (totalDisponible <= 0) {
            mostrarAlerta("⚠ Ya no hay stock disponible para este producto.");
            return;
        }
    
        if (cantidadRestante > 0) {
            mostrarAlerta("⚠ Solo se pudo agregar " + (cantidadSolicitada - cantidadRestante) + " unidades.");
        }
    
        // 🔄 Recalcular stock restante en vista
        int totalAgregado = detallesVenta.stream()
            .filter(d -> d.getIdProducto() == producto.getId())
            .mapToInt(DetalleVenta::getCantidad)
            .sum();
    
        int stockTotalBD = inventarios.stream()
            .mapToInt(Inventario::getCantidadDisponible)
            .sum();
    
        int stockDisponible = stockTotalBD - totalAgregado;
        lblStockDisponible.setText("Stock disponible: " + stockDisponible);
        lblStockDisponible.setStyle("-fx-text-fill: #e91e63; -fx-font-weight: bold;");
    
        calcularTotales();
        tblDetalleVenta.refresh();
    }
    
    
    
    
    

    /**
     * 🔹 Elimina un producto seleccionado de la lista.
     */
    @FXML
    private void eliminarProducto() {
        DetalleVenta productoSeleccionado = tblDetalleVenta.getSelectionModel().getSelectedItem();
    
        if (productoSeleccionado == null) {
            mostrarAlerta("Debe seleccionar un producto para eliminar.");
            return;
        }
    
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de eliminar este producto?", ButtonType.YES, ButtonType.NO);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.showAndWait();
    
        if (confirmacion.getResult() == ButtonType.YES) {
            detallesVenta.remove(productoSeleccionado);
            calcularTotales();
            tblDetalleVenta.refresh();
    
            // 🔄 Recalcular stock disponible en tiempo real
            InventarioDAO inventarioDAO = new InventarioDAO();
            List<Inventario> inventarios = inventarioDAO.obtenerInventarioDisponiblePorProductoPEPS(productoSeleccionado.getIdProducto());
    
            int stockTotal = inventarios.stream()
                .mapToInt(Inventario::getCantidadDisponible)
                .sum();
    
            int yaAgregado = detallesVenta.stream()
                .filter(d -> d.getIdProducto() == productoSeleccionado.getIdProducto())
                .mapToInt(DetalleVenta::getCantidad)
                .sum();
    
            int disponible = stockTotal - yaAgregado;
    
            if (detallesVenta.isEmpty()) {
                lblStockDisponible.setText(""); // 🔇 Oculta mensaje si no hay productos
            } else {
                lblStockDisponible.setText("Stock disponible: " + disponible);
                lblStockDisponible.setStyle("-fx-text-fill: #e91e63; -fx-font-weight: bold;");
            }
        }
    }
    
    

    /**
     * 🔹 Calcula el total de la venta.
     */
    private void calcularTotales() {
        BigDecimal subtotal = BigDecimal.ZERO;
    
        for (DetalleVenta detalle : detallesVenta) {
            subtotal = subtotal.add(detalle.getTotal());
        }
    
        txtSubtotal.setText(subtotal.setScale(2, RoundingMode.HALF_UP).toString());
    
        BigDecimal descuentoPorcentaje = txtDescuento.getText().isEmpty() ? BigDecimal.ZERO : new BigDecimal(txtDescuento.getText());
        BigDecimal descuento = subtotal.multiply(descuentoPorcentaje).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    
        BigDecimal totalPagar = subtotal.subtract(descuento).setScale(2, RoundingMode.HALF_UP);
        txtTotalPagar.setText(totalPagar.toString());
    }
    

    /**
     * 🔹 Cierra la ventana actual de ventas.
     */
    @FXML
    private void cerrarVentana() {
        try {
            // Obtener el botón que disparó el evento (btnSalir u otro)
            Button sourceButton = (Button) tblDetalleVenta.getScene().lookup("#btnSalir");
    
            if (sourceButton != null) {
                Stage stage = (Stage) sourceButton.getScene().getWindow();
                stage.close();
            } else {
                // Fallback si no encuentra btnSalir
                Stage stage = (Stage) tblDetalleVenta.getScene().getWindow();
                stage.close();
            }
        } catch (Exception e) {
            System.err.println("❌ Error al cerrar la ventana: " + e.getMessage());
        }
    }
    


    /**
     * 🔹 Inicia una nueva venta limpiando el formulario y reiniciando valores.
     */
    @FXML
    private void nuevaVenta() {
        limpiarFormulario(); // 🔹 Reutilizamos la función que ya tenemos para limpiar todo.
        ventaActual = new Venta(); // 🔹 Reiniciar el objeto de venta.
        detallesVenta.clear(); // 🔹 Vaciar la lista de detalles.
        lblFecha.setText(LocalDateTime.now().toString()); // 🔹 Actualizar la fecha a la actual.
    }

    /**
     * 🔹 Guarda la venta en la base de datos.
     */
    @FXML
    public void guardarVenta() {
        if (detallesVenta.isEmpty()) {
            mostrarAlerta("Debe agregar al menos un producto.");
            return;
        }
    
        if (ventaActual.getIdTipoComprobante() == 0) {
            mostrarAlerta("Debe seleccionar un tipo de comprobante antes de registrar la venta.");
            return;
        }
    
        // 🔒 Validación: precio de venta válido en cada detalle
        for (DetalleVenta d : detallesVenta) {
            if (d.getPrecioVenta() == null || d.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta("⚠ Debe ingresar un precio de venta válido para todos los productos.");
                return;
            }
    
            if (d.getPrecioVenta().compareTo(d.getCosto()) < 0) {
                mostrarAlerta("❌ El precio de venta de algún producto no puede ser menor al precio de compra.");
                return;
            }
        }
    
        try {
            String totalPagarStr = txtTotalPagar.getText().trim();
            String descuentoStr = txtDescuento.getText().trim();
    
            if (totalPagarStr.isEmpty()) {
                mostrarAlerta("Faltan datos de totales en la venta.");
                return;
            }
    
            BigDecimal totalPagar = new BigDecimal(totalPagarStr);
            BigDecimal descuento = descuentoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(descuentoStr);
    
            // Subtotal sin descuento
            BigDecimal subTotal = totalPagar.add(descuento).setScale(2, RoundingMode.HALF_UP);
    
            ventaActual.setIdUsuario(1); // Usuario temporal
            ventaActual.setIdCliente(1); // Cliente temporal
            ventaActual.setNumeroVenta(ventaService.generarNumeroVenta());
            ventaActual.setSubTotal(subTotal);
            ventaActual.setDescuento(descuento);
            ventaActual.setIva(BigDecimal.ZERO); // IVA eliminado
            ventaActual.setTotal(totalPagar);
            ventaActual.setDetalles(detallesVenta);
    
            System.out.println("📤 Registrando venta...");
            System.out.println("   - ID Tipo Comprobante: " + ventaActual.getIdTipoComprobante());
            System.out.println("   - Número Venta: " + ventaActual.getNumeroVenta());
            System.out.println("   - Total: " + ventaActual.getTotal());
    
            int idVenta = ventaService.registrarVenta(ventaActual);
            if (idVenta > 0) {
    
                // ✅ REGISTRAR DETALLE DE IMPUESTOS
                DetalleImpuestoService impuestoService = new DetalleImpuestoService();
                List<DetalleImpuesto> detallesImpuesto = detallesVenta.stream().map(detalle -> {
                    DetalleImpuesto impuesto = new DetalleImpuesto();
                    impuesto.setIdProducto(detalle.getIdProducto());
                    impuesto.setCantidad(detalle.getCantidad());
                    impuesto.setPrecioCompra(detalle.getCosto());
                    impuesto.setPrecioVenta(detalle.getPrecioVenta());
                    return impuesto;
                }).toList();
    
                impuestoService.registrarImpuestosVenta(idVenta, detallesImpuesto);

                generarReciboPDF(idVenta);

    
                mostrarAlerta("✅ Venta registrada con éxito.");
                limpiarFormulario();
            } else {
                mostrarAlerta("❌ Error al registrar la venta.");
            }
    
        } catch (NumberFormatException e) {
            mostrarAlerta("Error en los valores numéricos. Verifique los montos ingresados.");
        } catch (Exception e) {
            mostrarAlerta("Error inesperado: " + e.getMessage());
        }
    }
    

    

    /**
     * 🔹 Cancela una venta.
     */
    @FXML
    public void cancelarVenta() {
        try {
            if (ventaActual.getIdVenta() <= 0) {
                mostrarAlerta("Seleccione una venta para cancelar.");
                return;
            }

            if (ventaService.cancelarVenta(ventaActual.getIdVenta())) {
                mostrarAlerta("Venta cancelada con éxito.");
                limpiarFormulario();
            } else {
                mostrarAlerta("Error al cancelar la venta.");
            }
        } catch (Exception e) {
            mostrarAlerta("Error: " + e.getMessage());
        }
    }



    /**
     * 🔹 Limpia el formulario de venta.
     */
    @FXML
    private void limpiarFormulario() {
        txtCliente.clear();
        txtNIT.clear();
        txtProducto.clear();
        txtStock.clear();
        // txtPrecio.clear();
        txtCantidad.clear();
        // txtTotal.clear();
        txtDescuento.clear();
        // txtIVA.clear();
        txtTotalPagar.clear();
        txtComprobante.clear(); // ✅ Limpia el campo de comprobante
        detallesVenta.clear();           // Borra la lista observable
        tblDetalleVenta.refresh();       // Refresca la tabla visual
        lblStockDisponible.setText(""); // 🔇 Ocultar stock disponible

        
    }

    private void generarReciboPDF(int idVenta) {
        try {
            var datos = ventaService.obtenerDatosFactura(idVenta);
            var productos = ventaService.obtenerProductosVenta(idVenta);
    
            System.out.println("📋 Datos recibo: " + datos);
            System.out.println("📦 Productos recibo: " + productos);
            
            if (datos.isEmpty() || productos.isEmpty()) {
                mostrarAlerta("❌ No se pudo generar el recibo: datos incompletos.");
                return;
            }
            
    
            // 🟣 Seleccionar ubicación de guardado
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Recibo de Venta");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
            fileChooser.setInitialFileName("Recibo_Venta_" + datos.get("numeroVenta") + ".pdf");
    
            File file = fileChooser.showSaveDialog(btnGuardar.getScene().getWindow());
    
            if (file == null) {
                mostrarAlerta("⚠ Operación cancelada por el usuario.");
                return;
            }
    
            // ✅ Crear documento PDF
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
    
            // 🧾 Título
            doc.add(new Paragraph("📄 FACTURA / RECIBO DE VENTA")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15));
    
            // 👤 Información del cliente y venta
            Table info = new Table(UnitValue.createPercentArray(new float[]{2, 4}));
            info.setWidth(UnitValue.createPercentValue(100));
    
            info.addCell(new Cell().add(new Paragraph("Cliente:")).setBold());
            info.addCell(new Cell().add(new Paragraph(String.valueOf(datos.get("razonSocial")))));
    
            info.addCell(new Cell().add(new Paragraph("NIT:")).setBold());
            info.addCell(new Cell().add(new Paragraph(String.valueOf(datos.get("nit")))));
    
            info.addCell(new Cell().add(new Paragraph("Número Venta:")).setBold());
            info.addCell(new Cell().add(new Paragraph(String.valueOf(datos.get("numeroVenta")))));
    
            info.addCell(new Cell().add(new Paragraph("Fecha:")).setBold());
            info.addCell(new Cell().add(new Paragraph(String.valueOf(datos.get("fecha")))));
    
            info.addCell(new Cell().add(new Paragraph("Vendedor:")).setBold());
            info.addCell(new Cell().add(new Paragraph(String.valueOf(datos.get("vendedor")))));
    
            info.addCell(new Cell().add(new Paragraph("Comprobante:")).setBold());
            info.addCell(new Cell().add(new Paragraph(String.valueOf(datos.get("comprobante")))));
    
            info.addCell(new Cell().add(new Paragraph("Tipo de Pago:")).setBold());
            info.addCell(new Cell().add(new Paragraph(String.valueOf(datos.get("tipo_pago")))));
    
            doc.add(info);
            doc.add(new Paragraph(" ")); // Espacio
    
            // 📦 Detalle de productos
            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 2, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));
    
            table.addHeaderCell(new Cell().add(new Paragraph("Producto")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Cantidad")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Precio Unitario")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Total")).setBold());
    
            for (var p : productos) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(p.get("nombre")))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(p.get("cantidad")))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(p.get("precioVenta")))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(p.get("total")))));
            }
    
            doc.add(table);
            doc.add(new Paragraph(" ")); // Espacio
    
            // 💰 Totales
            Table totales = new Table(UnitValue.createPercentArray(new float[]{2, 1}));
            totales.setWidth(UnitValue.createPercentValue(40));
            totales.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);
    
            totales.addCell(new Cell().add(new Paragraph("Subtotal:")).setBold());
            totales.addCell(new Cell().add(new Paragraph(String.valueOf(datos.get("subTotal")))));
    
            totales.addCell(new Cell().add(new Paragraph("Descuento:")).setBold());
            totales.addCell(new Cell().add(new Paragraph(String.valueOf(datos.get("descuento")))));
    
            totales.addCell(new Cell().add(new Paragraph("Total Pagado:")).setBold());
            totales.addCell(new Cell().add(new Paragraph(String.valueOf(datos.get("total")))));
    
            doc.add(totales);
    
            // ✅ Finalizar
            doc.close();
            mostrarAlerta("✅ Recibo guardado en:\n" + file.getAbsolutePath());
    
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("❌ Error al generar el recibo PDF.");
        }
    }
    



    /**
     * 🔹 Muestra un mensaje de alerta.
     */
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
