package com.inventario.services;

import java.util.List;

import com.inventario.dao.UsuarioDAO;
import com.inventario.models.Usuario;
import com.inventario.utils.PasswordUtils;

public class UsuarioService {
    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * 🔹 Método para registrar un usuario.
     */
    public boolean registrarUsuario(Usuario usuario) {
        // 🔹 Validación de negocio: Evitar usuarios duplicados
        if (usuarioDAO.existeUsuario(usuario.getUsuario())) {
            System.out.println("⚠️ El usuario '" + usuario.getUsuario() + "' ya existe en la base de datos.");
            return false;
        }

        // 🔹 Aplicar hash a la contraseña antes de enviarla al DAO
        usuario.setPasswordHash(PasswordUtils.hashPassword(usuario.getPasswordHash()));

        // 🔹 Intentar agregarlo a la base de datos
        return usuarioDAO.agregarUsuario(usuario);
    }

    /**
     * 🔹 Método para obtener todos los roles disponibles desde la base de datos.
     */
    public List<String> obtenerRoles() {
        return usuarioDAO.obtenerRoles();
    }

    /**
     * 🔹 Método para obtener el ID de un rol basado en su nombre.
     */
    public int obtenerIdRol(String nombreRol) {
        return usuarioDAO.obtenerIdRol(nombreRol);
    }
}
