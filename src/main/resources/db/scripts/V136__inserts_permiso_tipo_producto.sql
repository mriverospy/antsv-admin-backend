-- Script para permisos de Tipos de Producto
-- Nombre: V136__insert_permisos_tipo_producto.sql
-- Script para permisos de Tipos de Producto
-- Nombre: V136__insert_permisos_tipo_producto.sql

INSERT INTO permiso (nombre, descripcion)
SELECT v.nombre, v.descripcion
FROM (VALUES
    ('tipoProducto:ver', 'Permiso para ver el modulo tipo de productos'),
    ('tipoProducto:listar', 'Permiso para listar los tipos de producto'),
    ('tipoProducto:crear', 'Permiso para crear un nuevo tipo de producto'),
    ('tipoProducto:editar', 'Permiso para editar un tipo de producto'),
    ('tipoProducto:cambiarEstado', 'Permiso para cambiar el estado de un tipo de producto')
) AS v(nombre, descripcion)
WHERE NOT EXISTS (
    SELECT 1
    FROM permiso p
    WHERE p.nombre = v.nombre
);


-- Asignar los permisos al rol Administrador HTV
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT
    r.id_rol,
    p.id_permiso
FROM rol r
JOIN permiso p
    ON p.nombre IN (
        'tipoProducto:ver',
        'tipoProducto:listar',
        'tipoProducto:crear',
        'tipoProducto:editar',
        'tipoProducto:cambiarEstado'
    )
WHERE r.nombre = 'Administrador del Sistema'
AND NOT EXISTS (
    SELECT 1
    FROM rol_permiso rp
    WHERE rp.id_rol = r.id_rol
      AND rp.id_permiso = p.id_permiso
);