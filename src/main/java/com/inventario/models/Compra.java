package com.inventario.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Compra {
    private final IntegerProperty idCompra;
    private final IntegerProperty idProveedor;
    private final IntegerProperty idUsuario;
    private final IntegerProperty idTipoComprobante;

    private final ObjectProperty<Proveedor> proveedor;
    private final ObjectProperty<Usuario> usuario;
    private final ObjectProperty<TipoComprobante> tipoComprobante;

    private final IntegerProperty numeroCompra;
    private final ObjectProperty<LocalDateTime> fecha;
    private final StringProperty tipoPago;
    private final ObjectProperty<BigDecimal> total;
    private final StringProperty estado;

    public Compra() {
        this.idCompra = new SimpleIntegerProperty();
        this.idProveedor = new SimpleIntegerProperty();
        this.idUsuario = new SimpleIntegerProperty();
        this.idTipoComprobante = new SimpleIntegerProperty();

        this.proveedor = new SimpleObjectProperty<>();
        this.usuario = new SimpleObjectProperty<>();
        this.tipoComprobante = new SimpleObjectProperty<>();

        this.numeroCompra = new SimpleIntegerProperty();
        this.fecha = new SimpleObjectProperty<>();
        this.tipoPago = new SimpleStringProperty();
        this.total = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.estado = new SimpleStringProperty("Activo");
    }

    // Getters y Setters
    public int getIdCompra() { return idCompra.get(); }
    public void setIdCompra(int value) { idCompra.set(value); }
    public IntegerProperty idCompraProperty() { return idCompra; }

    public int getIdProveedor() { return idProveedor.get(); }
    public void setIdProveedor(int value) { idProveedor.set(value); }
    public IntegerProperty idProveedorProperty() { return idProveedor; }

    public int getIdUsuario() { return idUsuario.get(); }
    public void setIdUsuario(int value) { idUsuario.set(value); }
    public IntegerProperty idUsuarioProperty() { return idUsuario; }

    public int getIdTipoComprobante() { return idTipoComprobante.get(); }
    public void setIdTipoComprobante(int value) { idTipoComprobante.set(value); }
    public IntegerProperty idTipoComprobanteProperty() { return idTipoComprobante; }

    public Proveedor getProveedor() { return proveedor.get(); }
    public void setProveedor(Proveedor p) { proveedor.set(p); }
    public ObjectProperty<Proveedor> proveedorProperty() { return proveedor; }

    public Usuario getUsuario() { return usuario.get(); }
    public void setUsuario(Usuario u) { usuario.set(u); }
    public ObjectProperty<Usuario> usuarioProperty() { return usuario; }

    public TipoComprobante getTipoComprobante() { return tipoComprobante.get(); }
    public void setTipoComprobante(TipoComprobante t) { tipoComprobante.set(t); }
    public ObjectProperty<TipoComprobante> tipoComprobanteProperty() { return tipoComprobante; }

    public int getNumeroCompra() { return numeroCompra.get(); }
    public void setNumeroCompra(int value) { numeroCompra.set(value); }
    public IntegerProperty numeroCompraProperty() { return numeroCompra; }

    public LocalDateTime getFecha() { return fecha.get(); }
    public void setFecha(LocalDateTime value) { fecha.set(value); }
    public ObjectProperty<LocalDateTime> fechaProperty() { return fecha; }

    public String getTipoPago() { return tipoPago.get(); }
    public void setTipoPago(String value) { tipoPago.set(value); }
    public StringProperty tipoPagoProperty() { return tipoPago; }

    public BigDecimal getTotal() { return total.get(); }
    public void setTotal(BigDecimal value) { total.set(value); }
    public ObjectProperty<BigDecimal> totalProperty() { return total; }

    public String getEstado() { return estado.get(); }
    public void setEstado(String value) { estado.set(value); }
    public StringProperty estadoProperty() { return estado; }
}
