-- Modificación de trigger para actualizar el catálogo HTV al insertar o actualizar una academia
CREATE OR REPLACE FUNCTION public.fn_attach_organizacion_catalogo_on_insupd()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id_catalogo integer;
    v_tipo_organizacion text;
BEGIN
    -- Obtener Tipo de Organizacion
    SELECT trim(upper(nombre))
    INTO v_tipo_organizacion
    FROM tipo_organizacion
    WHERE id_tipo_organizacion = NEW.id_tipo_organizacion;

    -- Normalizar singular-plural
    IF v_tipo_organizacion = 'ACADEMIA' THEN
        v_tipo_organizacion := 'ACADEMIAS';
    END IF;

    IF v_tipo_organizacion NOT IN ('STARTUPS', 'EMPRESAS', 'ACADEMIAS') THEN
        RETURN NEW;
    END IF;

	-- Si no existe entonces crear o actualizar
    IF NEW.id_catalogo_htv IS NULL THEN
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
        VALUES (
            (SELECT id_tipo_catalogo FROM tipo_catalogo WHERE upper(nombre) = v_tipo_organizacion),
            NEW.nombre_fantasia,
            COALESCE(NEW.descripcion, ''),
            COALESCE(NULLIF(NEW.referencia_imagen, ''), '-'),
            'ACTIVO',
            now(),
            NULL,
            NEW.referencia_imagen
        )
        ON CONFLICT (nombre) DO UPDATE SET
            descripcion = EXCLUDED.descripcion,
            url = EXCLUDED.url,
            fecha_actualizacion = now(),
            tipo_recurso = EXCLUDED.tipo_recurso,
            referencia_imagen = EXCLUDED.referencia_imagen
        RETURNING id_catalogo_htv INTO v_id_catalogo;

        NEW.id_catalogo_htv := v_id_catalogo;

	-- Actualizar existente
    ELSE
        UPDATE public.catalogo_htv
        SET
            id_tipo_catalogo = (SELECT id_tipo_catalogo FROM tipo_catalogo WHERE upper(nombre) = v_tipo_organizacion),
            nombre = NEW.nombre,
            descripcion = COALESCE(NEW.descripcion, ''),
            estado = NEW.estado,
            tipo_recurso = v_tipo_organizacion,
            referencia_imagen = NEW.referencia_imagen
        WHERE id_catalogo_htv = NEW.id_catalogo_htv;
    END IF;

    RETURN NEW;
END;
$function$
;
