ALTER TABLE organizacion ADD COLUMN id_institucion INTEGER;
ALTER TABLE organizacion ADD CONSTRAINT fk_organizacion_institucion
        FOREIGN KEY (id_institucion)
            REFERENCES institucion (id_institucion);
ALTER TABLE organizacion ADD COLUMN area_especializacion character varying(255);
ALTER TABLE organizacion ADD COLUMN cantidad_alumnos INTEGER;
