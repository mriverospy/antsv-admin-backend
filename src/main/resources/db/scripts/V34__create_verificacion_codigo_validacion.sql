CREATE TABLE verificacion_codigo_validacion (
    id_verificacion_codigo SERIAL PRIMARY KEY,
    correo VARCHAR(255) NOT NULL,
    codigo VARCHAR(200) NOT NULL,
    usado BOOLEAN NOT NULL,
    fecha_registro TIMESTAMP NOT NULL
);