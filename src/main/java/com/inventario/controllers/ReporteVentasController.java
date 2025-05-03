package com.inventario.controllers;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.inventario.models.ReporteVenta;
import com.inventario.services.ReporteVentaService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

public class ReporteVentasController {

    @FXML
    private TableView<ReporteVenta> tablaReporteVentas;

    @FXML
    private TableColumn<ReporteVenta, String> colProducto;

    @FXML
    private TableColumn<ReporteVenta, Integer> colCantidad;

    @FXML
    private TableColumn<ReporteVenta, BigDecimal> colPrecioCompra;

    @FXML
    private TableColumn<ReporteVenta, BigDecimal> colPrecioVenta;

    @FXML
    private TableColumn<ReporteVenta, BigDecimal> colUtilidad;

    @FXML
    private TableColumn<ReporteVenta, BigDecimal> colImpuesto3;

    @FXML
    private TableColumn<ReporteVenta, BigDecimal> colImpuesto13;

    @FXML
    private TableColumn<ReporteVenta, BigDecimal> colTotal;

    @FXML
    private Label lblTotalVenta;

    @FXML
    private Label lblTotalImpuestos;

    @FXML
    private TextField txtTotalVenta;

    @FXML
    private TextField txtTotalImpuestos;

    @FXML
    private TableColumn<ReporteVenta, String> colFecha;
    
    private final ReporteVentaService reporteService = new ReporteVentaService();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        colProducto.setCellValueFactory(new PropertyValueFactory<>("producto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colUtilidad.setCellValueFactory(new PropertyValueFactory<>("utilidad"));
        colImpuesto3.setCellValueFactory(new PropertyValueFactory<>("impuesto3p"));
        colImpuesto13.setCellValueFactory(new PropertyValueFactory<>("impuesto13p"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalVenta"));
    }

    private void cargarDatos() {
        List<ReporteVenta> lista = reporteService.obtenerReporteVentas();
        ObservableList<ReporteVenta> datos = FXCollections.observableArrayList(lista);
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        tablaReporteVentas.setItems(datos);
        calcularTotalesGenerales(datos);
    }

    private void calcularTotalesGenerales(List<ReporteVenta> lista) {
        BigDecimal sumaTotal = BigDecimal.ZERO;
        BigDecimal sumaImpuestos = BigDecimal.ZERO;

        for (ReporteVenta item : lista) {
            BigDecimal total = item.getTotalVenta() != null ? item.getTotalVenta() : BigDecimal.ZERO;
            BigDecimal imp3 = item.getImpuesto3p() != null ? item.getImpuesto3p() : BigDecimal.ZERO;
            BigDecimal imp13 = item.getImpuesto13p() != null ? item.getImpuesto13p() : BigDecimal.ZERO;
            

            sumaTotal = sumaTotal.add(total);
            sumaImpuestos = sumaImpuestos.add(imp3).add(imp13);
        }

        txtTotalVenta.setText(sumaTotal.setScale(2, RoundingMode.HALF_UP).toString());
        txtTotalImpuestos.setText(sumaImpuestos.setScale(2, RoundingMode.HALF_UP).toString());
    }



    @FXML
    private void exportarPDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte como PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
            File file = fileChooser.showSaveDialog(tablaReporteVentas.getScene().getWindow());
    
            if (file != null) {
                PdfWriter writer = new PdfWriter(file.getAbsolutePath());
                PdfDocument pdf = new PdfDocument(writer);
                Document doc = new Document(pdf);
    
                // 📌 Título centrado
                doc.add(new Paragraph("📊 Reporte de Ventas")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18)
                    .setBold()
                    .setMarginBottom(20));
    
                // 📌 Tabla con columnas proporcionales
                Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 2, 2, 2, 2, 3}));
                table.setWidth(UnitValue.createPercentValue(100));
    
                // 🟣 Encabezados
                // 🟣 Encabezados
                table.addHeaderCell(new Cell().add(new Paragraph("Producto")).setBold());
                table.addHeaderCell(new Cell().add(new Paragraph("Cantidad")).setBold());
                table.addHeaderCell(new Cell().add(new Paragraph("Precio Compra")).setBold());
                table.addHeaderCell(new Cell().add(new Paragraph("Precio Venta")).setBold());
                table.addHeaderCell(new Cell().add(new Paragraph("Utilidad")).setBold());
                table.addHeaderCell(new Cell().add(new Paragraph("3% Imp.")).setBold());
                table.addHeaderCell(new Cell().add(new Paragraph("13% Imp.")).setBold());
                table.addHeaderCell(new Cell().add(new Paragraph("Total Venta")).setBold());
                table.addHeaderCell(new Cell().add(new Paragraph("Fecha")).setBold());

    
                // 📊 Datos
                BigDecimal sumaTotal = BigDecimal.ZERO;
                BigDecimal sumaImpuestos = BigDecimal.ZERO;
    
                for (ReporteVenta r : tablaReporteVentas.getItems()) {
                    table.addCell(r.getProducto());
                    table.addCell(String.valueOf(r.getCantidad()));
                    table.addCell(r.getPrecioCompra().setScale(2, RoundingMode.HALF_UP).toString());
                    table.addCell(r.getPrecioVenta().setScale(2, RoundingMode.HALF_UP).toString());
                    table.addCell(r.getUtilidad().setScale(2, RoundingMode.HALF_UP).toString());
                    table.addCell(r.getImpuesto3p().setScale(2, RoundingMode.HALF_UP).toString());
                    table.addCell(r.getImpuesto13p().setScale(2, RoundingMode.HALF_UP).toString());
                    table.addCell(r.getTotalVenta().setScale(2, RoundingMode.HALF_UP).toString());
                    table.addCell(r.getFecha() != null ? r.getFecha() : "-");
    
                    // Acumular totales
                    sumaTotal = sumaTotal.add(r.getTotalVenta());
                    sumaImpuestos = sumaImpuestos.add(r.getImpuesto3p()).add(r.getImpuesto13p());
                }
    
                // 🔻 Fila de Totales (con colspan y negrita)
                Cell celdaTotales = new Cell(1, 7)
                    .add(new Paragraph("TOTALES:").setBold())
                    .setTextAlignment(TextAlignment.RIGHT);
                table.addCell(celdaTotales);
    
                table.addCell(new Cell().add(new Paragraph(sumaTotal.setScale(2, RoundingMode.HALF_UP).toString()).setBold()));
                table.addCell(new Cell().add(new Paragraph("Total Imp: " + sumaImpuestos.setScale(2, RoundingMode.HALF_UP).toString()).setBold()));
    
                doc.add(table);
                doc.close();
    
                mostrarAlerta("✅ PDF generado con éxito.");
            }
    
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("❌ Error al exportar PDF: " + e.getMessage());
        }
    }
    
    
    

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }


    @FXML
    private void imprimirReporte() {
        Printer printer = Printer.getDefaultPrinter();
        PrinterJob job = PrinterJob.createPrinterJob(printer);

        if (job != null && job.showPrintDialog(tablaReporteVentas.getScene().getWindow())) {
            boolean success = job.printPage(tablaReporteVentas);
            if (success) {
                job.endJob();
            } else {
                mostrarAlerta("❌ No se pudo imprimir el reporte.");
            }
        }
    }

}
