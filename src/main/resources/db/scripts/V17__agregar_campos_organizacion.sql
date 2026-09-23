ALTER TABLE organizacion ADD COLUMN fecha_constitucion timestamp;
ALTER TABLE organizacion ADD COLUMN fecha_creacion timestamp DEFAULT (now());
ALTER TABLE organizacion ADD COLUMN fecha_actualizacion timestamp;
ALTER TABLE organizacion ADD COLUMN estado_tributario CHARACTER VARYING(255);
ALTER TABLE organizacion ADD COLUMN representante_legal CHARACTER VARYING(512);
ALTER TABLE organizacion ADD COLUMN tipo_sociedad CHARACTER VARYING(64);
ALTER TABLE organizacion ADD COLUMN actividad_economica CHARACTER VARYING(64);
ALTER TABLE organizacion ADD COLUMN id_declaracion_jurada CHARACTER VARYING(255);
ALTER TABLE organizacion ADD COLUMN observacion CHARACTER VARYING(1000);