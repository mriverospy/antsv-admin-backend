-- Tabla Notificación Aprobaciones
CREATE TABLE public.usuario_aprobacion_historico (
            id_usuario_aprobacion_historico SERIAL PRIMARY KEY,
            id_usuario_aprobacion int4 NOT NULL,
            id_organizacion int4,
            id_usuario int4,
            observacion VARCHAR(500) NOT NULL,
            fecha_creacion TIMESTAMP DEFAULT NOW(),
            documento_original VARCHAR(200) NOT NULL,
            documento_nuevo VARCHAR(200),
            fecha_actualizacion TIMESTAMP,
            estado VARCHAR(200) NOT NULL, --PENDIENTE, RESPONDIDO
            codigo_validacion VARCHAR(200) NOT NULL,
            CONSTRAINT fk_notificacion_aprobaciones_usuario
                FOREIGN KEY (id_usuario_aprobacion)
                REFERENCES public.usuario (id_usuario),
            CONSTRAINT fk_id_organizacion_organizacion
                FOREIGN KEY (id_organizacion)
                    REFERENCES public.organizacion (id_organizacion),
            CONSTRAINT fk_notificacion_usuario_solicitante
                FOREIGN KEY (id_usuario)
                REFERENCES public.usuario (id_usuario)
);

ALTER TABLE organizacion
    ADD COLUMN id_usuario_creacion INT4;

ALTER TABLE organizacion
    ADD CONSTRAINT fk_organizacion_usuario_creacion
        FOREIGN KEY (id_usuario_creacion)
            REFERENCES public.usuario (id_usuario);

UPDATE organizacion o SET id_usuario_creacion = (
        select uo.id_usuario FROM usuario_organizacion uo where uo.id_organizacion = o.id_organizacion
            ORDER BY fecha_creacion
            LIMIT 1
    );

--NO se puede agregar esto ya que hay organizaciones que NO tienen asociacion en usuario_organizacion
--ALTER TABLE organizacion
--    ALTER COLUMN id_usuario_creacion SET NOT NULL;