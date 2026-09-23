-- V119__add_estado_to_programa_mentor_etapa.sql
-- Agregar columna estado a la tabla programa_mentor_etapa

ALTER TABLE public.programa_mentor_etapa
ADD COLUMN IF NOT EXISTS estado BOOLEAN DEFAULT true;

-- Actualizar registros existentes para que tengan estado = true por defecto
UPDATE public.programa_mentor_etapa
SET estado = true
WHERE estado IS NULL;

