-- Insertar permiso solo si no existe
INSERT INTO permiso(nombre, descripcion) 
VALUES('organizacion:administrarContenido', 'Permiso para administrar contenido en el modulo de Organizacion')
ON CONFLICT (nombre) DO NOTHING;

-- Asignar permiso al rol admin solo si no existe
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre = 'organizacion:administrarContenido'
AND NOT EXISTS (
	SELECT 1 FROM rol_permiso rp
	WHERE rp.id_rol = 1
	AND rp.id_permiso = p.id_permiso 
);