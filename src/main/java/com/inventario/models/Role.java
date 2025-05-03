package com.inventario.models;

public class Role {
    private int id;
    private String tipoRol;

    public Role() {}

    public Role(int id, String tipoRol) {
        this.id = id;
        this.tipoRol = tipoRol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipoRol() {
        return tipoRol;
    }

    public void setTipoRol(String tipoRol) {
        this.tipoRol = tipoRol;
    }

    @Override
    public String toString() {
        return tipoRol;  // Para mostrar en ComboBox o ListView
    }
}
