package com.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.inventario.models.DetalleCompra;
import com.inventario.utils.DatabaseConnection;

public class DetalleCompraDAO {

    public boolean guardarDetallesCompra(List<DetalleCompra> detalles, int idCompra) {
        String sql = "INSERT INTO detalle_compras (id_producto, id_compra, cantidad, precioUnitario, total) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING id_detallecomp";

        try (Connection conn = DatabaseConnection.getConnection()) {
            for (DetalleCompra d : detalles) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, d.getIdProducto());
                    stmt.setInt(2, idCompra);
                    stmt.setInt(3, d.getCantidad());
                    stmt.setBigDecimal(4, d.getPrecioUnitario());
                    stmt.setBigDecimal(5, d.getTotal());

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        d.setIdDetalleCompra(rs.getInt("id_detallecomp")); // ✅ Guardamos el ID generado
                    }
                }
            }
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar detalles de compra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
