# Requerimientos del Sistema - Gestión de Torneos de Videojuegos

Este documento especifica de manera exhaustiva los requerimientos funcionales y no funcionales para la plataforma de gestión de torneos de videojuegos, diseñada bajo una arquitectura modular y persistencia en MSSQL Server.

---

## 1. Requerimientos Funcionales (RF)

### 1.1 Módulo de Seguridad y Control de Acceso (Autenticación)
* **RF-01 (Inicio de Sesión):** El sistema debe proveer una interfaz gráfica para el ingreso de usuarios mediante correo electrónico único y contraseña cifrada.
* **RF-02 (Cierre de Sesión):** El sistema debe invalidar la sesión activa del usuario y destruir la instancia en memoria al solicitar la salida, redirigiendo inmediatamente a la pantalla de Login.
* **RF-03 (Control de Roles/Mapeo de Permisos):** El sistema debe validar el `rol_id` asignado al usuario desde la base de datos para restringir o habilitar módulos:
  * **Administrador:** Acceso total (Configuración, usuarios, base de datos).
  * **Organizador:** Gestión de torneos, emparejamientos y resultados.
  * **Jugador/Capitán:** Inscripción de equipos y consulta de estadísticas.
* **RF-04 (Bloqueo de Intentos):** Tras 5 intentos fallidos de inicio de sesión con el mismo correo electrónico, el sistema debe bloquear temporalmente el acceso de dicha cuenta por 15 minutos e registrar el evento en la bitácora.

### 1.2 Módulo de Gestión de Torneos y Eventos
* **RF-05 (Creación de Torneos):** El Administrador u Organizador debe poder registrar un nuevo torneo especificando: Nombre del torneo, Videojuego (ej. Clash Royale, Valorant), Fecha de inicio, Fecha de finalización, Máximo de equipos permitidos, y Formato de competencia (Eliminación directa, Round Robin, o Fase de grupos).
* **RF-06 (Control de Estados del Torneo):** El sistema debe manejar de manera automática o manual los estados de un torneo: *En Inscripción*, *Activo/En Progreso*, *Pausado* y *Finalizado*.
* **RF-07 (Generación Automática de Brackets/Fixture):** Una vez cerradas las inscripciones, el sistema debe ejecutar un algoritmo para generar los emparejamientos automáticos (Brackets de llaves) basados en el número de equipos inscritos. Si el número de equipos es impar, el sistema debe asignar un pase directo automático (BYE) según las reglas estándar de eSports.
* **RF-08 (Asignación de Servidores/Salas de Juego):** Permitir al organizador asignar la ID de la sala, código de partida privada o el servidor de juego donde se disputará cada partida programada.

### 1.3 Módulo de Gestión de Equipos y Participantes
* **RF-09 (Registro de Equipos):** Permitir la creación de perfiles de equipos con Nombre, Logotipo (ruta de imagen local o URL) y el capitán asignado.
* **RF-10 (Gestión de Rosters/Plantillas):** Permitir la adición, edición y eliminación de jugadores (Gamertags) dentro de un equipo específico, validando el límite máximo de jugadores por videojuego (ej. máximo 5 para juegos de disparos tácticos).
* **RF-11 (Validación de Inscripción Múltiple):** El sistema debe denegar la inscripción de un jugador en dos equipos diferentes que participen dentro del mismo torneo activo de manera simultánea.

### 1.4 Módulo de Encuentros y Marcadores (Resultados)
* **RF-12 (Registro de Scores/Marcadores):** El organizador debe ingresar los resultados de cada partida (Mapas ganados/perdidos). El sistema debe actualizar de forma automática el avance en los brackets de eliminación.
* **RF-13 (Carga de Evidencias):** Permitir adjuntar una cadena de texto (como enlace a captura de pantalla de victoria o stream) en cada encuentro para resolver disputas de resultados.
* **RF-14 (Tabla de Posiciones en Tiempo Real):** Para formatos de liga (Round Robin), el sistema debe calcular dinámicamente los puntos (3 por victoria, 1 por empate, 0 por derrota) y actualizar la tabla general ordenando por puntos, diferencia de mapas y enfrentamiento directo.

### 1.5 Módulo de Reportes, Auditoría y Estadísticas
* **RF-15 (Generación de Reportes PDF):** El sistema debe permitir la exportación en formato PDF de la tabla de posiciones final, lista de ganadores y el resumen de un torneo finalizado utilizando bibliotecas de Java.
* **RF-16 (Bitácora de Auditoría - Logs):** Cada acción crítica (modificación de marcadores, eliminación de usuarios, cambio de contraseñas) debe registrarse de manera interna guardando la fecha, hora, ID del usuario que realizó la acción y una descripción del cambio en la base de datos.
* **RF-17 (Módulo de Estadísticas del Jugador):** Mostrar métricas de rendimiento individuales dentro de la app (ej. partidas jugadas, porcentaje de victorias, títulos ganados).

---

## 2. Requerimientos No Funcionales (RNF)

### 2.1 Rendimiento y Concurrencia
* **RNF-01 (Optimización de Conexiones - Singleton):** El acceso a datos debe centralizarse obligatoriamente en el patrón de diseño Singleton (`Conexion.java`) para evitar la apertura desmedida de sockets de red y prevenir desbordamientos de memoria en SQL Server.
* **RNF-02 (Tiempo de Respuesta):** Las consultas de autenticación y carga de tablas principales de torneos no deben superar un tiempo de respuesta de 2.0 segundos bajo condiciones normales de red local.

### 2.2 Seguridad y Persistencia
* **RNF-03 (Persistencia Relacional):** El almacenamiento total de los datos del sistema (usuarios, contraseñas, logs, llaves de torneos, configuraciones) debe gestionarse en Microsoft SQL Server, utilizando integridad referencial estricta (Llaves primarias y foráneas).
* **RNF-04 (Cifrado de Cadenas de Conexión):** La URL de conexión JDBC debe forzar parámetros de seguridad (`encrypt=true` y `trustServerCertificate=true`) para encriptar el tráfico de credenciales entre la aplicación Java y el servidor de datos.
* **RNF-05 (Seguridad de Contraseñas de Aplicación):** Las contraseñas de los usuarios finales del sistema no deben guardarse en texto plano en la base de datos; deben procesarse con algoritmos de hashing criptográfico antes de su almacenamiento (o simulación en DAO).

### 2.3 Arquitectura y Mantenibilidad
* **RNF-06 (Separación de Capas):** El diseño de la solución debe respetar una arquitectura desacoplada y limpia:
  * **Vistas/UI:** Manejo exclusivo de componentes de interfaz de usuario.
  * **DAO/Persistencia:** Clases que encapsulan las sentencias SQL limpias de negocio.
  * **Dominio/Modelos:** Representación de las entidades de la base de datos mediante POJOs (Plain Old Java Objects).
* **RNF-07 (Portabilidad de Conexión):** La cadena de conexión debe estar parametrizada de manera que el cambio de credenciales de motor de base de datos SQL Server (`sa`, usuarios dedicados, puertos o hosts) se realice modificando un único archivo centralizado, sin alterar el código de la lógica de negocio.
* **RNF-08 (Manejo Global de Excepciones):** Toda llamada a servicios de base de datos mediante JDBC debe estar encapsulada en bloques `try-catch-finally`, capturando excepciones específicas (`SQLException`) y mostrando mensajes amigables al usuario en lugar de colapsar la aplicación.