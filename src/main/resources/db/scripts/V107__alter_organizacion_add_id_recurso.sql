-- Agregar columna id_recurso a la tabla organizacion
ALTER TABLE public.organizacion 
ADD COLUMN IF NOT EXISTS id_recurso BIGINT;

-- Eliminar constraint si existe (por si se ejecuta múltiples veces)
ALTER TABLE public.organizacion 
DROP CONSTRAINT IF EXISTS fk_organizacion_recurso;

-- Agregar foreign key constraint
ALTER TABLE public.organizacion 
ADD CONSTRAINT fk_organizacion_recurso 
FOREIGN KEY (id_recurso) 
REFERENCES public.recurso(id_recurso) 
ON DELETE SET NULL;

-- Eliminar índice si existe (por si se ejecuta múltiples veces)
DROP INDEX IF EXISTS idx_organizacion_id_recurso;

-- Crear índice para mejorar performance
CREATE INDEX idx_organizacion_id_recurso ON public.organizacion(id_recurso);

-- Agregar comentario a la columna
COMMENT ON COLUMN public.organizacion.id_recurso IS 'Referencia al recurso asociado para gestión de archivos';

