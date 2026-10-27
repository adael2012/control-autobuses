IF DB_ID('ControlAutobuses') IS NULL
    CREATE DATABASE ControlAutobuses;
GO
USE ControlAutobuses;
GO

IF OBJECT_ID('dbo.Rol') IS NULL
CREATE TABLE dbo.Rol (
    IdRol  INT IDENTITY(1,1) PRIMARY KEY,
    Nombre NVARCHAR(40) NOT NULL UNIQUE
);
GO
IF OBJECT_ID('dbo.Usuario') IS NULL
CREATE TABLE dbo.Usuario (
    IdUsuario     INT IDENTITY(1,1) PRIMARY KEY,
    Username      NVARCHAR(30)  NOT NULL UNIQUE,
    PasswordHash  NVARCHAR(100) NOT NULL,
    Salt          NVARCHAR(50)  NOT NULL,
    IdRol         INT NOT NULL REFERENCES dbo.Rol(IdRol),
    Activo        BIT NOT NULL DEFAULT 1,
    FechaCreacion DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO
IF OBJECT_ID('dbo.Conductor') IS NULL
CREATE TABLE dbo.Conductor (
    IdConductor         INT IDENTITY(1,1) PRIMARY KEY,
    Cedula              NVARCHAR(11) NOT NULL UNIQUE,
    Nombre              NVARCHAR(60) NOT NULL,
    Apellido            NVARCHAR(60) NOT NULL,
    Telefono            NVARCHAR(15) NULL,
    NumeroLicencia      NVARCHAR(30) NOT NULL UNIQUE,
    VencimientoLicencia DATE NOT NULL,
    Activo              BIT NOT NULL DEFAULT 1
);
GO
IF OBJECT_ID('dbo.Autobus') IS NULL
CREATE TABLE dbo.Autobus (
    IdAutobus INT IDENTITY(1,1) PRIMARY KEY,
    Placa     NVARCHAR(10) NOT NULL UNIQUE,
    Marca     NVARCHAR(40) NOT NULL,
    Modelo    NVARCHAR(40) NOT NULL,
    Anio      INT NOT NULL,
    Capacidad INT NOT NULL CHECK (Capacidad > 0),
    Estado    NVARCHAR(15) NOT NULL DEFAULT 'Operativo'
              CHECK (Estado IN ('Operativo','Mantenimiento','Baja'))
);
GO
IF OBJECT_ID('dbo.Ruta') IS NULL
CREATE TABLE dbo.Ruta (
    IdRuta      INT IDENTITY(1,1) PRIMARY KEY,
    Codigo      NVARCHAR(10) NOT NULL UNIQUE,
    Origen      NVARCHAR(60) NOT NULL,
    Destino     NVARCHAR(60) NOT NULL,
    DistanciaKm DECIMAL(8,2) NOT NULL,
    DuracionMin INT NOT NULL,
    Activo      BIT NOT NULL DEFAULT 1
);
GO
IF OBJECT_ID('dbo.Asignacion') IS NULL
CREATE TABLE dbo.Asignacion (
    IdAsignacion      INT IDENTITY(1,1) PRIMARY KEY,
    IdConductor       INT NOT NULL REFERENCES dbo.Conductor(IdConductor),
    IdAutobus         INT NOT NULL REFERENCES dbo.Autobus(IdAutobus),
    IdRuta            INT NOT NULL REFERENCES dbo.Ruta(IdRuta),
    FechaInicio       DATETIME2 NOT NULL,
    FechaFin          DATETIME2 NOT NULL,
    Estado            NVARCHAR(15) NOT NULL DEFAULT 'Programada'
                      CHECK (Estado IN ('Programada','Completada','Cancelada')),
    IdUsuarioRegistro INT NOT NULL REFERENCES dbo.Usuario(IdUsuario),
    CONSTRAINT CK_Asignacion_Fechas CHECK (FechaFin > FechaInicio)
);
GO
IF OBJECT_ID('dbo.Bitacora') IS NULL
CREATE TABLE dbo.Bitacora (
    IdBitacora INT IDENTITY(1,1) PRIMARY KEY,
    IdUsuario  INT NOT NULL REFERENCES dbo.Usuario(IdUsuario),
    Accion     NVARCHAR(30) NOT NULL,
    Tabla      NVARCHAR(30) NOT NULL,
    IdRegistro INT NULL,
    Fecha      DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    Detalle    NVARCHAR(300) NULL
);
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Rol WHERE Nombre = N'Administrador')
    INSERT INTO dbo.Rol (Nombre) VALUES (N'Administrador');
IF NOT EXISTS (SELECT 1 FROM dbo.Rol WHERE Nombre = N'Usuario Generico')
    INSERT INTO dbo.Rol (Nombre) VALUES (N'Usuario Generico');
GO

CREATE OR ALTER PROCEDURE dbo.sp_Rol_Listar
AS
BEGIN
    SET NOCOUNT ON;
    SELECT IdRol, Nombre FROM dbo.Rol ORDER BY IdRol;
END
GO

CREATE OR ALTER PROCEDURE dbo.sp_Usuario_Contar
AS
BEGIN
    SET NOCOUNT ON;
    SELECT COUNT(*) AS Total FROM dbo.Usuario;
END
GO

CREATE OR ALTER PROCEDURE dbo.sp_Usuario_ObtenerPorUsername
    @Username NVARCHAR(30)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT u.IdUsuario, u.Username, u.PasswordHash, u.Salt, u.IdRol,
           r.Nombre AS RolNombre, u.Activo
    FROM dbo.Usuario u INNER JOIN dbo.Rol r ON r.IdRol = u.IdRol
    WHERE u.Username = @Username;
END
GO

CREATE OR ALTER PROCEDURE dbo.sp_Usuario_Listar
AS
BEGIN
    SET NOCOUNT ON;
    SELECT u.IdUsuario, u.Username, u.PasswordHash, u.Salt, u.IdRol,
           r.Nombre AS RolNombre, u.Activo
    FROM dbo.Usuario u INNER JOIN dbo.Rol r ON r.IdRol = u.IdRol
    ORDER BY u.Username;
END
GO

CREATE OR ALTER PROCEDURE dbo.sp_Usuario_Insertar
    @Username     NVARCHAR(30),
    @PasswordHash NVARCHAR(100),
    @Salt         NVARCHAR(50),
    @IdRol        INT
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO dbo.Usuario (Username, PasswordHash, Salt, IdRol)
    VALUES (@Username, @PasswordHash, @Salt, @IdRol);
    SELECT CAST(SCOPE_IDENTITY() AS INT) AS IdUsuario;
END
GO

CREATE OR ALTER PROCEDURE dbo.sp_Usuario_Actualizar
    @IdUsuario INT,
    @IdRol     INT,
    @Activo    BIT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.Usuario SET IdRol = @IdRol, Activo = @Activo
    WHERE IdUsuario = @IdUsuario;
END
GO

CREATE OR ALTER PROCEDURE dbo.sp_Usuario_CambiarPassword
    @IdUsuario    INT,
    @PasswordHash NVARCHAR(100),
    @Salt         NVARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.Usuario SET PasswordHash = @PasswordHash, Salt = @Salt
    WHERE IdUsuario = @IdUsuario;
END
GO

CREATE OR ALTER PROCEDURE dbo.sp_Usuario_Eliminar
    @IdUsuario INT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.Usuario SET Activo = 0 WHERE IdUsuario = @IdUsuario;
END
GO

CREATE OR ALTER PROCEDURE dbo.sp_Conductor_Listar
AS
BEGIN
    SET NOCOUNT ON;
    SELECT IdConductor, Cedula, Nombre, Apellido, Telefono,
           NumeroLicencia, VencimientoLicencia, Activo
    FROM dbo.Conductor ORDER BY Apellido, Nombre;
END
GO

CREATE OR ALTER PROCEDURE dbo.sp_Conductor_Buscar
    @Texto NVARCHAR(60)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT IdConductor, Cedula, Nombre, Apellido, Telefono,
           NumeroLicencia, VencimientoLicencia, Activo
    FROM dbo.Conductor
    WHERE Cedula LIKE '%' + @Texto + '%'
       OR Nombre LIKE '%' + @Texto + '%'
       OR Apellido LIKE '%' + @Texto + '%'
       OR NumeroLicencia LIKE '%' + @Texto + '%'
    ORDER BY Apellido, Nombre;
END
GO

CREATE OR ALTER PROCEDURE dbo.sp_Conductor_Insertar
    @Cedula NVARCHAR(11), @Nombre NVARCHAR(60), @Apellido NVARCHAR(60),
    @Telefono NVARCHAR(15), @NumeroLicencia NVARCHAR(30),
    @VencimientoLicencia DATE, @Activo BIT
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO dbo.Conductor
        (Cedula, Nombre, Apellido, Telefono, NumeroLicencia, VencimientoLicencia, Activo)
    VALUES
        (@Cedula, @Nombre, @Apellido, @Telefono, @NumeroLicencia, @VencimientoLicencia, @Activo);
    SELECT CAST(SCOPE_IDENTITY() AS INT) AS IdConductor;
END
GO

CREATE OR ALTER PROCEDURE dbo.sp_Conductor_Actualizar
    @IdConductor INT, @Cedula NVARCHAR(11), @Nombre NVARCHAR(60),
    @Apellido NVARCHAR(60), @Telefono NVARCHAR(15), @NumeroLicencia NVARCHAR(30),
    @VencimientoLicencia DATE, @Activo BIT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.Conductor
    SET Cedula = @Cedula, Nombre = @Nombre, Apellido = @Apellido,
        Telefono = @Telefono, NumeroLicencia = @NumeroLicencia,
        VencimientoLicencia = @VencimientoLicencia, Activo = @Activo
    WHERE IdConductor = @IdConductor;
END
GO

CREATE OR ALTER PROCEDURE dbo.sp_Conductor_Eliminar
    @IdConductor INT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.Conductor SET Activo = 0 WHERE IdConductor = @IdConductor;
END
GO

CREATE OR ALTER PROCEDURE dbo.sp_Bitacora_Insertar
    @IdUsuario INT, @Accion NVARCHAR(30), @Tabla NVARCHAR(30),
    @IdRegistro INT, @Detalle NVARCHAR(300)
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO dbo.Bitacora (IdUsuario, Accion, Tabla, IdRegistro, Detalle)
    VALUES (@IdUsuario, @Accion, @Tabla, @IdRegistro, @Detalle);
END
GO
