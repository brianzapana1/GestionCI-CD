package com.inventario.models;

import java.math.BigDecimal;

public class ReporteVenta {

    private String producto;
    private int cantidad;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal utilidad;
    private BigDecimal impuesto3p;
    private BigDecimal impuesto13p;
    private BigDecimal totalVenta;
    private BigDecimal totalImpuestos;
    private String fecha; // ✅ Nuevo campo para la fecha

    // Getters y Setters

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getUtilidad() {
        return utilidad;
    }

    public void setUtilidad(BigDecimal utilidad) {
        this.utilidad = utilidad;
    }

    public BigDecimal getImpuesto3p() {
        return impuesto3p;
    }

    public void setImpuesto3p(BigDecimal impuesto3p) {
        this.impuesto3p = impuesto3p;
    }

    public BigDecimal getImpuesto13p() {
        return impuesto13p;
    }

    public void setImpuesto13p(BigDecimal impuesto13p) {
        this.impuesto13p = impuesto13p;
    }

    public BigDecimal getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(BigDecimal totalVenta) {
        this.totalVenta = totalVenta;
    }

    public BigDecimal getTotalImpuestos() {
        return totalImpuestos;
    }

    public void setTotalImpuestos(BigDecimal totalImpuestos) {
        this.totalImpuestos = totalImpuestos;
    }

    // ✅ Get/Set de fecha
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
