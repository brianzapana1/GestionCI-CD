package com.inventario.models;

import java.time.LocalDateTime;

public class Inventario {
    private int idInventario;
    private int idProducto;
    private int idDetalleCompra;
    private int cantidadDisponible;
    private LocalDateTime fechaIngreso;

    public Inventario() {
    }

    public Inventario(int idInventario, int idProducto, int idDetalleCompra, int cantidadDisponible, LocalDateTime fechaIngreso) {
        this.idInventario = idInventario;
        this.idProducto = idProducto;
        this.idDetalleCompra = idDetalleCompra;
        this.cantidadDisponible = cantidadDisponible;
        this.fechaIngreso = fechaIngreso;
    }

    // Getters y Setters

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdDetalleCompra() {
        return idDetalleCompra;
    }

    public void setIdDetalleCompra(int idDetalleCompra) {
        this.idDetalleCompra = idDetalleCompra;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
}
