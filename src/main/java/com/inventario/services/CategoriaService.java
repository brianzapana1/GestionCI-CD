package com.inventario.services;

import java.util.List;

import com.inventario.dao.CategoriaDAO;
import com.inventario.models.Categoria;

public class CategoriaService {
    private final CategoriaDAO categoriaDAO;

    public CategoriaService() {
        this.categoriaDAO = new CategoriaDAO();
    }

    /**
     * 🔹 Obtiene todas las categorías disponibles.
     */
    public List<Categoria> obtenerCategorias() {
        return categoriaDAO.obtenerCategorias();
    }

    /**
     * 🔹 Agrega una nueva categoría después de validar los datos.
     */
    public void agregarCategoria(Categoria categoria) throws Exception {
        if (!validarCategoria(categoria)) {
            throw new Exception("Error en la validación de la categoría. Revisa los campos ingresados.");
        }

        if (categoriaDAO.existeCategoria(categoria.getNombre())) {
            throw new Exception("La categoría '" + categoria.getNombre() + "' ya existe.");
        }

        if (!categoriaDAO.agregarCategoria(categoria)) {
            throw new Exception("No se pudo agregar la categoría. Intenta nuevamente.");
        }
    }

    /**
     * 🔹 Actualiza una categoría existente.
     */
    public void actualizarCategoria(Categoria categoria) throws Exception {
        if (categoria.getIdCategoria() <= 0) {
            throw new Exception("ID de categoría inválido.");
        }

        if (!validarCategoria(categoria)) {
            throw new Exception("Error en la validación de la categoría. Revisa los campos ingresados.");
        }

        if (!categoriaDAO.actualizarCategoria(categoria)) {
            throw new Exception("No se pudo actualizar la categoría. Intenta nuevamente.");
        }
    }

    /**
     * 🔹 Elimina una categoría por su ID después de verificar que no está asociada a productos.
     */
    public void eliminarCategoria(int idCategoria) throws Exception {
        if (idCategoria <= 0) {
            throw new Exception("ID de categoría inválido.");
        }

        if (!categoriaDAO.eliminarCategoria(idCategoria)) {
            throw new Exception("No se pudo eliminar la categoría. Puede estar asociada a productos.");
        }
    }

    /**
     * 🔹 Obtiene una categoría específica por su ID.
     */
    public Categoria obtenerCategoriaPorId(int idCategoria) {
        if (idCategoria <= 0) {
            System.out.println("❌ Error: ID de categoría inválido.");
            return null;
        }
        return categoriaDAO.obtenerCategoriaPorId(idCategoria);
    }

    /**
     * 🔹 Verifica si una categoría ya existe por su nombre.
     */
    public boolean existeCategoria(String nombre) {
        return categoriaDAO.existeCategoria(nombre);
    }

    /**
     * 🔹 Valida que los datos de la categoría sean correctos.
     */
    private boolean validarCategoria(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            System.out.println("❌ Error: El nombre de la categoría no puede estar vacío.");
            return false;
        }

        if (categoria.getDescripcion() != null && categoria.getDescripcion().length() > 200) {
            System.out.println("❌ Error: La descripción de la categoría no puede superar los 200 caracteres.");
            return false;
        }

        return true;
    }
}
