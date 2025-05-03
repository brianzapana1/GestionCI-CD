package com.inventario.services;

import java.util.List;

import com.inventario.dao.ProveedorDAO;
import com.inventario.models.Proveedor;

public class ProveedorService {

    private final ProveedorDAO proveedorDAO;

    public ProveedorService() {
        this.proveedorDAO = new ProveedorDAO();
    }

    /**
     * 🔹 Devuelve todos los proveedores
     */
    public List<Proveedor> obtenerTodos() {
        return proveedorDAO.obtenerTodos();
    }

    /**
     * 🔹 Devuelve un proveedor por ID
     */
    public Proveedor obtenerPorId(int id) {
        return proveedorDAO.obtenerPorId(id);
    }

    /**
     * 🔹 Agrega un nuevo proveedor si pasa las validaciones
     */
    public void agregarProveedor(Proveedor proveedor) throws Exception {
        if (!validarProveedor(proveedor)) {
            throw new Exception("❌ Datos del proveedor inválidos. Revisa los campos.");
        }

        boolean exito = proveedorDAO.agregar(proveedor);
        if (!exito) {
            throw new Exception("❌ No se pudo agregar el proveedor.");
        }
    }

    /**
     * 🔹 Actualiza un proveedor existente
     */
    public void actualizarProveedor(Proveedor proveedor) throws Exception {
        if (!validarProveedor(proveedor)) {
            throw new Exception("❌ Datos del proveedor inválidos. Revisa los campos.");
        }

        boolean exito = proveedorDAO.actualizar(proveedor);
        if (!exito) {
            throw new Exception("❌ No se pudo actualizar el proveedor.");
        }
    }

    /**
     * ❌ Elimina un proveedor por ID
     */
    public boolean eliminarProveedor(int id) {
        return proveedorDAO.eliminar(id);
    }

    /**
     * 🔍 Valida los datos del proveedor antes de guardar
     */
    private boolean validarProveedor(Proveedor proveedor) {
        boolean valido = true;

        // Razon Social y Nombre obligatorios
        if (proveedor.getRazonSocial() == null || proveedor.getRazonSocial().trim().isEmpty()) {
            System.err.println("⚠ La razón social es obligatoria.");
            valido = false;
        }

        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            System.err.println("⚠ El nombre del proveedor es obligatorio.");
            valido = false;
        }

        if (proveedor.getNit() == null || proveedor.getNit().trim().isEmpty()) {
            System.err.println("⚠ El NIT es obligatorio.");
            valido = false;
        }

        if (proveedor.getEmail() != null && !proveedor.getEmail().trim().isEmpty()) {
            if (!proveedor.getEmail().matches("^(.+)@(.+)$")) {
                System.err.println("⚠ El email no tiene formato válido.");
                valido = false;
            }
        }

        if (proveedor.getTelefono() < 1000000) {
            System.err.println("⚠ Número de teléfono inválido o muy corto.");
            valido = false;
        }

        return valido;
    }

    public List<Proveedor> obtenerProveedores() {
        return proveedorDAO.obtenerTodos();
    }
    
}
