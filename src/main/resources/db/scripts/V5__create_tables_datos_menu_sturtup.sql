-- ALTERT TABLES
ALTER TABLE catalogo_htv 
ADD COLUMN tipo_recurso varchar(30) COLLATE pg_catalog."default" NULL;

ALTER TABLE organizacion ALTER COLUMN id_catalogo_htv DROP NOT NULL;
ALTER TABLE organizacion_producto ALTER COLUMN id_catalogo_htv DROP NOT NULL;

ALTER TABLE organizacion_producto
ADD COLUMN nombre text COLLATE pg_catalog."default" NOT NULL,
ADD COLUMN descripcion text COLLATE pg_catalog."default" NOT NULL;

ALTER TABLE organizacion_producto
  ADD COLUMN id_organizacion integer NOT NULL,
  ADD CONSTRAINT organizacion_producto_id_organizacion_fkey
    FOREIGN KEY (id_organizacion)
    REFERENCES public.organizacion (id_organizacion)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;
		
ALTER TABLE organizacion_producto 
ADD COLUMN eslogan_producto text COLLATE pg_catalog."default" NULL,
ADD COLUMN dirigido_a text COLLATE pg_catalog."default" NULL;

ALTER TABLE organizacion_producto 
ADD COLUMN como_funciona text COLLATE pg_catalog."default" NULL;

ALTER TABLE organizacion_producto 
ADD COLUMN tipo_recurso varchar(30) COLLATE pg_catalog."default" NULL;


insert into tipo_producto(nombre, estado) values ('App', true);
insert into tipo_producto(nombre, estado) values ('Web', true);

