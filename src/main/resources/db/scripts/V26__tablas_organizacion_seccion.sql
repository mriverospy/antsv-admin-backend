CREATE TABLE IF NOT EXISTS public.organizacion_seccion
(
    id_organizacion_seccion bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 100 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    id_organizacion integer NOT NULL,
    id_seccion integer NOT NULL,
    nombre character varying(200) COLLATE pg_catalog."default" NOT NULL,
    descripcion text COLLATE pg_catalog."default" NOT NULL,
	link_descarga character varying(200) COLLATE pg_catalog."default" NULL,
	link_video character varying(200) COLLATE pg_catalog."default" NULL,
	referencia_imagen character varying(200) COLLATE pg_catalog."default" NULL,
    CONSTRAINT organizacion_seccion_pkey PRIMARY KEY (id_organizacion_seccion),
    CONSTRAINT organizacion_seccion_id_organizacion_fkey FOREIGN KEY (id_organizacion)
        REFERENCES public.organizacion (id_organizacion) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT organizacion_seccion_id_seccion_fkey FOREIGN KEY (id_seccion)
        REFERENCES public.seccion (id_seccion) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;


ALTER TABLE IF EXISTS public.organizacion
    ADD COLUMN referencia_imagen character varying(200) COLLATE pg_catalog."default";


	
