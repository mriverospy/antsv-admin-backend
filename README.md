# htv-admin-backend

Este proyecto es el backend del sistema HTV (Hub Tecnológico Virtual), desarrollado en Java con Spring Boot.
Incluye autenticación, servicios RESTful y gestión de base de datos mediante migraciones con Flyway.

---

## Cómo ejecutar el proyecto

Ejecuta el backend en modo desarrollo con el siguiente comando:

Windows
```bash
mvn clean spring-boot:run "-Dspring-boot.run.profiles=dev"
```
macOS
```bash
mvn clean spring-boot:run -Dspring-boot.run.profiles=dev
```

-Dspring-boot.run.profiles=dev: asegura que se cargue la configuración del perfil dev.


##  Migraciones con Flyway

Asegurate de tener configurado correctamente el archivo flyway.conf en src/main/resources/flyway.conf con los datos de conexión a la base de datos.

1. Inicializar Flyway si es la primera vez (opcional)

```bash
mvn flyway:baseline "-Dflyway.configFiles=src/main/resources/flyway.conf"
```
Este comando marca el estado actual de la base como el punto de partida para Flyway. Usar solo si estás migrando una base ya existente sin historial.

2. Ejecutar las migraciones
```bash
mvn clean package -DskipTests
mvn flyway:migrate "-Dflyway.configFiles=src/main/resources/flyway.conf"
```
Aplica los scripts SQL ubicados en la carpeta de migraciones (src/main/resources/db/migration) en orden de versión.

3. Reparar inconsistencias (opcional)
```bash
mvn clean package -DskipTests
mvn flyway:repair "-Dflyway.configFiles=src/main/resources/flyway.conf"
```
Repara la tabla de historial (flyway_schema_history), por ejemplo, si hubo errores o cambios en archivos ya aplicados (mismatch de checksum).
mvn -Dflyway.outOfOrder=true flyway:migrate  "-Dflyway.configFiles=src/main/resources/flyway.conf"

4. Limpieza
```bash
mvn clean package -DskipTests
mvn -Dflyway.cleanDisabled=false flyway:clean "-Dflyway.configFiles=src/main/resources/flyway.conf"
```

5. Si ya ejecutaste un archivo directamente en la bd pero de igual manera se necesita generar el history
```bash
mvn clean package -DskipTests
mvn flyway:baseline -Dflyway.baselineVersion=21 "-Dflyway.configFiles=src/main/resources/flyway.conf"
```






## Buenas prácticas
•	Nunca edites archivos SQL de migración ya ejecutados en producción.
•	Si necesitás cambiar la estructura de una tabla, creá un nuevo script V3__ajuste_tabla.sql.
•	Usá repair solo cuando estés seguro de que el cambio fue intencional y correcto.


## Estructura de carpetas relevante
```
src/
├── main/
│   ├── java/              → Código fuente Java
│   ├── resources/
│   │   ├── application.yml → Configuración por perfiles
│   │   ├── flyway.conf     → Configuración de Flyway
│   │   └── db/
│   │       └── migration/  → Scripts SQL de migración (V1__, V2__, ...)
```
