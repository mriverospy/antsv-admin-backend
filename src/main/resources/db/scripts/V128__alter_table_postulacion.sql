/*Script de modificacion de la tabla postulacion*/
ALTER TABLE public.postulacion
ADD COLUMN id_usuario INTEGER NULL,
ADD COLUMN motivacion TEXT NULL,
ADD COLUMN experiencia_previa TEXT NULL,
ADD COLUMN area_interes TEXT NULL;


ALTER TABLE public.postulacion
ADD CONSTRAINT postulacion_id_usuario_fkey
FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);