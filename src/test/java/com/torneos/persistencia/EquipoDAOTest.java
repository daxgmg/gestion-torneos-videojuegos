package com.torneos.persistencia;

import com.torneos.dominio.Equipo;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para {@link EquipoDAO}.
 * Requiere que la base de datos torneos_db esté disponible.
 *
 * Orden de ejecución: insertar → obtenerTodos → buscarPorId → actualizar → eliminar
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("EquipoDAO - Pruebas CRUD")
class EquipoDAOTest {

    private static final EquipoDAO dao = new EquipoDAO();

    /** ID generado al insertar; se comparte entre los métodos de prueba. */
    private static int idGenerado;

    // =========================================================================
    // 1. INSERTAR
    // =========================================================================

    @Test
    @Order(1)
    @DisplayName("1 - insertar: debe persistir un equipo y asignar id > 0")
    void testInsertar() {
        Equipo equipo = new Equipo();
        equipo.setNombre("Team Test");

        boolean resultado = dao.insertar(equipo);

        assertTrue(resultado, "insertar() debe retornar true");
        assertTrue(equipo.getId() > 0, "El id generado debe ser mayor que 0");

        idGenerado = equipo.getId();
        System.out.println("[EquipoDAOTest] Equipo insertado con id: " + idGenerado);
    }

    // =========================================================================
    // 2. OBTENER TODOS
    // =========================================================================

    @Test
    @Order(2)
    @DisplayName("2 - obtenerTodos: la lista no debe estar vacía")
    void testObtenerTodos() {
        List<Equipo> lista = dao.obtenerTodos();

        assertNotNull(lista, "La lista no debe ser null");
        assertFalse(lista.isEmpty(), "La lista debe contener al menos un equipo");

        System.out.println("[EquipoDAOTest] Total equipos encontrados: " + lista.size());
    }

    // =========================================================================
    // 3. BUSCAR POR ID
    // =========================================================================

    @Test
    @Order(3)
    @DisplayName("3 - buscarPorId: debe retornar el equipo insertado")
    void testBuscarPorId() {
        Equipo equipo = dao.buscarPorId(idGenerado);

        assertNotNull(equipo, "El equipo no debe ser null");
        assertEquals(idGenerado, equipo.getId(), "El id debe coincidir");
        assertEquals("Team Test", equipo.getNombre(), "El nombre debe coincidir");

        System.out.println("[EquipoDAOTest] Equipo encontrado: " + equipo);
    }

    // =========================================================================
    // 4. ACTUALIZAR
    // =========================================================================

    @Test
    @Order(4)
    @DisplayName("4 - actualizar: debe modificar el nombre del equipo")
    void testActualizar() {
        Equipo equipo = new Equipo();
        equipo.setId(idGenerado);
        equipo.setNombre("Team Test Actualizado");

        boolean resultado = dao.actualizar(equipo);

        assertTrue(resultado, "actualizar() debe retornar true");

        Equipo equipoActualizado = dao.buscarPorId(idGenerado);
        assertNotNull(equipoActualizado, "El equipo actualizado no debe ser null");
        assertEquals("Team Test Actualizado", equipoActualizado.getNombre(),
                "El nombre debe haberse actualizado");

        System.out.println("[EquipoDAOTest] Equipo actualizado: " + equipoActualizado);
    }

    // =========================================================================
    // 5. ELIMINAR
    // =========================================================================

    @Test
    @Order(5)
    @DisplayName("5 - eliminar: debe borrar el equipo y no encontrarlo después")
    void testEliminar() {
        boolean resultado = dao.eliminar(idGenerado);

        assertTrue(resultado, "eliminar() debe retornar true");

        Equipo equipoEliminado = dao.buscarPorId(idGenerado);
        assertNull(equipoEliminado, "El equipo eliminado no debe encontrarse");

        System.out.println("[EquipoDAOTest] Equipo con id " + idGenerado + " eliminado correctamente.");
    }
}
