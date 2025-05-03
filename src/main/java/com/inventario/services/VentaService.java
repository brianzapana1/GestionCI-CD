package com.inventario.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.inventario.dao.VentaDAO;
import com.inventario.models.DetalleVenta;
import com.inventario.models.Venta;

public class VentaService {
    private final VentaDAO ventaDAO;

    public VentaService() {
        this.ventaDAO = new VentaDAO();
    }

    /**
     * 🔹 Registra una venta validando todos los datos.
     */
    public int registrarVenta(Venta venta) throws Exception {
        if (!validarVenta(venta)) {
            throw new Exception("⚠ Error: Datos de la venta no son válidos.");
        }

        if (venta.getDescuento() == null) {
            venta.setDescuento(BigDecimal.ZERO);
        }

        if (venta.getIva() == null) {
            venta.setIva(venta.getSubTotal().multiply(BigDecimal.valueOf(0.13)));
        }

        BigDecimal totalConDescuento = venta.getSubTotal().subtract(venta.getDescuento());
        venta.setTotal(totalConDescuento.add(venta.getIva()));

        System.out.println("📌 Registrando venta...");
        System.out.println("   - ID Usuario: " + venta.getIdUsuario());
        System.out.println("   - ID Cliente: " + venta.getIdCliente());
        System.out.println("   - ID Tipo Comprobante: " + venta.getIdTipoComprobante());
        System.out.println("   - Número Venta: " + venta.getNumeroVenta());
        System.out.println("   - Subtotal: " + venta.getSubTotal());
        System.out.println("   - Descuento: " + venta.getDescuento());
        System.out.println("   - IVA: " + venta.getIva());
        System.out.println("   - Total: " + venta.getTotal());
        System.out.println("   - Productos en la venta: " + venta.getDetalles().size());

        int idVenta = ventaDAO.registrarVenta(venta);

        // ✅ Generar recibo automáticamente después de registrar la venta
        if (idVenta > 0) {
            ventaDAO.crearRecibo(idVenta, "Efectivo"); // Puedes reemplazar "Efectivo" por cualquier tipo de pago
        }

        return idVenta;
    }

    public List<Venta> obtenerVentas() {
        return ventaDAO.obtenerVentas();
    }

    public List<DetalleVenta> obtenerDetallesVenta(int idVenta) {
        return ventaDAO.obtenerDetallesVenta(idVenta);
    }

    public boolean cancelarVenta(int idVenta) throws Exception {
        List<DetalleVenta> detalles = ventaDAO.obtenerDetallesVenta(idVenta);
        if (detalles.isEmpty()) {
            throw new Exception("⚠ No se encontró la venta o ya fue cancelada.");
        }
        return ventaDAO.cancelarVenta(idVenta);
    }

    private boolean validarVenta(Venta venta) {
        boolean esValida = true;
        StringBuilder errores = new StringBuilder();

        if (venta.getIdUsuario() <= 0) {
            errores.append("⚠ La venta debe estar asociada a un usuario.\n");
            esValida = false;
        }

        if (venta.getSubTotal().compareTo(BigDecimal.ZERO) <= 0) {
            errores.append("⚠ El subtotal debe ser mayor a 0.\n");
            esValida = false;
        }

        if (venta.getDetalles().isEmpty()) {
            errores.append("⚠ Debe haber al menos un producto en la venta.\n");
            esValida = false;
        }

        if (venta.getIdTipoComprobante() <= 0) {
            errores.append("⚠ Se debe seleccionar un tipo de comprobante válido.\n");
            esValida = false;
        }

        if (!esValida) {
            System.err.println("❌ Errores en la venta:\n" + errores);
        }

        return esValida;
    }

    public String generarNumeroVenta() {
        String ultimoNumero = ventaDAO.obtenerUltimoNumeroVenta();
        int numero = Integer.parseInt(ultimoNumero.substring(1));
        return String.format("V%05d", numero + 1);
    }

    public Map<String, Object> obtenerDatosRecibo(int idVenta) {
        return ventaDAO.obtenerDatosRecibo(idVenta);
    }

    public List<Map<String, Object>> obtenerProductosVenta(int idVenta) {
        return ventaDAO.obtenerProductosVenta(idVenta);
    }

    public List<Map<String, Object>> obtenerProductosRecibo(int idVenta) {
        return ventaDAO.obtenerProductosVenta(idVenta);
    }

    public Map<String, Object> obtenerDatosFactura(int idVenta) {
        return ventaDAO.obtenerDatosRecibo(idVenta); // ✅ Correcto y coherente
    }
    
    
}
