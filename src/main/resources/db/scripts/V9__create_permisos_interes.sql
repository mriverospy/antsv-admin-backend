insert into permiso(nombre, descripcion) values ('interes:listar','Permiso para listar interes.');
insert into permiso(nombre, descripcion) values ('interes:crear','Permiso para crear interes.');
insert into permiso(nombre, descripcion) values ('interes:editar','Permiso para editar interes.');
insert into permiso(nombre, descripcion) values ('interes:borrar','Permiso para borrar interes.');
insert into permiso(nombre, descripcion) values ('interes:ver','Permiso para ver interess.');


-- Asignar permisos interes:* al rol ADMINISTRADOR (id_rol = 1)
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
  'interes:listar',
  'interes:crear',
  'interes:editar',
  'interes:borrar',
  'interes:ver'
)
AND NOT EXISTS (
  SELECT 1
  FROM rol_permiso rp
  WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);