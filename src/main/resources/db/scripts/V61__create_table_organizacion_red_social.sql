-- Tabla para redes sociales de organizaciones
CREATE TABLE organizacion_red_social (
    id_organizacion_red_social SERIAL PRIMARY KEY,
    id_organizacion INTEGER NOT NULL REFERENCES organizacion(id_organizacion) ON DELETE CASCADE,
    tipo_red_social VARCHAR(50) NOT NULL,  -- facebook, instagram, linkedin, twitter, youtube
    url_red_social VARCHAR(255) NOT NULL,
    estado BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar consultas
CREATE INDEX idx_organizacion_red_social_organizacion ON organizacion_red_social(id_organizacion);
CREATE INDEX idx_organizacion_red_social_tipo ON organizacion_red_social(tipo_red_social);
CREATE INDEX idx_organizacion_red_social_estado ON organizacion_red_social(estado);

-- Comentarios en la tabla y columnas
COMMENT ON TABLE organizacion_red_social IS 'Tabla para almacenar las redes sociales de las organizaciones';
COMMENT ON COLUMN organizacion_red_social.id_organizacion_red_social IS 'Identificador único de la red social de la organización';
COMMENT ON COLUMN organizacion_red_social.id_organizacion IS 'Identificador de la organización propietaria de la red social';
COMMENT ON COLUMN organizacion_red_social.tipo_red_social IS 'Tipo de red social (facebook, instagram, linkedin, twitter, youtube)';
COMMENT ON COLUMN organizacion_red_social.url_red_social IS 'URL de la red social';
COMMENT ON COLUMN organizacion_red_social.estado IS 'Estado de la red social (activo/inactivo)';
COMMENT ON COLUMN organizacion_red_social.fecha_creacion IS 'Fecha de creación del registro';

-- Insertar permisos para la gestión de redes sociales de organizaciones
INSERT INTO permiso (nombre, descripcion) VALUES 
('organizacion:administrarDatosAdicionales', 'Permite administrar datos adicionales de organizaciones (contenido y redes sociales)'),
('organizacion:administrarRedesSociales', 'Permite administrar las redes sociales de las organizaciones'),
('organizacion:verRedesSociales', 'Permite ver las redes sociales de las organizaciones'),
('organizacion:crearRedesSociales', 'Permite crear nuevas redes sociales para organizaciones'),
('organizacion:editarRedesSociales', 'Permite editar las redes sociales de las organizaciones'),
('organizacion:eliminarRedesSociales', 'Permite eliminar las redes sociales de las organizaciones');

-- Asignar permisos al rol admin (id_rol = 1)
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'organizacion:administrarDatosAdicionales',
    'organizacion:administrarRedesSociales',
    'organizacion:verRedesSociales',
    'organizacion:crearRedesSociales',
    'organizacion:editarRedesSociales',
    'organizacion:eliminarRedesSociales'
);
