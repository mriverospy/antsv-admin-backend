-- Insertar tipos de recurso basados en TipoRecursoEnum
INSERT INTO tipo_recurso (nombre, estado) VALUES 
    ('EVENTO', true),
    ('MENTOR', true),
    ('NOTICIA', true),
    ('CAPACITACION', true),
    ('INDICADOR', true),
    ('CURSO', true)
ON CONFLICT (nombre) DO NOTHING;

-- Comentarios para documentación
COMMENT ON TABLE tipo_recurso IS 'Tabla de tipos de recursos para el sistema de archivos';
COMMENT ON COLUMN tipo_recurso.nombre IS 'Nombre del tipo de recurso (EVENTO, MENTOR, NOTICIA, CAPACITACION, INDICADOR)';
COMMENT ON COLUMN tipo_recurso.estado IS 'Estado del tipo de recurso (activo/inactivo)';
