package com.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.inventario.models.ReporteVenta;
import com.inventario.utils.DatabaseConnection;

public class ReporteVentaDAO {

    public List<ReporteVenta> obtenerReporteVentas() {
        List<ReporteVenta> lista = new ArrayList<>();

        String sql = """
            SELECT 
                p.nombre AS producto,
                d.cantidad,
                d.precio_compra,
                d.precio_venta,
                d.utilidad,
                d.impuesto_3p,
                d.impuesto_13p,
                d.total_venta,
                d.total_impuestos,
                TO_CHAR(d.fecha, 'DD/MM/YYYY HH24:MI') AS fecha
            FROM Detalle_impuestos d
            JOIN Productos p ON d.id_producto = p.id_producto
            ORDER BY d.id_detalleimpuesto DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ReporteVenta r = new ReporteVenta();
                r.setProducto(rs.getString("producto"));
                r.setCantidad(rs.getInt("cantidad"));
                r.setPrecioCompra(rs.getBigDecimal("precio_compra"));
                r.setPrecioVenta(rs.getBigDecimal("precio_venta"));
                r.setUtilidad(rs.getBigDecimal("utilidad"));
                r.setImpuesto3p(rs.getBigDecimal("impuesto_3p"));
                r.setImpuesto13p(rs.getBigDecimal("impuesto_13p"));
                r.setTotalVenta(rs.getBigDecimal("total_venta"));
                r.setTotalImpuestos(rs.getBigDecimal("total_impuestos"));
                r.setFecha(rs.getString("fecha")); // ✅ nueva línea
                lista.add(r);
            }

        } catch (Exception e) {
            System.err.println("❌ Error al obtener reporte de ventas: " + e.getMessage());
        }

        return lista;
    }
}
