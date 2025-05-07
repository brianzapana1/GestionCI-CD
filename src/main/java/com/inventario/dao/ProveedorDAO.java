package com.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.inventario.models.Proveedor;
import com.inventario.utils.DatabaseConnection;

public class ProveedorDAO {

    /**
     * 🔹 Obtener todos los proveedores
     */
    public List<Proveedor> obtenerTodos() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM proveedores ORDER BY id_proveedor DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Proveedor proveedor = new Proveedor(
                    rs.getInt("id_proveedor"),
                    rs.getString("empresa"),
                    rs.getString("nombre"),
                    rs.getString("razonSocial"),
                    rs.getString("nit"),
                    rs.getString("email"),
                    rs.getInt("telefono"),
                    rs.getString("direccion")
                );
                lista.add(proveedor);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener proveedores: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * 🔎 Busqueda de proveedores por ID
     */
    public Proveedor obtenerPorId(int id) {
        String sql = "SELECT * FROM proveedores WHERE id_proveedor = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Proveedor(
                    rs.getInt("id_proveedor"),
                    rs.getString("empresa"),
                    rs.getString("nombre"),
                    rs.getString("razonSocial"),
                    rs.getString("nit"),
                    rs.getString("email"),
                    rs.getInt("telefono"),
                    rs.getString("direccion")
                );
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener proveedor por ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * ➕ Agregar nuevo proveedor
     */
    public boolean agregar(Proveedor proveedor) {
        String sql = "INSERT INTO proveedores (empresa, nombre, razonSocial, nit, email, telefono, direccion) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, proveedor.getEmpresa());
            stmt.setString(2, proveedor.getNombre());
            stmt.setString(3, proveedor.getRazonSocial());
            stmt.setString(4, proveedor.getNit());
            stmt.setString(5, proveedor.getEmail());
            stmt.setInt(6, proveedor.getTelefono());
            stmt.setString(7, proveedor.getDireccion());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al agregar proveedor: " + e.getMessage());
            return false;
        }
    }

    /**
     * ✏️ Actualizar proveedor existente
     */
    public boolean actualizar(Proveedor proveedor) {
        String sql = "UPDATE proveedores SET empresa = ?, nombre = ?, razonSocial = ?, nit = ?, email = ?, telefono = ?, direccion = ? " +
                     "WHERE id_proveedor = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, proveedor.getEmpresa());
            stmt.setString(2, proveedor.getNombre());
            stmt.setString(3, proveedor.getRazonSocial());
            stmt.setString(4, proveedor.getNit());
            stmt.setString(5, proveedor.getEmail());
            stmt.setInt(6, proveedor.getTelefono());
            stmt.setString(7, proveedor.getDireccion());
            stmt.setInt(8, proveedor.getIdProveedor());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar proveedor: " + e.getMessage());
            return false;
        }
    }

    /**
     * ❌ Eliminar proveedor por ID
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM proveedores WHERE id_proveedor = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar proveedor: " + e.getMessage());
            return false;
        }
    }
}
