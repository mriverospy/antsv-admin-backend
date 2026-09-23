-- Crear función para trigger
CREATE OR REPLACE FUNCTION public.fn_attach_catalogo_on_insupd()
RETURNS TRIGGER AS $$
DECLARE
    v_id_catalogo integer;
    v_estado_str text;
BEGIN
    -- 1. Determinar el estado como texto (ACTIVO/INACTIVO)
    -- Se usa un CASE para convertir el booleano (NEW.estado) a String.
    SELECT
    CASE
        WHEN NEW.estado = TRUE THEN 'ACTIVO'
        ELSE 'INACTIVO'
        END
    INTO v_estado_str;
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
            1,
            NEW.nombre,
            COALESCE(NEW.descripcion, ''),
            COALESCE(NULLIF(NEW.referencia_imagen, ''), '-'),
            v_estado_str,
            now(),
            'PRODUCTO',
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
        INTO v_id_catalogo;

        NEW.id_catalogo_htv := v_id_catalogo;
    ELSE
        -- 2. Proceso de Actualización
        UPDATE public.catalogo_htv
        SET
            nombre = NEW.nombre,
            descripcion = COALESCE(NEW.descripcion, ''),
            estado = v_estado_str,
            referencia_imagen = NEW.referencia_imagen
        WHERE id_catalogo_htv = NEW.id_catalogo_htv;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Crear trigger
DROP TRIGGER IF EXISTS trg_attach_catalogo_on_insupd ON public.organizacion_producto;

CREATE TRIGGER trg_attach_catalogo_on_insupd
    BEFORE INSERT OR UPDATE ON public.organizacion_producto
    FOR EACH ROW
    EXECUTE FUNCTION public.fn_attach_catalogo_on_insupd();