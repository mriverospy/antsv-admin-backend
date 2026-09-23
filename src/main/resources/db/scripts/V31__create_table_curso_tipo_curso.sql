/*TABLAS PARA ABM CURSOS CAPACITACIONES*/
CREATE TABLE public.tipo_curso(
	id_tipo_curso serial4 NOT NULL,
	nombre varchar(100) NOT NULL,
	estado bool DEFAULT true NULL,
	CONSTRAINT tipo_curso_pkey PRIMARY KEY (id_tipo_curso)
);

--insertar datos por defecto
INSERT INTO public.tipo_curso (nombre, estado)
VALUES 
('Interno', true),
('Externo', true);


CREATE TABLE public.curso (
	id_curso serial4 NOT NULL,
	id_organizacion int4 NOT NULL,
	id_tipo_curso int4 NOT NULL,
	titulo varchar(200) NOT NULL,
	referencia_imagen varchar(200) NOT NULL,
	resumen text NOT NULL,
	descripcion text NOT NULL,
	abierto_publico bool DEFAULT false NULL,
	con_invitacion bool DEFAULT false NULL,
	id_usuario_creacion int4 NULL,
	fecha_creacion timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	etiquetas text,
	url_curso_externo varchar(200),
	estado bool DEFAULT false NULL,
	id_recurso int4 NULL,
	CONSTRAINT curso_pkey PRIMARY KEY (id_curso),
	CONSTRAINT fk_curso_organizacion FOREIGN KEY (id_organizacion) REFERENCES public.organizacion(id_organizacion),
	CONSTRAINT fk_curso_tipo FOREIGN KEY (id_tipo_curso) REFERENCES public.tipo_curso(id_tipo_curso)
);
