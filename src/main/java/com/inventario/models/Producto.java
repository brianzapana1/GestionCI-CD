package com.inventario.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Producto {
    private final IntegerProperty id;
    private final IntegerProperty idCategoria;
    private final ObjectProperty<Categoria> categoria;
    private final StringProperty codigo;
    private final StringProperty nombre;
    private final StringProperty descripcion;
    private final ObjectProperty<LocalDateTime> creado;
    private final ObjectProperty<LocalDate> fechaVenc;
    private final StringProperty imagenUrl;
    private final BooleanProperty estado;

    public Producto() {
        this.id = new SimpleIntegerProperty();
        this.idCategoria = new SimpleIntegerProperty();
        this.categoria = new SimpleObjectProperty<>();
        this.codigo = new SimpleStringProperty();
        this.nombre = new SimpleStringProperty();
        this.descripcion = new SimpleStringProperty("");
        this.creado = new SimpleObjectProperty<>();
        this.fechaVenc = new SimpleObjectProperty<>(LocalDate.now());
        this.imagenUrl = new SimpleStringProperty();
        this.estado = new SimpleBooleanProperty(true);
    }

    public Producto(int id, int idCategoria, Categoria categoria, String codigo, String nombre,
                    String descripcion, LocalDateTime creado, String imagenUrl,
                    boolean estado, LocalDate fechaVenc) {
        this();
        this.id.set(id);
        this.idCategoria.set(idCategoria);
        this.categoria.set(categoria);
        this.codigo.set(codigo);
        this.nombre.set(nombre);
        this.descripcion.set(descripcion != null ? descripcion : "");
        this.creado.set(creado);
        this.imagenUrl.set(imagenUrl);
        this.estado.set(estado);
        this.fechaVenc.set(fechaVenc);
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public int getIdCategoria() { return idCategoria.get(); }
    public void setIdCategoria(int idCategoria) { this.idCategoria.set(idCategoria); }
    public IntegerProperty idCategoriaProperty() { return idCategoria; }

    public Categoria getCategoria() { return categoria.get(); }
    public void setCategoria(Categoria categoria) { this.categoria.set(categoria); }
    public ObjectProperty<Categoria> categoriaProperty() { return categoria; }

    public String getCodigo() { return codigo.get(); }
    public void setCodigo(String codigo) { this.codigo.set(codigo); }
    public StringProperty codigoProperty() { return codigo; }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public StringProperty nombreProperty() { return nombre; }

    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }
    public StringProperty descripcionProperty() { return descripcion; }

    public LocalDateTime getCreado() { return creado.get(); }
    public void setCreado(LocalDateTime creado) { this.creado.set(creado); }
    public ObjectProperty<LocalDateTime> creadoProperty() { return creado; }

    public LocalDate getFechaVenc() { return fechaVenc.get(); }
    public void setFechaVenc(LocalDate fechaVenc) { this.fechaVenc.set(fechaVenc); }
    public ObjectProperty<LocalDate> fechaVencProperty() { return fechaVenc; }

    public String getImagenUrl() { return imagenUrl.get(); }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl.set(imagenUrl); }
    public StringProperty imagenUrlProperty() { return imagenUrl; }

    public boolean isEstado() { return estado.get(); }
    public void setEstado(boolean estado) { this.estado.set(estado); }
    public BooleanProperty estadoProperty() { return estado; }
}
