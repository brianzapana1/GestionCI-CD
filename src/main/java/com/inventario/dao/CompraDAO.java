package com.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.inventario.models.Compra;
import com.inventario.utils.DatabaseConnection;

public class CompraDAO {

    public int registrarCompra(Compra compra) {
        int idGenerado = -1; 

        String sql = "INSERT INTO compra (id_proveedor, id_usuario, id_tipocomp, numeroCompra, fecha, tipoPago, total, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id_compra";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, compra.getIdProveedor());
            stmt.setInt(2, compra.getIdUsuario());
            stmt.setInt(3, compra.getIdTipoComprobante());
            stmt.setInt(4, compra.getNumeroCompra());
            stmt.setTimestamp(5, Timestamp.valueOf(compra.getFecha()));
            stmt.setString(6, compra.getTipoPago());
            stmt.setBigDecimal(7, compra.getTotal());
            stmt.setString(8, compra.getEstado());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                idGenerado = rs.getInt("id_compra");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al registrar compra: " + e.getMessage());
            e.printStackTrace();
        }

        return idGenerado;
    }

    public List<Compra> listarCompras() {
        List<Compra> lista = new ArrayList<>();
        String sql = "SELECT * FROM compra";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Compra compra = new Compra();
                compra.setIdCompra(rs.getInt("id_compra"));
                compra.setIdProveedor(rs.getInt("id_proveedor"));
                compra.setIdUsuario(rs.getInt("id_usuario"));
                compra.setIdTipoComprobante(rs.getInt("id_tipocomp"));
                compra.setNumeroCompra(rs.getInt("numeroCompra"));
                compra.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                compra.setTipoPago(rs.getString("tipoPago"));
                compra.setTotal(rs.getBigDecimal("total"));
                compra.setEstado(rs.getString("estado"));

                lista.add(compra);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar compras: " + e.getMessage());
        }

        return lista;
    }

    public boolean eliminarCompra(int idCompra) {
        String sql = "DELETE FROM compra WHERE id_compra = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCompra);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar compra: " + e.getMessage());
        }

        return false;
    }

    public int obtenerUltimoNumeroCompra() {
        String sql = "SELECT MAX(numeroCompra) FROM compra";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            if (rs.next()) {
                return rs.getInt(1); // Devuelve el número más alto o 0 si no hay registros
            }
    
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener el último número de compra: " + e.getMessage());
            e.printStackTrace();
        }
    
        return 0;
        
    }
    
}
