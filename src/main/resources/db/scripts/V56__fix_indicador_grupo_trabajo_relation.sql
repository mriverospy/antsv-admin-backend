-- Agregar la columna id_grupo_trabajo manteniendo id_recurso
-- Agregar la nueva columna id_grupo_trabajo
ALTER TABLE indicador ADD COLUMN id_grupo_trabajo INTEGER;

-- Agregar la restricción de clave foránea que apunta a grupo_trabajo
ALTER TABLE indicador ADD CONSTRAINT indicador_id_grupo_trabajo_fkey 
    FOREIGN KEY (id_grupo_trabajo) REFERENCES grupo_trabajo(id_grupo_trabajo) ON DELETE SET NULL;

-- Crear índice para la nueva columna
CREATE INDEX idx_indicador_grupo_trabajo ON indicador(id_grupo_trabajo);

-- Actualizar comentarios
COMMENT ON COLUMN indicador.id_recurso IS 'Referencia al recurso asociado';
COMMENT ON COLUMN indicador.id_grupo_trabajo IS 'Referencia al grupo de trabajo asociado';





