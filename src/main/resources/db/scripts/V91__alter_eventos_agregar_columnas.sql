ALTER TABLE evento ADD COLUMN cupo int4;
ALTER TABLE evento ADD COLUMN fecha_inicio timestamp;
ALTER TABLE evento ADD COLUMN fecha_fin timestamp;
ALTER TABLE evento ADD COLUMN direccion text;
ALTER TABLE evento ADD COLUMN telefono character varying(40);