package com.inventario.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private int idVenta;
    private int idUsuario;
    private int idCliente;
    private int idTipoComprobante; // ✅ Asegurar que existe este atributo
    private String numeroVenta;
    private BigDecimal descuento;
    private BigDecimal subTotal;
    private BigDecimal iva;
    private BigDecimal total;
    private LocalDateTime fecha;
    private String estado;
    private List<DetalleVenta> detalles; // Lista de productos en la venta

    public Venta() {
        this.detalles = new ArrayList<>();
    }

    public Venta(int idVenta, int idUsuario, int idCliente, String numeroVenta, BigDecimal descuento, 
                 BigDecimal subTotal, BigDecimal iva, BigDecimal total, LocalDateTime fecha, String estado) {
        this();
        this.idVenta = idVenta;
        this.idUsuario = idUsuario;
        this.idCliente = idCliente;
        this.numeroVenta = numeroVenta;
        this.descuento = descuento;
        this.subTotal = subTotal;
        this.iva = iva;
        this.total = total;
        this.fecha = fecha;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNumeroVenta() { return numeroVenta; }
    public void setNumeroVenta(String numeroVenta) { this.numeroVenta = numeroVenta; }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }

    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }

    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }

    public void agregarDetalle(DetalleVenta detalle) {
        this.detalles.add(detalle);
    }
    public void setIdTipoComprobante(int idTipoComprobante) {
        this.idTipoComprobante = idTipoComprobante;
    }
    
    public int getIdTipoComprobante() {
        return idTipoComprobante;
    }
    
}
