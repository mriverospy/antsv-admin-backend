-- ALTER TABLES
ALTER TABLE organizacion_producto drop COLUMN dirigido_a;
ALTER TABLE organizacion_producto drop COLUMN como_funciona;
ALTER TABLE organizacion_producto drop COLUMN tipo_recurso;

ALTER TABLE organizacion_producto 
ADD COLUMN subtitulo character varying(200) COLLATE pg_catalog."default" DEFAULT 'SIN DATO' NOT NULL,
ADD COLUMN referencia_imagen character varying(200) COLLATE pg_catalog."default" NULL,
ADD COLUMN descripcion_corta text COLLATE pg_catalog."default" DEFAULT 'SIN DATO' NOT NULL;

-- CREATE TABLES
CREATE TABLE IF NOT EXISTS public.seccion
(
    id_seccion SERIAL,
	nombre character varying(200) COLLATE pg_catalog."default" NOT NULL,
    descripcion text COLLATE pg_catalog."default" NOT NULL,
    id_tipo_catalogo integer,
	estilo smallint,
    CONSTRAINT seccion_pkey PRIMARY KEY (id_seccion),
    CONSTRAINT seccion_id_tipo_catalogo FOREIGN KEY (id_tipo_catalogo)
        REFERENCES public.tipo_catalogo (id_tipo_catalogo) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

--TABLESPACE pg_default;
--ALTER TABLE IF EXISTS public.seccion OWNER to postgres;
CREATE TABLE IF NOT EXISTS public.organizacion_producto_seccion
(
    id_organizacion_producto_seccion SERIAL,
    id_organizacion_producto integer NOT NULL,
    id_seccion integer NOT NULL,
    nombre character varying(200) COLLATE pg_catalog."default" NOT NULL,
    descripcion text COLLATE pg_catalog."default" NOT NULL,
	link_descarga character varying(200) COLLATE pg_catalog."default" NULL,
	link_video character varying(200) COLLATE pg_catalog."default" NULL,
	referencia_imagen character varying(200) COLLATE pg_catalog."default" NULL,
    CONSTRAINT organizacion_producto_seccion_pkey PRIMARY KEY (id_organizacion_producto_seccion),
    CONSTRAINT organizacion_producto_seccion_id_organizacion_producto_fkey FOREIGN KEY (id_organizacion_producto)
        REFERENCES public.organizacion_producto (id_organizacion_producto) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT organizacion_producto_seccion_id_seccion_fkey FOREIGN KEY (id_seccion)
        REFERENCES public.seccion (id_seccion) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);