package com.inventario.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Categoria {
    private final IntegerProperty idCategoria;  // ✅ ID de la categoría
    private final StringProperty nombre;        // ✅ Nombre de la categoría
    private final StringProperty descripcion;   // ✅ Descripción de la categoría (opcional)

    /**
     * 🔹 Constructor vacío.
     */
    public Categoria() {
        this.idCategoria = new SimpleIntegerProperty();
        this.nombre = new SimpleStringProperty();
        this.descripcion = new SimpleStringProperty();
    }

    /**
     * 🔹 Constructor completo.
     */
    public Categoria(int idCategoria, String nombre, String descripcion) {
        this();
        this.idCategoria.set(idCategoria);
        this.nombre.set(nombre);
        this.descripcion.set(descripcion);
    }

    // 🔹 Getters y Setters

    public int getIdCategoria() {
        return idCategoria.get();
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria.set(idCategoria);
    }

    public IntegerProperty idCategoriaProperty() {
        return idCategoria;
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    @Override
    public String toString() {
        return nombre.get();  // ✅ Para mostrar correctamente en ComboBox o ListView
    }
}
