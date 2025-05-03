package com.inventario.services;

import java.util.List;

import com.inventario.dao.ReporteVentaDAO;
import com.inventario.models.ReporteVenta;

public class ReporteVentaService {

    private final ReporteVentaDAO reporteVentaDAO;

    public ReporteVentaService() {
        this.reporteVentaDAO = new ReporteVentaDAO();
    }

    public List<ReporteVenta> obtenerReporteVentas() {
        return reporteVentaDAO.obtenerReporteVentas();
    }
}
