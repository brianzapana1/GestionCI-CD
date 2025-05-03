package com.inventario.services;

import java.util.List;

import com.inventario.dao.DetalleCompraDAO;
import com.inventario.models.DetalleCompra;

public class DetalleCompraService {

    private final DetalleCompraDAO dao;

    public DetalleCompraService() {
        this.dao = new DetalleCompraDAO();
    }

    public boolean guardarDetalles(List<DetalleCompra> detalles, int idCompra) {
        boolean exito = dao.guardarDetallesCompra(detalles, idCompra);

        if (exito) {
            InventarioService inventarioService = new InventarioService();
            for (DetalleCompra detalle : detalles) {
                // ✅ Asegúrate de que el DAO haya devuelto el idDetalleCompra en cada objeto
                inventarioService.agregarInventario(
                    detalle.getIdProducto(),
                    detalle.getIdDetalleCompra(),
                    detalle.getCantidad()
                );
            }
        }

        return exito;
    }
}
