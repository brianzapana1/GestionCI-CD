package com.inventario.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TipoComprobante {
    private int idTipoComp;
    private final StringProperty descripcion;
    private final BooleanProperty estado; // Ahora es un boolean en lugar de String

    // 🔹 Constructor
    public TipoComprobante(int idTipoComp, String descripcion, boolean estado) {
        this.idTipoComp = idTipoComp;
        this.descripcion = new SimpleStringProperty(descripcion);
        this.estado = new SimpleBooleanProperty(estado);
    }

    // 🔹 Getters y Setters
    public int getIdTipoComp() {
        return idTipoComp;
    }

    public void setIdTipoComp(int idTipoComp) {
        this.idTipoComp = idTipoComp;
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    public boolean isEstado() {
        return estado.get();
    }

    public void setEstado(boolean estado) {
        this.estado.set(estado);
    }

    // 🔹 Métodos `Property()` para TableView y Bindings
    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public BooleanProperty estadoProperty() {
        return estado;
    }

    @Override
    public String toString() {
        return descripcion.get(); // Para mostrar en ComboBox u otros elementos
    }
}
