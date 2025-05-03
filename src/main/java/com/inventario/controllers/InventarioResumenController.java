package com.inventario.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.inventario.models.InventarioResumen;
import com.inventario.services.InventarioService;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class InventarioResumenController {

    @FXML private TilePane tileInventario;
    private final InventarioService inventarioService = new InventarioService();

    @FXML
    public void initialize() {
        List<InventarioResumen> productos = inventarioService.obtenerResumenInventario();
        for (InventarioResumen p : productos) {
            tileInventario.getChildren().add(crearTarjetaProducto(p));
        }
    }

    private VBox crearTarjetaProducto(InventarioResumen producto) {
        VBox card = new VBox(5);
        card.setStyle("-fx-border-color: pink; -fx-background-color: #FFE4EC; -fx-padding: 10px; -fx-border-radius: 10px; -fx-alignment: center;");

        ImageView img = new ImageView();
        try {
            String ruta = Paths.get("resources", "images", "productos", producto.getImagenUrl()).toAbsolutePath().toString();
            File archivo = new File(ruta);
            if (archivo.exists()) {
                img.setImage(new javafx.scene.image.Image(archivo.toURI().toString()));
            } else {
                img.setImage(new javafx.scene.image.Image(getClass().getResource("/images/default.png").toExternalForm()));
            }
        } catch (Exception e) {
            img.setImage(new javafx.scene.image.Image(getClass().getResource("/images/default.png").toExternalForm()));
        }

        img.setFitWidth(120);
        img.setFitHeight(90);
        img.setPreserveRatio(true);

        Label nombre = new Label(producto.getNombre());
        nombre.setStyle("-fx-font-weight: bold;");
        Label stock = new Label("Stock: " + producto.getStockTotal());

        card.getChildren().addAll(img, nombre, stock);
        return card;
    }

    public void generarPDFInventario(List<InventarioResumen> productos) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar reporte de inventario");
            fileChooser.setInitialFileName("Inventario_" + java.time.LocalDate.now() + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

            File selectedFile = fileChooser.showSaveDialog(new Stage());
            if (selectedFile == null) {
                System.out.println("❌ Generación cancelada por el usuario.");
                return;
            }

            PdfWriter writer = new PdfWriter(selectedFile.getAbsolutePath());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Paragraph titulo = new Paragraph("Reporte de Inventario")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(titulo);

            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 4, 3, 2}))
                    .useAllAvailableWidth();

            table.addHeaderCell(crearCeldaCabecera("Imagen"));
            table.addHeaderCell(crearCeldaCabecera("Nombre"));
            table.addHeaderCell(crearCeldaCabecera("Código"));
            table.addHeaderCell(crearCeldaCabecera("Stock"));

            for (InventarioResumen item : productos) {
                Image img;
                try {
                    String path = System.getProperty("user.dir") + "/resources/images/productos/" + item.getImagenUrl();
                    File file = new File(path);
                    if (file.exists()) {
                        // 🔥 Solución: pasar como byte array
                        byte[] imgBytes = Files.readAllBytes(file.toPath());
                        ImageData data = ImageDataFactory.create(imgBytes);
                        img = new Image(data).scaleToFit(60, 60);
                    } else {
                        img = new Image(ImageDataFactory.create(new byte[0]));
                    }
                } catch (Exception ex) {
                    img = new Image(ImageDataFactory.create(new byte[0]));
                }

                table.addCell(new Cell().add(img).setPadding(5));
                table.addCell(new Cell().add(new Paragraph(item.getNombre())));
                table.addCell(new Cell().add(new Paragraph(item.getCodigo())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getStockTotal()))));
            }

            document.add(table);
            document.close();
            System.out.println("✅ PDF guardado en: " + selectedFile.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("❌ Error al generar PDF: " + e.getMessage());
        }
    }

    private Cell crearCeldaCabecera(String texto) {
        return new Cell().add(new Paragraph(texto))
                .setBold()
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }

    @FXML
    private void imprimirInventarioPDF() {
        List<InventarioResumen> productos = inventarioService.obtenerResumenInventario();
        generarPDFInventario(productos);
    }
}
