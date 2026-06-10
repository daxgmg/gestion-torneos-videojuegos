package com.torneos.persistencia;

import com.torneos.dominio.Equipo;
import com.torneos.dominio.Inscripcion;
import com.torneos.dominio.Torneo;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para {@link InscripcionDAO}.
 * Requiere:
 *   - Torneo con id=1 del script SQL.
 *   - Equipo con id=1 del script SQL.
 *   - La inscripción de prueba ya insertada en el script no debe colisionar
 *     con el UNIQUE (id_torneo, id_equipo). Se usará un equipo diferente
 *     para no violar la restricción.
 *
 * Orden de ejecución: insertar → obtenerTodos → buscarPorId → actualizar → eliminar
 *                     → buscarPorTorneo
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("InscripcionDAO - Pruebas CRUD")
class InscripcionDAOTest {

    private static final InscripcionDAO dao       = new InscripcionDAO();
    private static final EquipoDAO      equipoDao = new EquipoDAO();
    private static final TorneoDAO      torneoDao = new TorneoDAO();

    /** ID generado al insertar la inscripción de prueba. */
    private static int idInscripcion;

    /** Torneo de prueba temporal (para no depender del UNIQUE existente). */
    private static int idTorneoTemp;

    /** Equipo de prueba temporal. */
    private static int idEquipoTemp;

    // =========================================================================
    // SETUP: crear torneo y equipo temporales antes de los tests
    // =========================================================================

    @BeforeAll
    static void crearDatosPrevios() {
        Torneo torneo = new Torneo();
        torneo.setNombre("Torneo Temp InscripcionTest");
        torneo.setFechaInicio("2026-09-01");
        torneo.setFechaFin("2026-09-30");
        torneo.setEstado("ACTIVO");
        torneoDao.insertar(torneo);
        idTorneoTemp = torneo.getId();

        Equipo equipo = new Equipo();
        equipo.setNombre("Equipo Temp InscripcionTest");
        equipoDao.insertar(equipo);
        idEquipoTemp = equipo.getId();

        System.out.println("[InscripcionDAOTest] Setup: torneoTemp=" + idTorneoTemp
                + ", equipoTemp=" + idEquipoTemp);
    }

    // =========================================================================
    // TEARDOWN: eliminar torneo y equipo temporales al final
    // =========================================================================

    @AfterAll
    static void eliminarDatosPrevios() {
        torneoDao.eliminar(idTorneoTemp);
        equipoDao.eliminar(idEquipoTemp);
        System.out.println("[InscripcionDAOTest] Teardown: datos temporales eliminados.");
    }

    // =========================================================================
    // 1. INSERTAR
    // =========================================================================

    @Test
    @Order(1)
    @DisplayName("1 - insertar: debe persistir una inscripción y asignar id > 0")
    void testInsertar() {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setTorneo(new Torneo(idTorneoTemp, null, null, null, null));
        inscripcion.setEquipo(new Equipo(idEquipoTemp, null));

        boolean resultado = dao.insertar(inscripcion);

        assertTrue(resultado, "insertar() debe retornar true");
        assertTrue(inscripcion.getId() > 0, "El id generado debe ser mayor que 0");

        idInscripcion = inscripcion.getId();
        System.out.println("[InscripcionDAOTest] Inscripción insertada con id: " + idInscripcion);
    }

    // =========================================================================
    // 2. OBTENER TODOS
    // =========================================================================

    @Test
    @Order(2)
    @DisplayName("2 - obtenerTodos: la lista no debe estar vacía")
    void testObtenerTodos() {
        List<Inscripcion> lista = dao.obtenerTodos();

        assertNotNull(lista, "La lista no debe ser null");
        assertFalse(lista.isEmpty(), "La lista debe contener al menos una inscripción");

        System.out.println("[InscripcionDAOTest] Total inscripciones encontradas: " + lista.size());
    }

    // =========================================================================
    // 3. BUSCAR POR ID
    // =========================================================================

    @Test
    @Order(3)
    @DisplayName("3 - buscarPorId: debe retornar la inscripción con torneo y equipo")
    void testBuscarPorId() {
        Inscripcion inscripcion = dao.buscarPorId(idInscripcion);

        assertNotNull(inscripcion, "La inscripción no debe ser null");
        assertEquals(idInscripcion, inscripcion.getId(), "El id debe coincidir");
        assertNotNull(inscripcion.getTorneo(), "El torneo no debe ser null");
        assertNotNull(inscripcion.getEquipo(), "El equipo no debe ser null");

        System.out.println("[InscripcionDAOTest] Inscripción encontrada: " + inscripcion);
    }

    // =========================================================================
    // 4. ACTUALIZAR
    // =========================================================================

    @Test
    @Order(4)
    @DisplayName("4 - actualizar: debe modificar la inscripción hacia torneo id=1")
    void testActualizar() {
        // Apuntamos al torneo id=1 y al equipo temporal (el equipo temp no está en torneo 1)
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setId(idInscripcion);
        inscripcion.setTorneo(new Torneo(1, null, null, null, null));
        inscripcion.setEquipo(new Equipo(idEquipoTemp, null));

        boolean resultado = dao.actualizar(inscripcion);

        assertTrue(resultado, "actualizar() debe retornar true");

        Inscripcion inscripcionActualizada = dao.buscarPorId(idInscripcion);
        assertNotNull(inscripcionActualizada, "La inscripción actualizada no debe ser null");
        assertEquals(1, inscripcionActualizada.getTorneo().getId(),
                "El id del torneo debe haberse actualizado a 1");

        System.out.println("[InscripcionDAOTest] Inscripción actualizada: " + inscripcionActualizada);
    }

    // =========================================================================
    // 5. ELIMINAR
    // =========================================================================

    @Test
    @Order(5)
    @DisplayName("5 - eliminar: debe borrar la inscripción y no encontrarla después")
    void testEliminar() {
        boolean resultado = dao.eliminar(idInscripcion);

        assertTrue(resultado, "eliminar() debe retornar true");

        Inscripcion inscripcionEliminada = dao.buscarPorId(idInscripcion);
        assertNull(inscripcionEliminada, "La inscripción eliminada no debe encontrarse");

        System.out.println("[InscripcionDAOTest] Inscripción con id " + idInscripcion
                + " eliminada correctamente.");
    }

    // =========================================================================
    // 6. BUSCAR POR TORNEO
    // =========================================================================

    @Test
    @Order(6)
    @DisplayName("6 - buscarPorTorneo: debe retornar inscripciones del torneo id=1")
    void testBuscarPorTorneo() {
        List<Inscripcion> lista = dao.buscarPorTorneo(1);

        assertNotNull(lista, "La lista no debe ser null");
        assertFalse(lista.isEmpty(), "El torneo 1 debe tener al menos una inscripción");

        lista.forEach(i -> assertEquals(1, i.getTorneo().getId(),
                "Todas las inscripciones deben pertenecer al torneo 1"));

        System.out.println("[InscripcionDAOTest] Inscripciones en torneo 1: " + lista.size());
    }
}
