CREATE TABLE public.institucion (
                             id_institucion serial PRIMARY KEY,
                             nombre character varying(500),
                             estado BOOLEAN,
                             CONSTRAINT institucion_nombre_unique UNIQUE (nombre)
);