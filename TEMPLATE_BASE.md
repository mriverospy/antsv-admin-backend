# Backend base

Se conservan 110 archivos Java de los 478 originales, siguiendo los endpoints utilizados por `htv-admin-frontend` y sus dependencias internas.

## Funcionalidades conservadas

- Autenticación, identidad electrónica, sesiones Redis y recuperación de contraseña por correo.
- Usuarios, roles, permisos, perfil y aprobación de usuarios.
- Auditoría, notificaciones, métodos de registro y tipos de documento.
- Archivos y almacenamiento MongoDB.
- Organizaciones y sus dependencias de modelo/DTO necesarias para la sesión y la asignación de usuarios. Su ABM fue retirado.
- `/organizacion/download/{id}` como compatibilidad con el componente compartido de archivos.

Se retiraron los controladores y las clases sin dependencias desde estas funcionalidades: cursos, eventos, mentorías, programas, postulaciones, indicadores, productos, clientes, portal, registro público y reportes. La aprobación de usuarios ya no crea mentores ni relaciones de mentoría.

También se retiraron JasperReports, sus fuentes, Apache POI, Java EE API, los diez archivos de reportes y la imagen exclusiva de reportes. Se conservan las dos imágenes que utiliza MailService.

Los enums y modelos utilizados indirectamente por los contratos conservados permanecen, incluidos los tipos de recurso de archivos. La limpieza no implica eliminar todos los nombres del dominio anterior.

## Base de datos y configuración

Las migraciones históricas SQL permanecen intactas. La limpieza inicial de código no modificó tablas. Posteriormente se limpió la base local `antsv` mediante el script manual indicado más abajo. Una instalación nueva que utilice esas migraciones seguirá creando el esquema histórico; preparar un esquema inicial mínimo es un trabajo separado.

La aplicación conserva sus dependencias de PostgreSQL, MongoDB, Redis, SMTP e identidad electrónica. Personalizar sus configuraciones y la identidad visual para la nueva aplicación. Se respetaron los cambios locales preexistentes de `application-dev.properties`.

## Verificación

```sh
mvn -DskipTests clean compile
mvn -Dtest=TemplateBackendTests test
```

Las pruebas comprueban el mapeo de entidades, la resolución de consultas JPQL, los controladores conservados, la compatibilidad de descarga y las imágenes del correo, sin conexión a bases de datos. No ejecutan SQL nativo ni verifican servicios externos. La prueba existente `HtvBackendApplicationTests` requiere el entorno completo y no se ejecutó en esta limpieza.

Se eliminaron `SeccionContenido` y `TipoSeccionContenido`, junto con la relación JPA desde `Organizacion`. Se conserva el identificador escalar `idSeccionContenido` de solo lectura y su campo DTO por compatibilidad con el contrato existente.

## Limpieza aplicada a la base local antsv

Se ejecutó `maintenance/antsv-remove-unused-tables.sql` exclusivamente en `localhost:5432/antsv`: se eliminaron 75 tablas del dominio anterior con 167 registros en total y se conservaron 20 tablas, incluido `flyway_schema_history`. Los conteos de filas de las tablas conservadas no cambiaron.

El script elimina explícitamente cuatro claves foráneas obsoletas en usuario/organizacion y los triggers y funciones del catálogo retirado. Conserva las columnas escalares del contrato actual. Utiliza una transacción, tiempo límite de bloqueo y RESTRICT; se validó primero con ROLLBACK. Todas las secuencias referenciadas por JPA siguen existiendo.

El respaldo completo anterior al borrado está fuera de los repositorios, en `../.local-backups/antsv-20260923-161948/antsv.dump`, con permisos privados. `manifest.json` registra las tablas conservadas, eliminadas y sus conteos. Se verificó la lectura completa del respaldo con PostgreSQL 16. Para recuperar, restaurar preferentemente en una base vacía con `/usr/lib/postgresql/16/bin/pg_restore`; no importar encima de la base activa sin preparar la recuperación.

Este script manual no es una migración automática y no debe volver a ejecutarse en la base ya limpiada. El historial de Flyway y las migraciones originales siguen intactos: una instalación desde cero aún crearía el esquema anterior. Esta operación no limpió registros de permisos/roles ni archivos MongoDB.

Tras la eliminación manual de la tabla `institucion` y su columna en `organizacion`, se retiraron la relación JPA, `Institucion`, `InstitucionDTO` y las referencias del frontend. El mapeo anterior utilizaba `id_institucion`. El script de limpieza anterior permanece como registro histórico, no debe reejecutarse.
