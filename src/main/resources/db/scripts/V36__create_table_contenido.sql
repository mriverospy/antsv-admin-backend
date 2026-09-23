-- Tablas para la gestion de contenidos
-- Creación de la tabla Tipo Sección Contenido
CREATE TABLE public.tipo_seccion_contenido (
	id_tipo_seccion_contenido serial4 NOT NULL,
	nombre varchar NULL,
	estado boolean NULL,
	CONSTRAINT tipo_seccion_contenido_pk PRIMARY KEY (id_tipo_seccion_contenido)
);

-- Creación de la tabla Sección Contenido
CREATE TABLE public.seccion_contenido (
	id_seccion_contenido serial4 NOT NULL,
	id_tipo_seccion_contenido int4 NULL,
	fecha_creacion timestamp NULL,
	estado boolean NULL,
	CONSTRAINT seccion_contenido_pk PRIMARY KEY (id_seccion_contenido)
);

-- Creación de la tabla Contenido
CREATE TABLE public.contenido (
	id_contenido serial4 NOT NULL,
	id_seccion int4 NULL,
	id_seccion_contenido int4 NULL,
	nombre varchar NULL,
	descripcion text NULL,
	link_descarga varchar NULL,
	link_video varchar NULL,
	referencia_imagen varchar NULL,
	fecha_creacion timestamp NULL,
	estado bool NULL,
	fecha_modificacion timestamp NULL,
	CONSTRAINT contenido_pk PRIMARY KEY (id_contenido)
);

-- Se agrega el id de la seccion contenido
ALTER TABLE public.organizacion_producto ADD id_seccion_contenido int4 NULL;