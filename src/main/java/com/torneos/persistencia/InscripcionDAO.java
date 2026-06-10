package com.torneos.persistencia;

import com.torneos.dominio.Equipo;
import com.torneos.dominio.Inscripcion;
import com.torneos.dominio.Torneo;
import com.torneos.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla <b>inscripciones</b>.
 * Las consultas incluyen JOINs con {@code torneos} y {@code equipos}
 * para construir objetos completos.
 */
public class InscripcionDAO {

    // =========================================================================
    // INSERTAR
    // =========================================================================

    /**
     * Inserta una nueva inscripción en la base de datos.
     *
     * @param inscripcion objeto {@link Inscripcion} a persistir.
     * @return {@code true} si la inserción fue exitosa.
     */
    public boolean insertar(Inscripcion inscripcion) {
        String sql = "INSERT INTO dbo.inscripciones (id_torneo, id_equipo) VALUES (?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, inscripcion.getTorneo().getId());
            ps.setInt(2, inscripcion.getEquipo().getId());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        inscripcion.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[InscripcionDAO.insertar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // OBTENER TODOS
    // =========================================================================

    /**
     * Recupera todas las inscripciones con los datos de torneo y equipo.
     *
     * @return lista de {@link Inscripcion}; vacía si no hay registros o hay error.
     */
    public List<Inscripcion> obtenerTodos() {
        List<Inscripcion> lista = new ArrayList<>();
        String sql =
            "SELECT i.id, " +
            "       t.id AS t_id, t.nombre AS t_nombre, t.fecha_inicio, t.fecha_fin, t.estado, " +
            "       e.id AS e_id, e.nombre AS e_nombre " +
            "FROM dbo.inscripciones i " +
            "INNER JOIN dbo.torneos t ON i.id_torneo = t.id " +
            "INNER JOIN dbo.equipos e ON i.id_equipo = e.id";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[InscripcionDAO.obtenerTodos] Error: " + e.getMessage());
        }
        return lista;
    }

    // =========================================================================
    // BUSCAR POR ID
    // =========================================================================

    /**
     * Busca una inscripción por su identificador.
     *
     * @param id identificador de la inscripción.
     * @return el objeto {@link Inscripcion} completo, o {@code null} si no existe.
     */
    public Inscripcion buscarPorId(int id) {
        String sql =
            "SELECT i.id, " +
            "       t.id AS t_id, t.nombre AS t_nombre, t.fecha_inicio, t.fecha_fin, t.estado, " +
            "       e.id AS e_id, e.nombre AS e_nombre " +
            "FROM dbo.inscripciones i " +
            "INNER JOIN dbo.torneos t ON i.id_torneo = t.id " +
            "INNER JOIN dbo.equipos e ON i.id_equipo = e.id " +
            "WHERE i.id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[InscripcionDAO.buscarPorId] Error: " + e.getMessage());
        }
        return null;
    }

    // =========================================================================
    // BUSCAR POR TORNEO
    // =========================================================================

    /**
     * Recupera todas las inscripciones de un torneo específico.
     *
     * @param idTorneo identificador del torneo.
     * @return lista de {@link Inscripcion} del torneo indicado.
     */
    public List<Inscripcion> buscarPorTorneo(int idTorneo) {
        List<Inscripcion> lista = new ArrayList<>();
        String sql =
            "SELECT i.id, " +
            "       t.id AS t_id, t.nombre AS t_nombre, t.fecha_inicio, t.fecha_fin, t.estado, " +
            "       e.id AS e_id, e.nombre AS e_nombre " +
            "FROM dbo.inscripciones i " +
            "INNER JOIN dbo.torneos t ON i.id_torneo = t.id " +
            "INNER JOIN dbo.equipos e ON i.id_equipo = e.id " +
            "WHERE i.id_torneo = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTorneo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[InscripcionDAO.buscarPorTorneo] Error: " + e.getMessage());
        }
        return lista;
    }

    // =========================================================================
    // ACTUALIZAR
    // =========================================================================

    /**
     * Actualiza los datos de una inscripción existente.
     *
     * @param inscripcion objeto {@link Inscripcion} con los nuevos valores.
     * @return {@code true} si se actualizó al menos un registro.
     */
    public boolean actualizar(Inscripcion inscripcion) {
        String sql =
            "UPDATE dbo.inscripciones SET id_torneo = ?, id_equipo = ? WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, inscripcion.getTorneo().getId());
            ps.setInt(2, inscripcion.getEquipo().getId());
            ps.setInt(3, inscripcion.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[InscripcionDAO.actualizar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // ELIMINAR
    // =========================================================================

    /**
     * Elimina una inscripción por su identificador.
     *
     * @param id identificador de la inscripción a eliminar.
     * @return {@code true} si se eliminó el registro.
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM dbo.inscripciones WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[InscripcionDAO.eliminar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // HELPER PRIVADO
    // =========================================================================

    /** Convierte la fila actual del {@link ResultSet} en un objeto {@link Inscripcion}. */
    private Inscripcion mapear(ResultSet rs) throws SQLException {
        Torneo torneo = new Torneo();
        torneo.setId(rs.getInt("t_id"));
        torneo.setNombre(rs.getString("t_nombre"));
        torneo.setFechaInicio(rs.getString("fecha_inicio"));
        torneo.setFechaFin(rs.getString("fecha_fin"));
        torneo.setEstado(rs.getString("estado"));

        Equipo equipo = new Equipo();
        equipo.setId(rs.getInt("e_id"));
        equipo.setNombre(rs.getString("e_nombre"));

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setId(rs.getInt("id"));
        inscripcion.setTorneo(torneo);
        inscripcion.setEquipo(equipo);
        return inscripcion;
    }
}
