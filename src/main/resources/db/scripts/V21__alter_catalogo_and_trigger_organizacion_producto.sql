-- Script V21: Alter catalogo and trigger organizacion producto

-- Recrear secuencia para tipo_catalogo
DROP SEQUENCE IF EXISTS public.tipo_catalogo_id_tipo_catalogo_seq CASCADE;
CREATE SEQUENCE public.tipo_catalogo_id_tipo_catalogo_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

-- Configurar secuencia en la tabla
ALTER TABLE public.tipo_catalogo ALTER COLUMN id_tipo_catalogo DROP DEFAULT;
ALTER TABLE public.tipo_catalogo ALTER COLUMN id_tipo_catalogo SET DEFAULT nextval('public.tipo_catalogo_id_tipo_catalogo_seq'::regclass);
ALTER SEQUENCE public.tipo_catalogo_id_tipo_catalogo_seq OWNED BY public.tipo_catalogo.id_tipo_catalogo;

-- Insertar tipos de catálogo solo si no existen
INSERT INTO public.tipo_catalogo(nombre, estado) 
VALUES ('PRODUCTOS Y SERVICIOS', true)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO public.tipo_catalogo(nombre, estado) 
VALUES ('EMPRESAS', true)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO public.tipo_catalogo(nombre, estado) 
VALUES ('STARTUPS', true)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO public.tipo_catalogo(nombre, estado) 
VALUES ('MENTORES', true)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO public.tipo_catalogo(nombre, estado) 
VALUES ('CAPACITACIONES', true)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO public.tipo_catalogo(nombre, estado) 
VALUES ('PROGRAMAS', true)
ON CONFLICT (nombre) DO NOTHING;

-- Agregar columna referencia_imagen si no existe
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'catalogo_htv' AND column_name = 'referencia_imagen') THEN
        ALTER TABLE public.catalogo_htv ADD COLUMN referencia_imagen varchar(50);
    END IF;
END $$;

-- Crear función para trigger
CREATE OR REPLACE FUNCTION public.fn_attach_catalogo_on_insupd()
RETURNS TRIGGER AS $$
DECLARE
    v_id_catalogo integer;
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
            1,
            NEW.nombre,
            COALESCE(NEW.descripcion, ''),
            COALESCE(NULLIF(NEW.referencia_imagen, ''), '-'),
            'ACTIVO',
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