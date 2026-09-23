-- Crear tabla categoria_indicador
CREATE TABLE categoria_indicador (
    id_categoria_indicador SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    estado BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP
);

-- Crear índices
CREATE INDEX idx_categoria_indicador_nombre ON categoria_indicador(nombre);
CREATE INDEX idx_categoria_indicador_estado ON categoria_indicador(estado);

-- Insertar permisos
INSERT INTO permiso(nombre, descripcion) VALUES('categoria-indicador:ver', 'Permiso para ver una Categoría de Indicador');
INSERT INTO permiso(nombre, descripcion) VALUES('categoria-indicador:listar', 'Permiso para listar Categorías de Indicador');
INSERT INTO permiso(nombre, descripcion) VALUES('categoria-indicador:crear', 'Permiso para crear una Categoría de Indicador');
INSERT INTO permiso(nombre, descripcion) VALUES('categoria-indicador:editar', 'Permiso para editar una Categoría de Indicador');
INSERT INTO permiso(nombre, descripcion) VALUES('categoria-indicador:actualizarEstado', 'Permiso para actualizar el estado de una Categoría de Indicador');

-- Asignar permisos al rol admin
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'categoria-indicador:ver',
    'categoria-indicador:listar',
    'categoria-indicador:crear',
    'categoria-indicador:editar',
    'categoria-indicador:actualizarEstado'
)
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);
