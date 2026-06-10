package com.torneos.persistencia;

import com.torneos.dominio.Rol;
import com.torneos.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla <b>roles</b>.
 * Proporciona operaciones CRUD usando {@link PreparedStatement}.
 */
public class RolDAO {

    // =========================================================================
    // INSERTAR
    // =========================================================================

    /**
     * Inserta un nuevo rol en la base de datos.
     *
     * @param rol objeto {@link Rol} a persistir (id se genera automáticamente).
     * @return {@code true} si la inserción fue exitosa.
     */
    public boolean insertar(Rol rol) {
        String sql = "INSERT INTO dbo.roles (nombre) VALUES (?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, rol.getNombre());
            int filas = ps.executeUpdate();

            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        rol.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[RolDAO.insertar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // OBTENER TODOS
    // =========================================================================

    /**
     * Recupera todos los roles de la base de datos.
     *
     * @return lista de {@link Rol}; vacía si no hay registros o hay error.
     */
    public List<Rol> obtenerTodos() {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM dbo.roles";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[RolDAO.obtenerTodos] Error: " + e.getMessage());
        }
        return lista;
    }

    // =========================================================================
    // BUSCAR POR ID
    // =========================================================================

    /**
     * Busca un rol por su identificador.
     *
     * @param id identificador del rol.
     * @return el {@link Rol} encontrado, o {@code null} si no existe.
     */
    public Rol buscarPorId(int id) {
        String sql = "SELECT id, nombre FROM dbo.roles WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[RolDAO.buscarPorId] Error: " + e.getMessage());
        }
        return null;
    }

    // =========================================================================
    // ACTUALIZAR
    // =========================================================================

    /**
     * Actualiza el nombre de un rol existente.
     *
     * @param rol objeto {@link Rol} con el id y el nuevo nombre.
     * @return {@code true} si se actualizó al menos un registro.
     */
    public boolean actualizar(Rol rol) {
        String sql = "UPDATE dbo.roles SET nombre = ? WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, rol.getNombre());
            ps.setInt(2, rol.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[RolDAO.actualizar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // ELIMINAR
    // =========================================================================

    /**
     * Elimina un rol por su identificador.
     *
     * @param id identificador del rol a eliminar.
     * @return {@code true} si se eliminó el registro.
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM dbo.roles WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[RolDAO.eliminar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // HELPER PRIVADO
    // =========================================================================

    /** Convierte la fila actual del {@link ResultSet} en un objeto {@link Rol}. */
    private Rol mapear(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setId(rs.getInt("id"));
        rol.setNombre(rs.getString("nombre"));
        return rol;
    }
}
