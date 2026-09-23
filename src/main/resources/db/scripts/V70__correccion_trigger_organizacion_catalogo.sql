CREATE OR REPLACE FUNCTION public.fn_attach_organizacion_catalogo_on_insupd()
  RETURNS trigger
  LANGUAGE plpgsql
  COST 100
  VOLATILE NOT LEAKPROOF
AS $BODY$
DECLARE
  v_id_catalogo integer;
  v_tipo_organizacion text;
BEGIN
  -- Obtener el tipo de organización
  SELECT upper(nombre) INTO v_tipo_organizacion
  FROM tipo_organizacion
  WHERE id_tipo_organizacion = NEW.id_tipo_organizacion;

  -- Si no tiene catálogo vinculado, crearlo y asignarlo
  IF NEW.id_catalogo_htv IS NULL THEN
    IF v_tipo_organizacion = 'STARTUPS' THEN
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
        (SELECT id_tipo_catalogo FROM tipo_catalogo WHERE upper(nombre) = 'STARTUPS'),
        NEW.nombre_fantasia,
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
    ELSIF v_tipo_organizacion = 'EMPRESAS' THEN
      -- Insertar para empresas
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
        (SELECT id_tipo_catalogo FROM tipo_catalogo WHERE upper(nombre) = 'EMPRESAS'),
        NEW.nombre_fantasia,
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
    END IF;
  ELSE
    IF v_tipo_organizacion = 'EMPRESAS' THEN
      UPDATE public.catalogo_htv
      SET
        id_tipo_catalogo = (SELECT id_tipo_catalogo FROM tipo_catalogo WHERE upper(nombre) = 'EMPRESAS'),
        nombre = NEW.nombre,
        descripcion = COALESCE(NEW.descripcion, ''),
        --url = COALESCE(NEW.url, ''),
        estado = NEW.estado,
        tipo_recurso = (SELECT id_tipo_catalogo FROM tipo_catalogo WHERE upper(nombre) = 'EMPRESAS'),
        referencia_imagen = NEW.referencia_imagen
      WHERE id_catalogo_htv = NEW.id_catalogo_htv;
    ELSIF v_tipo_organizacion = 'STARTUPS' THEN
      UPDATE public.catalogo_htv
      SET
        id_tipo_catalogo = (SELECT id_tipo_catalogo FROM tipo_catalogo WHERE upper(nombre) = 'STARTUPS'),
        nombre = NEW.nombre,
        descripcion = COALESCE(NEW.descripcion, ''),
        --Aurl = COALESCE(NEW.url, ''),
        estado = NEW.estado,
        tipo_recurso = (SELECT id_tipo_catalogo FROM tipo_catalogo WHERE upper(nombre) = 'STARTUPS'),
        referencia_imagen = NEW.referencia_imagen
      WHERE id_catalogo_htv = NEW.id_catalogo_htv;
    END IF;
  END IF;

  RETURN NEW;
END;
$BODY$;

ALTER FUNCTION public.fn_attach_organizacion_catalogo_on_insupd() OWNER TO postgres;