-- Crear tabla de categorías de productos
CREATE TABLE categoria_producto (
    id_categoria_producto SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    estado BOOLEAN DEFAULT true
);

-- Crear tabla de relación entre organizaciones, productos y categorías
CREATE TABLE organizacion_producto_categoria (
    id_organizacion_producto_categoria SERIAL PRIMARY KEY,
    id_organizacion_producto INTEGER NOT NULL REFERENCES organizacion_producto(id_organizacion_producto) ON DELETE CASCADE,
    id_categoria_producto INTEGER NOT NULL REFERENCES categoria_producto(id_categoria_producto) ON DELETE CASCADE,
    fecha_creacion DATE DEFAULT CURRENT_DATE,
    UNIQUE (id_organizacion_producto, id_categoria_producto)
);

-- Crear índices para optimizar consultas
CREATE INDEX idx_organizacion_producto_categoria_producto ON organizacion_producto_categoria(id_organizacion_producto);
CREATE INDEX idx_organizacion_producto_categoria_categoria ON organizacion_producto_categoria(id_categoria_producto);

-- Agregar comentarios descriptivos
COMMENT ON TABLE categoria_producto IS 'Catálogo de categorías de productos';
COMMENT ON COLUMN categoria_producto.id_categoria_producto IS 'Identificador único de la categoría de producto';
COMMENT ON COLUMN categoria_producto.nombre IS 'Nombre de la categoría de producto';
COMMENT ON COLUMN categoria_producto.estado IS 'Estado de la categoría (activo/inactivo)';

COMMENT ON TABLE organizacion_producto_categoria IS 'Relación entre organizaciones, productos y categorías';
COMMENT ON COLUMN organizacion_producto_categoria.id_organizacion_producto_categoria IS 'Identificador único de la relación';
COMMENT ON COLUMN organizacion_producto_categoria.id_organizacion_producto IS 'Identificador del producto de la organización';
COMMENT ON COLUMN organizacion_producto_categoria.id_categoria_producto IS 'Identificador de la categoría de producto';
COMMENT ON COLUMN organizacion_producto_categoria.fecha_creacion IS 'Fecha de creación de la relación';
