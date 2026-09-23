-- Agregar columna asistencia a la tabla evento_inscripcion
ALTER TABLE public.evento_inscripcion 
ADD COLUMN IF NOT EXISTS asistencia BOOLEAN;

COMMENT ON COLUMN public.evento_inscripcion.asistencia IS 'Indica si el usuario asistió al evento';

