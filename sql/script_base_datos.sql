-- ============================================================
-- SCRIPT DE BASE DE DATOS: torneos_db
-- Motor: SQL Server
-- Descripción: Creación de la base de datos para el sistema
--              de gestión de torneos de videojuegos.
-- ============================================================

-- ============================================================
-- 1. CREAR BASE DE DATOS
-- ============================================================
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'torneos_db')
BEGIN
    CREATE DATABASE torneos_db;
END
GO

USE torneos_db;
GO

-- ============================================================
-- 2. CREAR TABLAS
-- ============================================================

-- ------------------------------------------------------------
-- Tabla: roles
-- ------------------------------------------------------------
IF OBJECT_ID('dbo.roles', 'U') IS NOT NULL
    DROP TABLE dbo.roles;
GO

CREATE TABLE dbo.roles (
    id     INT           IDENTITY(1,1) PRIMARY KEY,
    nombre NVARCHAR(50)  NOT NULL
        CONSTRAINT CK_roles_nombre CHECK (nombre IN ('ADMIN', 'JUGADOR'))
);
GO

-- ------------------------------------------------------------
-- Tabla: users
-- ------------------------------------------------------------
IF OBJECT_ID('dbo.users', 'U') IS NOT NULL
    DROP TABLE dbo.users;
GO

CREATE TABLE dbo.users (
    id       INT           IDENTITY(1,1) PRIMARY KEY,
    nombre   NVARCHAR(100) NOT NULL,
    email    NVARCHAR(150) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    id_rol   INT           NOT NULL,
    CONSTRAINT FK_users_roles FOREIGN KEY (id_rol)
        REFERENCES dbo.roles(id)
);
GO

-- ------------------------------------------------------------
-- Tabla: equipos
-- ------------------------------------------------------------
IF OBJECT_ID('dbo.equipos', 'U') IS NOT NULL
    DROP TABLE dbo.equipos;
GO

CREATE TABLE dbo.equipos (
    id     INT           IDENTITY(1,1) PRIMARY KEY,
    nombre NVARCHAR(100) NOT NULL
);
GO

-- ------------------------------------------------------------
-- Tabla: jugadores
-- ------------------------------------------------------------
IF OBJECT_ID('dbo.jugadores', 'U') IS NOT NULL
    DROP TABLE dbo.jugadores;
GO

CREATE TABLE dbo.jugadores (
    id         INT           IDENTITY(1,1) PRIMARY KEY,
    nombre     NVARCHAR(100) NOT NULL,
    alias      NVARCHAR(100) NOT NULL,
    id_equipo  INT           NOT NULL,
    id_user    INT           NOT NULL,
    CONSTRAINT FK_jugadores_equipos FOREIGN KEY (id_equipo)
        REFERENCES dbo.equipos(id),
    CONSTRAINT FK_jugadores_users FOREIGN KEY (id_user)
        REFERENCES dbo.users(id)
);
GO

-- ------------------------------------------------------------
-- Tabla: torneos
-- ------------------------------------------------------------
IF OBJECT_ID('dbo.torneos', 'U') IS NOT NULL
    DROP TABLE dbo.torneos;
GO

CREATE TABLE dbo.torneos (
    id           INT           IDENTITY(1,1) PRIMARY KEY,
    nombre       NVARCHAR(150) NOT NULL,
    fecha_inicio DATE          NOT NULL,
    fecha_fin    DATE          NOT NULL,
    estado       NVARCHAR(20)  NOT NULL DEFAULT 'ACTIVO'
        CONSTRAINT CK_torneos_estado CHECK (estado IN ('ACTIVO', 'FINALIZADO'))
);
GO

-- ------------------------------------------------------------
-- Tabla: partidas
-- ------------------------------------------------------------
IF OBJECT_ID('dbo.partidas', 'U') IS NOT NULL
    DROP TABLE dbo.partidas;
GO

CREATE TABLE dbo.partidas (
    id          INT           IDENTITY(1,1) PRIMARY KEY,
    id_torneo   INT           NOT NULL,
    id_equipo1  INT           NOT NULL,
    id_equipo2  INT           NOT NULL,
    resultado   NVARCHAR(100) NULL,
    fecha       DATETIME      NOT NULL,
    CONSTRAINT FK_partidas_torneos   FOREIGN KEY (id_torneo)
        REFERENCES dbo.torneos(id),
    CONSTRAINT FK_partidas_equipo1   FOREIGN KEY (id_equipo1)
        REFERENCES dbo.equipos(id),
    CONSTRAINT FK_partidas_equipo2   FOREIGN KEY (id_equipo2)
        REFERENCES dbo.equipos(id),
    CONSTRAINT CK_partidas_equipos_distintos
        CHECK (id_equipo1 <> id_equipo2)
);
GO

-- ------------------------------------------------------------
-- Tabla: inscripciones
-- ------------------------------------------------------------
IF OBJECT_ID('dbo.inscripciones', 'U') IS NOT NULL
    DROP TABLE dbo.inscripciones;
GO

CREATE TABLE dbo.inscripciones (
    id         INT  IDENTITY(1,1) PRIMARY KEY,
    id_torneo  INT  NOT NULL,
    id_equipo  INT  NOT NULL,
    CONSTRAINT FK_inscripciones_torneos FOREIGN KEY (id_torneo)
        REFERENCES dbo.torneos(id),
    CONSTRAINT FK_inscripciones_equipos FOREIGN KEY (id_equipo)
        REFERENCES dbo.equipos(id),
    CONSTRAINT UQ_inscripcion UNIQUE (id_torneo, id_equipo)
);
GO

-- ============================================================
-- 3. DATOS DE PRUEBA
-- ============================================================

-- ------------------------------------------------------------
-- Roles
-- ------------------------------------------------------------
SET IDENTITY_INSERT dbo.roles ON;

INSERT INTO dbo.roles (id, nombre) VALUES
    (1, 'ADMIN'),
    (2, 'JUGADOR');

SET IDENTITY_INSERT dbo.roles OFF;
GO

-- ------------------------------------------------------------
-- Usuario administrador por defecto
-- Nota: En producción la contraseña debe almacenarse con hash.
-- ------------------------------------------------------------
INSERT INTO dbo.users (nombre, email, password, id_rol) VALUES
    ('Administrador', 'admin@torneos.com', 'admin123', 1);
GO

-- ------------------------------------------------------------
-- Equipos de prueba
-- ------------------------------------------------------------
INSERT INTO dbo.equipos (nombre) VALUES
    ('Team Alpha'),
    ('Team Omega');
GO

-- ------------------------------------------------------------
-- Torneo de prueba
-- ------------------------------------------------------------
INSERT INTO dbo.torneos (nombre, fecha_inicio, fecha_fin, estado) VALUES
    ('Torneo Inaugural 2026', '2026-06-15', '2026-07-15', 'ACTIVO');
GO

-- ------------------------------------------------------------
-- Inscripciones de prueba (ambos equipos en el torneo)
-- ------------------------------------------------------------
INSERT INTO dbo.inscripciones (id_torneo, id_equipo) VALUES
    (1, 1),
    (1, 2);
GO

-- ============================================================
-- VERIFICACIÓN RÁPIDA
-- ============================================================
SELECT 'roles'         AS tabla, COUNT(*) AS registros FROM dbo.roles
UNION ALL
SELECT 'users',                  COUNT(*)               FROM dbo.users
UNION ALL
SELECT 'equipos',                COUNT(*)               FROM dbo.equipos
UNION ALL
SELECT 'torneos',                COUNT(*)               FROM dbo.torneos
UNION ALL
SELECT 'inscripciones',          COUNT(*)               FROM dbo.inscripciones
UNION ALL
SELECT 'jugadores',              COUNT(*)               FROM dbo.jugadores
UNION ALL
SELECT 'partidas',               COUNT(*)               FROM dbo.partidas;
GO
ALTER LOGIN sa ENABLE;
ALTER LOGIN sa WITH PASSWORD = 'Admin123!';
GO

-- Habilitar modo mixto (por si acaso)
EXEC xp_instance_regwrite 
    N'HKEY_LOCAL_MACHINE', 
    N'Software\Microsoft\MSSQLServer\MSSQLServer',
    N'LoginMode', REG_DWORD, 2;
GO