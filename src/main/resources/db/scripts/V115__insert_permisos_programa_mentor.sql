--V114__insert_permisos_programa_mentor.sql

-- Insertar permisos para programa_mentor
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor:ver', 'Permiso para ver programa mentor');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor:listar', 'Permiso para listar mentores de un programas');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor:crear', 'Permiso para crear un programa mentor');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor:editar', 'Permiso para editar mentor de un programa');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor:actualizarEstado', 'Permiso para actualizar el estado de una programa mentor');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor:obtenerMentores', 'Permiso para obtener mentores');

-- Asignar permisos al rol administrador organizacion
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 6, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'programa-mentor:ver',
    'programa-mentor:listar',
    'programa-mentor:crear',
    'programa-mentor:editar',
    'programa-mentor:actualizarEstado',
    'programa-mentor:obtenerMentores'
)
AND NOT EXISTS (
    SELECT 6 FROM rol_permiso rp
    WHERE rp.id_rol = 6
    AND rp.id_permiso = p.id_permiso
);
