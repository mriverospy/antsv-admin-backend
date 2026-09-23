ALTER TABLE curso ADD COLUMN cupo int4;
ALTER TABLE curso ADD COLUMN fecha_inicio timestamp;
ALTER TABLE curso ADD COLUMN fecha_fin timestamp;
ALTER TABLE curso ADD COLUMN modalidad character varying(255);
ALTER TABLE curso ADD COLUMN horas_catedra int4;
ALTER TABLE curso ADD COLUMN enlace_curso character varying(255);