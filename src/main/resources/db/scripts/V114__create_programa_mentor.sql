--V114_create_programa_mentor.sql
CREATE TABLE programa_mentor (
    id_programa_mentor SERIAL PRIMARY KEY,
    id_programa INTEGER NOT NULL,
    id_mentor INTEGER NOT NULL,
    id_usuario_creacion INTEGER NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT now(),
    fecha_actualizacion TIMESTAMP DEFAULT now(),
    estado BOOLEAN
);

ALTER TABLE programa_mentor
    ADD CONSTRAINT fk_programa
        FOREIGN KEY (id_programa) REFERENCES programa(id_programa);

ALTER TABLE programa_mentor
    ADD CONSTRAINT fk_mentor
        FOREIGN KEY (id_mentor) REFERENCES mentor(id_mentor);

ALTER TABLE programa_mentor
    ADD CONSTRAINT fk_usuario_creacion
        FOREIGN KEY (id_usuario_creacion) REFERENCES usuario(id_usuario);
       