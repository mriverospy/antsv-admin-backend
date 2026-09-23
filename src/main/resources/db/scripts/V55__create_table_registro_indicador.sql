-- Crear tabla registro_indicador
CREATE TABLE registro_indicador (
    id_registro_indicador SERIAL PRIMARY KEY,
    id_indicador INTEGER REFERENCES indicador(id_indicador) ON DELETE CASCADE,
    registrado_por TEXT,
    fecha_registro DATE,
    periodo VARCHAR(50),
    dato_valor VARCHAR(50),
    observacion TEXT,
    id_recurso INTEGER REFERENCES recurso(id_recurso) ON DELETE CASCADE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    estado BOOLEAN DEFAULT true
);

-- Crear índices para optimización
CREATE INDEX idx_registro_indicador_indicador ON registro_indicador(id_indicador);
CREATE INDEX idx_registro_indicador_estado ON registro_indicador(estado);
CREATE INDEX idx_registro_indicador_fecha_registro ON registro_indicador(fecha_registro);
CREATE INDEX idx_registro_indicador_periodo ON registro_indicador(periodo);
CREATE INDEX idx_registro_indicador_recurso ON registro_indicador(id_recurso);
CREATE INDEX idx_registro_indicador_fecha_creacion ON registro_indicador(fecha_creacion);

-- Comentarios en la tabla
COMMENT ON TABLE registro_indicador IS 'Tabla de registros de datos para indicadores';

-- Comentarios en las columnas
COMMENT ON COLUMN registro_indicador.id_registro_indicador IS 'Identificador único del registro de indicador';
COMMENT ON COLUMN registro_indicador.id_indicador IS 'Referencia al indicador';
COMMENT ON COLUMN registro_indicador.registrado_por IS 'Persona o sistema que registró el dato';
COMMENT ON COLUMN registro_indicador.fecha_registro IS 'Fecha del registro del dato';
COMMENT ON COLUMN registro_indicador.periodo IS 'Periodo al que corresponde el dato';
COMMENT ON COLUMN registro_indicador.dato_valor IS 'Valor del dato registrado';
COMMENT ON COLUMN registro_indicador.observacion IS 'Observaciones adicionales del registro';
COMMENT ON COLUMN registro_indicador.id_recurso IS 'Referencia al recurso asociado';
COMMENT ON COLUMN registro_indicador.fecha_creacion IS 'Fecha de creación del registro';
COMMENT ON COLUMN registro_indicador.fecha_actualizacion IS 'Fecha de última actualización';
COMMENT ON COLUMN registro_indicador.estado IS 'Estado del registro (true=activo, false=inactivo)';

-- Insertar permisos para el módulo de Registro de Indicador
INSERT INTO permiso (nombre, descripcion) VALUES 
('registro-indicador:ver', 'Ver detalles de registros de indicadores'),
('registro-indicador:listar', 'Listar registros de indicadores'),
('registro-indicador:crear', 'Crear nuevos registros de indicadores'),
('registro-indicador:editar', 'Editar registros de indicadores existentes'),
('registro-indicador:actualizarEstado', 'Cambiar estado de registros de indicadores'),
('registro-indicador:eliminar', 'Eliminar registros de indicadores');

-- Asignar permisos al rol admin (asumiendo que el rol admin tiene id_rol = 1)
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'registro-indicador:ver',
    'registro-indicador:listar',
    'registro-indicador:crear',
    'registro-indicador:editar',
    'registro-indicador:actualizarEstado',
    'registro-indicador:eliminar'
)
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);
