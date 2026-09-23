-- Tabla para datos adicionales de organizaciones (pitch, viabilidad técnica, viabilidad económica)
CREATE TABLE organizacion_dato_adicional (
    id_organizacion_dato_adicional SERIAL PRIMARY KEY,
    id_organizacion INTEGER NOT NULL REFERENCES organizacion(id_organizacion) ON DELETE CASCADE,
    tipo_dato VARCHAR(50) NOT NULL, -- pitch, viabilidad_tecnica, viabilidad_economica
    contenido TEXT NOT NULL,
    estado BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar consultas
CREATE INDEX idx_organizacion_dato_adicional_organizacion ON organizacion_dato_adicional(id_organizacion);
CREATE INDEX idx_organizacion_dato_adicional_tipo ON organizacion_dato_adicional(tipo_dato);
CREATE INDEX idx_organizacion_dato_adicional_estado ON organizacion_dato_adicional(estado);

-- Insertar permisos para la gestión de datos adicionales de organizaciones
INSERT INTO permiso (nombre, descripcion) VALUES 
('organizacion:administrarDatosAdicionalesPitch', 'Permite administrar los datos adicionales (pitch y viabilidad) de las organizaciones'),
('organizacion:verDatosAdicionalesPitch', 'Permite ver los datos adicionales (pitch y viabilidad) de las organizaciones'),
('organizacion:crearDatosAdicionalesPitch', 'Permite crear nuevos datos adicionales (pitch y viabilidad) para organizaciones'),
('organizacion:editarDatosAdicionalesPitch', 'Permite editar los datos adicionales (pitch y viabilidad) de las organizaciones'),
('organizacion:eliminarDatosAdicionalesPitch', 'Permite eliminar los datos adicionales (pitch y viabilidad) de las organizaciones');

-- Asignar permisos al rol admin (id_rol = 1)
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'organizacion:administrarDatosAdicionalesPitch',
    'organizacion:verDatosAdicionalesPitch',
    'organizacion:crearDatosAdicionalesPitch',
    'organizacion:editarDatosAdicionalesPitch',
    'organizacion:eliminarDatosAdicionalesPitch'
);

