-- Tabla para contactos públicos de organizaciones
CREATE TABLE organizacion_contacto_publico (
    id_organizacion_contacto_publico SERIAL PRIMARY KEY,
    id_organizacion INTEGER NOT NULL REFERENCES organizacion(id_organizacion) ON DELETE CASCADE,
    tipo_contacto VARCHAR(50) NOT NULL,  -- telefono, email, direccion, horario_atencion, whatsapp, telegram, facebook, instagram, linkedin, twitter, youtube, tiktok, web
    valor_contacto VARCHAR(255) NOT NULL,
    visible_publico BOOLEAN DEFAULT TRUE,
    estado BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar consultas
CREATE INDEX idx_organizacion_contacto_publico_organizacion ON organizacion_contacto_publico(id_organizacion);
CREATE INDEX idx_organizacion_contacto_publico_tipo ON organizacion_contacto_publico(tipo_contacto);
CREATE INDEX idx_organizacion_contacto_publico_estado ON organizacion_contacto_publico(estado);
CREATE INDEX idx_organizacion_contacto_publico_visible ON organizacion_contacto_publico(visible_publico);

-- Comentarios en la tabla y columnas
COMMENT ON TABLE organizacion_contacto_publico IS 'Tabla para almacenar los contactos públicos de las organizaciones';
COMMENT ON COLUMN organizacion_contacto_publico.id_organizacion_contacto_publico IS 'Identificador único del contacto público de la organización';
COMMENT ON COLUMN organizacion_contacto_publico.id_organizacion IS 'Identificador de la organización propietaria del contacto';
COMMENT ON COLUMN organizacion_contacto_publico.tipo_contacto IS 'Tipo de contacto (telefono, email, direccion, horario_atencion, whatsapp, telegram, facebook, instagram, linkedin, twitter, youtube, tiktok, web)';
COMMENT ON COLUMN organizacion_contacto_publico.valor_contacto IS 'Valor del contacto (número, email, dirección, etc.)';
COMMENT ON COLUMN organizacion_contacto_publico.visible_publico IS 'Indica si el contacto es visible públicamente';
COMMENT ON COLUMN organizacion_contacto_publico.estado IS 'Estado del contacto (activo/inactivo)';
COMMENT ON COLUMN organizacion_contacto_publico.fecha_creacion IS 'Fecha de creación del registro';

-- Insertar permisos para la gestión de contactos públicos de organizaciones
INSERT INTO permiso (nombre, descripcion) VALUES 
('organizacion:administrarContactosPublicos', 'Permite administrar los contactos públicos de las organizaciones'),
('organizacion:verContactosPublicos', 'Permite ver los contactos públicos de las organizaciones'),
('organizacion:crearContactosPublicos', 'Permite crear nuevos contactos públicos para organizaciones'),
('organizacion:editarContactosPublicos', 'Permite editar los contactos públicos de las organizaciones'),
('organizacion:eliminarContactosPublicos', 'Permite eliminar los contactos públicos de las organizaciones'),
('organizacion:gestionarVisibilidadContactos', 'Permite gestionar la visibilidad pública de los contactos');

-- Asignar permisos al rol admin (id_rol = 1)
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'organizacion:administrarContactosPublicos',
    'organizacion:verContactosPublicos',
    'organizacion:crearContactosPublicos',
    'organizacion:editarContactosPublicos',
    'organizacion:eliminarContactosPublicos',
    'organizacion:gestionarVisibilidadContactos'
);
