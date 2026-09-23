/*Script para creacion de tablas Modulo: PROGRAMAS*/



-- =====================================
-- TABLAS DE APOYO
-- =====================================

CREATE TABLE tipo_programa (
    id_tipo_programa SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    estado BOOLEAN DEFAULT TRUE
);

CREATE TABLE programa_estado (
    id_programa_estado SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    estado BOOLEAN DEFAULT TRUE
);

ALTER TABLE programa_estado ADD CONSTRAINT uq_programa_estado_nombre UNIQUE (nombre);

CREATE TABLE estado_postulacion (
    id_estado_postulacion SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    estado BOOLEAN DEFAULT TRUE
);

ALTER TABLE estado_postulacion ADD CONSTRAINT uq_estado_postulacion_nombre UNIQUE (nombre);


-- =====================================
-- TABLA PRINCIPAL: PROGRAMA
-- =====================================

CREATE TABLE programa (
    id_programa SERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    id_organizacion INTEGER REFERENCES organizacion(id_organizacion),
    id_tipo_programa INTEGER REFERENCES tipo_programa(id_tipo_programa),
    id_responsable INTEGER REFERENCES usuario(id_usuario), 
    id_programa_estado INTEGER REFERENCES programa_estado(id_programa_estado),
    fecha_inicio DATE,
    fecha_fin DATE,
    id_recurso INTEGER references recurso(id_recurso),
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW()
);

-- =====================================
-- TABLAS RELACIONADAS A PROGRAMA
-- =====================================

CREATE TABLE programa_hito (
    id_hito SERIAL PRIMARY KEY,
    id_programa INTEGER REFERENCES programa(id_programa) ON DELETE CASCADE,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    fecha_inicio DATE,
    fecha_fin DATE,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW(),
    estado BOOLEAN DEFAULT TRUE
);

CREATE TABLE programa_version (
    id_version SERIAL PRIMARY KEY,
    id_programa INTEGER REFERENCES programa(id_programa) ON DELETE CASCADE,
    id_usuario INTEGER REFERENCES usuario(id_usuario), 
    fecha_version DATE DEFAULT NOW(),
    descripcion_cambio TEXT,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW(),
    estado BOOLEAN DEFAULT TRUE
);

CREATE TABLE programa_revision (
    id_revision SERIAL PRIMARY KEY,
    id_programa INTEGER REFERENCES programa(id_programa) ON DELETE CASCADE,
    id_revisor INTEGER  REFERENCES usuario(id_usuario), 
    id_usuario_solicitante INTEGER REFERENCES usuario(id_usuario), 
    id_version INTEGER REFERENCES programa_version(id_version),
    fecha_revision DATE,
    estado_revision VARCHAR(50) CHECK (estado_revision IN ('pendiente','aprobado','rechazado')),
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW(),
    observacion TEXT
);

-- =====================================
-- TABLAS DE POSTULACIÓN
-- =====================================

CREATE TABLE postulacion (
    id_postulacion SERIAL PRIMARY KEY,
    id_programa INTEGER REFERENCES programa(id_programa),
   	id_organizacion INTEGER  REFERENCES organizacion(id_organizacion),
    id_postulacion_estado INTEGER REFERENCES estado_postulacion(id_estado_postulacion),
    id_recurso INTEGER references recurso(id_recurso),
    fecha_postulacion DATE,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW()
);

CREATE TABLE postulacion_detalle (
    id_detalle SERIAL PRIMARY KEY,
    id_postulacion INTEGER REFERENCES postulacion(id_postulacion) ON DELETE CASCADE,
    campo VARCHAR(100) NOT NULL,
    valor TEXT
);

CREATE TABLE postulacion_observacion (
    id_observacion SERIAL PRIMARY KEY,
    id_postulacion INTEGER REFERENCES postulacion(id_postulacion) ON DELETE CASCADE,
    id_usuario INTEGER REFERENCES usuario(id_usuario), 
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW(),
    comentario TEXT,
    estado BOOLEAN DEFAULT TRUE
);

-- =====================================
-- COMITÉ DE EVALUACIÓN
-- =====================================

CREATE TABLE comite_evaluacion (
    id_comite SERIAL PRIMARY KEY,
    id_programa INTEGER REFERENCES programa(id_programa) ON DELETE CASCADE,
    nombre VARCHAR(200) NOT NULL,
    id_recurso  INTEGER references recurso(id_recurso),
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW(),
    estado VARCHAR(20) CHECK (estado IN ('ACTIVO','INACTIVO')) DEFAULT 'ACTIVO'
);

CREATE TABLE comite_miembro (
    id_miembro SERIAL PRIMARY KEY,
    id_comite INTEGER REFERENCES comite_evaluacion(id_comite) ON DELETE CASCADE,
    id_usuario INTEGER REFERENCES usuario(id_usuario), 
    perfil VARCHAR(50) CHECK (perfil IN ('presidente','evaluador','secretario'))
);

-- =====================================
-- EVALUACIÓN DE POSTULACIONES
-- =====================================

CREATE TABLE evaluacion_postulacion (
    id_evaluacion SERIAL PRIMARY KEY,
    id_postulacion INTEGER REFERENCES postulacion(id_postulacion) ON DELETE CASCADE,
    id_comite INTEGER REFERENCES comite_evaluacion(id_comite),
    id_usuario INTEGER REFERENCES usuario(id_usuario), 
    puntaje NUMERIC(10,2),
    ponderacion NUMERIC(5,2),
    observacion TEXT,
    decision VARCHAR(20) CHECK (decision IN ('aprobado','rechazado','reserva')),
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW(),
    estado BOOLEAN DEFAULT TRUE
);
