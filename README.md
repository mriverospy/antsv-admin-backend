# ANTSV — Backend

API de administración desarrollada con Java 21 y Spring Boot 3.4.1.

Incluye autenticación, identidad electrónica, recuperación de contraseña, usuarios, roles, permisos, perfil, aprobación de usuarios, auditoría, notificaciones y archivos. Mantiene las dependencias de organizaciones necesarias para la sesión y la asignación de usuarios.

## Requisitos

- JDK 21 y Maven.
- PostgreSQL para los datos de la aplicación.
- MongoDB para archivos y Redis para sesiones.
- Servicio SMTP para correo y configuración de identidad electrónica para ese método de acceso.

Ejecutar los comandos desde la raíz de este proyecto.

## Configuración

Revisar los archivos de `src/main/resources`:

| Archivo | Uso |
| --- | --- |
| `application.properties` | Configuración general |
| `application-dev.properties` | Desarrollo; API en `http://localhost:8085/api` |
| `application-prod.properties` | Producción; puerto configurado `8083` |
| `flyway.conf` | Configuración del plugin Maven de Flyway |

Adaptar las conexiones de PostgreSQL, MongoDB y Redis, los parámetros JWT, SMTP, identidad electrónica y las URL de acceso al frontend. El perfil `dev` apunta a la base local `antsv`; los otros archivos todavía contienen valores heredados de HTV. Configurar credenciales propias fuera del repositorio.

El perfil `dev` tiene `spring.jpa.hibernate.ddl-auto=update`. Para una instalación gestionada mediante migraciones, usar `validate` o `none` después de inicializar la base.

## Base de datos nueva

Seguir la [guía de inicialización](src/main/resources/db/README.md) antes de iniciar la aplicación. La carpeta `src/main/resources/db/scripts` contiene:

- `V1__estructura_inicial_antsv.sql`: 18 tablas, secuencias, índices y relaciones.
- `V2__datos_iniciales_antsv.sql`: catálogos, rol ADMINISTRADOR y 56 permisos utilizados por la aplicación.

Estos scripts son exclusivamente para una base vacía. Reemplazan el historial SQL de HTV y no deben aplicarse sobre bases que conserven aquel historial. No utilizar `repair` para forzar esa transición.

No se crea una cuenta con credenciales predeterminadas. La instalación debe provisionar el primer usuario administrador y asociarlo al rol ADMINISTRADOR.

Para ejecutar las migraciones desde Spring Boot, configurar explícitamente `spring.flyway.locations=classpath:db/scripts` y la conexión de destino. El archivo `flyway.conf` del plugin Maven debe revisarse por separado: todavía contiene una conexión heredada.

## Desarrollo

Con la base preparada y los servicios configurados:

```sh
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

El frontend local utiliza `http://localhost:8085/api` mediante su proxy de desarrollo.

## Compilación y pruebas

```sh
mvn -DskipTests clean package
mvn -Dtest=TemplateBackendTests test
```

El artefacto conserva el nombre definido en `pom.xml`: `target/htv-admin-backend.jar`.

```sh
java -jar target/htv-admin-backend.jar --spring.profiles.active=dev
```

`TemplateBackendTests` comprueba entidades, consultas JPQL, controladores conservados y recursos de correo sin conectarse a las bases. La prueba `HtvBackendApplicationTests` requiere el entorno completo. `-DskipTests` omite la ejecución de pruebas al empaquetar.

## Estructura

```text
src/main/java/                 Código de la API
src/main/resources/            Configuración y recursos
src/main/resources/db/scripts/ Inicialización SQL de ANTSV
src/test/java/                 Pruebas
maintenance/                   Scripts históricos de mantenimiento manual
```

Consultar [TEMPLATE_BASE.md](TEMPLATE_BASE.md) para el alcance de la limpieza y las dependencias conservadas. Los scripts de `maintenance` documentan operaciones anteriores y no forman parte de una instalación nueva.

### Registro mediante Identidad Electrónica

Al validar IE, un documento sin usuario genera una cuenta activa y aprobada, con el
documento como nombre de usuario y el método de registro IE. Se conservan nombres,
apellidos, correo, nacionalidad, teléfono, domicilio y fecha de nacimiento recibidos.
No se persisten el token ni sus metadatos como datos del perfil. Las cuentas existentes
conservan sus datos, roles y restricciones de acceso.

El rol se define en `ROLES.TRAMITANTE_ANTSV` (8) y se parametriza con
`ie.registro.rol-id` o la variable `IE_REGISTRO_ROL_ID`. Debe existir y estar activo,
con los permisos necesarios para acceder. El catálogo inicial sólo crea ADMINISTRADOR;
el rol Tramitante ANTSV y sus permisos deben existir en la instalación.
El parámetro es del servidor y no se acepta desde el cliente.

Aplicar `V3__fecha_nacimiento_usuario.sql` mediante el procedimiento de migración
correspondiente a la instalación antes de desplegar. La cuenta IE se crea sin
organización ni vencimiento, y con un hash de un secreto aleatorio no compartido,
porque IE no proporciona una contraseña local. Puede usar el flujo existente
de recuperación de contraseña si necesita habilitar ese acceso.
