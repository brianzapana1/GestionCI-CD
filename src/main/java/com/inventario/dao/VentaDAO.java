package com.inventario.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inventario.models.DetalleVenta;
import com.inventario.models.Venta;
import com.inventario.utils.DatabaseConnection;

public class VentaDAO {

    /**
     * 🔹 Registra una nueva venta en la base de datos.
     * 🔹 Usa una transacción para garantizar consistencia.
     * 🔹 Se descuenta el stock respetando PEPS.
     */
    public int registrarVenta(Venta venta) {
        String sqlVenta = "INSERT INTO ventas (id_usuario, id_cliente, id_tipocomp, numeroVenta, descuento, subTotal, iva, total, fecha, estado) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), 'EMITIDO') RETURNING id_venta";

        String sqlDetalle = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, costo, precioVenta, total) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";

        int idVenta = -1;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtVenta = conn.prepareStatement(sqlVenta)) {
                stmtVenta.setInt(1, venta.getIdUsuario());
                stmtVenta.setInt(2, venta.getIdCliente());
                stmtVenta.setInt(3, venta.getIdTipoComprobante());
                stmtVenta.setString(4, venta.getNumeroVenta());
                stmtVenta.setBigDecimal(5, venta.getDescuento());
                stmtVenta.setBigDecimal(6, venta.getSubTotal());
                stmtVenta.setBigDecimal(7, venta.getIva());
                stmtVenta.setBigDecimal(8, venta.getTotal());

                ResultSet rs = stmtVenta.executeQuery();
                if (rs.next()) {
                    idVenta = rs.getInt("id_venta");
                }

                if (idVenta > 0) {
                    for (DetalleVenta detalle : venta.getDetalles()) {
                        try (PreparedStatement stmtDetalle = conn.prepareStatement(sqlDetalle)) {
                            stmtDetalle.setInt(1, idVenta);
                            stmtDetalle.setInt(2, detalle.getIdProducto());
                            stmtDetalle.setInt(3, detalle.getCantidad());
                            stmtDetalle.setBigDecimal(4, detalle.getCosto());
                            stmtDetalle.setBigDecimal(5, detalle.getPrecioVenta());
                            stmtDetalle.setBigDecimal(6, detalle.getTotal());

                            stmtDetalle.executeUpdate();
                        }

                        descontarStockPEPS(conn, detalle.getIdProducto(), detalle.getCantidad());
                    }

                    conn.commit();
                } else {
                    conn.rollback();
                }

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("❌ Error al registrar venta: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error en la conexión a la base de datos: " + e.getMessage());
        }

        return idVenta;
    }
    

    /**
     * 🔹 Método para descontar stock siguiendo la lógica PEPS.
     */
    private void descontarStockPEPS(Connection conn, int idProducto, int cantidadNecesaria) throws SQLException {
        String sqlSelect = "SELECT id_inventario, cantidad_disponible FROM inventario " +
                           "WHERE id_producto = ? AND cantidad_disponible > 0 ORDER BY fecha_ingreso ASC";

        String sqlUpdate = "UPDATE inventario SET cantidad_disponible = ? WHERE id_inventario = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {
            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next() && cantidadNecesaria > 0) {
                int idInventario = rs.getInt("id_inventario");
                int stockActual = rs.getInt("cantidad_disponible");

                int aDescontar = Math.min(cantidadNecesaria, stockActual);
                int nuevoStock = stockActual - aDescontar;

                try (PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate)) {
                    updateStmt.setInt(1, nuevoStock);
                    updateStmt.setInt(2, idInventario);
                    updateStmt.executeUpdate();
                }

                cantidadNecesaria -= aDescontar;
            }

            if (cantidadNecesaria > 0) {
                System.err.println("⚠ No hay suficiente stock PEPS para el producto ID: " + idProducto);
            }
        }
    }

    /**
     * 🔹 Obtiene una lista de todas las ventas registradas.
     */
    public List<Venta> obtenerVentas() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT id_venta, id_usuario, id_cliente, id_tipocomp, numeroVenta, " +
                     "descuento, subTotal, iva, total, fecha, estado FROM ventas ORDER BY fecha DESC";
    
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
    
            System.out.println("📌 Ejecutando consulta: " + sql); // ✅ Imprimir la consulta SQL para depuración
    
            while (rs.next()) {
                Venta venta = new Venta();
                venta.setIdVenta(rs.getInt("id_venta"));
                venta.setIdUsuario(rs.getInt("id_usuario"));
                venta.setIdCliente(rs.getInt("id_cliente"));
                venta.setIdTipoComprobante(rs.getInt("id_tipocomp")); // ✅ Incluir id_tipocomp
                venta.setNumeroVenta(rs.getString("numeroVenta"));
                venta.setDescuento(rs.getBigDecimal("descuento"));
                venta.setSubTotal(rs.getBigDecimal("subTotal"));
                venta.setIva(rs.getBigDecimal("iva"));
                venta.setTotal(rs.getBigDecimal("total"));
                venta.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                venta.setEstado(rs.getString("estado"));
    
                ventas.add(venta);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ventas: " + e.getMessage());
            e.printStackTrace();
        }
        return ventas;
    }
    

    /**
     * 🔹 Obtiene los detalles de una venta específica.
     */
    public List<DetalleVenta> obtenerDetallesVenta(int idVenta) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT dv.*, p.nombre AS nombreProducto FROM detalle_ventas dv " +
                     "JOIN productos p ON dv.id_producto = p.id_producto " +
                     "WHERE dv.id_venta = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idVenta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                detalles.add(new DetalleVenta(
                        rs.getInt("id_detalle"),
                        rs.getInt("id_venta"),
                        rs.getInt("id_producto"),
                        rs.getString("nombreProducto"),
                        rs.getInt("cantidad"),
                        rs.getBigDecimal("costo"),
                        rs.getBigDecimal("precioVenta"),
                        rs.getBigDecimal("total")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener detalles de venta: " + e.getMessage());
            e.printStackTrace();
        }
        return detalles;
    }

    public String obtenerUltimoNumeroVenta() {
        String sql = "SELECT numeroVenta FROM ventas ORDER BY id_venta DESC LIMIT 1";
        String ultimoNumero = "V00000"; // Valor por defecto si no hay ventas previas
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            if (rs.next()) {
                ultimoNumero = rs.getString("numeroVenta");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener el último número de venta: " + e.getMessage());
        }
    
        return ultimoNumero;
    }
    

    /**
     * 🔹 Cancela una venta y devuelve el stock respetando PEPS.
     */
    public boolean cancelarVenta(int idVenta) {
        String sqlUpdateEstado = "UPDATE ventas SET estado = 'CANCELADO' WHERE id_venta = ?";
        String sqlDetalles = "SELECT id_producto, cantidad, costo FROM detalle_ventas WHERE id_venta = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // ✅ Actualizar estado de la venta
            try (PreparedStatement stmtEstado = conn.prepareStatement(sqlUpdateEstado)) {
                stmtEstado.setInt(1, idVenta);
                stmtEstado.executeUpdate();
            }

            // ✅ Obtener detalles de la venta
            ProductosDAO productosDAO = new ProductosDAO(); // ✅ Instancia del DAO

            try (PreparedStatement stmtDetalles = conn.prepareStatement(sqlDetalles)) {
                stmtDetalles.setInt(1, idVenta);
                ResultSet rs = stmtDetalles.executeQuery();

                while (rs.next()) {
                    int idProducto = rs.getInt("id_producto");
                    int cantidad = rs.getInt("cantidad");
                    BigDecimal costo = rs.getBigDecimal("costo");

                    productosDAO.reintegrarStockPEPS(conn, idProducto, cantidad, costo); // ✅ Llamada correcta
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error al cancelar venta: " + e.getMessage());
            return false;
        }
    }

    public Map<String, Object> obtenerDatosRecibo(int idVenta) {
    Map<String, Object> datos = new HashMap<>();

    String sql = """
        SELECT v.numeroVenta, v.fecha, v.subTotal, v.descuento, v.total,
               c.razonSocial, c.nit, tc.descripcion AS comprobante,
               u.nombre AS vendedor, r.tipo_pago, r.montoTotal
        FROM Ventas v
        JOIN Clientes c ON v.id_cliente = c.id_cliente
        JOIN Tipo_comprobante tc ON v.id_tipocomp = tc.id_tipocomp
        JOIN Usuarios u ON v.id_usuario = u.id_usuario
        JOIN Recibos r ON v.id_venta = r.id_venta
        WHERE v.id_venta = ?
    """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, idVenta);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                datos.put("numeroVenta", rs.getString("numeroVenta"));
                datos.put("fecha", rs.getTimestamp("fecha"));
                datos.put("subTotal", rs.getBigDecimal("subTotal"));
                datos.put("descuento", rs.getBigDecimal("descuento"));
                datos.put("total", rs.getBigDecimal("total"));
                datos.put("razonSocial", rs.getString("razonSocial"));
                datos.put("nit", rs.getString("nit"));
                datos.put("comprobante", rs.getString("comprobante"));
                datos.put("vendedor", rs.getString("vendedor"));
                datos.put("tipo_pago", rs.getString("tipo_pago"));
                datos.put("montoTotal", rs.getBigDecimal("montoTotal"));
            }
        }
    } catch (Exception e) {
        System.err.println("❌ Error al obtener datos del recibo: " + e.getMessage());
    }

    return datos;
}

    public List<Map<String, Object>> obtenerProductosVenta(int idVenta) {
        List<Map<String, Object>> productos = new ArrayList<>();

        String sql = """
            SELECT p.nombre, dv.cantidad, dv.precioVenta, dv.total
            FROM Detalle_ventas dv
            JOIN Productos p ON dv.id_producto = p.id_producto
            WHERE dv.id_venta = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> prod = new HashMap<>();
                    prod.put("nombre", rs.getString("nombre"));
                    prod.put("cantidad", rs.getInt("cantidad"));
                    prod.put("precioVenta", rs.getBigDecimal("precioVenta"));
                    prod.put("total", rs.getBigDecimal("total"));
                    productos.add(prod);
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error al obtener productos del recibo: " + e.getMessage()); //error al obtener producto
        }

        return productos;
}

    public void crearRecibo(int idVenta, String tipoPago) {
        String sql = "INSERT INTO Recibos (id_venta, tipo_pago, fecha, montoTotal) " +
                    "VALUES (?, ?, NOW(), (SELECT total FROM ventas WHERE id_venta = ?))";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenta);
            stmt.setString(2, tipoPago);
            stmt.setInt(3, idVenta);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error al crear recibo: " + e.getMessage());
        }
    }


}
