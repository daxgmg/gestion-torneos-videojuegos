package com.torneos.persistencia;

import com.torneos.dominio.Torneo;
import com.torneos.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla <b>torneos</b>.
 * Proporciona operaciones CRUD usando {@link PreparedStatement}.
 */
public class TorneoDAO {

    // =========================================================================
    // INSERTAR
    // =========================================================================

    /**
     * Inserta un nuevo torneo en la base de datos.
     *
     * @param torneo objeto {@link Torneo} a persistir.
     * @return {@code true} si la inserción fue exitosa.
     */
    public boolean insertar(Torneo torneo) {
        String sql =
            "INSERT INTO dbo.torneos (nombre, fecha_inicio, fecha_fin, estado) " +
            "VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, torneo.getNombre());
            ps.setString(2, torneo.getFechaInicio());
            ps.setString(3, torneo.getFechaFin());
            ps.setString(4, torneo.getEstado());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        torneo.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[TorneoDAO.insertar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // OBTENER TODOS
    // =========================================================================

    /**
     * Recupera todos los torneos de la base de datos.
     *
     * @return lista de {@link Torneo}; vacía si no hay registros o hay error.
     */
    public List<Torneo> obtenerTodos() {
        List<Torneo> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, fecha_inicio, fecha_fin, estado FROM dbo.torneos";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[TorneoDAO.obtenerTodos] Error: " + e.getMessage());
        }
        return lista;
    }

    // =========================================================================
    // BUSCAR POR ID
    // =========================================================================

    /**
     * Busca un torneo por su identificador.
     *
     * @param id identificador del torneo.
     * @return el {@link Torneo} encontrado, o {@code null} si no existe.
     */
    public Torneo buscarPorId(int id) {
        String sql =
            "SELECT id, nombre, fecha_inicio, fecha_fin, estado " +
            "FROM dbo.torneos WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[TorneoDAO.buscarPorId] Error: " + e.getMessage());
        }
        return null;
    }

    // =========================================================================
    // ACTUALIZAR
    // =========================================================================

    /**
     * Actualiza los datos de un torneo existente.
     *
     * @param torneo objeto {@link Torneo} con los nuevos valores.
     * @return {@code true} si se actualizó al menos un registro.
     */
    public boolean actualizar(Torneo torneo) {
        String sql =
            "UPDATE dbo.torneos " +
            "SET nombre = ?, fecha_inicio = ?, fecha_fin = ?, estado = ? " +
            "WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, torneo.getNombre());
            ps.setString(2, torneo.getFechaInicio());
            ps.setString(3, torneo.getFechaFin());
            ps.setString(4, torneo.getEstado());
            ps.setInt(5, torneo.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[TorneoDAO.actualizar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // ELIMINAR
    // =========================================================================

    /**
     * Elimina un torneo por su identificador.
     *
     * @param id identificador del torneo a eliminar.
     * @return {@code true} si se eliminó el registro.
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM dbo.torneos WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[TorneoDAO.eliminar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // HELPER PRIVADO
    // =========================================================================

    /** Convierte la fila actual del {@link ResultSet} en un objeto {@link Torneo}. */
    private Torneo mapear(ResultSet rs) throws SQLException {
        Torneo torneo = new Torneo();
        torneo.setId(rs.getInt("id"));
        torneo.setNombre(rs.getString("nombre"));
        torneo.setFechaInicio(rs.getString("fecha_inicio"));
        torneo.setFechaFin(rs.getString("fecha_fin"));
        torneo.setEstado(rs.getString("estado"));
        return torneo;
    }
}
