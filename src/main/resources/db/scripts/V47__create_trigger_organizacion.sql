-- Crear función para trigger
CREATE OR REPLACE FUNCTION public.fn_attach_organizacion_catalogo_on_insupd()
RETURNS TRIGGER AS $$
DECLARE
    v_id_catalogo_organizacion integer;
BEGIN
    -- Si no tiene catálogo vinculado, crearlo y asignarlo
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
            2,
            NEW.nombre,
            COALESCE(NEW.descripcion, ''),
            COALESCE(NULLIF(NEW.referencia_imagen, ''), '-'),
            'ACTIVO',
            now(),
            'EMPRESA',
            NEW.referencia_imagen
        )
        ON CONFLICT (nombre)
        DO UPDATE SET
            descripcion           = EXCLUDED.descripcion,
            url                   = EXCLUDED.url,
            fecha_actualizacion   = now(),
            tipo_recurso          = EXCLUDED.tipo_recurso,
            referencia_imagen     = EXCLUDED.referencia_imagen
        RETURNING id_catalogo_htv
        INTO v_id_catalogo_organizacion;

        NEW.id_catalogo_htv := v_id_catalogo_organizacion;
    ELSE
        UPDATE public.catalogo_htv
            SET
            nombre                = NEW.nombre,
            descripcion           = COALESCE(NEW.descripcion, ''),
            url                   = COALESCE(NULLIF(NEW.referencia_imagen, ''), '-'),
            fecha_actualizacion   = now(),
            tipo_recurso          = 'EMPRESA',
            referencia_imagen     = NEW.referencia_imagen
        WHERE id_catalogo_htv = NEW.id_catalogo_htv;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Crear trigger
DROP TRIGGER IF EXISTS trg_attach_organizacion_catalogo_on_insupd ON public.organizacion;

CREATE TRIGGER trg_attach_organizacion_catalogo_on_insupd
    BEFORE INSERT OR UPDATE ON public.organizacion
    FOR EACH ROW
    EXECUTE FUNCTION public.fn_attach_organizacion_catalogo_on_insupd();