package com.inventario.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Proveedor {
    private final IntegerProperty idProveedor;
    private final StringProperty empresa;
    private final StringProperty nombre;
    private final StringProperty razonSocial;
    private final StringProperty nit;
    private final StringProperty email;
    private final IntegerProperty telefono;
    private final StringProperty direccion;

    // 🔹 Constructor vacío
    public Proveedor() {
        this.idProveedor = new SimpleIntegerProperty();
        this.empresa = new SimpleStringProperty();
        this.nombre = new SimpleStringProperty();
        this.razonSocial = new SimpleStringProperty();
        this.nit = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.telefono = new SimpleIntegerProperty();
        this.direccion = new SimpleStringProperty();
    }

    // 🔹 Constructor completo
    public Proveedor(int idProveedor, String empresa, String nombre, String razonSocial,
                     String nit, String email, int telefono, String direccion) {
        this();
        this.idProveedor.set(idProveedor);
        this.empresa.set(empresa);
        this.nombre.set(nombre);
        this.razonSocial.set(razonSocial);
        this.nit.set(nit);
        this.email.set(email);
        this.telefono.set(telefono);
        this.direccion.set(direccion);
    }

    // 🔹 Getters y Setters
    public int getIdProveedor() { return idProveedor.get(); }
    public void setIdProveedor(int idProveedor) { this.idProveedor.set(idProveedor); }
    public IntegerProperty idProveedorProperty() { return idProveedor; }

    public String getEmpresa() { return empresa.get(); }
    public void setEmpresa(String empresa) { this.empresa.set(empresa); }
    public StringProperty empresaProperty() { return empresa; }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public StringProperty nombreProperty() { return nombre; }

    public String getRazonSocial() { return razonSocial.get(); }
    public void setRazonSocial(String razonSocial) { this.razonSocial.set(razonSocial); }
    public StringProperty razonSocialProperty() { return razonSocial; }

    public String getNit() { return nit.get(); }
    public void setNit(String nit) { this.nit.set(nit); }
    public StringProperty nitProperty() { return nit; }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    public int getTelefono() { return telefono.get(); }
    public void setTelefono(int telefono) { this.telefono.set(telefono); }
    public IntegerProperty telefonoProperty() { return telefono; }

    public String getDireccion() { return direccion.get(); }
    public void setDireccion(String direccion) { this.direccion.set(direccion); }
    public StringProperty direccionProperty() { return direccion; }
}
