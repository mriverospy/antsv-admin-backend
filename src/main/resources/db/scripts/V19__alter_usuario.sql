alter table usuario add column nacionalidad character varying(50);

INSERT INTO metodo_registro (nombre, estado) VALUES ('Identidad Electrónica', true);
INSERT INTO metodo_registro (nombre, estado) VALUES ('Básico', true);
INSERT INTO metodo_registro (nombre, estado) VALUES ('LinkedIn', true);


CREATE TABLE usuario_metodo_registro (
    id_usuario_metodo_registro SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL,
    id_metodo_registro INTEGER NOT NULL,
    CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
    CONSTRAINT fk_metodo_registro FOREIGN KEY (id_metodo_registro) REFERENCES metodo_registro (id_metodo_registro)
);