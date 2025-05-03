package com.inventario.services;

import java.util.List;

import com.inventario.dao.RoleDAO;
import com.inventario.models.Role;

public class RoleService {
    private final RoleDAO roleDAO = new RoleDAO();

    // Obtener todos los roles
    public List<Role> obtenerRoles() {
        return roleDAO.obtenerTodosLosRoles();
    }

    // Agregar un rol con validaciones
    public boolean agregarRol(String tipoRol) {
        if (tipoRol == null || tipoRol.trim().isEmpty()) {
            System.err.println("Error: El nombre del rol no puede estar vacío.");
            return false;
        }

        // Validar si el rol ya existe
        for (Role rol : obtenerRoles()) {
            if (rol.getTipoRol().equalsIgnoreCase(tipoRol.trim())) {
                System.err.println("Error: Ya existe un rol con el mismo nombre.");
                return false;
            }
        }

        return roleDAO.agregarRol(tipoRol.trim());
    }

    // Actualizar un rol
    public boolean actualizarRol(int idRol, String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            System.err.println("Error: El nombre del rol no puede estar vacío.");
            return false;
        }

        return roleDAO.actualizarRol(idRol, nuevoNombre.trim());
    }

    // Eliminar un rol
    public boolean eliminarRol(int idRol) {
        return roleDAO.eliminarRol(idRol);
    }
}
