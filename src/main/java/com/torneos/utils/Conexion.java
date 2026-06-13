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
 * * <p>
 * Modificado para forzar la autenticación por usuario (SQL Server Authentication)
 * y desactivar la seguridad integrada de Windows que generaba conflictos.
 * </p>
 */
public class Conexion {

    // -------------------------------------------------------------------------
    // Configuración de la base de datos
    // -------------------------------------------------------------------------

    private static final String URL =
            "jdbc:sqlserver://localhost;" +
            "databaseName=torneos_db;" +
            "integratedSecurity=true;" +
            "encrypt=true;" +
            "trustServerCertificate=true";

    private static final String USUARIO  = "";
    private static final String PASSWORD = "";

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    private static Connection instancia;

    static {
        try {
            // Mantenemos el bloque por si el proyecto exige la existencia de la DLL,
            // pero al estar integratedSecurity en false, Java no la utilizará para loguearse.
            java.io.File dll = new java.io.File("mssql-jdbc_auth-12.4.2.x64.dll");
            if (!dll.exists()) {
                dll = new java.io.File("gestion-torneos-videojuegos/mssql-jdbc_auth-12.4.2.x64.dll");
            }
            if (dll.exists()) {
                System.load(dll.getAbsolutePath());
                System.out.println("[Conexion] DLL nativa detectada en el sistema.");
            }
        } catch (Throwable t) {
            System.err.println("[Conexion] Advertencia al revisar DLL nativa: " + t.getMessage());
        }
    }

    /** Constructor privado: impide instanciación externa. */
    private Conexion() {
    }

    /**
     * Devuelve la conexión activa. Si no existe o está cerrada,
     * crea una nueva usando las credenciales explícitas de SQL Server.
     *
     * @return {@link Connection} lista para ejecutar sentencias SQL.
     * @throws SQLException si los datos de conexión son incorrectos.
     */
    public static Connection getConexion() throws SQLException {
        if (instancia == null || instancia.isClosed()) {
            if (USUARIO == null || USUARIO.isEmpty()) {
                instancia = DriverManager.getConnection(URL);
            } else {
                instancia = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            }
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