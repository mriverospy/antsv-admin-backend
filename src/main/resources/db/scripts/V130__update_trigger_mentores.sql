CREATE OR REPLACE FUNCTION public.fn_attach_mentores_catalogo_on_insupd()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
DECLARE
  v_id_catalogo integer;
  v_nombre_mentor varchar;
begin
	--cargar el nombre del mentor
	SELECT nombre || ' ' || apellido INTO v_nombre_mentor FROM usuario WHERE id_usuario = NEW.id_usuario;
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
      ) VALUES (
        (SELECT id_tipo_catalogo FROM tipo_catalogo WHERE upper(nombre) = 'MENTORES'),
        -- Asigno el nombre del mentor como nombre catálogo (puedes ajustar a tu lógica)
        CONCAT(COALESCE(v_nombre_mentor,''), ' - ', COALESCE(NEW.subtitulo,'')),
        COALESCE(NEW.descripcion, ''),
        COALESCE(NULLIF(NEW.referencia_imagen, ''), '-'),
        'ACTIVO',
        now(),
        NULL,
        NEW.referencia_imagen
      ) ON CONFLICT (nombre) DO UPDATE SET
        descripcion = EXCLUDED.descripcion,
        url = EXCLUDED.url,
        fecha_actualizacion = now(),
        tipo_recurso = EXCLUDED.tipo_recurso,
        referencia_imagen = EXCLUDED.referencia_imagen
      RETURNING id_catalogo_htv INTO v_id_catalogo;

      NEW.id_catalogo_htv := v_id_catalogo;
  ELSE
      UPDATE public.catalogo_htv
      SET
        id_tipo_catalogo = (SELECT id_tipo_catalogo FROM tipo_catalogo WHERE upper(nombre) = 'MENTORES'),
         nombre = CONCAT(COALESCE(v_nombre_mentor,''), ' - ', COALESCE(NEW.subtitulo,'')),
        descripcion = COALESCE(NEW.descripcion, ''),
        estado = CASE WHEN NEW.estado THEN 'ACTIVO' ELSE 'INACTIVO' END,
        tipo_recurso = (SELECT nombre FROM tipo_catalogo WHERE upper(nombre) = 'MENTORES'),
        referencia_imagen = NEW.referencia_imagen
      WHERE id_catalogo_htv = NEW.id_catalogo_htv;
  END IF;

  RETURN NEW;
END;
$function$
;