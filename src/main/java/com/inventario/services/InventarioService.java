package com.inventario.services;

import java.util.List;

import com.inventario.dao.InventarioDAO;
import com.inventario.models.Inventario;
import com.inventario.models.InventarioResumen;

public class InventarioService {

    private final InventarioDAO dao;

    public InventarioService() {
        this.dao = new InventarioDAO();
    }

    public void descontarStockPEPS(int idProducto, int cantidadNecesaria) {
        List<Inventario> inventarioList = dao.obtenerInventarioDisponiblePorProductoPEPS(idProducto);

        for (Inventario inv : inventarioList) {
            if (cantidadNecesaria <= 0) break;

            int stockActual = inv.getCantidadDisponible();

            if (stockActual <= cantidadNecesaria) {
                dao.actualizarCantidadDisponible(inv.getIdInventario(), 0);
                cantidadNecesaria -= stockActual;
            } else {
                dao.actualizarCantidadDisponible(inv.getIdInventario(), stockActual - cantidadNecesaria);
                cantidadNecesaria = 0;
            }
        }

        if (cantidadNecesaria > 0) {
            System.err.println("⚠ No hay suficiente stock para completar la venta del producto ID: " + idProducto);
        }
    }

    public boolean agregarInventario(int idProducto, int idDetalleCompra, int cantidad) {
        return dao.agregarInventario(idProducto, idDetalleCompra, cantidad);
    }
    public List<InventarioResumen> obtenerResumenInventario() {
    return dao.obtenerResumenInventario();
}

public int obtenerStockTotalPorProducto(int idProducto) {
    InventarioDAO dao = new InventarioDAO();
    return dao.obtenerStockTotalPorProducto(idProducto);
}


}
