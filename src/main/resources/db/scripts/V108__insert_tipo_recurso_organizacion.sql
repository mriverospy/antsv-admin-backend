-- Insertar tipo de recurso ORGANIZACION
-- Este script agrega el tipo de recurso ORGANIZACION que es necesario
-- para asociar archivos a las organizaciones
INSERT INTO tipo_recurso (nombre, estado) 
VALUES ('ORGANIZACION', true)
ON CONFLICT (nombre) DO UPDATE SET estado = true;

-- Actualizar comentario de la tabla para incluir ORGANIZACION
COMMENT ON COLUMN tipo_recurso.nombre IS 'Nombre del tipo de recurso (EVENTO, MENTOR, NOTICIA, CAPACITACION, INDICADOR, PROGRAMA, POSTULACION, COMITE, ORGANIZACION)';

