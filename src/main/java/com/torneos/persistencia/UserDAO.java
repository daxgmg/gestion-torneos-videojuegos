package com.torneos.persistencia;

import com.torneos.dominio.Rol;
import com.torneos.dominio.User;
import com.torneos.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla <b>users</b>.
 * Incluye operaciones CRUD y el método {@link #autenticar(String, String)}.
 */
public class UserDAO {

    // =========================================================================
    // INSERTAR
    // =========================================================================

    /**
     * Inserta un nuevo usuario en la base de datos.
     *
     * @param user objeto {@link User} a persistir (id se genera automáticamente).
     * @return {@code true} si la inserción fue exitosa.
     */
    public boolean insertar(User user) {
        String sql = "INSERT INTO dbo.users (nombre, email, password, id_rol) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getNombre());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getRol().getId());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.insertar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // OBTENER TODOS
    // =========================================================================

    /**
     * Recupera todos los usuarios junto con su rol.
     *
     * @return lista de {@link User}; vacía si no hay registros o hay error.
     */
    public List<User> obtenerTodos() {
        List<User> lista = new ArrayList<>();
        String sql =
            "SELECT u.id, u.nombre, u.email, u.password, " +
            "       r.id AS rol_id, r.nombre AS rol_nombre " +
            "FROM dbo.users u " +
            "INNER JOIN dbo.roles r ON u.id_rol = r.id";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.obtenerTodos] Error: " + e.getMessage());
        }
        return lista;
    }

    // =========================================================================
    // BUSCAR POR ID
    // =========================================================================

    /**
     * Busca un usuario por su identificador.
     *
     * @param id identificador del usuario.
     * @return el {@link User} con su {@link Rol}, o {@code null} si no existe.
     */
    public User buscarPorId(int id) {
        String sql =
            "SELECT u.id, u.nombre, u.email, u.password, " +
            "       r.id AS rol_id, r.nombre AS rol_nombre " +
            "FROM dbo.users u " +
            "INNER JOIN dbo.roles r ON u.id_rol = r.id " +
            "WHERE u.id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.buscarPorId] Error: " + e.getMessage());
        }
        return null;
    }

    // =========================================================================
    // ACTUALIZAR
    // =========================================================================

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param user objeto {@link User} con los nuevos valores (id debe ser válido).
     * @return {@code true} si se actualizó al menos un registro.
     */
    public boolean actualizar(User user) {
        String sql =
            "UPDATE dbo.users " +
            "SET nombre = ?, email = ?, password = ?, id_rol = ? " +
            "WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getNombre());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getRol().getId());
            ps.setInt(5, user.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UserDAO.actualizar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // ELIMINAR
    // =========================================================================

    /**
     * Elimina un usuario por su identificador.
     *
     * @param id identificador del usuario a eliminar.
     * @return {@code true} si se eliminó el registro.
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM dbo.users WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UserDAO.eliminar] Error: " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // AUTENTICAR
    // =========================================================================

    /**
     * Verifica las credenciales del usuario y retorna el objeto completo con Rol.
     *
     * <p><strong>Nota:</strong> En producción la contraseña debe compararse
     * como hash (bcrypt / SHA-256), nunca en texto plano.</p>
     *
     * @param email    correo electrónico del usuario.
     * @param password contraseña en texto plano.
     * @return el {@link User} autenticado con su {@link Rol}, o {@code null}
     *         si las credenciales son incorrectas.
     */
    public User autenticar(String email, String password) {
        String sql =
            "SELECT u.id, u.nombre, u.email, u.password, " +
            "       r.id AS rol_id, r.nombre AS rol_nombre " +
            "FROM dbo.users u " +
            "INNER JOIN dbo.roles r ON u.id_rol = r.id " +
            "WHERE u.email = ? AND u.password = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.autenticar] Error: " + e.getMessage());
        }
        return null;
    }

    // =========================================================================
    // HELPER PRIVADO
    // =========================================================================

    /** Convierte la fila actual del {@link ResultSet} en un objeto {@link User}. */
    private User mapear(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setId(rs.getInt("rol_id"));
        rol.setNombre(rs.getString("rol_nombre"));

        User user = new User();
        user.setId(rs.getInt("id"));
        user.setNombre(rs.getString("nombre"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRol(rol);
        return user;
    }
}
