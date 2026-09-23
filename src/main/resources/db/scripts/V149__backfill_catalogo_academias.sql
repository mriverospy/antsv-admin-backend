
-- Crear/actualizar catalogo para academias con id_catalogo_htv nulo
INSERT INTO public.catalogo_htv (
    id_tipo_catalogo,
    nombre,
    descripcion,
    url,
    estado,
    fecha_creacion,
    tipo_recurso,
    referencia_imagen
)
SELECT
    tc.id_tipo_catalogo,
    o.nombre_fantasia,
    COALESCE(o.descripcion, ''),
    COALESCE(NULLIF(o.referencia_imagen, ''), '-'),
    'ACTIVO',
    now(),
    NULL,
    o.referencia_imagen
FROM public.organizacion o
JOIN public.tipo_organizacion to2
    ON to2.id_tipo_organizacion = o.id_tipo_organizacion
JOIN public.tipo_catalogo tc
    ON UPPER(TRIM(tc.nombre)) = 'ACADEMIAS'
WHERE UPPER(TRIM(to2.nombre)) IN ('ACADEMIA', 'ACADEMIAS')
  AND o.id_catalogo_htv IS NULL
ON CONFLICT (nombre) DO UPDATE SET
    id_tipo_catalogo = EXCLUDED.id_tipo_catalogo,
    descripcion = EXCLUDED.descripcion,
    url = EXCLUDED.url,
    fecha_actualizacion = now(),
    tipo_recurso = EXCLUDED.tipo_recurso,
    referencia_imagen = EXCLUDED.referencia_imagen;

-- Vincular organizaciones con el catalogo correspondiente
UPDATE public.organizacion o
SET id_catalogo_htv = c.id_catalogo_htv
FROM public.catalogo_htv c,
         public.tipo_organizacion to2
WHERE UPPER(TRIM(to2.nombre)) IN ('ACADEMIA', 'ACADEMIAS')
    AND to2.id_tipo_organizacion = o.id_tipo_organizacion
  AND o.id_catalogo_htv IS NULL
  AND c.nombre = o.nombre_fantasia;
