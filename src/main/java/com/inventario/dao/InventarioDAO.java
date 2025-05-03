package com.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.inventario.models.Inventario;
import com.inventario.models.InventarioResumen;
import com.inventario.utils.DatabaseConnection;

public class InventarioDAO {

    public boolean agregarInventario(int idProducto, int idDetalleCompra, int cantidad) {
        String sql = "INSERT INTO inventario (id_producto, id_detallecomp, cantidad_disponible, fecha_ingreso) " +
                     "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProducto);
            stmt.setInt(2, idDetalleCompra);
            stmt.setInt(3, cantidad);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al agregar al inventario: " + e.getMessage());
            return false;
        }
    }

    public List<Inventario> obtenerInventarioDisponiblePorProductoPEPS(int idProducto) {
        List<Inventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM inventario " +
                     "WHERE id_producto = ? AND cantidad_disponible > 0 " +
                     "ORDER BY fecha_ingreso ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Inventario inv = new Inventario();
                inv.setIdInventario(rs.getInt("id_inventario"));
                inv.setIdProducto(rs.getInt("id_producto"));
                inv.setIdDetalleCompra(rs.getInt("id_detallecomp"));
                inv.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                inv.setFechaIngreso(rs.getTimestamp("fecha_ingreso").toLocalDateTime());

                lista.add(inv);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener inventario PEPS: " + e.getMessage());
        }

        return lista;
    }

    public boolean actualizarCantidadDisponible(int idInventario, int nuevaCantidad) {
        String sql = "UPDATE inventario SET cantidad_disponible = ? WHERE id_inventario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nuevaCantidad);
            stmt.setInt(2, idInventario);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar stock del inventario: " + e.getMessage());
            return false;
        }
    }

    public List<InventarioResumen> obtenerResumenInventario() {
        List<InventarioResumen> lista = new ArrayList<>();
        String sql = """
            SELECT p.codigo, p.nombre, p.imagen_url, 
                   COALESCE(SUM(i.cantidad_disponible), 0) AS stock_total
            FROM productos p
            LEFT JOIN inventario i ON p.id_producto = i.id_producto
            GROUP BY p.codigo, p.nombre, p.imagen_url
            ORDER BY p.nombre ASC
        """;
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                InventarioResumen resumen = new InventarioResumen();
                resumen.setCodigo(rs.getString("codigo"));
                resumen.setNombre(rs.getString("nombre"));
                resumen.setImagenUrl(rs.getString("imagen_url"));
                resumen.setStockTotal(rs.getInt("stock_total"));
    
                lista.add(resumen);
            }
    
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener resumen de inventario: " + e.getMessage());
        }
    
        return lista;
    }
    
    public int obtenerStockTotalPorProducto(int idProducto) {
        String sql = "SELECT COALESCE(SUM(cantidad_disponible), 0) AS stock_total " +
                     "FROM inventario WHERE id_producto = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                return rs.getInt("stock_total");
            }
    
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener stock total por producto: " + e.getMessage());
        }
    
        return 0;
    }
    
}
