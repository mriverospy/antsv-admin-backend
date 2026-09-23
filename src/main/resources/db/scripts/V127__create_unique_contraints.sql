--Se agregan constraints de tipo unique para Mentor y OrganizacionMentor.
ALTER TABLE public.mentor ADD CONSTRAINT mentor_unique UNIQUE (id_usuario);
ALTER TABLE public.organizacion_mentor ADD CONSTRAINT organizacion_mentor_unique UNIQUE (id_organizacion,id_usuario);
