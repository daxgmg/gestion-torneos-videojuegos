package com.torneos.persistencia;

import com.torneos.dominio.Rol;
import com.torneos.dominio.User;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para {@link UserDAO}.
 * Requiere que la base de datos torneos_db esté disponible y que
 * exista al menos el rol con id=1 (ADMIN) del script de datos de prueba.
 *
 * Orden de ejecución: insertar → obtenerTodos → buscarPorId → actualizar → eliminar → autenticar
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UserDAO - Pruebas CRUD + autenticar")
class UserDAOTest {

    private static final UserDAO dao = new UserDAO();

    /** ID generado al insertar; se comparte entre los métodos de prueba. */
    private static int idGenerado;

    // Rol de referencia (debe existir en la BD – id=1 ADMIN del script SQL)
    private static final Rol ROL_ADMIN = new Rol(1, "ADMIN");

    // =========================================================================
    // 1. INSERTAR
    // =========================================================================

    @Test
    @Order(1)
    @DisplayName("1 - insertar: debe persistir un usuario y asignar id > 0")
    void testInsertar() {
        User user = new User();
        user.setNombre("Test Usuario");
        user.setEmail("test@torneos.com");
        user.setPassword("test1234");
        user.setRol(ROL_ADMIN);

        boolean resultado = dao.insertar(user);

        assertTrue(resultado, "insertar() debe retornar true");
        assertTrue(user.getId() > 0, "El id generado debe ser mayor que 0");

        idGenerado = user.getId();
        System.out.println("[UserDAOTest] Usuario insertado con id: " + idGenerado);
    }

    // =========================================================================
    // 2. OBTENER TODOS
    // =========================================================================

    @Test
    @Order(2)
    @DisplayName("2 - obtenerTodos: la lista no debe estar vacía")
    void testObtenerTodos() {
        List<User> lista = dao.obtenerTodos();

        assertNotNull(lista, "La lista no debe ser null");
        assertFalse(lista.isEmpty(), "La lista debe contener al menos un usuario");

        System.out.println("[UserDAOTest] Total usuarios encontrados: " + lista.size());
    }

    // =========================================================================
    // 3. BUSCAR POR ID
    // =========================================================================

    @Test
    @Order(3)
    @DisplayName("3 - buscarPorId: debe retornar el usuario insertado con su rol")
    void testBuscarPorId() {
        User user = dao.buscarPorId(idGenerado);

        assertNotNull(user, "El usuario no debe ser null");
        assertEquals(idGenerado, user.getId(), "El id debe coincidir");
        assertEquals("test@torneos.com", user.getEmail(), "El email debe coincidir");
        assertNotNull(user.getRol(), "El rol no debe ser null");
        assertEquals("ADMIN", user.getRol().getNombre(), "El rol debe ser ADMIN");

        System.out.println("[UserDAOTest] Usuario encontrado: " + user);
    }

    // =========================================================================
    // 4. ACTUALIZAR
    // =========================================================================

    @Test
    @Order(4)
    @DisplayName("4 - actualizar: debe modificar el nombre y email del usuario")
    void testActualizar() {
        User user = new User();
        user.setId(idGenerado);
        user.setNombre("Test Actualizado");
        user.setEmail("actualizado@torneos.com");
        user.setPassword("nuevaPass456");
        user.setRol(ROL_ADMIN);

        boolean resultado = dao.actualizar(user);

        assertTrue(resultado, "actualizar() debe retornar true");

        User userActualizado = dao.buscarPorId(idGenerado);
        assertNotNull(userActualizado, "El usuario actualizado no debe ser null");
        assertEquals("Test Actualizado", userActualizado.getNombre(), "El nombre debe haberse actualizado");
        assertEquals("actualizado@torneos.com", userActualizado.getEmail(), "El email debe haberse actualizado");

        System.out.println("[UserDAOTest] Usuario actualizado: " + userActualizado);
    }

    // =========================================================================
    // 5. ELIMINAR
    // =========================================================================

    @Test
    @Order(5)
    @DisplayName("5 - eliminar: debe borrar el usuario y no encontrarlo después")
    void testEliminar() {
        boolean resultado = dao.eliminar(idGenerado);

        assertTrue(resultado, "eliminar() debe retornar true");

        User userEliminado = dao.buscarPorId(idGenerado);
        assertNull(userEliminado, "El usuario eliminado no debe encontrarse");

        System.out.println("[UserDAOTest] Usuario con id " + idGenerado + " eliminado correctamente.");
    }

    // =========================================================================
    // 6. AUTENTICAR
    // =========================================================================

    @Test
    @Order(6)
    @DisplayName("6 - autenticar: debe retornar el admin con credenciales correctas")
    void testAutenticar() throws Exception {
        // Usa el usuario admin del script de datos de prueba
        User user = dao.autenticar("admin@torneos.com", "admin123");

        assertNotNull(user, "autenticar() debe retornar un User con credenciales válidas");
        assertEquals("admin@torneos.com", user.getEmail(), "El email debe coincidir");
        assertNotNull(user.getRol(), "El rol no debe ser null");

        System.out.println("[UserDAOTest] Usuario autenticado: " + user);
    }

    @Test
    @Order(7)
    @DisplayName("7 - autenticar: debe retornar null con credenciales incorrectas")
    void testAutenticarFallido() throws Exception {
        User user = dao.autenticar("noexiste@torneos.com", "wrongpass");

        assertNull(user, "autenticar() debe retornar null con credenciales inválidas");

        System.out.println("[UserDAOTest] Autenticación fallida retornó null correctamente.");
    }
}
