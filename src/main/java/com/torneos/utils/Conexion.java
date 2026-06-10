package com.torneos.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para gestionar la conexión JDBC a SQL Server.
 *
 * <p>
 * Implementa el patrón <b>Singleton</b> para reutilizar la misma
 * instancia de {@link Connection} durante toda la sesión.
 * </p>
 *
 * <p>
 * Usa <strong>Windows Authentication</strong> (Integrated Security).
 * No requiere usuario ni contraseña explícitos; la identidad del
 * proceso actual de Windows se usa para autenticarse en SQL Server.
 * </p>
 */
public class Conexion {

    // -------------------------------------------------------------------------
    // Configuración de la base de datos
    // -------------------------------------------------------------------------

    private static final String URL =
            "jdbc:sqlserver://localhost\\SQLEXPRESS08;" +
            "databaseName=torneos_db;" +
            "encrypt=true;" +
            "trustServerCertificate=true";

    private static final String USUARIO  = "sa";
    private static final String PASSWORD = "Admin123!";

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    private static Connection instancia;

    /** Constructor privado: impide instanciación externa. */
    private Conexion() {
    }

    /**
     * Devuelve la conexión activa. Si no existe o está cerrada,
     * crea una nueva.
     *
     * @return {@link Connection} lista para ejecutar sentencias SQL.
     * @throws SQLException si el driver no está disponible o los
     *                      datos de conexión son incorrectos.
     */
    public static Connection getConexion() throws SQLException {
        if (instancia == null || instancia.isClosed()) {
            instancia = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("[Conexion] Conexión establecida con torneos_db.");
        }
        return instancia;
    }

    /**
     * Cierra la conexión activa si existe y está abierta.
     */
    public static void cerrarConexion() {
        if (instancia != null) {
            try {
                if (!instancia.isClosed()) {
                    instancia.close();
                    System.out.println("[Conexion] Conexión cerrada.");
                }
            } catch (SQLException e) {
                System.err.println("[Conexion] Error al cerrar la conexión: " + e.getMessage());
            } finally {
                instancia = null;
            }
        }
    }
}
