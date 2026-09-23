-- Insertamos los tipos de seccion contenido
insert into tipo_seccion_contenido values (1, 'NOTICIA', true);
insert into tipo_seccion_contenido values (2, 'PRODUCTO_SERVICIO', true);
insert into tipo_seccion_contenido values (3, 'ORGANIZACION', true);

-- Se crea la tabla de referencia con el tipo seccion contenido
ALTER TABLE public.seccion ADD id_tipo_seccion_contenido int4 NULL;

-- Se crean las columnas de fecha de creacion, actualización y estado
ALTER TABLE public.seccion ADD fecha_creacion timestamp NULL;
ALTER TABLE public.seccion ADD fecha_actualizacion timestamp NULL;
ALTER TABLE public.seccion ADD estado boolean NULL;