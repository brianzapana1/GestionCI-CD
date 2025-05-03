package com.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.inventario.models.Categoria;
import com.inventario.utils.DatabaseConnection;

public class CategoriaDAO {

    /**
     * 🔹 Obtiene todas las categorías de la base de datos.
     */
    public List<Categoria> obtenerCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY nombre ASC"; // ✅ Orden alfabético

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categorias.add(new Categoria(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                ));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener categorías: " + e.getMessage());
        }
        return categorias;
    }

    /**
     * 🔹 Agrega una nueva categoría a la base de datos.
     */
    public boolean agregarCategoria(Categoria categoria) {
        String sql = "INSERT INTO categorias (nombre, descripcion) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNombre());
            stmt.setString(2, categoria.getDescripcion());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al agregar categoría: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔹 Verifica si una categoría ya existe por su nombre.
     */
    public boolean existeCategoria(String nombre) {
        String sql = "SELECT COUNT(*) FROM categorias WHERE LOWER(nombre) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre.trim());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // ✅ Si existe, devuelve true
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al verificar la existencia de la categoría: " + e.getMessage());
        }
        return false;
    }

    /**
     * 🔹 Obtiene una categoría específica por su ID.
     */
    public Categoria obtenerCategoriaPorId(int idCategoria) {
        String sql = "SELECT * FROM categorias WHERE id_categoria = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCategoria);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Categoria(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener categoría por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * 🔹 Actualiza una categoría existente.
     */
    public boolean actualizarCategoria(Categoria categoria) {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ? WHERE id_categoria = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNombre());
            stmt.setString(2, categoria.getDescripcion());
            stmt.setInt(3, categoria.getIdCategoria());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar categoría: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔹 Elimina una categoría por su ID.
     */
    public boolean eliminarCategoria(int idCategoria) {
        // Primero verificamos si la categoría está asociada a algún producto
        String verificarProductos = "SELECT COUNT(*) FROM productos WHERE id_categoria = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(verificarProductos)) {

            checkStmt.setInt(1, idCategoria);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("⚠ No se puede eliminar la categoría porque está asociada a productos.");
                return false; // No se puede eliminar si hay productos asociados
            }

            // Si no hay productos asociados, procedemos con la eliminación
            String eliminarSQL = "DELETE FROM categorias WHERE id_categoria = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(eliminarSQL)) {
                deleteStmt.setInt(1, idCategoria);
                return deleteStmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }
}
