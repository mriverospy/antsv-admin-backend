--V124__add_id_organizacion_to_programa_mentor_sesion.sql
--Script para agregar id_organizacion a la tabla programa_mentor_sesion

-- Agregar columna id_organizacion
ALTER TABLE public.programa_mentor_sesion
ADD COLUMN IF NOT EXISTS id_organizacion INTEGER NULL;

-- Agregar foreign key constraint
ALTER TABLE public.programa_mentor_sesion
ADD CONSTRAINT fk_sesion_organizacion
    FOREIGN KEY (id_organizacion)
    REFERENCES public.organizacion(id_organizacion);

-- Crear índice para mejorar performance
CREATE INDEX IF NOT EXISTS idx_sesion_organizacion
ON public.programa_mentor_sesion(id_organizacion);

-- Comentario en la columna
COMMENT ON COLUMN public.programa_mentor_sesion.id_organizacion IS 'Organización beneficiada de la sesión de mentoría';

