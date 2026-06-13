# Plan de Implementación: Vistas Swing y Registro de Torneos

Este plan detalla las modificaciones y adiciones de interfaz gráfica de usuario Swing realizadas en el proyecto de gestión de torneos de videojuegos.

---

## Cambios Realizados y Propuestos

### Componente: Vistas del Jugador

#### [NEW] TorneosJugadorFrame.java
- Muestra una tabla con los torneos cuyo estado es `ACTIVO`.
- **Botón "Ver Partidas":** Abre la vista `PartidasTorneoFrame` para el torneo seleccionado.
- **Botón "¿Te quieres unir?":** Permite al jugador inscribir a su equipo en el torneo activo seleccionado (con validación de inscripción previa y verificación de pertenencia a un equipo).
- **Botón de regreso ("<"):** Ubicado en la esquina superior izquierda, cierra la ventana con `dispose()`.

#### [NEW] PartidasTorneoFrame.java
- Recibe un objeto `Torneo` en el constructor.
- Muestra las partidas del torneo específico: ID, Equipo 1, Equipo 2, Fecha, Resultado.
- Si el resultado no ha sido asignado, muestra `"Pendiente"`.
- Muestra los nombres de los equipos en lugar de sus IDs numéricos.
- **Botón de regreso ("<"):** Ubicado en la esquina superior izquierda.

#### [NEW] MisPartidasFrame.java
- Recibe un objeto `User` en el constructor.
- Detecta el equipo del jugador actual a través de `JugadorDAO`.
- Muestra únicamente las partidas en las que juegue su equipo.
- **Botón de regreso ("<"):** Ubicado en la esquina superior izquierda.

#### [NEW] ClasificacionFrame.java
- Muestra la tabla de posiciones con estadísticas acumuladas (Partidas jugadas, Victorias, Derrotas, Puntos).
- Calcula 3 puntos por victoria y 1 por empate leyendo los resultados en formato `X-Y` de las partidas.
- Ordena la tabla de posiciones por puntos en orden descendente.
- **Botón de regreso ("<"):** Ubicado en la esquina superior izquierda.

---

### Componente: Vistas de Administración

#### [NEW] PartidaFrame.java
- CRUD completo de partidas para los administradores.
- Formulario interactivo con dropdowns (`JComboBox`) para seleccionar torneos y equipos, ingreso de fecha (`yyyy-MM-dd`) y resultado (`X-Y`).
- Validaciones para evitar partidos entre el mismo equipo y fechas en formato incorrecto.
- **Botón de regreso ("<"):** Ubicado en la esquina superior izquierda.

#### [MODIFY] TorneoFrame.java
- **Botón de regreso ("<"):** Añadido en un nuevo panel de encabezado superior izquierdo para volver al menú de administración.

#### [MODIFY] EquipoFrame.java
- **Botón de regreso ("<"):** Añadido en un nuevo panel de encabezado superior izquierdo para volver al menú de administración.

#### [MODIFY] JugadorFrame.java
- **Botón de regreso ("<"):** Añadido en un nuevo panel de encabezado superior izquierdo para volver al menú de administración.

#### [MODIFY] UserFrame.java
- **Botón de regreso ("<"):** Añadido en un nuevo panel de encabezado superior izquierdo para volver al menú de administración.

---

### Componente: Menús Principales

#### [MODIFY] MenuJugadorFrame.java
- Conecta los botones del panel de control a las vistas correspondientes (`TorneosJugadorFrame`, `MisPartidasFrame`, `ClasificacionFrame`).

#### [MODIFY] MenuAdminFrame.java
- Diseña una cuadrícula `3x2` para incorporar el botón "⚽ Gestionar Partidas" que abre `PartidaFrame`.

---

## Plan de Verificación

### Pruebas Automatizadas
- Compilación del proyecto completo:
  ```bash
  mvn clean compile