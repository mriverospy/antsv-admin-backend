-- DROP FUNCTION public.fn_attach_programa_catalogo_htv_on_insupd();

CREATE OR REPLACE FUNCTION public.fn_attach_programa_catalogo_htv_on_insupd()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id_catalogo_organizacion integer;
    v_estado_str text;
    v_id_estado_aprobado integer;
begin
	
    SELECT id_programa_estado
    INTO v_id_estado_aprobado
    FROM programa_estado
    WHERE upper(nombre) = 'PUBLICADO';

    IF NEW.id_programa_estado IS DISTINCT FROM v_id_estado_aprobado THEN
        RETURN NEW;
    END IF;

    v_estado_str :=
        CASE
            WHEN NEW.estado = TRUE THEN 'ACTIVO'
            ELSE 'INACTIVO'
        END;

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
            (SELECT id_tipo_catalogo FROM tipo_catalogo WHERE nombre = 'PROGRAMAS'),
            NEW.nombre,
            COALESCE(NEW.descripcion, ''),
            COALESCE(NULLIF(NEW.referencia_imagen, ''), '-'),
            v_estado_str,
            now(),
            (
                SELECT upper(tipOr.nombre)
                FROM organizacion o
                JOIN tipo_organizacion tipOr 
                  ON o.id_tipo_organizacion = tipOr.id_tipo_organizacion
                WHERE o.id_organizacion = NEW.id_organizacion
            ),
            NEW.referencia_imagen
        )
        ON CONFLICT (nombre)
        DO UPDATE SET
            descripcion         = EXCLUDED.descripcion,
            url                 = EXCLUDED.url,
            fecha_actualizacion = now(),
            tipo_recurso        = EXCLUDED.tipo_recurso,
            referencia_imagen   = EXCLUDED.referencia_imagen,
            estado              = EXCLUDED.estado
        RETURNING id_catalogo_htv
        INTO v_id_catalogo_organizacion;

        NEW.id_catalogo_htv := v_id_catalogo_organizacion;

    ELSE

        UPDATE public.catalogo_htv
        SET
            nombre              = NEW.nombre,
            descripcion         = COALESCE(NEW.descripcion, ''),
            url                 = COALESCE(NULLIF(NEW.referencia_imagen, ''), '-'),
            fecha_actualizacion = now(),
            tipo_recurso        = (
                SELECT upper(tipOr.nombre)
                FROM organizacion o
                JOIN tipo_organizacion tipOr 
                  ON o.id_tipo_organizacion = tipOr.id_tipo_organizacion
                WHERE o.id_organizacion = NEW.id_organizacion
            ),
            referencia_imagen   = NEW.referencia_imagen,
            estado              = v_estado_str
        WHERE id_catalogo_htv = NEW.id_catalogo_htv;

    END IF;

    RETURN NEW;
END;
$function$
;
