# Sistema de Control de Autobuses

Aplicacion de escritorio en **Java (Swing)** con **SQL Server** para gestionar conductores, autobuses, rutas y asignaciones. Proyecto final de ITLA.

**Autor:** Adael Araujo Pineda · Matricula 2025-1127

## Estado actual (entrega 3: implementacion inicial)

| Modulo | Estado |
|---|---|
| Login con roles (Administrador / Usuario Generico) | Funcionando |
| Gestion de usuarios (crear, cambiar rol, restablecer contrasena, desactivar) | Funcionando (solo Administrador) |
| Gestion de conductores (CRUD, busqueda, validaciones) | Funcionando (consulta para todos, edicion solo Administrador) |
| Bitacora de operaciones | Registro automatico (tabla y procedimiento listos) |
| Autobuses, rutas y asignaciones | Tablas creadas en la BD; interfaz pendiente |

## Arquitectura

```
src/main/java/com/itla/autobuses
├── Main.java          Punto de entrada
├── ui/                Capa de presentacion (Swing): LoginFrame, MainFrame, paneles
├── service/           Capa de logica de negocio: reglas, validaciones y permisos
├── dao/               Capa de datos: DAOs con CallableStatement (solo procedimientos almacenados)
├── model/             Entidades: Usuario, Rol, Conductor
└── util/              ConexionDB (Singleton), Sesion (Singleton), Seguridad (hash), NegocioException
database/script.sql    Tablas, datos iniciales y procedimientos almacenados
```

Patrones: **Singleton** (conexion y sesion), **DAO**, capa de **servicios**.
Seguridad: contrasenas con PBKDF2-HMAC-SHA256 + salt aleatorio; permisos validados en la capa de negocio; sin SQL en la interfaz.

## Requisitos

- JDK 17 o superior
- Maven 3.8+
- SQL Server (Express o Developer) con conexiones TCP/IP habilitadas en el puerto 1433

## Instalacion y ejecucion

1. **Base de datos:** abre `database/script.sql` en SSMS o Azure Data Studio y ejecutalo completo. Crea la base `ControlAutobuses`, las tablas y los procedimientos almacenados.
2. **Conexion:** edita `src/main/resources/db.properties` con tu usuario y contrasena de SQL Server.
   (Alternativa sin recompilar: copia ese archivo junto al JAR y editalo alli.)
3. **Compilar:**
   ```
   mvn clean package
   ```
4. **Ejecutar:**
   ```
   java -jar target/control-autobuses-1.0.0.jar
   ```

En la primera ejecucion, la aplicacion crea el administrador inicial:

| Usuario | Contrasena |
|---|---|
| `admin` | `Admin123!` |

Cambiala desde el modulo Usuarios (Restablecer contrasena) despues de entrar.

## Rol de cada usuario

- **Administrador:** acceso completo (conductores, usuarios).
- **Usuario Generico:** solo consulta de conductores; no ve el modulo de usuarios.

## Subir el proyecto a GitHub

```
git init
git add .
git commit -m "feat: estructura en capas, login y modulo de conductores"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/control-autobuses.git
git push -u origin main
```

Haz commits pequenos y frecuentes (uno por funcionalidad) para que el historial muestre el avance.
