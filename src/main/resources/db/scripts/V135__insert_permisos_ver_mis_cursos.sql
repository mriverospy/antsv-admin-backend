--Se insertar permisos faltantes para ver el modulo de Mis Cursos
-- Permisos faltantes para ver el módulo de Mis Cursos
INSERT INTO permiso (nombre, descripcion)
SELECT v.nombre, v.descripcion
FROM (VALUES
    ('misCursos:ver', 'Permiso para ver mis cursos.'),
    ('misCursos:listar', 'Permiso para listar mis cursos.'),
    ('cursos:obtenerTiposCursos', 'Permiso para obtener tipos de cursos.'),
    ('cursos:obtenerOganizaciones', 'Permiso para obtener organizaciones de cursos.')
) AS v(nombre, descripcion)
WHERE NOT EXISTS (
    SELECT 1
    FROM permiso p
    WHERE p.nombre = v.nombre
);

INSERT INTO public.rol_permiso (id_rol, id_permiso)
SELECT
    r.id_rol,
    p.id_permiso
FROM public.rol r
JOIN public.permiso p
    ON p.nombre IN (
        'misCursos:ver',
        'misCursos:listar',
        'cursos:obtenerTiposCursos',
        'cursos:obtenerOganizaciones'
    )
WHERE r.nombre IN (
    'Mentor',
    'Administrador de Organización',
    'Gestor de Programas',
    'Evaluador de Postulaciones',
    'Usuario Público',
    'Postulante'
)
AND NOT EXISTS (
    SELECT 1
    FROM public.rol_permiso rp
    WHERE rp.id_rol = r.id_rol
      AND rp.id_permiso = p.id_permiso
);
