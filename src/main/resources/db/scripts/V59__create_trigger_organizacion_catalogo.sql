
CREATE OR REPLACE FUNCTION public.fn_attach_organizacion_catalogo_on_insupd()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
DECLARE
    v_id_catalogo integer;
BEGIN
    -- Si no tiene catálogo vinculado, crearlo y asignarlo
    IF NEW.id_catalogo_htv IS NULL THEN
		IF (select upper(nombre) from tipo_organizacion where id_tipo_organizacion = NEW.id_tipo_organizacion) = 'STARTUPS'  THEN
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
	            (select id_tipo_catalogo from tipo_catalogo where upper(nombre) = 'STARTUPS'),
	            NEW.nombre_fantasia,
	            COALESCE(NEW.descripcion, ''),
	            COALESCE(NULLIF(NEW.referencia_imagen, ''), '-'),
	            'ACTIVO',
	            now(),
	            NULL,
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
		END IF;
		IF (select upper(nombre) from tipo_organizacion where id_tipo_organizacion = NEW.id_tipo_organizacion) = 'EMPRESAS'  THEN
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
	            (select id_tipo_catalogo from tipo_catalogo where upper(nombre) = 'EMPRESAS'),
	            NEW.nombre_fantasia,
	            COALESCE(NEW.descripcion, ''),
	            COALESCE(NULLIF(NEW.referencia_imagen, ''), '-'),
	            'ACTIVO',
	            now(),
	            NULL,
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
		END IF;
    END IF;

    RETURN NEW;
END;
$BODY$;

ALTER FUNCTION public.fn_attach_organizacion_catalogo_on_insupd()
    OWNER TO postgres;



CREATE OR REPLACE TRIGGER fn_attach_organizacion_catalogo_on_insupd
    BEFORE INSERT OR UPDATE 
    ON public.organizacion
    FOR EACH ROW
    EXECUTE FUNCTION public.fn_attach_organizacion_catalogo_on_insupd();