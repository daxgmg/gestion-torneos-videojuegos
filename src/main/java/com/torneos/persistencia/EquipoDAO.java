package com.torneos.persistencia;

import com.torneos.dominio.Equipo;
import com.torneos.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla <b>equipos</b>.
 * Proporciona operaciones CRUD usando {@link PreparedStatement}.
 */
public class EquipoDAO {

    // =========================================================================
    // INSERTAR
    // =========================================================================

    /**
     * Inserta un nuevo equipo en la base de datos.
     *
     * @param equipo objeto {@link Equipo} a persistir (id se genera automáticamente).
     * @return {@code true} si la inserción fue exitosa.
     */
    public boolean insertar(Equipo equipo) {
        String sql = "INSERT INTO dbo.equipos (nombre) VALUES (?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, equipo.getNombre());
            int filas = ps.executeUpdate();

            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        equipo.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[EquipoDAO.insertar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // OBTENER TODOS
    // =========================================================================

    /**
     * Recupera todos los equipos de la base de datos.
     *
     * @return lista de {@link Equipo}; vacía si no hay registros o hay error.
     */
    public List<Equipo> obtenerTodos() {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM dbo.equipos";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[EquipoDAO.obtenerTodos] Error: " + e.getMessage());
        }
        return lista;
    }

    // =========================================================================
    // BUSCAR POR ID
    // =========================================================================

    /**
     * Busca un equipo por su identificador.
     *
     * @param id identificador del equipo.
     * @return el {@link Equipo} encontrado, o {@code null} si no existe.
     */
    public Equipo buscarPorId(int id) {
        String sql = "SELECT id, nombre FROM dbo.equipos WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[EquipoDAO.buscarPorId] Error: " + e.getMessage());
        }
        return null;
    }

    // =========================================================================
    // ACTUALIZAR
    // =========================================================================

    /**
     * Actualiza el nombre de un equipo existente.
     *
     * @param equipo objeto {@link Equipo} con el id y el nuevo nombre.
     * @return {@code true} si se actualizó al menos un registro.
     */
    public boolean actualizar(Equipo equipo) {
        String sql = "UPDATE dbo.equipos SET nombre = ? WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, equipo.getNombre());
            ps.setInt(2, equipo.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[EquipoDAO.actualizar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // ELIMINAR
    // =========================================================================

    /**
     * Elimina un equipo por su identificador.
     *
     * @param id identificador del equipo a eliminar.
     * @return {@code true} si se eliminó el registro.
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM dbo.equipos WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[EquipoDAO.eliminar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // HELPER PRIVADO
    // =========================================================================

    /** Convierte la fila actual del {@link ResultSet} en un objeto {@link Equipo}. */
    private Equipo mapear(ResultSet rs) throws SQLException {
        Equipo equipo = new Equipo();
        equipo.setId(rs.getInt("id"));
        equipo.setNombre(rs.getString("nombre"));
        return equipo;
    }
}
