-- ===================================================================
-- SCRIPT V122: CAMBIAR id_recurso POR referencia_archivo
-- ===================================================================
-- Este script cambia el campo id_recurso por referencia_archivo
-- para almacenar directamente el ObjectId de MongoDB GridFS
-- ===================================================================

-- Eliminar la constraint y columna id_recurso
ALTER TABLE public.programa_mentor_actividad_respuesta
DROP CONSTRAINT IF EXISTS fk_respuesta_recurso;

ALTER TABLE public.programa_mentor_actividad_respuesta
DROP COLUMN IF EXISTS id_recurso;

-- Agregar columna referencia_archivo
ALTER TABLE public.programa_mentor_actividad_respuesta
ADD COLUMN IF NOT EXISTS referencia_archivo VARCHAR;

-- Comentario para la columna
COMMENT ON COLUMN public.programa_mentor_actividad_respuesta.referencia_archivo IS 'ID del archivo en MongoDB GridFS (ObjectId como String)';

-- Crear índice para mejorar búsquedas por referencia_archivo
CREATE INDEX IF NOT EXISTS idx_actividad_respuesta_referencia_archivo ON public.programa_mentor_actividad_respuesta(referencia_archivo);

