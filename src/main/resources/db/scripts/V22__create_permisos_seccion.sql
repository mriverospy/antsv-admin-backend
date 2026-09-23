--Nombre Script: V22__create_permisos_seccion
-- Script de carga de permisos y rol_permisos para el modulo Sección.

-- Insertar permisos para el módulo Sección
insert into permiso(nombre, descripcion) values('seccion:ver', 'Permiso para ver una Sección');
insert into permiso(nombre, descripcion) values('seccion:listar', 'Permiso para ver el módulo Secciones');
insert into permiso(nombre, descripcion) values('seccion:crear', 'Permiso para crear una nueva Sección');
insert into permiso(nombre, descripcion) values('seccion:editar', 'Permiso para editar una Sección');
insert into permiso(nombre, descripcion) values('seccion:eliminar', 'Permiso para eliminar una Sección');
insert into permiso(nombre, descripcion) values('seccion:obtenerTiposCatalogo', 'Permiso para obtener los tipos de catálogo');

-- Asignar permisos seccion:* al rol ADMINISTRADOR (id_rol = 1)
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
  'seccion:ver',
  'seccion:listar',
  'seccion:crear',
  'seccion:editar',
  'seccion:eliminar',
  'seccion:obtenerTiposCatalogo'
)
AND NOT EXISTS (
  SELECT 1
  FROM rol_permiso rp
  WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);
