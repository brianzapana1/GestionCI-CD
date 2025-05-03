package com.inventario.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.inventario.dao.ProductosDAO;
import com.inventario.models.Producto;

public class ProductoService {
    private final ProductosDAO productosDAO;

    public ProductoService() {
        this.productosDAO = new ProductosDAO();
    }

    public List<Producto> obtenerProductos() {
        return productosDAO.obtenerProductos();
    }

    public List<Producto> buscarProductos(String criterio) {
        return productosDAO.buscarProductos(criterio);
    }

    public void agregarProducto(Producto producto) throws Exception {
        if (!validarProducto(producto)) {
            throw new Exception("Error en la validación del producto. Revisa los campos ingresados.");
        }

        if (productosDAO.existeProducto(producto.getCodigo())) {
            throw new Exception("El código '" + producto.getCodigo() + "' ya está en uso.");
        }

        if (producto.getCreado() == null) {
            producto.setCreado(LocalDateTime.now());
        }

        if (!productosDAO.agregarProducto(producto)) {
            throw new Exception("No se pudo agregar el producto. Intenta nuevamente.");
        }
    }

    public void actualizarProducto(Producto producto) throws Exception {
        if (!validarProducto(producto)) {
            throw new Exception("Error en la validación del producto. Revisa los campos ingresados.");
        }

        Producto productoExistente = productosDAO.obtenerProductoPorCodigo(producto.getCodigo());
        if (productoExistente != null && productoExistente.getId() != producto.getId()) {
            throw new Exception("El código '" + producto.getCodigo() + "' ya está en uso.");
        }

        producto.setCreado(LocalDateTime.now());

        if (!productosDAO.actualizarProducto(producto)) {
            throw new Exception("No se pudo actualizar el producto. Verifica si el producto existe.");
        }
    }

    public boolean eliminarProducto(int idProducto) {
        if (idProducto <= 0) {
            System.out.println("❌ Error: ID de producto inválido.");
            return false;
        }

        return productosDAO.eliminarProducto(idProducto);
    }

    public Producto obtenerProductoPorCodigo(String codigo) {
        if (codigo.trim().isEmpty()) {
            System.out.println("❌ Error: El código del producto no puede estar vacío.");
            return null;
        }
        return productosDAO.obtenerProductoPorCodigo(codigo);
    }

    public Producto obtenerProductoPorId(int id) {
        return productosDAO.obtenerProductoPorId(id);
    }

    private boolean validarProducto(Producto producto) {
        StringBuilder errores = new StringBuilder();
        boolean esValido = true;

        if (producto.getCodigo().trim().isEmpty()) {
            errores.append("⚠ El código del producto no puede estar vacío.\n");
            esValido = false;
        }

        if (producto.getNombre().trim().isEmpty()) {
            errores.append("⚠ El nombre del producto no puede estar vacío.\n");
            esValido = false;
        }

        if (producto.getIdCategoria() <= 0) {
            errores.append("⚠ Debe seleccionarse una categoría válida para el producto.\n");
            esValido = false;
        }

        if (producto.getDescripcion().length() > 200) {
            errores.append("⚠ La descripción no puede superar los 200 caracteres.\n");
            esValido = false;
        }

        if (!producto.getImagenUrl().trim().isEmpty() && !producto.getImagenUrl().matches("^.+\\.(jpg|jpeg|png|gif)$")) {
            errores.append("⚠ La imagen debe ser un archivo válido (jpg, jpeg, png, gif).\n");
            esValido = false;
        }

        if (!esValido) {
            System.out.println("❌ Errores en validación de producto:\n" + errores.toString());
        }

        return esValido;
    }

    public BigDecimal obtenerCostoUnitarioPorDetalleCompra(int idDetalleCompra) {
        return productosDAO.obtenerCostoUnitarioPorIdDetalleCompra(idDetalleCompra);
    }
}
