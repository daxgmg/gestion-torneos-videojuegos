package com.torneos.persistencia;

import com.torneos.dominio.Rol;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para {@link RolDAO}.
 * Requiere que la base de datos torneos_db esté disponible.
 *
 * Orden de ejecución: insertar → obtenerTodos → buscarPorId → actualizar → eliminar
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("RolDAO - Pruebas CRUD")
class RolDAOTest {

    private static final RolDAO dao = new RolDAO();

    /** ID generado al insertar; se comparte entre los métodos de prueba. */
    private static int idGenerado;

    // =========================================================================
    // 1. INSERTAR
    // =========================================================================

    @Test
    @Order(1)
    @DisplayName("1 - insertar: debe persistir un rol y asignar id > 0")
    void testInsertar() {
        Rol rol = new Rol();
        rol.setNombre("JUGADOR");

        boolean resultado = dao.insertar(rol);

        assertTrue(resultado, "insertar() debe retornar true");
        assertTrue(rol.getId() > 0, "El id generado debe ser mayor que 0");

        idGenerado = rol.getId();
        System.out.println("[RolDAOTest] Rol insertado con id: " + idGenerado);
    }

    // =========================================================================
    // 2. OBTENER TODOS
    // =========================================================================

    @Test
    @Order(2)
    @DisplayName("2 - obtenerTodos: la lista no debe estar vacía")
    void testObtenerTodos() {
        List<Rol> lista = dao.obtenerTodos();

        assertNotNull(lista, "La lista no debe ser null");
        assertFalse(lista.isEmpty(), "La lista debe contener al menos un rol");

        System.out.println("[RolDAOTest] Total roles encontrados: " + lista.size());
    }

    // =========================================================================
    // 3. BUSCAR POR ID
    // =========================================================================

    @Test
    @Order(3)
    @DisplayName("3 - buscarPorId: debe retornar el rol insertado")
    void testBuscarPorId() {
        Rol rol = dao.buscarPorId(idGenerado);

        assertNotNull(rol, "El rol buscado no debe ser null");
        assertEquals(idGenerado, rol.getId(), "El id debe coincidir");
        assertEquals("JUGADOR", rol.getNombre(), "El nombre debe ser JUGADOR");

        System.out.println("[RolDAOTest] Rol encontrado: " + rol);
    }

    // =========================================================================
    // 4. ACTUALIZAR
    // =========================================================================

    @Test
    @Order(4)
    @DisplayName("4 - actualizar: debe modificar el nombre del rol")
    void testActualizar() {
        Rol rol = new Rol();
        rol.setId(idGenerado);
        rol.setNombre("ADMIN");

        boolean resultado = dao.actualizar(rol);

        assertTrue(resultado, "actualizar() debe retornar true");

        Rol rolActualizado = dao.buscarPorId(idGenerado);
        assertNotNull(rolActualizado, "El rol actualizado no debe ser null");
        assertEquals("ADMIN", rolActualizado.getNombre(), "El nombre debe haberse actualizado");

        System.out.println("[RolDAOTest] Rol actualizado: " + rolActualizado);
    }

    // =========================================================================
    // 5. ELIMINAR
    // =========================================================================

    @Test
    @Order(5)
    @DisplayName("5 - eliminar: debe borrar el rol y no encontrarlo después")
    void testEliminar() {
        boolean resultado = dao.eliminar(idGenerado);

        assertTrue(resultado, "eliminar() debe retornar true");

        Rol rolEliminado = dao.buscarPorId(idGenerado);
        assertNull(rolEliminado, "El rol eliminado no debe encontrarse");

        System.out.println("[RolDAOTest] Rol con id " + idGenerado + " eliminado correctamente.");
    }
}
