package com.torneos.persistencia;

import com.torneos.dominio.Equipo;
import com.torneos.dominio.Jugador;
import com.torneos.dominio.Rol;
import com.torneos.dominio.User;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para {@link JugadorDAO}.
 * Requiere:
 *   - Equipo con id=1 (Team Alpha) del script SQL.
 *   - User con id=1 (admin) del script SQL.
 *
 * Orden de ejecución: insertar → obtenerTodos → buscarPorId → actualizar → eliminar
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("JugadorDAO - Pruebas CRUD")
class JugadorDAOTest {

    private static final JugadorDAO dao = new JugadorDAO();

    /** ID generado al insertar; se comparte entre los métodos de prueba. */
    private static int idGenerado;

    // Referencias a registros del script de datos de prueba
    private static final Equipo EQUIPO_REF = new Equipo(1, "Team Alpha");
    private static final Rol    ROL_REF    = new Rol(1, "ADMIN");
    private static final User   USER_REF;

    static {
        USER_REF = new User();
        USER_REF.setId(1);
        USER_REF.setNombre("Administrador");
        USER_REF.setEmail("admin@torneos.com");
        USER_REF.setPassword("admin123");
        USER_REF.setRol(ROL_REF);
    }

    // =========================================================================
    // 1. INSERTAR
    // =========================================================================

    @Test
    @Order(1)
    @DisplayName("1 - insertar: debe persistir un jugador y asignar id > 0")
    void testInsertar() {
        Jugador jugador = new Jugador();
        jugador.setNombre("Carlos Gómez");
        jugador.setAlias("CarlosG");
        jugador.setEquipo(EQUIPO_REF);
        jugador.setUser(USER_REF);

        boolean resultado = dao.insertar(jugador);

        assertTrue(resultado, "insertar() debe retornar true");
        assertTrue(jugador.getId() > 0, "El id generado debe ser mayor que 0");

        idGenerado = jugador.getId();
        System.out.println("[JugadorDAOTest] Jugador insertado con id: " + idGenerado);
    }

    // =========================================================================
    // 2. OBTENER TODOS
    // =========================================================================

    @Test
    @Order(2)
    @DisplayName("2 - obtenerTodos: la lista no debe estar vacía")
    void testObtenerTodos() {
        List<Jugador> lista = dao.obtenerTodos();

        assertNotNull(lista, "La lista no debe ser null");
        assertFalse(lista.isEmpty(), "La lista debe contener al menos un jugador");

        System.out.println("[JugadorDAOTest] Total jugadores encontrados: " + lista.size());
    }

    // =========================================================================
    // 3. BUSCAR POR ID
    // =========================================================================

    @Test
    @Order(3)
    @DisplayName("3 - buscarPorId: debe retornar el jugador con equipo y usuario")
    void testBuscarPorId() {
        Jugador jugador = dao.buscarPorId(idGenerado);

        assertNotNull(jugador, "El jugador no debe ser null");
        assertEquals(idGenerado, jugador.getId(), "El id debe coincidir");
        assertEquals("CarlosG", jugador.getAlias(), "El alias debe coincidir");
        assertNotNull(jugador.getEquipo(), "El equipo no debe ser null");
        assertNotNull(jugador.getUser(), "El user no debe ser null");

        System.out.println("[JugadorDAOTest] Jugador encontrado: " + jugador);
    }

    // =========================================================================
    // 4. ACTUALIZAR
    // =========================================================================

    @Test
    @Order(4)
    @DisplayName("4 - actualizar: debe modificar el alias del jugador")
    void testActualizar() {
        Jugador jugador = new Jugador();
        jugador.setId(idGenerado);
        jugador.setNombre("Carlos Gómez");
        jugador.setAlias("CarlosGUpdated");
        jugador.setEquipo(EQUIPO_REF);
        jugador.setUser(USER_REF);

        boolean resultado = dao.actualizar(jugador);

        assertTrue(resultado, "actualizar() debe retornar true");

        Jugador jugadorActualizado = dao.buscarPorId(idGenerado);
        assertNotNull(jugadorActualizado, "El jugador actualizado no debe ser null");
        assertEquals("CarlosGUpdated", jugadorActualizado.getAlias(),
                "El alias debe haberse actualizado");

        System.out.println("[JugadorDAOTest] Jugador actualizado: " + jugadorActualizado);
    }

    // =========================================================================
    // 5. ELIMINAR
    // =========================================================================

    @Test
    @Order(5)
    @DisplayName("5 - eliminar: debe borrar el jugador y no encontrarlo después")
    void testEliminar() {
        boolean resultado = dao.eliminar(idGenerado);

        assertTrue(resultado, "eliminar() debe retornar true");

        Jugador jugadorEliminado = dao.buscarPorId(idGenerado);
        assertNull(jugadorEliminado, "El jugador eliminado no debe encontrarse");

        System.out.println("[JugadorDAOTest] Jugador con id " + idGenerado + " eliminado correctamente.");
    }
}
