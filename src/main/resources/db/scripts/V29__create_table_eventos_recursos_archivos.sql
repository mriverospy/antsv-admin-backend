/*Script para Creacion de tablas
 * tipo_recurso: tabla que contiene los tipos de recurso que seran los modulos con los que estaran relacionados
 * recurso: es la tabla que estara relacionada con los archivos
 * archivos: tabla que contendra toda la informacion acerca de archivos de los diferentes modulos
 * tipo_evento: Catálogo maestro de tipos de evento (ej: INTERNO, EXTERNO).
 * evento: Representa un evento organizado por la institución*/

CREATE TABLE public.tipo_recurso (
	id_tipo_recurso serial4 NOT NULL,
	nombre varchar(100) NOT NULL,
	estado bool DEFAULT true NOT NULL,
	CONSTRAINT tipo_recurso_nombre_key UNIQUE (nombre),
	CONSTRAINT tipo_recurso_pkey PRIMARY KEY (id_tipo_recurso)
);

INSERT INTO public.tipo_recurso (nombre, estado)
VALUES ('EVENTO', true);

CREATE TABLE public.recurso (
	id_recurso serial4 NOT NULL,
	fecha_creacion timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	id_tipo_recurso int4 NOT NULL,
	CONSTRAINT recurso_pkey PRIMARY KEY (id_recurso),
	CONSTRAINT fk_recurso_tipo_recurso FOREIGN KEY (id_tipo_recurso) REFERENCES public.tipo_recurso(id_tipo_recurso) ON DELETE RESTRICT ON UPDATE CASCADE
);
CREATE INDEX idx_recurso_tipo ON public.recurso USING btree (id_tipo_recurso);


CREATE TABLE public.archivo (
	id_archivo serial4 NOT NULL,
	id_recurso int4 NOT NULL,
	referencia_archivo varchar(200) NOT NULL,
	fecha_creacion timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	estado bool DEFAULT false NOT NULL,
	nombre_archivo varchar(100) NULL,
	tipo_mime varchar NULL,
	CONSTRAINT archivo_pkey PRIMARY KEY (id_archivo),
	CONSTRAINT fk_archivo_recurso FOREIGN KEY (id_recurso) REFERENCES public.recurso(id_recurso) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX idx_archivo_recurso ON public.archivo USING btree (id_recurso);

CREATE TABLE public.tipo_evento (
	id_tipo_evento serial4 NOT NULL,
	nombre varchar(100) NOT NULL,
	estado bool DEFAULT true NULL,
	CONSTRAINT tipo_evento_pkey PRIMARY KEY (id_tipo_evento)
);

INSERT INTO public.tipo_evento (nombre, estado)
VALUES 
  ('INTERNO', true),
  ('EXTERNO', true);

CREATE TABLE public.evento (
	id_evento serial4 NOT NULL,
	id_organizacion int4 NOT NULL,
	id_tipo_evento int4 NOT NULL,
	titulo varchar(200) NOT NULL,
	referencia_imagen varchar(200) NOT NULL,
	resumen text NOT NULL,
	descripcion text NOT NULL,
	abierto_publico bool DEFAULT false NULL,
	con_invitacion bool DEFAULT false NULL,
	id_usuario_creacion int4 NULL,
	fecha_creacion timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	estado bool DEFAULT false NULL,
	id_recurso int4 NULL,
	CONSTRAINT evento_pkey PRIMARY KEY (id_evento),
	CONSTRAINT fk_evento_organizacion FOREIGN KEY (id_organizacion) REFERENCES public.organizacion(id_organizacion),
	CONSTRAINT fk_evento_tipo FOREIGN KEY (id_tipo_evento) REFERENCES public.tipo_evento(id_tipo_evento)
);


