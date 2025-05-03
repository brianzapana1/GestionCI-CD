package com.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.inventario.models.Usuario;
import com.inventario.utils.DatabaseConnection;

public class UsuarioDAO {

    /**
     * 🔹 Agrega un nuevo usuario a la base de datos.
     */
    public boolean agregarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (id_rol, nombre, email, usuario, password, creado) VALUES (?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuario.getIdRol());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getUsuario());
            stmt.setString(5, usuario.getPasswordHash()); // 🔹 Ya debe venir hasheada desde el Service

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al agregar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔹 Verifica si un usuario ya existe en la base de datos.
     * 🔹 Solo devuelve un booleano, sin imprimir mensajes.
     */
    public boolean existeUsuario(String usuario) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia del usuario: " + e.getMessage());
        }
        return false;
    }

    /**
     * 🔹 Obtiene el ID de un rol a partir del nombre del rol.
     */
    public int obtenerIdRol(String nombreRol) {
        String sql = "SELECT id_rol FROM roles WHERE tipoRol = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreRol);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_rol");
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ID del rol: " + e.getMessage());
        }
        return -1;
    }

    /**
     * 🔹 Obtiene todos los nombres de los roles para llenar el ComboBox.
     */
    public List<String> obtenerRoles() {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT tipoRol FROM roles";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                roles.add(rs.getString("tipoRol"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener los roles: " + e.getMessage());
        }

        return roles;
    }
}
