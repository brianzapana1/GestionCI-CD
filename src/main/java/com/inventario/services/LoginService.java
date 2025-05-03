package com.inventario.services;

import com.inventario.dao.LoginDAO;
import com.inventario.models.Usuario;
import com.inventario.utils.PasswordUtils;
import com.inventario.utils.SessionManager;

public class LoginService {
    private final LoginDAO loginDAO;

    public LoginService() {
        this.loginDAO = new LoginDAO();
    }

    public boolean autenticarUsuario(String usuario, String password) {
        Usuario usuarioEncontrado = loginDAO.obtenerUsuarioPorNombre(usuario);
        
        if (usuarioEncontrado == null) {
            return false; // No se encontró el usuario
        }

        // Verificar la contraseña con hash
        if (PasswordUtils.verifyPassword(password, usuarioEncontrado.getPasswordHash())) {
            // Obtener el nombre del rol en base al ID del rol
            String nombreRol = loginDAO.obtenerNombreRol(usuarioEncontrado.getIdRol());

            // 🔹 Asegurar que el nombre del rol no sea nulo antes de iniciar sesión
            if (nombreRol == null) {
                System.err.println("❌ Error: No se pudo obtener el nombre del rol.");
                return false;
            }

            // Iniciar sesión y guardar en SessionManager
            SessionManager.iniciarSesion(usuarioEncontrado, nombreRol);
            return true;
        }

        return false;
    }
}
