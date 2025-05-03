package com.inventario.services;

import java.util.List;

import com.inventario.dao.CompraDAO;
import com.inventario.models.Compra;

public class CompraService {
    private final CompraDAO compraDAO;

    public CompraService() {
        this.compraDAO = new CompraDAO();
    }

    // 🔁 Cambiado de boolean a int
    public int registrarCompra(Compra compra) {
        return compraDAO.registrarCompra(compra); // debe retornar el id generado
    }

    public List<Compra> obtenerCompras() {
        return compraDAO.listarCompras();
    }

    public boolean eliminarCompra(int idCompra) {
        return compraDAO.eliminarCompra(idCompra);
    }
    
    public int obtenerUltimoNumeroCompra() {
        return compraDAO.obtenerUltimoNumeroCompra();
    }
    
}

