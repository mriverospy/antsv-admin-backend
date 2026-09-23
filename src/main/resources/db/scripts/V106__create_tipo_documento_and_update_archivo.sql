-- ===================================================================
-- SCRIPT V105: CREACIÓN DE TIPO_DOCUMENTO Y ACTUALIZACIÓN DE ARCHIVO
-- ===================================================================
-- Este script crea la tabla tipo_documento (catálogo maestro) y
-- actualiza la tabla archivo para incluir FK a tipo_documento.
-- 
-- Estrategia:
-- 1. Crear tabla tipo_documento
-- 2. Crear tipos "Archivo" por defecto para cada tipo_recurso
-- 3. Agregar columna id_tipo_documento a archivo (NOT NULL)
-- 4. Migrar datos existentes asignando tipo "Archivo" por defecto
-- 5. Agregar constraint NOT NULL y FK
-- ===================================================================

-- ===================================================================
-- 1. CREAR TABLA TIPO_DOCUMENTO
-- ===================================================================
CREATE TABLE IF NOT EXISTS public.tipo_documento (
    id_tipo_documento SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo_recurso VARCHAR(50) NOT NULL,
    estado BOOLEAN DEFAULT true NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_tipo_documento_nombre_recurso UNIQUE (nombre, tipo_recurso)
);

-- Comentarios
COMMENT ON TABLE public.tipo_documento IS 'Catálogo maestro de tipos de documento asociados a recursos';
COMMENT ON COLUMN public.tipo_documento.nombre IS 'Nombre del tipo de documento (ej: Plan de Negocio, Pitch, Archivo)';
COMMENT ON COLUMN public.tipo_documento.descripcion IS 'Descripción opcional del tipo de documento';
COMMENT ON COLUMN public.tipo_documento.tipo_recurso IS 'Tipo de recurso asociado (EVENTO, INDICADOR, PROGRAMA, etc.)';
COMMENT ON COLUMN public.tipo_documento.estado IS 'Estado del tipo de documento (activo/inactivo)';

-- Índices
CREATE INDEX IF NOT EXISTS idx_tipo_documento_tipo_recurso ON public.tipo_documento(tipo_recurso);
CREATE INDEX IF NOT EXISTS idx_tipo_documento_estado ON public.tipo_documento(estado);

-- ===================================================================
-- 2. CREAR TIPOS "ARCHIVO" POR DEFECTO PARA CADA TIPO_RECURSO
-- ===================================================================
INSERT INTO public.tipo_documento (nombre, descripcion, tipo_recurso, estado)
SELECT 
    'Archivo' AS nombre,
    'Tipo de documento por defecto para ' || nombre AS descripcion,
    nombre AS tipo_recurso,
    true AS estado
FROM public.tipo_recurso
WHERE estado = true
ON CONFLICT (nombre, tipo_recurso) DO NOTHING;

-- ===================================================================
-- 3. AGREGAR COLUMNA id_tipo_documento A ARCHIVO (TEMPORALMENTE NULL)
-- ===================================================================
ALTER TABLE public.archivo 
ADD COLUMN IF NOT EXISTS id_tipo_documento INTEGER;

-- ===================================================================
-- 4. MIGRAR DATOS EXISTENTES: ASIGNAR TIPO "ARCHIVO" POR DEFECTO
-- ===================================================================
UPDATE public.archivo a
SET id_tipo_documento = (
    SELECT td.id_tipo_documento
    FROM public.tipo_documento td
    INNER JOIN public.recurso r ON r.id_recurso = a.id_recurso
    INNER JOIN public.tipo_recurso tr ON tr.id_tipo_recurso = r.id_tipo_recurso
    WHERE td.nombre = 'Archivo'
    AND td.tipo_recurso = tr.nombre
    AND td.estado = true
    LIMIT 1
)
WHERE a.id_tipo_documento IS NULL;

-- ===================================================================
-- 5. AGREGAR CONSTRAINT NOT NULL Y FOREIGN KEY
-- ===================================================================
-- Primero asegurar que todos los archivos tengan tipo_documento
UPDATE public.archivo
SET id_tipo_documento = (
    SELECT id_tipo_documento 
    FROM public.tipo_documento 
    WHERE nombre = 'Archivo' 
    AND tipo_recurso = 'EVENTO'  -- Fallback si no encuentra el tipo_recurso correcto
    LIMIT 1
)
WHERE id_tipo_documento IS NULL;

-- Ahora agregar NOT NULL
ALTER TABLE public.archivo
ALTER COLUMN id_tipo_documento SET NOT NULL;

-- Agregar Foreign Key
ALTER TABLE public.archivo
ADD CONSTRAINT fk_archivo_tipo_documento
    FOREIGN KEY (id_tipo_documento)
    REFERENCES public.tipo_documento(id_tipo_documento)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- Índice para performance
CREATE INDEX IF NOT EXISTS idx_archivo_tipo_documento ON public.archivo(id_tipo_documento);

-- ===================================================================
-- 6. INSERTAR PERMISOS PARA EL MÓDULO TIPO_DOCUMENTO
-- ===================================================================
INSERT INTO public.permiso (nombre, descripcion)
SELECT nombre, descripcion
FROM (VALUES
    ('tipoDocumento:ver', 'Permiso para ver el módulo tipo documento'),
    ('tipoDocumento:listar', 'Permiso para listar los tipos de documento'),
    ('tipoDocumento:crear', 'Permiso para crear tipos de documento'),
    ('tipoDocumento:editar', 'Permiso para editar tipos de documento'),
    ('tipoDocumento:eliminar', 'Permiso para eliminar tipos de documento'),
    ('tipoDocumento:cambiarEstado', 'Permiso para cambiar estado de tipos de documento')
) AS v(nombre, descripcion)
WHERE NOT EXISTS (
    SELECT 1 FROM public.permiso p WHERE p.nombre = v.nombre
);

