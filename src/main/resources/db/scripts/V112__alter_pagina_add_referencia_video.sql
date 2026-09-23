-- ===================================================================
-- SCRIPT V112: AGREGAR CAMPO REFERENCIA_VIDEO Y MIGRAR DATOS EXISTENTES
-- ===================================================================
-- Este script agrega el campo referencia_video a la tabla pagina y
-- migra los datos existentes que tienen tipo_archivo = 'VIDEO' desde
-- referencia_imagen a referencia_video.
-- ===================================================================

-- Agregar columna referencia_video a la tabla pagina
ALTER TABLE public.pagina 
ADD COLUMN IF NOT EXISTS referencia_video VARCHAR;

-- Comentario para la columna
COMMENT ON COLUMN public.pagina.referencia_video IS 'ID del archivo de video en MongoDB GridFS';

-- Migrar datos existentes: si tipo_archivo = 'VIDEO', mover referencia_imagen a referencia_video
UPDATE public.pagina
SET referencia_video = referencia_imagen,
    referencia_imagen = NULL
WHERE tipo_archivo = 'VIDEO' 
  AND referencia_imagen IS NOT NULL 
  AND referencia_imagen != '';

-- Crear índice para mejorar búsquedas por referencia_video
CREATE INDEX IF NOT EXISTS idx_pagina_referencia_video ON public.pagina(referencia_video);

