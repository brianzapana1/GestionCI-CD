package com.inventario.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cliente {
    private final IntegerProperty idCliente;
    private final StringProperty empresa;
    private final StringProperty nombre;
    private final StringProperty razonSocial;
    private final StringProperty nit;
    private final IntegerProperty telefono;
    private final StringProperty email;
    private final StringProperty direccion;

    public Cliente(int id, String empresa, String nombre, String razonSocial, String nit, int telefono, String email, String direccion) {
        this.idCliente = new SimpleIntegerProperty(id);
        this.empresa = new SimpleStringProperty(empresa);
        this.nombre = new SimpleStringProperty(nombre);
        this.razonSocial = new SimpleStringProperty(razonSocial);
        this.nit = new SimpleStringProperty(nit);
        this.telefono = new SimpleIntegerProperty(telefono);
        this.email = new SimpleStringProperty(email);
        this.direccion = new SimpleStringProperty(direccion);
    }

    // ✅ Getters con Property para JavaFX TableView
    public IntegerProperty idClienteProperty() { return idCliente; }
    public StringProperty empresaProperty() { return empresa; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty razonSocialProperty() { return razonSocial; }
    public StringProperty nitProperty() { return nit; }
    
    // 🔹 Corrección aquí: Convertir IntegerProperty a ObjectProperty<Integer> para TableView
    public ObjectProperty<Integer> telefonoProperty() { return telefono.asObject(); }
    public StringProperty emailProperty() { return email; }
    public StringProperty direccionProperty() { return direccion; }

    // ✅ Getters normales
    public int getIdCliente() { return idCliente.get(); }
    public String getEmpresa() { return empresa.get(); }
    public String getNombre() { return nombre.get(); }
    public String getRazonSocial() { return razonSocial.get(); }
    public String getNit() { return nit.get(); }
    public int getTelefono() { return telefono.get(); }
    public String getEmail() { return email.get(); }
    public String getDireccion() { return direccion.get(); }

    // ✅ Setters
    public void setIdCliente(int id) { this.idCliente.set(id); }
    public void setEmpresa(String empresa) { this.empresa.set(empresa); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public void setRazonSocial(String razonSocial) { this.razonSocial.set(razonSocial); }
    public void setNit(String nit) { this.nit.set(nit); }
    public void setTelefono(int telefono) { this.telefono.set(telefono); }
    public void setEmail(String email) { this.email.set(email); }
    public void setDireccion(String direccion) { this.direccion.set(direccion); }
}
