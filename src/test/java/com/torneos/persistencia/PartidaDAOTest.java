package com.torneos.persistencia;

import com.torneos.dominio.Equipo;
import com.torneos.dominio.Partida;
import com.torneos.dominio.Torneo;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para {@link PartidaDAO}.
 * Requiere:
 *   - Torneo con id=1 del script SQL.
 *   - Equipos con id=1 y id=2 del script SQL.
 *
 * Orden de ejecución: insertar → obtenerTodos → buscarPorId → actualizar → eliminar
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PartidaDAO - Pruebas CRUD")
class PartidaDAOTest {

    private static final PartidaDAO dao = new PartidaDAO();

    /** ID generado al insertar; se comparte entre los métodos de prueba. */
    private static int idGenerado;

    // Referencias a registros del script de datos de prueba
    private static final Torneo TORNEO_REF = new Torneo(1, "Torneo Inaugural 2026",
            "2026-06-15", "2026-07-15", "ACTIVO");
    private static final Equipo EQUIPO1_REF = new Equipo(1, "Team Alpha");
    private static final Equipo EQUIPO2_REF = new Equipo(2, "Team Omega");

    // =========================================================================
    // 1. INSERTAR
    // =========================================================================

    @Test
    @Order(1)
    @DisplayName("1 - insertar: debe persistir una partida y asignar id > 0")
    void testInsertar() {
        Partida partida = new Partida();
        partida.setTorneo(TORNEO_REF);
        partida.setEquipo1(EQUIPO1_REF);
        partida.setEquipo2(EQUIPO2_REF);
        partida.setResultado(null);           // pendiente
        partida.setFecha("2026-06-20 15:00");

        boolean resultado = dao.insertar(partida);

        assertTrue(resultado, "insertar() debe retornar true");
        assertTrue(partida.getId() > 0, "El id generado debe ser mayor que 0");

        idGenerado = partida.getId();
        System.out.println("[PartidaDAOTest] Partida insertada con id: " + idGenerado);
    }

    // =========================================================================
    // 2. OBTENER TODOS
    // =========================================================================

    @Test
    @Order(2)
    @DisplayName("2 - obtenerTodos: la lista no debe estar vacía")
    void testObtenerTodos() {
        List<Partida> lista = dao.obtenerTodos();

        assertNotNull(lista, "La lista no debe ser null");
        assertFalse(lista.isEmpty(), "La lista debe contener al menos una partida");

        System.out.println("[PartidaDAOTest] Total partidas encontradas: " + lista.size());
    }

    // =========================================================================
    // 3. BUSCAR POR ID
    // =========================================================================

    @Test
    @Order(3)
    @DisplayName("3 - buscarPorId: debe retornar la partida con torneo y equipos")
    void testBuscarPorId() {
        Partida partida = dao.buscarPorId(idGenerado);

        assertNotNull(partida, "La partida no debe ser null");
        assertEquals(idGenerado, partida.getId(), "El id debe coincidir");
        assertNotNull(partida.getTorneo(), "El torneo no debe ser null");
        assertNotNull(partida.getEquipo1(), "El equipo1 no debe ser null");
        assertNotNull(partida.getEquipo2(), "El equipo2 no debe ser null");

        System.out.println("[PartidaDAOTest] Partida encontrada: " + partida);
    }

    // =========================================================================
    // 4. ACTUALIZAR
    // =========================================================================

    @Test
    @Order(4)
    @DisplayName("4 - actualizar: debe registrar el resultado de la partida")
    void testActualizar() {
        Partida partida = new Partida();
        partida.setId(idGenerado);
        partida.setTorneo(TORNEO_REF);
        partida.setEquipo1(EQUIPO1_REF);
        partida.setEquipo2(EQUIPO2_REF);
        partida.setResultado("Team Alpha 3 - 1 Team Omega");
        partida.setFecha("2026-06-20 17:30");

        boolean resultado = dao.actualizar(partida);

        assertTrue(resultado, "actualizar() debe retornar true");

        Partida partidaActualizada = dao.buscarPorId(idGenerado);
        assertNotNull(partidaActualizada, "La partida actualizada no debe ser null");
        assertEquals("Team Alpha 3 - 1 Team Omega", partidaActualizada.getResultado(),
                "El resultado debe haberse actualizado");

        System.out.println("[PartidaDAOTest] Partida actualizada: " + partidaActualizada);
    }

    // =========================================================================
    // 5. ELIMINAR
    // =========================================================================

    @Test
    @Order(5)
    @DisplayName("5 - eliminar: debe borrar la partida y no encontrarla después")
    void testEliminar() {
        boolean resultado = dao.eliminar(idGenerado);

        assertTrue(resultado, "eliminar() debe retornar true");

        Partida partidaEliminada = dao.buscarPorId(idGenerado);
        assertNull(partidaEliminada, "La partida eliminada no debe encontrarse");

        System.out.println("[PartidaDAOTest] Partida con id " + idGenerado + " eliminada correctamente.");
    }
}
