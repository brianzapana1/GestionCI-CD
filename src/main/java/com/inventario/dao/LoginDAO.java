package com.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.inventario.models.Usuario;
import com.inventario.utils.DatabaseConnection;

public class LoginDAO {

    public Usuario obtenerUsuarioPorNombre(String usuario) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getInt("id_rol"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("usuario"),
                        rs.getString("password"),
                        rs.getString("creado")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener usuario: " + e.getMessage());
        }
        return null;
    }

    public String obtenerNombreRol(int idRol) {
        String sql = "SELECT tipoRol FROM roles WHERE id_rol = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRol);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tipoRol"); 
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener el nombre del rol: " + e.getMessage());
        }
        return null;
    }
}
