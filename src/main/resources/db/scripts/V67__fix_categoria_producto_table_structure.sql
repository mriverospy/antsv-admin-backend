-- Corregir la estructura de la tabla categoria_producto para que coincida con el modelo Java

-- Agregar las columnas de fecha que faltan
ALTER TABLE categoria_producto 
ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE categoria_producto 
ADD COLUMN IF NOT EXISTS fecha_actualizacion TIMESTAMP;

-- Cambiar el tamaño del campo nombre de 50 a 100 caracteres
ALTER TABLE categoria_producto 
ALTER COLUMN nombre TYPE VARCHAR(100);

-- Actualizar las columnas de fecha para registros existentes
UPDATE categoria_producto 
SET fecha_creacion = CURRENT_TIMESTAMP 
WHERE fecha_creacion IS NULL;

-- Agregar comentarios actualizados
COMMENT ON COLUMN categoria_producto.fecha_creacion IS 'Fecha de creación del registro';
COMMENT ON COLUMN categoria_producto.fecha_actualizacion IS 'Fecha de última actualización del registro';
