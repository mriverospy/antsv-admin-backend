--Script para crear la tabla inscripcion.

CREATE TABLE public.inscripcion (
	id_inscripcion serial4 NOT NULL,
	id_evento int4 NOT NULL,
	id_usuario int4 NOT NULL,
	fecha_creacion timestamp DEFAULT now() NOT NULL,
	metodo_inscripcion varchar(20) NOT NULL,
	estado bool DEFAULT true NOT NULL,
	CONSTRAINT inscripcion_metodo_inscripcion_check CHECK (((metodo_inscripcion)::text = ANY ((ARRAY['online'::character varying, 'manual'::character varying])::text[]))),
	CONSTRAINT inscripcion_pkey PRIMARY KEY (id_inscripcion),
	CONSTRAINT uq_usuario_evento UNIQUE (id_evento, id_usuario),
	CONSTRAINT fk_evento FOREIGN KEY (id_evento) REFERENCES public.evento(id_evento),
	CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario)
);