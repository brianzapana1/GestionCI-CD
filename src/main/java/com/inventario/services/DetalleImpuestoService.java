package com.inventario.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.inventario.dao.DetalleImpuestoDAO;
import com.inventario.models.DetalleImpuesto;

public class DetalleImpuestoService {

    private final DetalleImpuestoDAO detalleImpuestoDAO = new DetalleImpuestoDAO();

    public void registrarImpuestosVenta(int idVenta, List<DetalleImpuesto> detalles) {
        for (DetalleImpuesto detalle : detalles) {
            int cantidad = detalle.getCantidad();
            BigDecimal precioVenta = detalle.getPrecioVenta();
            BigDecimal precioCompra = detalle.getPrecioCompra();

            // Calcular utilidad = (venta - compra) * cantidad
            BigDecimal utilidad = precioVenta.subtract(precioCompra).multiply(BigDecimal.valueOf(cantidad));
            detalle.setUtilidad(utilidad);

            // Total venta = venta * cantidad
            BigDecimal totalVenta = precioVenta.multiply(BigDecimal.valueOf(cantidad));

            // Impuestos
            BigDecimal impuesto3p = totalVenta.multiply(BigDecimal.valueOf(0.03));
            BigDecimal impuesto13p = utilidad.multiply(BigDecimal.valueOf(0.13));
            BigDecimal totalImpuestos = impuesto3p.add(impuesto13p);
            
            detalle.setFecha(LocalDateTime.now());

            // Redondeo y asignación
            detalle.setTotalVenta(totalVenta.setScale(2, BigDecimal.ROUND_HALF_UP));
            detalle.setImpuesto3p(impuesto3p.setScale(2, BigDecimal.ROUND_HALF_UP));
            detalle.setImpuesto13p(impuesto13p.setScale(2, BigDecimal.ROUND_HALF_UP));
            detalle.setTotalImpuestos(totalImpuestos.setScale(2, BigDecimal.ROUND_HALF_UP));

            // Guardar
            detalle.setIdVenta(idVenta);
            detalleImpuestoDAO.guardarDetalleImpuesto(detalle);
        }
    }
}
