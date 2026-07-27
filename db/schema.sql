
IF DB_ID('cgti') IS NULL 
    CREATE DATABASE cgti;
GO

USE cgti;
GO

PRINT 'Eliminando tablas existentes...';
GO

IF OBJECT_ID('dbo.registros', 'U') IS NOT NULL DROP TABLE dbo.registros;
IF OBJECT_ID('dbo.apartados', 'U') IS NOT NULL DROP TABLE dbo.apartados;
IF OBJECT_ID('dbo.equipos', 'U') IS NOT NULL DROP TABLE dbo.equipos;
IF OBJECT_ID('dbo.usuarios', 'U') IS NOT NULL DROP TABLE dbo.usuarios;
GO


CREATE TABLE dbo.usuarios
(
    matricula INT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(100) NOT NULL,
    apellido_materno VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NULL,
    telefono VARCHAR(20) NULL,
    rol VARCHAR(30) NOT NULL,
    cuatrimestre VARCHAR(50) NOT NULL,
    carrera VARCHAR(100) NOT NULL,
    grupo VARCHAR(20) NOT NULL
);
GO

CREATE TABLE dbo.equipos
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre_eqp VARCHAR(100) NOT NULL,
    laboratorio VARCHAR(20) NOT NULL,
    software VARCHAR(255) NULL
);
GO

CREATE TABLE dbo.apartados
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    usuario_matricula INT NOT NULL,
    laboratorio VARCHAR(20) NOT NULL,
    equipo_id BIGINT NULL,
    dia_semana VARCHAR(20) NULL,
    hora_inicio DATETIME2 NULL,
    hora_fin DATETIME2 NULL,
    proposito VARCHAR(255) NULL,
    estado VARCHAR(20) NOT NULL,
    CONSTRAINT FK_apartados_usuarios 
        FOREIGN KEY (usuario_matricula) REFERENCES dbo.usuarios(matricula),
    CONSTRAINT FK_apartados_equipos 
        FOREIGN KEY (equipo_id) REFERENCES dbo.equipos(id)
);
GO

CREATE TABLE dbo.registros
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    hora_entrada DATETIME2 NOT NULL,
    hora_salida DATETIME2 NULL,
    proposito VARCHAR(255) NULL,
    laboratorio VARCHAR(20) NOT NULL,
    usuario_matricula INT NOT NULL,
    equipo_id BIGINT NULL,
    apartado_id BIGINT NULL,
    incidencia BIT NOT NULL DEFAULT 0,
    nota_incidencia VARCHAR(255) NULL,
    CONSTRAINT FK_registros_usuarios 
        FOREIGN KEY (usuario_matricula) REFERENCES dbo.usuarios(matricula),
    CONSTRAINT FK_registros_equipos 
        FOREIGN KEY (equipo_id) REFERENCES dbo.equipos(id),
    CONSTRAINT FK_registros_apartados 
        FOREIGN KEY (apartado_id) REFERENCES dbo.apartados(id)
);
GO

INSERT INTO dbo.usuarios
    (matricula, nombre, apellido_paterno, apellido_materno, rol, telefono,grupo,cuatrimestre,carrera)
VALUES
    (1225100516, 'Jose', 'Lopez', 'Perez', 'ALUMNO', '1234567890', 'GTID343', '1', 'TSU'),
    (1225100517, 'Maria', 'Garcia', 'Lopez', 'ADMINISTRADOR', '0987654321', '0', '0', 'O'),
    (1225100518, 'Carlos', 'Ruiz', 'Martinez', 'SUPERADMINISTRADOR', '1122334455', '0', '0', 'O'),
    (1225100519, 'Ana', 'Gomez', 'Fernandez', 'OBSERVADOR', '5544332211', '0', '0', '0');
GO


