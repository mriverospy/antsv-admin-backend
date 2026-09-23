--V121__insert_permisos_actividad_respuesta.sql
--Script de insercion de permisos para el modulo de respuestas de actividades

-- =====================================
-- PERMISOS PARA RESPUESTAS DE ACTIVIDADES
-- =====================================
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-actividad-respuesta:listar', 'Permiso para listar respuestas de actividades');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-actividad-respuesta:crear', 'Permiso para crear respuesta de actividad');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-actividad-respuesta:ver', 'Permiso para ver respuesta de actividad');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-actividad-respuesta:editar', 'Permiso para editar respuesta de actividad');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-actividad-respuesta:evaluar', 'Permiso para evaluar respuesta de actividad');

-- =====================================
-- ASIGNAR PERMISOS AL ROL ADMINISTRADOR (id_rol = 1)
-- =====================================
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'programa-mentor-actividad-respuesta:listar',
    'programa-mentor-actividad-respuesta:crear',
    'programa-mentor-actividad-respuesta:ver',
    'programa-mentor-actividad-respuesta:editar',
    'programa-mentor-actividad-respuesta:evaluar'
)
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);

-- =====================================
-- ASIGNAR PERMISOS AL ROL ADMINISTRADOR ORGANIZACION (id_rol = 6)
-- =====================================
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 6, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'programa-mentor-actividad-respuesta:listar',
    'programa-mentor-actividad-respuesta:crear',
    'programa-mentor-actividad-respuesta:ver',
    'programa-mentor-actividad-respuesta:editar',
    'programa-mentor-actividad-respuesta:evaluar'
)
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = 6
    AND rp.id_permiso = p.id_permiso
);

