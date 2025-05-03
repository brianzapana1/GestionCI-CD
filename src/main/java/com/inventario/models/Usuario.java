package com.inventario.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Usuario {
    private final IntegerProperty id;
    private final IntegerProperty idRol;
    private final StringProperty nombre;
    private final StringProperty email;
    private final StringProperty usuario; // 🔹 Nuevo campo para el nombre de usuario
    private final StringProperty passwordHash;
    private final StringProperty creado; // 🔹 Timestamp en formato String

    public Usuario() {
        this.id = new SimpleIntegerProperty();
        this.idRol = new SimpleIntegerProperty();
        this.nombre = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.usuario = new SimpleStringProperty();
        this.passwordHash = new SimpleStringProperty();
        this.creado = new SimpleStringProperty();
    }

    public Usuario(int id, int idRol, String nombre, String email, String usuario, String passwordHash, String creado) {
        this();
        this.id.set(id);
        this.idRol.set(idRol);
        this.nombre.set(nombre);
        this.email.set(email);
        this.usuario.set(usuario);
        this.passwordHash.set(passwordHash);
        this.creado.set(creado);
    }

    // Getters y Setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public int getIdRol() { return idRol.get(); }
    public void setIdRol(int idRol) { this.idRol.set(idRol); }
    public IntegerProperty idRolProperty() { return idRol; }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public StringProperty nombreProperty() { return nombre; }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    public String getUsuario() { return usuario.get(); }
    public void setUsuario(String usuario) { this.usuario.set(usuario); }
    public StringProperty usuarioProperty() { return usuario; }

    public String getPasswordHash() { return passwordHash.get(); }
    public void setPasswordHash(String passwordHash) { this.passwordHash.set(passwordHash); }
    public StringProperty passwordHashProperty() { return passwordHash; }

    public String getCreado() { return creado.get(); }
    public void setCreado(String creado) { this.creado.set(creado); }
    public StringProperty creadoProperty() { return creado; }
}
