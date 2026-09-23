CREATE TABLE public.tipo_noticia (
	id_tipo_noticia serial4 NOT NULL,
	nombre varchar NOT NULL,
	estado bool NULL,
	CONSTRAINT tipo_noticia_pk PRIMARY KEY (id_tipo_noticia)
);

insert into PUBLIC.tipo_noticia values (1, 'Tecnología', true);
insert into PUBLIC.tipo_noticia values (2, 'Educación', true);
insert into PUBLIC.tipo_noticia values (3, 'Artículo', true);

ALTER TABLE public.noticia ADD id_tipo_noticia serial4 NOT NULL;