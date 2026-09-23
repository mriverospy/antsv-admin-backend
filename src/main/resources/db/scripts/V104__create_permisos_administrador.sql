--Script para permisos de Administrador General y Administrador Organizacion

INSERT INTO permiso (nombre,descripcion) VALUES
	 ('usuarios:obtenerOrganizaciones','Permiso para obtener organizaciones de un usuario');

	
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
  'usuarios:obtenerOrganizaciones'
)
AND NOT EXISTS (
  SELECT 1
  FROM rol_permiso rp
  WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);

	
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 6, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
  'usuarios:obtenerOrganizaciones'
  'roles:listar'
)
AND NOT EXISTS (
  SELECT 6
  FROM rol_permiso rp
  WHERE rp.id_rol = 6
    AND rp.id_permiso = p.id_permiso
);
-- Fin script permisos Administrador General y Administrador Organizacion