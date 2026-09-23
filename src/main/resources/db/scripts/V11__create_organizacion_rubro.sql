CREATE TABLE "organizacion_rubro" (
                                   "id_organizacion_rubro" serial PRIMARY KEY,
                                   "id_organizacion" INT NOT NULL REFERENCES organizacion(id_organizacion),
                                   "id_rubro" INT NOT NULL REFERENCES rubro(id_rubro),
                                   CONSTRAINT organizacion_rubro_id_organizacion_id_rubro_unique UNIQUE (id_organizacion, id_rubro)
                                );