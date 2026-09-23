# Inicialización de ANTSV

- `scripts/V1__estructura_inicial_antsv.sql`: las 18 tablas utilizadas por las entidades actuales, relaciones, índices y secuencias. Las columnas escalares heredadas que aún exponen los modelos se conservan, sin recrear tablas de módulos retirados.
- `scripts/V2__datos_iniciales_antsv.sql`: rol ADMINISTRADOR, 56 permisos usados por el código actual, métodos de registro IE/MU, tipos de organización y tipos de recurso/documento. Los permisos se asignan por nombre, sin depender de identificadores preexistentes.

## Instalación nueva

Utilizar una base PostgreSQL vacía y configurar explícitamente la conexión de destino y `classpath:db/scripts` como ubicación de Flyway. Los archivos `flyway.conf` heredados apuntan a HTV; no usarlos sin adaptar la conexión.

Por ejemplo, desde la raíz del backend, con `FLYWAY_URL`, `FLYWAY_USER` y `FLYWAY_PASSWORD` definidos en el entorno para la nueva base:

```sh
mvn resources:resources flyway:migrate \
  -Dflyway.url="$FLYWAY_URL" \
  -Dflyway.user="$FLYWAY_USER" \
  -Dflyway.password="$FLYWAY_PASSWORD" \
  -Dflyway.locations=filesystem:src/main/resources/db/scripts \
  -Dflyway.baselineOnMigrate=false
```

Para ejecutar las migraciones mediante Spring Boot, establecer `spring.flyway.locations=classpath:db/scripts` y la conexión de la nueva base. La configuración de instalación debe usar `spring.jpa.hibernate.ddl-auto=validate` o `none` después de migrar.

No se crea una cuenta con credenciales predefinidas. El primer administrador debe provisionarse mediante el procedimiento de administración de la instalación y asociarse al rol ADMINISTRADOR.

## Bases existentes

Estos archivos reemplazan las 147 migraciones HTV; no son una actualización compatible con aquel historial. No aplicarlos sobre una base existente ni ejecutar `repair` para forzar la coincidencia. La adopción en una base con datos requiere un procedimiento separado de transición y respaldo. Los originales se conservan en el historial Git.

## Validación

Ambos scripts se ejecutaron consecutivamente y dentro de una transacción sobre PostgreSQL 16 temporal, sin conectar con la base operativa. La inicialización crea 18 tablas, 56 permisos y ningún usuario. También se ejecutó `mvn -o -Dtest=TemplateBackendTests clean test`.
