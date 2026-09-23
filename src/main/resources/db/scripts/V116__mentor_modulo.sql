--V116_mentor_modulo.sql
--Script de creacion de tablas Modulo: MENTORIAS

-- =====================================
-- 1. DISPONIBILIDAD DE MENTORÍAS
-- =====================================

CREATE TABLE IF NOT EXISTS programa_mentor_disponibilidad (
    id_programa_mentor_disponibilidad SERIAL PRIMARY KEY,
    id_mentor INTEGER NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT now(),
    fecha_actualizacion TIMESTAMP DEFAULT now()
);

ALTER TABLE programa_mentor_disponibilidad
    ADD CONSTRAINT fk_disponibilidad_mentor
        FOREIGN KEY (id_mentor)
        REFERENCES mentor(id_mentor);

-- =====================================
-- 2. SESIONES AGENDADAS
-- =====================================

CREATE TABLE IF NOT EXISTS programa_mentor_sesion (
    id_programa_mentor_sesion SERIAL PRIMARY KEY,
    id_programa_mentor_disponibilidad INTEGER NOT NULL,
    id_programa INTEGER NOT NULL,
    id_usuario INTEGER NOT NULL,
    estado VARCHAR(20) DEFAULT 'AGENDADO',
    fecha_creacion TIMESTAMP DEFAULT now()
);

ALTER TABLE programa_mentor_sesion
    ADD CONSTRAINT fk_sesion_disponibilidad
        FOREIGN KEY (id_programa_mentor_disponibilidad)
        REFERENCES programa_mentor_disponibilidad(id_programa_mentor_disponibilidad);

ALTER TABLE programa_mentor_sesion
    ADD CONSTRAINT fk_sesion_programa
        FOREIGN KEY (id_programa)
        REFERENCES programa(id_programa);

ALTER TABLE programa_mentor_sesion
    ADD CONSTRAINT fk_sesion_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario);

-- Índice único para evitar doble reserva
CREATE UNIQUE INDEX IF NOT EXISTS uq_sesion_unica
    ON programa_mentor_sesion(id_programa_mentor_disponibilidad)
    WHERE estado = 'AGENDADO';

-- =====================================
-- 3. HISTORIAL DE SESIÓN
-- =====================================

CREATE TABLE IF NOT EXISTS programa_mentor_sesion_historial (
    id_programa_mentor_sesion_historial SERIAL PRIMARY KEY,
    id_programa_mentor_sesion INTEGER NOT NULL,
    fecha DATE NOT NULL,
    temas_tratados TEXT NOT NULL,
    observaciones TEXT NULL,
    fecha_creacion TIMESTAMP DEFAULT now()
);

ALTER TABLE programa_mentor_sesion_historial
    ADD CONSTRAINT fk_historial_sesion
        FOREIGN KEY (id_programa_mentor_sesion)
        REFERENCES programa_mentor_sesion(id_programa_mentor_sesion);

-- =====================================
-- 4. EVALUACIÓN POST SESIÓN
-- =====================================

CREATE TABLE IF NOT EXISTS programa_mentor_sesion_evaluacion (
    id_programa_mentor_sesion_evaluacion SERIAL PRIMARY KEY,
    id_programa_mentor_sesion INTEGER NOT NULL,
    evaluador INTEGER NOT NULL,
    tipo_evaluador VARCHAR(20) NOT NULL,
    puntuacion INTEGER NULL,
    comentarios TEXT NULL,
    fecha_creacion TIMESTAMP DEFAULT now()
);

ALTER TABLE programa_mentor_sesion_evaluacion
    ADD CONSTRAINT fk_evaluacion_sesion
        FOREIGN KEY (id_programa_mentor_sesion)
        REFERENCES programa_mentor_sesion(id_programa_mentor_sesion);

ALTER TABLE programa_mentor_sesion_evaluacion
    ADD CONSTRAINT fk_evaluacion_usuario
        FOREIGN KEY (evaluador)
        REFERENCES usuario(id_usuario);

-- =====================================
-- 5. ETAPAS DEL PROCESO DE MENTORÍA
-- =====================================

CREATE TABLE IF NOT EXISTS programa_mentor_etapa (
    id_programa_mentor_etapa SERIAL PRIMARY KEY,
    id_programa INTEGER NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT NULL,
    orden INTEGER NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT now(),
    fecha_actualizacion TIMESTAMP DEFAULT now()
);

ALTER TABLE programa_mentor_etapa
    ADD CONSTRAINT fk_etapa_programa
        FOREIGN KEY (id_programa)
        REFERENCES programa(id_programa);

-- =====================================
-- 6. ACTIVIDADES POR ETAPA (CRONOGRAMA EDITABLE)
-- =====================================

CREATE TABLE IF NOT EXISTS programa_mentor_actividad (
    id_programa_mentor_actividad SERIAL PRIMARY KEY,
    id_programa_mentor_etapa INTEGER NOT NULL,
    id_programa_mentor_sesion INTEGER NULL,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT NULL,
    fecha_inicio DATE NULL,
    fecha_fin DATE NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    fecha_creacion TIMESTAMP DEFAULT now(),
    fecha_actualizacion TIMESTAMP DEFAULT now()
);

ALTER TABLE programa_mentor_actividad
    ADD CONSTRAINT fk_actividad_etapa
        FOREIGN KEY (id_programa_mentor_etapa)
        REFERENCES programa_mentor_etapa(id_programa_mentor_etapa);

ALTER TABLE programa_mentor_actividad
    ADD CONSTRAINT fk_actividad_sesion
        FOREIGN KEY (id_programa_mentor_sesion)
        REFERENCES programa_mentor_sesion(id_programa_mentor_sesion);

-- =====================================
-- 7. MENSAJERÍA ENTRE MENTOR Y BENEFICIADO
-- =====================================

CREATE TABLE IF NOT EXISTS programa_mentor_mensaje (
    id_programa_mentor_mensaje SERIAL PRIMARY KEY,
    id_programa_mentor_sesion INTEGER NOT NULL,
    id_remitente INTEGER NOT NULL,
    mensaje TEXT NOT NULL,
    fecha_envio TIMESTAMP DEFAULT now()
);

ALTER TABLE programa_mentor_mensaje
    ADD CONSTRAINT fk_mensaje_sesion
        FOREIGN KEY (id_programa_mentor_sesion)
        REFERENCES programa_mentor_sesion(id_programa_mentor_sesion);

ALTER TABLE programa_mentor_mensaje
    ADD CONSTRAINT fk_mensaje_remitente
        FOREIGN KEY (id_remitente)
        REFERENCES usuario(id_usuario);

