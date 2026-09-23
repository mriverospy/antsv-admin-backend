--Agergar la columna de referenciaImagen para imagen principal de programa
alter table programa add column referencia_imagen varchar(200);
alter table programa add column estado boolean default true;
ALTER TABLE programa ADD COLUMN id_catalogo_htv INT;

-- DROP FUNCTION public.fn_attach_curso_catalogo_htv_on_insupd();

CREATE OR REPLACE FUNCTION public.fn_attach_programa_catalogo_htv_on_insupd()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
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
            (select id_tipo_catalogo FROM tipo_catalogo WHERE nombre = 'PROGRAMAS'),
            NEW.nombre,
            COALESCE(NEW.descripcion, ''),
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
            nombre                = NEW.nombre,
            descripcion           = COALESCE(NEW.descripcion, ''),
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
$function$
;



--replicar el trigger generado en otras entidades
create trigger trg_attach_programa_catalogo_htv_on_insupd before
insert
    or
update
    on
    public.programa for each row execute function fn_attach_programa_catalogo_htv_on_insupd()