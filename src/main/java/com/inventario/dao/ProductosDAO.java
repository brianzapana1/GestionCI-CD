package com.inventario.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.inventario.models.Categoria;
import com.inventario.models.Producto;
import com.inventario.utils.DatabaseConnection;

public class ProductosDAO {

    /**
     * 🔹 Obtiene todos los productos almacenados en la base de datos.
     */
    public List<Producto> obtenerProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, c.id_categoria, c.nombre AS categoria_nombre, c.descripcion AS categoria_descripcion " +
                     "FROM productos p " +
                     "LEFT JOIN categorias c ON p.id_categoria = c.id_categoria";
    
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
    
            while (rs.next()) {
                Categoria categoria = new Categoria(
                    rs.getInt("id_categoria"),
                    rs.getString("categoria_nombre"),
                    rs.getString("categoria_descripcion")
                );
    
                productos.add(new Producto(
                    rs.getInt("id_producto"),
                    rs.getInt("id_categoria"),
                    categoria,
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getTimestamp("creado").toLocalDateTime(),
                    rs.getString("imagen_url"),
                    rs.getBoolean("estado"),
                    rs.getDate("fecha_venc").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener productos: " + e.getMessage());
        }
    
        return productos;
    }
    
    
    /**
     * 🔹 Agrega un producto a la base de datos.
     */
    public boolean agregarProducto(Producto producto) {
        String sql = "INSERT INTO productos (id_categoria, codigo, nombre, descripcion, creado, imagen_url, estado, fecha_venc) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
    
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, producto.getIdCategoria());
                stmt.setString(2, producto.getCodigo());
                stmt.setString(3, producto.getNombre());
                stmt.setString(4, producto.getDescripcion());
                stmt.setTimestamp(5, Timestamp.valueOf(producto.getCreado()));
                stmt.setString(6, producto.getImagenUrl());
                stmt.setBoolean(7, producto.isEstado());
                stmt.setDate(8, Date.valueOf(producto.getFechaVenc()));
    
                int filasAfectadas = stmt.executeUpdate();
                if (filasAfectadas > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
    
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 🔹 Modifica un producto existente en la base de datos y actualiza la fecha de modificación.
     */
    public boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE productos SET id_categoria = ?, codigo = ?, nombre = ?, descripcion = ?, imagen_url = ?, estado = ?, fecha_venc = ? WHERE id_producto = ?";
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
    
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, producto.getIdCategoria());
                stmt.setString(2, producto.getCodigo());
                stmt.setString(3, producto.getNombre());
                stmt.setString(4, producto.getDescripcion());
                stmt.setString(5, producto.getImagenUrl());
                stmt.setBoolean(6, producto.isEstado());
                stmt.setDate(7, Date.valueOf(producto.getFechaVenc()));
                stmt.setInt(8, producto.getId());
    
                int filasAfectadas = stmt.executeUpdate();
                if (filasAfectadas > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
    
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 🔹 Elimina un producto por su ID de forma segura con transacciones.
     */
    public boolean eliminarProducto(int idProducto) {
        System.out.println("🔍 Intentando eliminar producto con ID: " + idProducto);

        if (idProducto <= 0) {
            System.err.println("⚠ ID de producto inválido.");
            return false;
        }

        // ✅ Verificar si el producto existe antes de intentar eliminarlo
        String sqlVerificarExistencia = "SELECT COUNT(*) FROM productos WHERE id_producto = ?";
        String sqlEliminarProducto = "DELETE FROM productos WHERE id_producto = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // ✅ Iniciar transacción

            // ✅ Verificar si el producto existe antes de eliminarlo
            try (PreparedStatement stmtVerificar = conn.prepareStatement(sqlVerificarExistencia)) {
                stmtVerificar.setInt(1, idProducto);
                ResultSet rs = stmtVerificar.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    System.err.println("⚠ No se encontró un producto con ID: " + idProducto);
                    conn.rollback();
                    return false;
                }
            }

            // ✅ Si el producto existe, proceder con la eliminación
            try (PreparedStatement stmtEliminar = conn.prepareStatement(sqlEliminarProducto)) {
                stmtEliminar.setInt(1, idProducto);
                int filasAfectadas = stmtEliminar.executeUpdate();

                if (filasAfectadas > 0) {
                    conn.commit(); // ✅ Confirmar eliminación
                    System.out.println("✅ Producto eliminado con éxito.");
                    return true;
                } else {
                    System.err.println("⚠ No se encontró el producto con ID: " + idProducto);
                    conn.rollback(); // ❌ Revertir si no se encontró el producto
                    return false;
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 🔹 Verifica si un producto ya existe en la base de datos por su código, sin importar mayúsculas o minúsculas.
     */
    public boolean existeProducto(String codigo) {
        String sql = "SELECT 1 FROM productos WHERE LOWER(codigo) = LOWER(?) LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // ✅ Si hay resultado, el producto existe

        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia del producto: " + e.getMessage());
            e.printStackTrace(); // ✅ Muestra más detalles para depuración
            return false;
        }
    }


    /**
     * 🔹 Obtiene un producto específico por su código.
     */
    public Producto obtenerProductoPorCodigo(String codigo) {
        String sql = "SELECT * FROM productos WHERE TRIM(LOWER(codigo)) = TRIM(LOWER(?)) LIMIT 1";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, codigo.trim());
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                CategoriaDAO categoriaDAO = new CategoriaDAO();
                Categoria categoria = categoriaDAO.obtenerCategoriaPorId(rs.getInt("id_categoria"));
    
                return new Producto(
                    rs.getInt("id_producto"),
                    rs.getInt("id_categoria"),
                    categoria,
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getTimestamp("creado").toLocalDateTime(),
                    rs.getString("imagen_url"),
                    rs.getBoolean("estado"),
                    rs.getDate("fecha_venc").toLocalDate()
                );
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    

    public List<Producto> buscarProductos(String criterio) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, c.id_categoria, c.nombre AS categoria_nombre, c.descripcion AS categoria_descripcion " +
                     "FROM productos p " +
                     "LEFT JOIN categorias c ON p.id_categoria = c.id_categoria " +
                     "WHERE LOWER(p.nombre) LIKE LOWER(?) OR LOWER(p.codigo) LIKE LOWER(?)";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
    
            ps.setString(1, "%" + criterio + "%");
            ps.setString(2, "%" + criterio + "%");
    
            ResultSet rs = ps.executeQuery();
    
            while (rs.next()) {
                Categoria categoria = new Categoria(
                    rs.getInt("id_categoria"),
                    rs.getString("categoria_nombre"),
                    rs.getString("categoria_descripcion")
                );
    
                Producto producto = new Producto(
                    rs.getInt("id_producto"),
                    rs.getInt("id_categoria"),
                    categoria,
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getTimestamp("creado") != null ? rs.getTimestamp("creado").toLocalDateTime() : LocalDateTime.now(),
                    rs.getString("imagen_url") != null ? rs.getString("imagen_url") : "default.png",
                    rs.getBoolean("estado"),
                    rs.getDate("fecha_venc") != null ? rs.getDate("fecha_venc").toLocalDate() : null
                );
    
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }
    
    
    

    public boolean reintegrarStockPEPS(Connection conn, int idProducto, int cantidad, BigDecimal costo) {
        String sqlReintegro = "UPDATE productos SET stock = stock + ? WHERE id_producto = ? AND costo = ?";
    
        try (PreparedStatement stmt = conn.prepareStatement(sqlReintegro)) {
            stmt.setInt(1, cantidad);
            stmt.setInt(2, idProducto);
            stmt.setBigDecimal(3, costo);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error al reintegrar stock PEPS: " + e.getMessage());
            return false;
        }
    }
    
/**
 * 🔹 Obtiene un producto por su ID desde la base de datos.
 */
public Producto obtenerProductoPorId(int idProducto) {
    String sql = "SELECT p.*, c.id_categoria, c.nombre AS categoria_nombre, c.descripcion AS categoria_descripcion " +
                "FROM productos p " +
                "LEFT JOIN categorias c ON p.id_categoria = c.id_categoria " +
                "WHERE p.id_producto = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, idProducto);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Categoria categoria = new Categoria(
                rs.getInt("id_categoria"),
                rs.getString("categoria_nombre"),
                rs.getString("categoria_descripcion")
            );

            return new Producto(
                rs.getInt("id_producto"),
                rs.getInt("id_categoria"),
                categoria,
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getTimestamp("creado").toLocalDateTime(),
                rs.getString("imagen_url"),
                rs.getBoolean("estado"),
                rs.getDate("fecha_venc").toLocalDate()
            );
        }

    } catch (SQLException e) {
        System.err.println("❌ Error al obtener producto por ID: " + e.getMessage());
    }

    return null;
}

    public BigDecimal obtenerCostoUnitarioPorIdDetalleCompra(int idDetalleCompra) {
        String sql = "SELECT preciounitario FROM detalle_compras WHERE id_detallecomp = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDetalleCompra);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("preciounitario");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener costo unitario: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }


    
}
