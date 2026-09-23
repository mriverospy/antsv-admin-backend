-- Insertar permisos para categoria_producto
INSERT INTO permiso(nombre, descripcion) VALUES('categoria-producto:ver', 'Permiso para ver una Categoría de Producto');
INSERT INTO permiso(nombre, descripcion) VALUES('categoria-producto:listar', 'Permiso para listar Categorías de Producto');
INSERT INTO permiso(nombre, descripcion) VALUES('categoria-producto:crear', 'Permiso para crear una Categoría de Producto');
INSERT INTO permiso(nombre, descripcion) VALUES('categoria-producto:editar', 'Permiso para editar una Categoría de Producto');
INSERT INTO permiso(nombre, descripcion) VALUES('categoria-producto:actualizarEstado', 'Permiso para actualizar el estado de una Categoría de Producto');

-- Asignar permisos al rol admin
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'categoria-producto:ver',
    'categoria-producto:listar',
    'categoria-producto:crear',
    'categoria-producto:editar',
    'categoria-producto:actualizarEstado'
)
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);
