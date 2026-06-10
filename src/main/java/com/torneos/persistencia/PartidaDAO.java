package com.torneos.persistencia;

import com.torneos.dominio.Equipo;
import com.torneos.dominio.Partida;
import com.torneos.dominio.Torneo;
import com.torneos.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla <b>partidas</b>.
 * Las consultas incluyen JOINs con {@code torneos} y {@code equipos}
 * para construir objetos completos.
 */
public class PartidaDAO {

    // =========================================================================
    // INSERTAR
    // =========================================================================

    /**
     * Inserta una nueva partida en la base de datos.
     *
     * @param partida objeto {@link Partida} a persistir.
     * @return {@code true} si la inserción fue exitosa.
     */
    public boolean insertar(Partida partida) {
        String sql =
            "INSERT INTO dbo.partidas (id_torneo, id_equipo1, id_equipo2, resultado, fecha) " +
            "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, partida.getTorneo().getId());
            ps.setInt(2, partida.getEquipo1().getId());
            ps.setInt(3, partida.getEquipo2().getId());
            ps.setString(4, partida.getResultado());
            ps.setString(5, partida.getFecha());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        partida.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[PartidaDAO.insertar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // OBTENER TODOS
    // =========================================================================

    /**
     * Recupera todas las partidas con los datos de torneo y equipos.
     *
     * @return lista de {@link Partida}; vacía si no hay registros o hay error.
     */
    public List<Partida> obtenerTodos() {
        List<Partida> lista = new ArrayList<>();
        String sql =
            "SELECT p.id, p.resultado, p.fecha, " +
            "       t.id AS t_id, t.nombre AS t_nombre, t.fecha_inicio, t.fecha_fin, t.estado, " +
            "       e1.id AS e1_id, e1.nombre AS e1_nombre, " +
            "       e2.id AS e2_id, e2.nombre AS e2_nombre " +
            "FROM dbo.partidas p " +
            "INNER JOIN dbo.torneos t  ON p.id_torneo  = t.id " +
            "INNER JOIN dbo.equipos e1 ON p.id_equipo1 = e1.id " +
            "INNER JOIN dbo.equipos e2 ON p.id_equipo2 = e2.id";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[PartidaDAO.obtenerTodos] Error: " + e.getMessage());
        }
        return lista;
    }

    // =========================================================================
    // BUSCAR POR ID
    // =========================================================================

    /**
     * Busca una partida por su identificador.
     *
     * @param id identificador de la partida.
     * @return el objeto {@link Partida} completo, o {@code null} si no existe.
     */
    public Partida buscarPorId(int id) {
        String sql =
            "SELECT p.id, p.resultado, p.fecha, " +
            "       t.id AS t_id, t.nombre AS t_nombre, t.fecha_inicio, t.fecha_fin, t.estado, " +
            "       e1.id AS e1_id, e1.nombre AS e1_nombre, " +
            "       e2.id AS e2_id, e2.nombre AS e2_nombre " +
            "FROM dbo.partidas p " +
            "INNER JOIN dbo.torneos t  ON p.id_torneo  = t.id " +
            "INNER JOIN dbo.equipos e1 ON p.id_equipo1 = e1.id " +
            "INNER JOIN dbo.equipos e2 ON p.id_equipo2 = e2.id " +
            "WHERE p.id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[PartidaDAO.buscarPorId] Error: " + e.getMessage());
        }
        return null;
    }

    // =========================================================================
    // ACTUALIZAR
    // =========================================================================

    /**
     * Actualiza los datos de una partida existente.
     *
     * @param partida objeto {@link Partida} con los nuevos valores.
     * @return {@code true} si se actualizó al menos un registro.
     */
    public boolean actualizar(Partida partida) {
        String sql =
            "UPDATE dbo.partidas " +
            "SET id_torneo = ?, id_equipo1 = ?, id_equipo2 = ?, resultado = ?, fecha = ? " +
            "WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, partida.getTorneo().getId());
            ps.setInt(2, partida.getEquipo1().getId());
            ps.setInt(3, partida.getEquipo2().getId());
            ps.setString(4, partida.getResultado());
            ps.setString(5, partida.getFecha());
            ps.setInt(6, partida.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[PartidaDAO.actualizar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // ELIMINAR
    // =========================================================================

    /**
     * Elimina una partida por su identificador.
     *
     * @param id identificador de la partida a eliminar.
     * @return {@code true} si se eliminó el registro.
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM dbo.partidas WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[PartidaDAO.eliminar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // HELPER PRIVADO
    // =========================================================================

    /** Convierte la fila actual del {@link ResultSet} en un objeto {@link Partida}. */
    private Partida mapear(ResultSet rs) throws SQLException {
        Torneo torneo = new Torneo();
        torneo.setId(rs.getInt("t_id"));
        torneo.setNombre(rs.getString("t_nombre"));
        torneo.setFechaInicio(rs.getString("fecha_inicio"));
        torneo.setFechaFin(rs.getString("fecha_fin"));
        torneo.setEstado(rs.getString("estado"));

        Equipo equipo1 = new Equipo();
        equipo1.setId(rs.getInt("e1_id"));
        equipo1.setNombre(rs.getString("e1_nombre"));

        Equipo equipo2 = new Equipo();
        equipo2.setId(rs.getInt("e2_id"));
        equipo2.setNombre(rs.getString("e2_nombre"));

        Partida partida = new Partida();
        partida.setId(rs.getInt("id"));
        partida.setTorneo(torneo);
        partida.setEquipo1(equipo1);
        partida.setEquipo2(equipo2);
        partida.setResultado(rs.getString("resultado"));
        partida.setFecha(rs.getString("fecha"));
        return partida;
    }
}
