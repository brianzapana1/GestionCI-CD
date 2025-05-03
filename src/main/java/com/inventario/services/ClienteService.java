package com.inventario.services;

import java.util.List;

import com.inventario.dao.ClienteDAO;
import com.inventario.models.Cliente;

import javafx.application.Platform;

public class ClienteService {
    private final ClienteDAO clienteDAO;
    private static ClienteServiceListener listener;

    public static void setClienteServiceListener(ClienteServiceListener newListener) {
        listener = newListener;
    }

    public interface ClienteServiceListener {
        void onClientesActualizados();
    }

    public ClienteService() {
        this.clienteDAO = new ClienteDAO();

        // Registrar el listener para detectar cambios en la base de datos
        ClienteDAO.setClienteDAOListener(() -> Platform.runLater(() -> {
            System.out.println("🔄 Se detectaron cambios en la tabla de clientes, actualizando...");
            notificarCambioClientes();
        }));
    }

    /**
     * 🔹 Notifica a los controladores cuando hay un cambio en la base de datos.
     */
    private void notificarCambioClientes() {
        if (listener != null) {
            listener.onClientesActualizados();
        }
    }

    /**
     * 🔹 Obtiene un cliente por su ID.
     */
    public Cliente obtenerClientePorId(int idCliente) {
        if (idCliente <= 0) {
            System.err.println("⚠ ID de cliente inválido.");
            return null;
        }
        return clienteDAO.obtenerClientePorId(idCliente);
    }

    /**
     * 🔹 Obtiene un cliente por su NIT.
     */
    public Cliente obtenerClientePorNIT(String nit) {
        if (nit == null || nit.trim().isEmpty()) {
            System.err.println("⚠ NIT inválido. No se puede buscar un cliente.");
            return null;
        }
        return clienteDAO.obtenerClientePorNIT(nit.trim());
    }

    /**
     * 🔹 Registra un nuevo cliente.
     */
    public boolean registrarCliente(Cliente cliente) {
        if (cliente == null) {
            System.err.println("⚠ Error: El cliente no puede ser nulo.");
            return false;
        }
        if (cliente.getRazonSocial() == null || cliente.getRazonSocial().trim().isEmpty()) {
            System.err.println("⚠ Error: La razon social del cliente es obligatoria.");
            return false;
        }
        if (cliente.getNit() == null || cliente.getNit().trim().isEmpty()) {
            System.err.println("⚠ Error: El NIT del cliente es obligatorio.");
            return false;
        }

        return clienteDAO.registrarCliente(cliente);
    }

    /**
     * 🔹 Actualiza un cliente existente.
     */
    public boolean actualizarCliente(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() <= 0) {
            System.err.println("⚠ Error: Cliente inválido para actualizar.");
            return false;
        }
        return clienteDAO.actualizarCliente(cliente);
    }

    /**
     * 🔹 Elimina un cliente por ID.
     */
    public boolean eliminarCliente(int idCliente) {
        if (idCliente <= 0) {
            System.err.println("⚠ Error: ID de cliente inválido para eliminar.");
            return false;
        }
        return clienteDAO.eliminarCliente(idCliente);
    }

    /**
     * 🔹 Lista todos los clientes.
     */
    public List<Cliente> listarClientes() {
        return clienteDAO.listarClientes();
    }

    /**
     * 🔹 Busca clientes por nombre, NIT o empresa.
     */
    public List<Cliente> buscarClientes(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return listarClientes(); // Si el criterio está vacío, devuelve todos
        }
        return clienteDAO.buscarClientes(criterio.trim());
    }

    /**
     * 🔹 Busca clientes por Razón Social.
     */
    public List<Cliente> buscarClientesPorRazonSocial(String razonSocial) {
        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            return listarClientes(); // Si el campo está vacío, devuelve todos
        }
        return clienteDAO.buscarClientesPorRazonSocial(razonSocial.trim());
    }
}
