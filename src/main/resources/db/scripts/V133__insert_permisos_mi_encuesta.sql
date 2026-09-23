--Permisos para el modulo de Mis Cursos y su encuesta asociada
insert into permiso(nombre, descripcion) values('misCursos:ver', 'Permiso para ver mis cursos.');
insert into permiso(nombre, descripcion) values('misCursos:listar', 'Permiso para listar mis cursos.');
insert into permiso(nombre, descripcion) values('cursosEncuestaPregunta:listarPreguntas', 'Permiso para listar las preguntas de la encuesta.');

INSERT INTO public.rol_permiso (id_rol, id_permiso)
SELECT
    r.id_rol,
    p.id_permiso
FROM public.permiso p
JOIN public.rol r
    ON r.nombre = 'Mentor'
WHERE p.nombre IN (
    'misCursos:ver',
    'misCursos:listar',
    'cursosEncuesta:listar',
    'cursosEncuestaPregunta:listarPreguntas'
)
AND NOT EXISTS (
    SELECT 1
    FROM public.rol_permiso rp
    WHERE rp.id_rol = r.id_rol
      AND rp.id_permiso = p.id_permiso
);

INSERT INTO public.rol_permiso (id_rol, id_permiso)
SELECT
    r.id_rol,
    p.id_permiso
FROM public.permiso p
JOIN public.rol r
    ON r.nombre = 'Administrador de Organización'
WHERE p.nombre IN (
    'misCursos:ver',
    'misCursos:listar',
    'cursosEncuesta:listar',
    'cursosEncuestaPregunta:listarPreguntas'
)
AND NOT EXISTS (
    SELECT 1
    FROM public.rol_permiso rp
    WHERE rp.id_rol = r.id_rol
      AND rp.id_permiso = p.id_permiso
);

INSERT INTO public.rol_permiso (id_rol, id_permiso)
SELECT
    r.id_rol,
    p.id_permiso
FROM public.permiso p
JOIN public.rol r
    ON r.nombre = 'Gestor de Programas'
WHERE p.nombre IN (
    'misCursos:ver',
    'misCursos:listar',
    'cursosEncuesta:listar',
    'cursosEncuestaPregunta:listarPreguntas'
)
AND NOT EXISTS (
    SELECT 1
    FROM public.rol_permiso rp
    WHERE rp.id_rol = r.id_rol
      AND rp.id_permiso = p.id_permiso
);


INSERT INTO public.rol_permiso (id_rol, id_permiso)
SELECT
    r.id_rol,
    p.id_permiso
FROM public.permiso p
JOIN public.rol r
    ON r.nombre = 'Evaluador de Postulaciones'
WHERE p.nombre IN (
    'misCursos:ver',
    'misCursos:listar',
    'cursosEncuesta:listar',
    'cursosEncuestaPregunta:listarPreguntas'
)
AND NOT EXISTS (
    SELECT 1
    FROM public.rol_permiso rp
    WHERE rp.id_rol = r.id_rol
      AND rp.id_permiso = p.id_permiso
);

INSERT INTO public.rol_permiso (id_rol, id_permiso)
SELECT
    r.id_rol,
    p.id_permiso
FROM public.permiso p
JOIN public.rol r
    ON r.nombre = 'Usuario Público'
WHERE p.nombre IN (
    'misCursos:ver',
    'misCursos:listar',
    'cursosEncuesta:listar',
    'cursosEncuestaPregunta:listarPreguntas'
)
AND NOT EXISTS (
    SELECT 1
    FROM public.rol_permiso rp
    WHERE rp.id_rol = r.id_rol
      AND rp.id_permiso = p.id_permiso
);


INSERT INTO public.rol_permiso (id_rol, id_permiso)
SELECT
    r.id_rol,
    p.id_permiso
FROM public.permiso p
JOIN public.rol r
    ON r.nombre = 'Postulante'
WHERE p.nombre IN (
    'misCursos:ver',
    'misCursos:listar',
    'cursosEncuesta:listar',
    'cursosEncuestaPregunta:listarPreguntas'
)
AND NOT EXISTS (
    SELECT 1
    FROM public.rol_permiso rp
    WHERE rp.id_rol = r.id_rol
      AND rp.id_permiso = p.id_permiso
);
