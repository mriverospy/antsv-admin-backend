-- ===================================================================
-- SCRIPT V109: MIGRAR tipo_recurso DE VARCHAR A FOREIGN KEY
-- ===================================================================
-- Este script migra el campo tipo_recurso de tipo_documento
-- de VARCHAR(50) a una relación de clave foránea con tipo_recurso
-- 
-- Estrategia:
-- 1. Validar que todos los valores existen en tipo_recurso
-- 2. Agregar columna id_tipo_recurso
-- 3. Migrar datos existentes
-- 4. Agregar constraints (NOT NULL, FK)
-- 5. Actualizar constraint UNIQUE
-- 6. Eliminar columna antigua tipo_recurso (VARCHAR)
-- 7. Actualizar índices
-- ===================================================================

-- ===================================================================
-- 1. VALIDACIÓN: Verificar que todos los valores existen
-- ===================================================================
-- Si esta query retorna filas, hay datos inconsistentes que deben corregirse
DO $$
DECLARE
    inconsistent_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO inconsistent_count
    FROM tipo_documento td
    WHERE NOT EXISTS (
        SELECT 1 FROM tipo_recurso tr 
        WHERE UPPER(TRIM(tr.nombre)) = UPPER(TRIM(td.tipo_recurso))
    );
    
    IF inconsistent_count > 0 THEN
        RAISE EXCEPTION 'Existen % registros en tipo_documento con tipo_recurso que no existe en tipo_recurso. Revise los datos antes de continuar.', inconsistent_count;
    END IF;
END $$;

-- ===================================================================
-- 2. AGREGAR COLUMNA id_tipo_recurso (TEMPORALMENTE NULL)
-- ===================================================================
ALTER TABLE public.tipo_documento 
ADD COLUMN IF NOT EXISTS id_tipo_recurso INTEGER;

-- ===================================================================
-- 3. MIGRAR DATOS EXISTENTES: Mapear VARCHAR a FK
-- ===================================================================
UPDATE public.tipo_documento td
SET id_tipo_recurso = (
    SELECT tr.id_tipo_recurso 
    FROM public.tipo_recurso tr 
    WHERE UPPER(TRIM(tr.nombre)) = UPPER(TRIM(td.tipo_recurso))
    AND tr.estado = true
    LIMIT 1
)
WHERE td.id_tipo_recurso IS NULL;

-- Validar que todos los registros tienen id_tipo_recurso
DO $$
DECLARE
    null_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO null_count
    FROM tipo_documento
    WHERE id_tipo_recurso IS NULL;
    
    IF null_count > 0 THEN
        RAISE EXCEPTION 'Existen % registros sin id_tipo_recurso asignado. Revise la migración.', null_count;
    END IF;
END $$;

-- ===================================================================
-- 4. AGREGAR CONSTRAINTS (NOT NULL Y FOREIGN KEY)
-- ===================================================================
-- Agregar NOT NULL
ALTER TABLE public.tipo_documento 
ALTER COLUMN id_tipo_recurso SET NOT NULL;

-- Eliminar constraint FK si existe (por si se ejecuta múltiples veces)
ALTER TABLE public.tipo_documento 
DROP CONSTRAINT IF EXISTS fk_tipo_documento_tipo_recurso;

-- Agregar Foreign Key
ALTER TABLE public.tipo_documento 
ADD CONSTRAINT fk_tipo_documento_tipo_recurso 
FOREIGN KEY (id_tipo_recurso) 
REFERENCES public.tipo_recurso(id_tipo_recurso) 
ON DELETE RESTRICT 
ON UPDATE CASCADE;

-- ===================================================================
-- 5. ACTUALIZAR CONSTRAINT UNIQUE
-- ===================================================================
-- Eliminar constraint antiguo
ALTER TABLE public.tipo_documento 
DROP CONSTRAINT IF EXISTS uk_tipo_documento_nombre_recurso;

-- Agregar nuevo constraint con id_tipo_recurso
ALTER TABLE public.tipo_documento 
ADD CONSTRAINT uk_tipo_documento_nombre_recurso 
UNIQUE (nombre, id_tipo_recurso);

-- ===================================================================
-- 6. ELIMINAR COLUMNA ANTIGUA tipo_recurso (VARCHAR)
-- ===================================================================
ALTER TABLE public.tipo_documento 
DROP COLUMN IF EXISTS tipo_recurso;

-- ===================================================================
-- 7. ACTUALIZAR ÍNDICES
-- ===================================================================
-- Eliminar índice antiguo
DROP INDEX IF EXISTS idx_tipo_documento_tipo_recurso;

-- Crear nuevo índice con id_tipo_recurso
CREATE INDEX IF NOT EXISTS idx_tipo_documento_id_tipo_recurso 
ON public.tipo_documento(id_tipo_recurso);

-- ===================================================================
-- 8. ACTUALIZAR COMENTARIOS
-- ===================================================================
COMMENT ON COLUMN public.tipo_documento.id_tipo_recurso IS 'Referencia al tipo de recurso asociado (FK a tipo_recurso)';

