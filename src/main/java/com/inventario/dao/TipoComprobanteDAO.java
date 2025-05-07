package com.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.inventario.models.TipoComprobante;
import com.inventario.utils.DatabaseConnection;

public class TipoComprobanteDAO {

    /**
     * 🔹 Registra un nuevo tipo de comprobante en la base de datos.
     */
    public boolean registrarTipoComprobante(TipoComprobante tipoComprobante) {
        String sql = "INSERT INTO tipo_comprobante (descripcion, estado) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    
            stmt.setString(1, tipoComprobante.getDescripcion());
            stmt.setBoolean(2, tipoComprobante.isEstado()); // ✅ Cambio aquí: setBoolean en lugar de setString
    
            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    tipoComprobante.setIdTipoComp(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al registrar tipo de comprobante: " + e.getMessage());
        }
        return false;
    }
    

    /**
     * 🔹 Obtiene un tipo de comprobante por su ID.
     */
    public TipoComprobante obtenerTipoComprobantePorId(int id) {
        String sql = "SELECT * FROM tipo_comprobante WHERE id_tipocomp = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                return new TipoComprobante(
                    rs.getInt("id_tipocomp"),
                    rs.getString("descripcion"),
                    rs.getBoolean("estado") 
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener tipo de comprobante por ID: " + e.getMessage());
        }
        return null;
    }
    

    /**
     * 🔹 Actualiza un tipo de comprobante existente.
     */
    public boolean actualizarTipoComprobante(TipoComprobante tipoComprobante) {
        String sql = "UPDATE tipo_comprobante SET descripcion = ?, estado = ? WHERE id_tipocomp = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, tipoComprobante.getDescripcion());
            stmt.setBoolean(2, tipoComprobante.isEstado()); // ✅ Cambio aquí: setBoolean en lugar de setString
            stmt.setInt(3, tipoComprobante.getIdTipoComp());
    
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar tipo de comprobante: " + e.getMessage());
        }
        return false;
    }
    

    /**
     * 🔹 Elimina un tipo de comprobante por su ID.
     */
    public boolean eliminarTipoComprobante(int id) {
        String sql = "DELETE FROM tipo_comprobante WHERE id_tipocomp = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar tipo de comprobante: " + e.getMessage());
        }
        return false;
    }

    /**
     * 🔹 Obtiene una lista de todos los tipos de comprobantes.
     */
    public List<TipoComprobante> listarTiposComprobantes() {
        List<TipoComprobante> lista = new ArrayList<>();
        String sql = "SELECT * FROM tipo_comprobante ORDER BY descripcion ASC";
    
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
    
            while (rs.next()) {
                lista.add(new TipoComprobante(
                    rs.getInt("id_tipocomp"),
                    rs.getString("descripcion"),
                    rs.getBoolean("estado") // ✅ Cambio aquí: ahora usa getBoolean()
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar tipos de comprobantes: " + e.getMessage());
        }
        return lista;
    }
    
    /**
     * 🔹 Busca tipos de comprobantes por descripción.
     */
    public List<TipoComprobante> buscarTipoComprobantePorDescripcion(String descripcion) {
        List<TipoComprobante> lista = new ArrayList<>();
        String sql = "SELECT * FROM tipo_comprobante WHERE LOWER(descripcion) LIKE LOWER(?)";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, "%" + descripcion.trim() + "%");
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                lista.add(new TipoComprobante(
                    rs.getInt("id_tipocomp"),
                    rs.getString("descripcion"),
                    rs.getBoolean("estado") // ✅ Cambio aquí: ahora usa getBoolean()
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar tipos de comprobantes por descripción: " + e.getMessage());
        }
        return lista;
    }
    

    /**
     * 🔹 Busca tipos de comprobantes por estado.
     */
    public List<TipoComprobante> buscarTipoComprobantePorEstado(boolean estado) {
        List<TipoComprobante> lista = new ArrayList<>();
        String sql = "SELECT * FROM tipo_comprobante WHERE estado = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setBoolean(1, estado); // ✅ Cambio aquí: ahora usa setBoolean()
    
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                lista.add(new TipoComprobante(
                    rs.getInt("id_tipocomp"),
                    rs.getString("descripcion"),
                    rs.getBoolean("estado") // ✅ Cambio aquí: ahora usa getBoolean()
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar tipos de comprobantes por estado: " + e.getMessage());
        }
        return lista;
    }
    
}
