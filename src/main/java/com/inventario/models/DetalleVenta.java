package com.inventario.models;

import java.math.BigDecimal;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DetalleVenta {
    private final IntegerProperty idDetalle;
    private final IntegerProperty idVenta;
    private final IntegerProperty idProducto;
    private final IntegerProperty idDetalleCompra; // ✅ Usado para control PEPS
    private final StringProperty nombreProducto;
    private final IntegerProperty cantidad;
    private final ObjectProperty<BigDecimal> costo;
    private final ObjectProperty<BigDecimal> precioVenta;
    private final ObjectProperty<BigDecimal> total;

    public DetalleVenta(int idDetalle, int idVenta, int idProducto, String nombreProducto,
                        int cantidad, BigDecimal costo, BigDecimal precioVenta, BigDecimal total) {
        this.idDetalle = new SimpleIntegerProperty(idDetalle);
        this.idVenta = new SimpleIntegerProperty(idVenta);
        this.idProducto = new SimpleIntegerProperty(idProducto);
        this.idDetalleCompra = new SimpleIntegerProperty(0); // Inicialización por defecto
        this.nombreProducto = new SimpleStringProperty(nombreProducto);
        this.cantidad = new SimpleIntegerProperty(cantidad);
        this.costo = new SimpleObjectProperty<>(costo);
        this.precioVenta = new SimpleObjectProperty<>(precioVenta);
        this.total = new SimpleObjectProperty<>(total);
    }

    // 🔹 Getters y propiedades para tabla

    public int getIdDetalle() { return idDetalle.get(); }
    public void setIdDetalle(int value) { this.idDetalle.set(value); }
    public IntegerProperty idDetalleProperty() { return idDetalle; }

    public int getIdVenta() { return idVenta.get(); }
    public void setIdVenta(int value) { this.idVenta.set(value); }
    public IntegerProperty idVentaProperty() { return idVenta; }

    public int getIdProducto() { return idProducto.get(); }
    public void setIdProducto(int value) { this.idProducto.set(value); }
    public IntegerProperty idProductoProperty() { return idProducto; }

    public int getIdDetalleCompra() { return idDetalleCompra.get(); }
    public void setIdDetalleCompra(int idDetalleCompra) { this.idDetalleCompra.set(idDetalleCompra); }
    public IntegerProperty idDetalleCompraProperty() { return idDetalleCompra; }

    public String getNombreProducto() { return nombreProducto.get(); }
    public void setNombreProducto(String nombre) { this.nombreProducto.set(nombre); }
    public StringProperty nombreProductoProperty() { return nombreProducto; }

    public int getCantidad() { return cantidad.get(); }
    public void setCantidad(int cantidad) { this.cantidad.set(cantidad); }
    public IntegerProperty cantidadProperty() { return cantidad; }

    public BigDecimal getCosto() { return costo.get(); }
    public void setCosto(BigDecimal costo) { this.costo.set(costo); }
    public ObjectProperty<BigDecimal> costoProperty() { return costo; }

    public BigDecimal getPrecioVenta() { return precioVenta.get(); }
    public void setPrecioVenta(BigDecimal precio) { this.precioVenta.set(precio); }
    public ObjectProperty<BigDecimal> precioVentaProperty() { return precioVenta; }

    public BigDecimal getTotal() { return total.get(); }
    public void setTotal(BigDecimal total) { this.total.set(total); }
    public ObjectProperty<BigDecimal> totalProperty() { return total; }
}
