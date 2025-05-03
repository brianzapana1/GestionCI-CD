package com.inventario.models;

import java.math.BigDecimal;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class DetalleCompra {
    private final IntegerProperty idDetalleCompra;
    private final IntegerProperty idProducto;
    private final IntegerProperty idCompra;
    private final IntegerProperty cantidad;
    private final ObjectProperty<BigDecimal> precioUnitario;
    private final ObjectProperty<BigDecimal> total;
    private final ObjectProperty<Producto> producto; // Opcional, para mostrar info del producto

    // Constructor por defecto
    public DetalleCompra() {
        this.idDetalleCompra = new SimpleIntegerProperty();
        this.idProducto = new SimpleIntegerProperty();
        this.idCompra = new SimpleIntegerProperty();
        this.cantidad = new SimpleIntegerProperty();
        this.precioUnitario = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.total = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.producto = new SimpleObjectProperty<>();
    }

    // Constructor completo
    public DetalleCompra(int idDetalleCompra, int idProducto, int idCompra, int cantidad, BigDecimal precioUnitario, BigDecimal total) {
        this(); // Inicializa propiedades
        setIdDetalleCompra(idDetalleCompra);
        setIdProducto(idProducto);
        setIdCompra(idCompra);
        setCantidad(cantidad);
        setPrecioUnitario(precioUnitario);
        setTotal(total);
    }

    // Getters y Setters con Property support
    public int getIdDetalleCompra() { return idDetalleCompra.get(); }
    public void setIdDetalleCompra(int id) { this.idDetalleCompra.set(id); }
    public IntegerProperty idDetalleCompraProperty() { return idDetalleCompra; }

    public int getIdProducto() { return idProducto.get(); }
    public void setIdProducto(int id) { this.idProducto.set(id); }
    public IntegerProperty idProductoProperty() { return idProducto; }

    public int getIdCompra() { return idCompra.get(); }
    public void setIdCompra(int id) { this.idCompra.set(id); }
    public IntegerProperty idCompraProperty() { return idCompra; }

    public int getCantidad() { return cantidad.get(); }
    public void setCantidad(int cantidad) { this.cantidad.set(cantidad); }
    public IntegerProperty cantidadProperty() { return cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario.get(); }
    public void setPrecioUnitario(BigDecimal precio) { this.precioUnitario.set(precio); }
    public ObjectProperty<BigDecimal> precioUnitarioProperty() { return precioUnitario; }

    public BigDecimal getTotal() { return total.get(); }
    public void setTotal(BigDecimal total) { this.total.set(total); }
    public ObjectProperty<BigDecimal> totalProperty() { return total; }

    public Producto getProducto() { return producto.get(); }
    public void setProducto(Producto producto) { this.producto.set(producto); }
    public ObjectProperty<Producto> productoProperty() { return producto; }
}
