--Se agrega este permiso para el usaurio administrador general.

INSERT INTO permiso (nombre,descripcion) VALUES
	('usuarios:editarClaveUsuarioAdmin','Permiso para cambio de credenciales de usuarios');

INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'usuarios:editarClaveUsuarioAdmin'
)
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);

