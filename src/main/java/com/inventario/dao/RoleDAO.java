package com.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.inventario.models.Role;
import com.inventario.utils.DatabaseConnection;

public class RoleDAO {

    // Obtener todos los roles
    public List<Role> obtenerTodosLosRoles() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM public.roles ORDER BY id_rol ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                roles.add(new Role(rs.getInt("id_rol"), rs.getString("tiporol")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    // Agregar un nuevo rol
    public boolean agregarRol(String tipoRol) {
        String sql = "INSERT INTO public.roles (tiporol) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipoRol);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Actualizar un rol existente
    public boolean actualizarRol(int idRol, String nuevoNombre) {
        String sql = "UPDATE public.roles SET tiporol = ? WHERE id_rol = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoNombre);
            stmt.setInt(2, idRol);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
   

    // Eliminar un rol
    public boolean eliminarRol(int idRol) {
        String sql = "DELETE FROM public.roles WHERE id_rol = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRol);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
