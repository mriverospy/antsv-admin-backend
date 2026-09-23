-- Script para permisos de Menu.
-- Nombre: V137__inserts_permiso_menu.sql


INSERT INTO permiso(nombre, descripcion)
SELECT v.nombre, v.descripcion
FROM (VALUES
    ('mi-organizacion:verPanelGestion', 'Permiso para ver el menu de Panel de Gestión')
) AS v(nombre, descripcion)
WHERE NOT EXISTS (
    SELECT 1
    FROM permiso p
    WHERE p.nombre = v.nombre
);

-- Asignar el permiso al rol Administrador del Sistema
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM rol r
JOIN permiso p
    ON p.nombre = 'mi-organizacion:verPanelGestion'
WHERE r.nombre = 'Administrador del Sistema'
AND NOT EXISTS (
    SELECT 1
    FROM rol_permiso rp
    WHERE rp.id_rol = r.id_rol
      AND rp.id_permiso = p.id_permiso
);

-- Asignar el permiso al rol Administrador de Organización
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM rol r
JOIN permiso p
    ON p.nombre = 'mi-organizacion:verPanelGestion'
WHERE r.nombre = 'Administrador de Organización'
AND NOT EXISTS (
    SELECT 1
    FROM rol_permiso rp
    WHERE rp.id_rol = r.id_rol
      AND rp.id_permiso = p.id_permiso
);

