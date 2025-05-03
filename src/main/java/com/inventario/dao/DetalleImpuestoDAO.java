package com.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.inventario.models.DetalleImpuesto;
import com.inventario.utils.DatabaseConnection;

public class DetalleImpuestoDAO {

    public boolean guardarDetalleImpuesto(DetalleImpuesto detalle) {
        String sql = """
            INSERT INTO Detalle_impuestos (
                id_venta, id_producto, cantidad, precio_compra, precio_venta,
                utilidad, impuesto_3p, impuesto_13p, total_venta, total_impuestos, fecha
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, detalle.getIdVenta());
                stmt.setInt(2, detalle.getIdProducto());
                stmt.setInt(3, detalle.getCantidad());
                stmt.setBigDecimal(4, detalle.getPrecioCompra());
                stmt.setBigDecimal(5, detalle.getPrecioVenta());
                stmt.setBigDecimal(6, detalle.getUtilidad());
                stmt.setBigDecimal(7, detalle.getImpuesto3p());
                stmt.setBigDecimal(8, detalle.getImpuesto13p());
                stmt.setBigDecimal(9, detalle.getTotalVenta());
                stmt.setBigDecimal(10, detalle.getTotalImpuestos());
                stmt.setTimestamp(11, Timestamp.valueOf(detalle.getFecha())); // NUEVA FECHA
                

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar detalle de impuesto: " + e.getMessage());
            return false;
        }
    }
}
