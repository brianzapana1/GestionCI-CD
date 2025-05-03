package com.inventario.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DetalleImpuesto {
    private int idDetalleImpuesto;
    private int idVenta;
    private int idProducto;
    private int cantidad;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal utilidad;
    private BigDecimal impuesto3p;
    private BigDecimal impuesto13p;
    private BigDecimal totalVenta;
    private BigDecimal totalImpuestos;
    private LocalDateTime fecha;

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }


    // Getters y Setters

    public int getIdDetalleImpuesto() { return idDetalleImpuesto; }
    public void setIdDetalleImpuesto(int idDetalleImpuesto) { this.idDetalleImpuesto = idDetalleImpuesto; }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }

    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

    public BigDecimal getUtilidad() { return utilidad; }
    public void setUtilidad(BigDecimal utilidad) { this.utilidad = utilidad; }

    public BigDecimal getImpuesto3p() { return impuesto3p; }
    public void setImpuesto3p(BigDecimal impuesto3p) { this.impuesto3p = impuesto3p; }

    public BigDecimal getImpuesto13p() { return impuesto13p; }
    public void setImpuesto13p(BigDecimal impuesto13p) { this.impuesto13p = impuesto13p; }

    public BigDecimal getTotalVenta() { return totalVenta; }
    public void setTotalVenta(BigDecimal totalVenta) { this.totalVenta = totalVenta; }

    public BigDecimal getTotalImpuestos() { return totalImpuestos; }
    public void setTotalImpuestos(BigDecimal totalImpuestos) { this.totalImpuestos = totalImpuestos; }
}
