-- ============================================
-- SCRIPT COMPLETO DE BASE DE DATOS CGTI
-- Sistema de Gestion de Laboratorios
-- ============================================

-- Crear la base de datos si no existe
IF DB_ID('cgti') IS NULL 
    CREATE DATABASE cgti;
GO

USE cgti;
GO

-- ============================================
-- ELIMINAR TABLAS EXISTENTES (en orden correcto por dependencias)
-- ============================================
PRINT 'Eliminando tablas existentes...';
GO

-- Primero eliminar tablas con dependencias (FK)
IF OBJECT_ID('dbo.registros', 'U') IS NOT NULL DROP TABLE dbo.registros;
IF OBJECT_ID('dbo.Horario', 'U') IS NOT NULL DROP TABLE dbo.Horario;
IF OBJECT_ID('dbo.equipos', 'U') IS NOT NULL DROP TABLE dbo.equipos;
IF OBJECT_ID('dbo.usuarios', 'U') IS NOT NULL DROP TABLE dbo.usuarios;
IF OBJECT_ID('dbo.carreras', 'U') IS NOT NULL DROP TABLE dbo.carreras;
IF OBJECT_ID('dbo.cuatrimestres', 'U') IS NOT NULL DROP TABLE dbo.cuatrimestres;
IF OBJECT_ID('dbo.area', 'U') IS NOT NULL DROP TABLE dbo.area;
IF OBJECT_ID('dbo.laboratorio', 'U') IS NOT NULL DROP TABLE dbo.laboratorio;
IF OBJECT_ID('dbo.software', 'U') IS NOT NULL DROP TABLE dbo.software;
GO

PRINT 'Tablas eliminadas correctamente.';
GO

-- ============================================
-- CREAR TABLAS
-- ============================================
PRINT 'Creando tablas...';
GO

--Tabla: LABORATORIOS
CREATE TABLE dbo.laboratorio
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombreLab VARCHAR(20) NOT NULL UNIQUE
);
GO

-- Tabla: SOFTWARE
CREATE TABLE dbo.software
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombreSft VARCHAR(50) NOT NULL UNIQUE
);
GO

-- Tabla: AREAS ACADEMICAS
CREATE TABLE dbo.area
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);
GO

-- Tabla: CUATRIMESTRES
CREATE TABLE dbo.cuatrimestres
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    numeroCuatri VARCHAR(50) NOT NULL UNIQUE
);
GO

-- Tabla: CARRERAS (con relacion a area)
CREATE TABLE dbo.carreras
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL UNIQUE,
    area_id INT NOT NULL,
    CONSTRAINT FK_carreras_area 
        FOREIGN KEY (area_id) REFERENCES dbo.area(id)
);
GO

-- Tabla: USUARIOS
CREATE TABLE dbo.usuarios
(
    matricula INT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL,
    apellido_paterno VARCHAR(20) NOT NULL,
    apellido_materno VARCHAR(20) NOT NULL DEFAULT '',
    fecha_nacimiento DATE NULL,
    telefono VARCHAR(10) NULL,
    rol VARCHAR(30) NOT NULL,
    cuatrimestre VARCHAR(50) NULL,
    carrera VARCHAR(150) NULL,
    grupo VARCHAR(20) NULL,
    CONSTRAINT FK_usuarios_carrera
        FOREIGN KEY (carrera) REFERENCES dbo.carreras(nombre),
    CONSTRAINT FK_usuarios_cuatrimestre
        FOREIGN KEY (cuatrimestre) REFERENCES dbo.cuatrimestres(numeroCuatri)
);
GO

-- Tabla: EQUIPOS
CREATE TABLE dbo.equipos
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre_eqp VARCHAR(100) NOT NULL,
    nombreLab VARCHAR(20) NOT NULL,
    nombreSft VARCHAR(200) NOT NULL,
    CONSTRAINT FK_equipos_laboratorio
        FOREIGN KEY (nombreLab) REFERENCES dbo.laboratorio(nombreLab)
);
GO

-- Tabla: HORARIO
CREATE TABLE dbo.Horario
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombreLab VARCHAR(20) NOT NULL,
    dia_semana VARCHAR(20) NULL,
    hora_inicio DATETIME2 NULL,
    hora_fin DATETIME2 NULL,
    proposito VARCHAR(255) NULL,
    estado VARCHAR(20) NOT NULL,
    CONSTRAINT FK_horario_laboratorio
        FOREIGN KEY (nombreLab) REFERENCES dbo.laboratorio(nombreLab)
);
GO

-- Tabla: REGISTROS (entradas/salidas)
CREATE TABLE dbo.registros
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    hora_entrada DATETIME2 NOT NULL,
    hora_salida DATETIME2 NULL,
    proposito VARCHAR(255) NULL,
    laboratorio VARCHAR(20) NOT NULL,
    usuario_matricula INT NULL,
    equipo_id BIGINT NULL,
    incidencia BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_registros_usuarios 
        FOREIGN KEY (usuario_matricula) REFERENCES dbo.usuarios(matricula),
    CONSTRAINT FK_registros_equipos 
        FOREIGN KEY (equipo_id) REFERENCES dbo.equipos(id)
);
GO

INSERT INTO dbo.laboratorio
    (nombreLab)
VALUES
    ('LAB-01'),
    ('LAB-02'),
    ('LAB-03'),
    ('LAB-04'),
    ('LAB-05'),
    ('LAB-06'),
    ('LAB-07');
GO

INSERT INTO dbo.software
    (nombreSft)
VALUES
    ('adobe'),
    ('microsoft');
GO

-- AREAS ACADEMICAS
INSERT INTO dbo.area
    (nombre)
VALUES
    ('Economico Administrativo'),
    ('Tecnologias de la Informacion'),
    ('Industrial, Electrica y Electronica');
GO

-- CUATRIMESTRES
INSERT INTO dbo.cuatrimestres
    (numeroCuatri)
VALUES
    ('1 cuatrimestre'),
    ('2 cuatrimestre'),
    ('3 cuatrimestre'),
    ('4 cuatrimestre'),
    ('5 cuatrimestre'),
    ('6 cuatrimestre'),
    ('7 cuatrimestre'),
    ('8 cuatrimestre'),
    ('9 cuatrimestre'),
    ('10 cuatrimestre'),
    ('11 cuatrimestre'),
    ('12 cuatrimestre');
GO

-- CARRERAS (con area_id)
INSERT INTO dbo.carreras
    (nombre, area_id)
VALUES
    -- Area: Economico Administrativo (id=1)
    ('TSU en Administracion area Capital Humano', 1),
    ('TSU en Contaduria', 1),
    ('TSU en Desarrollo de Negocios area Mercadotecnia', 1),
    ('TSU en Turismo Area Hoteleria', 1),
    ('Licenciatura en Contaduria', 1),
    ('Licenciatura en Gestion del Capital Humano', 1),
    ('Licenciatura en Innovacion de Negocios y Mercadotecnia', 1),

    -- Area: Tecnologias de la Informacion (id=2)
    ('TSU en Desarrollo de software multiplataforma', 2),
    ('TSU en Diseno Digital area Animacion', 2),
    ('TSU en Entornos Virtuales y Negocios Digitales', 2),
    ('TSU en Infraestructura de Redes digitales', 2),
    ('Licenciatura en Diseno Digital y Produccion Audiovisual', 2),
    ('Ingenieria en Desarrollo y Gestion de Software', 2),
    ('Ingenieria en Entornos Virtuales y Negocios Digitales', 2),
    ('Ingenieria en Redes Inteligentes y Ciberseguridad', 2),

    -- Area: Industrial, Electrica y Electronica (id=3)
    ('TSU en Energias renovables area Calidad y Ahorro de Energia', 3),
    ('TSU en Mecatronica area Instalaciones Electricas Eficientes', 3),
    ('TSU en Procesos industriales area manufactura', 3),
    ('Ingenieria en Mecatronica', 3),
    ('Ingenieria en Tecnologias de la Produccion', 3);
GO

-- USUARIOS DE PRUEBA
INSERT INTO dbo.usuarios
    (matricula, nombre, apellido_paterno, apellido_materno, rol, telefono, grupo, cuatrimestre, carrera)
VALUES
    (1225100516, 'Jose', 'Lopez', 'Perez', 'ALUMNO', '1234567890', 'GTID343', '1 cuatrimestre',
        'TSU en Desarrollo de software multiplataforma'),
    (1225100517, 'Maria', 'Garcia', 'Lopez', 'ADMINISTRADOR', '0987654321', NULL, NULL, NULL),
    (1225100518, 'Carlos', 'Ruiz', 'Martinez', 'SUPERADMINISTRADOR', '1122334455', NULL, NULL, NULL),
    (1225100519, 'Ana', 'Gomez', 'Fernandez', 'OBSERVADOR', '5544332211', NULL, NULL, NULL);
GO