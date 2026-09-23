-- ===================================================================
-- SCRIPT V111: AGREGAR CAMPO TIPO_ARCHIVO A TABLA PAGINA
-- ===================================================================
-- Este script agrega el campo tipo_archivo a la tabla pagina para
-- identificar si el archivo asociado es una IMAGEN o un VIDEO.
-- ===================================================================

-- Agregar columna tipo_archivo a la tabla pagina
ALTER TABLE public.pagina 
ADD COLUMN IF NOT EXISTS tipo_archivo VARCHAR(20);

-- Comentario para la columna
COMMENT ON COLUMN public.pagina.tipo_archivo IS 'Tipo de archivo asociado: IMAGEN o VIDEO';

-- Crear índice para mejorar búsquedas por tipo de archivo
CREATE INDEX IF NOT EXISTS idx_pagina_tipo_archivo ON public.pagina(tipo_archivo);

