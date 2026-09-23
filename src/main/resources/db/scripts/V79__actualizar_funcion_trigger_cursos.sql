-- Crear/Actualizar función para trigger
CREATE OR REPLACE FUNCTION public.fn_attach_curso_catalogo_htv_on_insupd()
RETURNS TRIGGER AS $$
DECLARE
    v_id_catalogo_organizacion integer;
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
            (select id_tipo_catalogo FROM tipo_catalogo WHERE nombre = 'CURSOS'),
            NEW.titulo,
            COALESCE(NEW.resumen, ''),
            COALESCE(NULLIF(NEW.referencia_imagen, ''), '-'),
            v_estado_str,
            now(),
            (SELECT upper(tipOr.nombre)
                FROM organizacion o
                JOIN tipo_organizacion tipOr ON o.id_tipo_organizacion = tipOr.id_tipo_organizacion
                WHERE o.id_organizacion = NEW.id_organizacion),
            NEW.referencia_imagen
        )
        ON CONFLICT (nombre)
        DO UPDATE SET
            descripcion           = EXCLUDED.nombre,
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
            nombre                = NEW.titulo,
            descripcion           = COALESCE(NEW.resumen, ''),
            url                   = COALESCE(NULLIF(NEW.referencia_imagen, ''), '-'),
            fecha_actualizacion   = now(),
            tipo_recurso          = (SELECT upper(tipOr.nombre)
                                     FROM organizacion o
                                        JOIN tipo_organizacion tipOr ON o.id_tipo_organizacion = tipOr.id_tipo_organizacion
                                     WHERE o.id_organizacion = NEW.id_organizacion),
            referencia_imagen     = NEW.referencia_imagen,
            estado = v_estado_str
        WHERE id_catalogo_htv = NEW.id_catalogo_htv;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Crear trigger
DROP TRIGGER IF EXISTS trg_attach_curso_catalogo_htv_on_insupd ON public.curso;

CREATE TRIGGER trg_attach_curso_catalogo_htv_on_insupd
    BEFORE INSERT OR UPDATE ON public.curso
    FOR EACH ROW
    EXECUTE FUNCTION public.fn_attach_curso_catalogo_htv_on_insupd();