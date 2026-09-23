-- Crear tabla indicador
CREATE TABLE indicador (
    id_indicador SERIAL PRIMARY KEY,
    nombre VARCHAR(300) NOT NULL,
    descripcion TEXT,
    periodicidad VARCHAR(50),
    fuente_datos TEXT,
    id_tipo_indicador INTEGER REFERENCES tipo_indicador(id_tipo_indicador) ON DELETE SET NULL,
    id_recurso INTEGER REFERENCES recurso(id_recurso) ON DELETE CASCADE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

-- Crear tabla de relación muchos a muchos entre indicador y categoría
CREATE TABLE indicador_categoria (
    id_indicador_categoria SERIAL PRIMARY KEY,
    id_indicador INTEGER REFERENCES indicador(id_indicador) ON DELETE CASCADE,
    id_categoria_indicador INTEGER REFERENCES categoria_indicador(id_categoria_indicador) ON DELETE CASCADE,
    UNIQUE(id_indicador, id_categoria_indicador)
);

-- Crear índices para optimización
CREATE INDEX idx_indicador_nombre ON indicador(nombre);
CREATE INDEX idx_indicador_estado ON indicador(estado);
CREATE INDEX idx_indicador_tipo ON indicador(id_tipo_indicador);
CREATE INDEX idx_indicador_recurso ON indicador(id_recurso);
CREATE INDEX idx_indicador_periodicidad ON indicador(periodicidad);
CREATE INDEX idx_indicador_fecha_creacion ON indicador(fecha_creacion);
CREATE INDEX idx_indicador_categoria_indicador ON indicador_categoria(id_indicador);
CREATE INDEX idx_indicador_categoria_categoria ON indicador_categoria(id_categoria_indicador);

-- Comentarios en las tablas
COMMENT ON TABLE indicador IS 'Tabla principal de indicadores del sistema';
COMMENT ON TABLE indicador_categoria IS 'Tabla de relación muchos a muchos entre indicadores y categorías';

-- Comentarios en las columnas
COMMENT ON COLUMN indicador.id_indicador IS 'Identificador único del indicador';
COMMENT ON COLUMN indicador.nombre IS 'Nombre del indicador';
COMMENT ON COLUMN indicador.descripcion IS 'Descripción detallada del indicador';
COMMENT ON COLUMN indicador.periodicidad IS 'Periodicidad del indicador (diario, semanal, mensual, etc.)';
COMMENT ON COLUMN indicador.fuente_datos IS 'Fuente de datos del indicador';
COMMENT ON COLUMN indicador.id_tipo_indicador IS 'Referencia al tipo de indicador';
COMMENT ON COLUMN indicador.id_recurso IS 'Referencia al recurso asociado';
COMMENT ON COLUMN indicador.fecha_creacion IS 'Fecha de creación del registro';
COMMENT ON COLUMN indicador.fecha_actualizacion IS 'Fecha de última actualización';
COMMENT ON COLUMN indicador.estado IS 'Estado del indicador (ACTIVO, INACTIVO)';

-- Insertar permisos para el módulo de Indicador
INSERT INTO permiso (nombre, descripcion) VALUES 
('indicador:ver', 'Ver detalles de indicadores'),
('indicador:listar', 'Listar indicadores'),
('indicador:crear', 'Crear nuevos indicadores'),
('indicador:editar', 'Editar indicadores existentes'),
('indicador:actualizarEstado', 'Cambiar estado de indicadores');

-- Asignar permisos al rol admin (asumiendo que el rol admin tiene id_rol = 1)
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'indicador:ver',
    'indicador:listar',
    'indicador:crear',
    'indicador:editar',
    'indicador:actualizarEstado'
)
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);
