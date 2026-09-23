ALTER TABLE public.tipo_noticia 
ADD COLUMN fecha_creacion timestamp without time zone DEFAULT now(),
ADD COLUMN fecha_actualizacion timestamp without time zone DEFAULT now();
