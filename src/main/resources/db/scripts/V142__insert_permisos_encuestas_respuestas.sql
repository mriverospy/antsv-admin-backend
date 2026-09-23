
--Script permisos para ver inscripciones y respuestas de encuestas

INSERT INTO permiso (nombre, descripcion)
SELECT v.nombre, v.descripcion
FROM (VALUES
    ('inscripciones-encuestas:ver', 'Permiso para ver la lista de inscripciones para responder la encuesta.'),
    ('inscripciones-respuestas:ver', 'Permiso para ver las respuestas de los inscriptos.'),
    ('curso-inscripciones:listarInscripcionesEncuestas', 'Listar solamente las inscripciones que tienen respuestas.')
) AS v(nombre, descripcion)
WHERE NOT EXISTS (
    SELECT 1
    FROM permiso p
    WHERE p.nombre = v.nombre
);

/*Permisos para rol Administrador de Organizacion para visualizar respuestas de encuestados*/
INSERT INTO public.rol_permiso (id_rol, id_permiso)
SELECT
    r.id_rol,
    p.id_permiso
FROM public.rol r
JOIN public.permiso p
    ON p.nombre IN (
        'inscripciones-encuestas:ver',
        'inscripciones-respuestas:ver',
        'curso-inscripciones:listarInscripcionesEncuestas'
        
    )
WHERE r.nombre IN (
    'Administrador de Organización'
)
AND NOT EXISTS (
    SELECT 1
    FROM public.rol_permiso rp
    WHERE rp.id_rol = r.id_rol
      AND rp.id_permiso = p.id_permiso
);
