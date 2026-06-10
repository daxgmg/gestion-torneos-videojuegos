package com.torneos.persistencia;

import com.torneos.dominio.Equipo;
import com.torneos.dominio.Jugador;
import com.torneos.dominio.Rol;
import com.torneos.dominio.User;
import com.torneos.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla <b>jugadores</b>.
 * Las consultas incluyen JOINs con {@code equipos}, {@code users} y {@code roles}
 * para construir objetos completos.
 */
public class JugadorDAO {

    // =========================================================================
    // INSERTAR
    // =========================================================================

    /**
     * Inserta un nuevo jugador en la base de datos.
     *
     * @param jugador objeto {@link Jugador} a persistir.
     * @return {@code true} si la inserción fue exitosa.
     */
    public boolean insertar(Jugador jugador) {
        String sql =
            "INSERT INTO dbo.jugadores (nombre, alias, id_equipo, id_user) " +
            "VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, jugador.getNombre());
            ps.setString(2, jugador.getAlias());
            ps.setInt(3, jugador.getEquipo().getId());
            ps.setInt(4, jugador.getUser().getId());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        jugador.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[JugadorDAO.insertar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // OBTENER TODOS
    // =========================================================================

    /**
     * Recupera todos los jugadores con sus datos de equipo y usuario.
     *
     * @return lista de {@link Jugador}; vacía si no hay registros o hay error.
     */
    public List<Jugador> obtenerTodos() {
        List<Jugador> lista = new ArrayList<>();
        String sql =
            "SELECT j.id, j.nombre, j.alias, " +
            "       e.id AS eq_id, e.nombre AS eq_nombre, " +
            "       u.id AS u_id, u.nombre AS u_nombre, u.email, u.password, " +
            "       r.id AS rol_id, r.nombre AS rol_nombre " +
            "FROM dbo.jugadores j " +
            "INNER JOIN dbo.equipos e ON j.id_equipo = e.id " +
            "INNER JOIN dbo.users   u ON j.id_user   = u.id " +
            "INNER JOIN dbo.roles   r ON u.id_rol     = r.id";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[JugadorDAO.obtenerTodos] Error: " + e.getMessage());
        }
        return lista;
    }

    // =========================================================================
    // BUSCAR POR ID
    // =========================================================================

    /**
     * Busca un jugador por su identificador.
     *
     * @param id identificador del jugador.
     * @return el {@link Jugador} completo, o {@code null} si no existe.
     */
    public Jugador buscarPorId(int id) {
        String sql =
            "SELECT j.id, j.nombre, j.alias, " +
            "       e.id AS eq_id, e.nombre AS eq_nombre, " +
            "       u.id AS u_id, u.nombre AS u_nombre, u.email, u.password, " +
            "       r.id AS rol_id, r.nombre AS rol_nombre " +
            "FROM dbo.jugadores j " +
            "INNER JOIN dbo.equipos e ON j.id_equipo = e.id " +
            "INNER JOIN dbo.users   u ON j.id_user   = u.id " +
            "INNER JOIN dbo.roles   r ON u.id_rol     = r.id " +
            "WHERE j.id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[JugadorDAO.buscarPorId] Error: " + e.getMessage());
        }
        return null;
    }

    // =========================================================================
    // ACTUALIZAR
    // =========================================================================

    /**
     * Actualiza los datos de un jugador existente.
     *
     * @param jugador objeto {@link Jugador} con los nuevos valores.
     * @return {@code true} si se actualizó al menos un registro.
     */
    public boolean actualizar(Jugador jugador) {
        String sql =
            "UPDATE dbo.jugadores " +
            "SET nombre = ?, alias = ?, id_equipo = ?, id_user = ? " +
            "WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, jugador.getNombre());
            ps.setString(2, jugador.getAlias());
            ps.setInt(3, jugador.getEquipo().getId());
            ps.setInt(4, jugador.getUser().getId());
            ps.setInt(5, jugador.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[JugadorDAO.actualizar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // ELIMINAR
    // =========================================================================

    /**
     * Elimina un jugador por su identificador.
     *
     * @param id identificador del jugador a eliminar.
     * @return {@code true} si se eliminó el registro.
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM dbo.jugadores WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[JugadorDAO.eliminar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // HELPER PRIVADO
    // =========================================================================

    /** Convierte la fila actual del {@link ResultSet} en un objeto {@link Jugador}. */
    private Jugador mapear(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setId(rs.getInt("rol_id"));
        rol.setNombre(rs.getString("rol_nombre"));

        User user = new User();
        user.setId(rs.getInt("u_id"));
        user.setNombre(rs.getString("u_nombre"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRol(rol);

        Equipo equipo = new Equipo();
        equipo.setId(rs.getInt("eq_id"));
        equipo.setNombre(rs.getString("eq_nombre"));

        Jugador jugador = new Jugador();
        jugador.setId(rs.getInt("id"));
        jugador.setNombre(rs.getString("nombre"));
        jugador.setAlias(rs.getString("alias"));
        jugador.setEquipo(equipo);
        jugador.setUser(user);
        return jugador;
    }
}
