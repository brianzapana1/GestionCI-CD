package com.inventario.services;

import java.util.List;

import com.inventario.dao.TipoComprobanteDAO;
import com.inventario.models.TipoComprobante;

public class TipoComprobanteService {
    private final TipoComprobanteDAO tipoComprobanteDAO;

    public TipoComprobanteService() {
        this.tipoComprobanteDAO = new TipoComprobanteDAO();
    }

    /**
     * 🔹 Registra un nuevo tipo de comprobante.
     */
    public boolean registrarTipoComprobante(TipoComprobante tipoComprobante) {
        if (tipoComprobante == null || tipoComprobante.getDescripcion().trim().isEmpty()) {
            System.err.println("⚠ Error: La descripción del tipo de comprobante es obligatoria.");
            return false;
        }
        return tipoComprobanteDAO.registrarTipoComprobante(tipoComprobante);
    }

    /**
     * 🔹 Obtiene un tipo de comprobante por su ID.
     */
    public TipoComprobante obtenerTipoComprobantePorId(int id) {
        if (id <= 0) {
            System.err.println("⚠ Error: ID inválido.");
            return null;
        }
        return tipoComprobanteDAO.obtenerTipoComprobantePorId(id);
    }

    /**
     * 🔹 Actualiza un tipo de comprobante.
     */
    public boolean actualizarTipoComprobante(TipoComprobante tipoComprobante) {
        if (tipoComprobante == null || tipoComprobante.getIdTipoComp() <= 0) {
            System.err.println("⚠ Error: Tipo de comprobante inválido para actualizar.");
            return false;
        }
        return tipoComprobanteDAO.actualizarTipoComprobante(tipoComprobante);
    }

    /**
     * 🔹 Elimina un tipo de comprobante por ID.
     */
    public boolean eliminarTipoComprobante(int id) {
        if (id <= 0) {
            System.err.println("⚠ Error: ID inválido para eliminar.");
            return false;
        }
        return tipoComprobanteDAO.eliminarTipoComprobante(id);
    }

    /**
     * 🔹 Obtiene una lista de todos los tipos de comprobantes.
     */
    public List<TipoComprobante> listarTiposComprobantes() {
        return tipoComprobanteDAO.listarTiposComprobantes();
    }

    /**
     * 🔹 Busca tipos de comprobantes por descripción.
     */
    public List<TipoComprobante> buscarTipoComprobantePorDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return listarTiposComprobantes();
        }
        return tipoComprobanteDAO.buscarTipoComprobantePorDescripcion(descripcion);
    }

    /**
     * 🔹 Busca tipos de comprobantes por estado (true = activo, false = inactivo).
     */
    public List<TipoComprobante> buscarTipoComprobantePorEstado(boolean estado) {
        return tipoComprobanteDAO.buscarTipoComprobantePorEstado(estado);
    }
    public List<TipoComprobante> obtenerTodos() {
        return listarTiposComprobantes();
    }
    
}
