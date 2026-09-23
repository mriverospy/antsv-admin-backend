-- =====================================
-- TABLA DE RESPUESTAS DE ACTIVIDADES
-- =====================================

CREATE TABLE IF NOT EXISTS public.programa_mentor_actividad_respuesta (
    id_programa_mentor_actividad_respuesta SERIAL PRIMARY KEY,
    id_programa_mentor_actividad INT NOT NULL,
    id_organizacion INT NOT NULL,
    respuesta TEXT NULL,
    id_recurso INT NULL,
    fecha_respuesta TIMESTAMP DEFAULT now(),
    calificacion INT NULL,
    comentarios_evaluacion TEXT NULL,
    fecha_evaluacion TIMESTAMP NULL,
    id_usuario_evaluador INT NULL,
    estado VARCHAR(30) DEFAULT 'PENDIENTE',
    CONSTRAINT fk_actividad_respuesta FOREIGN KEY (id_programa_mentor_actividad)
        REFERENCES public.programa_mentor_actividad(id_programa_mentor_actividad),
    CONSTRAINT fk_respuesta_organizacion FOREIGN KEY (id_organizacion)
        REFERENCES public.organizacion(id_organizacion),
    CONSTRAINT fk_respuesta_recurso FOREIGN KEY (id_recurso)
        REFERENCES public.recurso(id_recurso),
    CONSTRAINT fk_respuesta_evaluador FOREIGN KEY (id_usuario_evaluador)
        REFERENCES public.usuario(id_usuario)
);

-- Índices para optimización
CREATE INDEX IF NOT EXISTS idx_actividad_respuesta_actividad ON public.programa_mentor_actividad_respuesta(id_programa_mentor_actividad);
CREATE INDEX IF NOT EXISTS idx_actividad_respuesta_organizacion ON public.programa_mentor_actividad_respuesta(id_organizacion);
CREATE INDEX IF NOT EXISTS idx_actividad_respuesta_estado ON public.programa_mentor_actividad_respuesta(estado);

-- Comentarios
COMMENT ON TABLE public.programa_mentor_actividad_respuesta IS 'Respuestas de organizaciones postulantes a actividades del programa';
COMMENT ON COLUMN public.programa_mentor_actividad_respuesta.estado IS 'Estados: PENDIENTE, ENTREGADO, EN_REVISION, EVALUADO';

