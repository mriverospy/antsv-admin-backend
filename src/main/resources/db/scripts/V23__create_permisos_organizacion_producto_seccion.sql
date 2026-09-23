--Nombre Script: V23__create_permisos_organizacion_producto_seccion
-- Script de carga de permisos y rol_permisos para el módulo Contenido - Productos y Servicios.

-- Insertar permisos para el módulo Contenido - Productos y Servicios
insert into permiso(nombre, descripcion) values('organizacion-producto-seccion:listar', 'Permiso para listar contenidos de productos y servicios');
insert into permiso(nombre, descripcion) values('organizacion-producto-seccion:crear', 'Permiso para crear un nuevo contenido de producto/servicio');
insert into permiso(nombre, descripcion) values('organizacion-producto-seccion:editar', 'Permiso para editar un contenido de producto/servicio');
insert into permiso(nombre, descripcion) values('organizacion-producto-seccion:eliminar', 'Permiso para activar/inactivar un contenido de producto/servicio');
insert into permiso(nombre, descripcion) values('organizacion-producto-seccion:ver', 'Permiso para ver un contenido de producto/servicio');

-- Asignar permisos organizacion-producto-seccion:* al rol ADMINISTRADOR (id_rol = 1)
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
  'organizacion-producto-seccion:listar',
  'organizacion-producto-seccion:crear',
  'organizacion-producto-seccion:editar',
  'organizacion-producto-seccion:eliminar',
  'organizacion-producto-seccion:ver'
)
AND NOT EXISTS (
  SELECT 1
  FROM rol_permiso rp
  WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);
