package com.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.inventario.models.Cliente;
import com.inventario.utils.DatabaseConnection;

public class ClienteDAO {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    private static ClienteDAOListener listener;

    public static void setClienteDAOListener(ClienteDAOListener newListener) {
        listener = newListener;
}

    public ClienteDAO() {
        escucharCambiosClientes();
    }

    public interface ClienteDAOListener {
        void onClientesActualizados();
    }
    
    /**
     * 🔹 Obtiene un cliente por su ID.
     */
    public Cliente obtenerClientePorId(int idCliente) {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("empresa"),
                    rs.getString("nombre"),
                    rs.getString("razonSocial"),
                    rs.getString("nit"),
                    rs.getInt("telefono"),
                    rs.getString("email"),
                    rs.getString("direccion")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener cliente por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * 🔹 Obtiene un cliente por su NIT.
     */
    public Cliente obtenerClientePorNIT(String nit) {
        String sql = "SELECT * FROM clientes WHERE TRIM(LOWER(nit)) = TRIM(LOWER(?)) LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nit.trim());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("empresa"),
                    rs.getString("nombre"),
                    rs.getString("razonSocial"),
                    rs.getString("nit"),
                    rs.getInt("telefono"),
                    rs.getString("email"),
                    rs.getString("direccion")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar cliente por NIT: " + e.getMessage());
        }
        return null;
    }

    /**
     * 🔹 Registra un nuevo cliente en la base de datos.
     */
    public boolean registrarCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes (empresa, nombre, razonSocial, nit, telefono, email, direccion) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getEmpresa());
            stmt.setString(2, cliente.getNombre());
            stmt.setString(3, cliente.getRazonSocial());
            stmt.setString(4, cliente.getNit());
            stmt.setInt(5, cliente.getTelefono());
            stmt.setString(6, cliente.getEmail());
            stmt.setString(7, cliente.getDireccion());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    cliente.setIdCliente(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al registrar cliente: " + e.getMessage());
        }
        return false;
    }

    /**
     * 🔹 Actualiza un cliente existente en la base de datos.
     */
    public boolean actualizarCliente(Cliente cliente) {
        String sql = "UPDATE clientes SET empresa = ?, nombre = ?, razonSocial = ?, nit = ?, telefono = ?, email = ?, direccion = ? WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getEmpresa());
            stmt.setString(2, cliente.getNombre());
            stmt.setString(3, cliente.getRazonSocial());
            stmt.setString(4, cliente.getNit());
            stmt.setInt(5, cliente.getTelefono());
            stmt.setString(6, cliente.getEmail());
            stmt.setString(7, cliente.getDireccion());
            stmt.setInt(8, cliente.getIdCliente());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar cliente: " + e.getMessage());
        }
        return false;
    }

    /**
     * 🔹 Elimina un cliente por su ID.
     */
    public boolean eliminarCliente(int idCliente) {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar cliente: " + e.getMessage());
        }
        return false;
    }

    /**
     * 🔹 Lista todos los clientes.
     */
    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY nombre ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("empresa"),
                    rs.getString("nombre"),
                    rs.getString("razonSocial"),
                    rs.getString("nit"),
                    rs.getInt("telefono"),
                    rs.getString("email"),
                    rs.getString("direccion")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar clientes: " + e.getMessage());
        }
        return clientes;
    }

    /**
     * 🔹 Busca clientes por nombre, NIT o empresa.
     */
    public List<Cliente> buscarClientes(String criterio) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE LOWER(nombre) LIKE LOWER(?) OR LOWER(nit) LIKE LOWER(?) OR LOWER(empresa) LIKE LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + criterio + "%");
            stmt.setString(2, "%" + criterio + "%");
            stmt.setString(3, "%" + criterio + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                clientes.add(new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("empresa"),
                    rs.getString("nombre"),
                    rs.getString("razonSocial"),
                    rs.getString("nit"),
                    rs.getInt("telefono"),
                    rs.getString("email"),
                    rs.getString("direccion")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar clientes: " + e.getMessage());
        }
        return clientes;
    }

    public List<Cliente> buscarClientesPorRazonSocial(String razonSocial) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE LOWER(razonSocial) LIKE LOWER(?)";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, "%" + razonSocial.trim() + "%");
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                clientes.add(new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("empresa"),
                    rs.getString("nombre"),
                    rs.getString("razonSocial"),
                    rs.getString("nit"),
                    rs.getInt("telefono"),
                    rs.getString("email"),
                    rs.getString("direccion")
                ));
            }
    
            System.out.println("✅ Clientes encontrados por razón social: " + clientes.size());
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar clientes por razón social: " + e.getMessage());
            e.printStackTrace();
        }
        return clientes;
    }

    /**
     * 🔹 Escucha cambios en la base de datos con `LISTEN clientes_cambio`
     */

/**
 * 🔹 Escucha cambios en la base de datos con `LISTEN clientes_cambio`
 */
private void escucharCambiosClientes() {
    executorService.execute(() -> {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("LISTEN clientes_cambio;");
            System.out.println("🔍 Escuchando cambios en clientes...");

            while (!Thread.currentThread().isInterrupted()) {
                try (Statement listenStmt = conn.createStatement();
                     ResultSet rs = listenStmt.executeQuery("SELECT pg_notification_queue_usage();")) {

                    if (rs.next() && rs.getDouble(1) > 0) {
                        // 🔹 Ahora verificamos el contenido de la notificación
                        try (Statement notifyStmt = conn.createStatement();
                             ResultSet notifyRs = notifyStmt.executeQuery("SELECT 1 FROM pg_listener WHERE relname = 'clientes_cambio'")) {

                            if (notifyRs.next()) {
                                System.out.println("🔄 Cambio detectado en clientes.");

                                if (listener != null) {
                                    listener.onClientesActualizados();
                                }

                                // 🔹 Limpia la notificación para evitar duplicados
                                try (Statement cleanupStmt = conn.createStatement()) {
                                    cleanupStmt.execute("UNLISTEN clientes_cambio;");
                                    cleanupStmt.execute("LISTEN clientes_cambio;");
                                }
                            }
                        }
                    }
                }
                Thread.sleep(2000); // ⏳ Reduce la carga de la base de datos
            }
        } catch (SQLException | InterruptedException e) {
            System.err.println("❌ Error al escuchar cambios en clientes: " + e.getMessage());
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    });
}




    
}


