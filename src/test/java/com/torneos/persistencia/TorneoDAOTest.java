package com.torneos.persistencia;

import com.torneos.dominio.Torneo;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para {@link TorneoDAO}.
 * Requiere que la base de datos torneos_db esté disponible.
 *
 * Orden de ejecución: insertar → obtenerTodos → buscarPorId → actualizar → eliminar
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("TorneoDAO - Pruebas CRUD")
class TorneoDAOTest {

    private static final TorneoDAO dao = new TorneoDAO();

    /** ID generado al insertar; se comparte entre los métodos de prueba. */
    private static int idGenerado;

    // =========================================================================
    // 1. INSERTAR
    // =========================================================================

    @Test
    @Order(1)
    @DisplayName("1 - insertar: debe persistir un torneo y asignar id > 0")
    void testInsertar() {
        Torneo torneo = new Torneo();
        torneo.setNombre("Torneo JUnit Test");
        torneo.setFechaInicio("2026-07-01");
        torneo.setFechaFin("2026-07-31");
        torneo.setEstado("ACTIVO");
        torneo.setRecompensa("฿ 50,000,000");

        boolean resultado = dao.insertar(torneo);

        assertTrue(resultado, "insertar() debe retornar true");
        assertTrue(torneo.getId() > 0, "El id generado debe ser mayor que 0");

        idGenerado = torneo.getId();
        System.out.println("[TorneoDAOTest] Torneo insertado con id: " + idGenerado);
    }

    // =========================================================================
    // 2. OBTENER TODOS
    // =========================================================================

    @Test
    @Order(2)
    @DisplayName("2 - obtenerTodos: la lista no debe estar vacía")
    void testObtenerTodos() {
        List<Torneo> lista = dao.obtenerTodos();

        assertNotNull(lista, "La lista no debe ser null");
        assertFalse(lista.isEmpty(), "La lista debe contener al menos un torneo");

        System.out.println("[TorneoDAOTest] Total torneos encontrados: " + lista.size());
    }

    // =========================================================================
    // 3. BUSCAR POR ID
    // =========================================================================

    @Test
    @Order(3)
    @DisplayName("3 - buscarPorId: debe retornar el torneo insertado")
    void testBuscarPorId() {
        Torneo torneo = dao.buscarPorId(idGenerado);

        assertNotNull(torneo, "El torneo no debe ser null");
        assertEquals(idGenerado, torneo.getId(), "El id debe coincidir");
        assertEquals("Torneo JUnit Test", torneo.getNombre(), "El nombre debe coincidir");
        assertEquals("ACTIVO", torneo.getEstado(), "El estado debe ser ACTIVO");
        assertEquals("฿ 50,000,000", torneo.getRecompensa(), "La recompensa debe coincidir");

        System.out.println("[TorneoDAOTest] Torneo encontrado: " + torneo);
    }

    // =========================================================================
    // 4. ACTUALIZAR
    // =========================================================================

    @Test
    @Order(4)
    @DisplayName("4 - actualizar: debe modificar el nombre y estado del torneo")
    void testActualizar() {
        Torneo torneo = new Torneo();
        torneo.setId(idGenerado);
        torneo.setNombre("Torneo JUnit Test Actualizado");
        torneo.setFechaInicio("2026-07-01");
        torneo.setFechaFin("2026-08-15");
        torneo.setEstado("FINALIZADO");
        torneo.setRecompensa("฿ 100,000,000");

        boolean resultado = dao.actualizar(torneo);

        assertTrue(resultado, "actualizar() debe retornar true");

        Torneo torneoActualizado = dao.buscarPorId(idGenerado);
        assertNotNull(torneoActualizado, "El torneo actualizado no debe ser null");
        assertEquals("FINALIZADO", torneoActualizado.getEstado(),
                "El estado debe haberse actualizado a FINALIZADO");
        assertEquals("Torneo JUnit Test Actualizado", torneoActualizado.getNombre(),
                "El nombre debe haberse actualizado");
        assertEquals("฿ 100,000,000", torneoActualizado.getRecompensa(),
                "La recompensa debe haberse actualizado");

        System.out.println("[TorneoDAOTest] Torneo actualizado: " + torneoActualizado);
    }

    // =========================================================================
    // 5. ELIMINAR
    // =========================================================================

    @Test
    @Order(5)
    @DisplayName("5 - eliminar: debe borrar el torneo y no encontrarlo después")
    void testEliminar() {
        boolean resultado = dao.eliminar(idGenerado);

        assertTrue(resultado, "eliminar() debe retornar true");

        Torneo torneoEliminado = dao.buscarPorId(idGenerado);
        assertNull(torneoEliminado, "El torneo eliminado no debe encontrarse");

        System.out.println("[TorneoDAOTest] Torneo con id " + idGenerado + " eliminado correctamente.");
    }
}
