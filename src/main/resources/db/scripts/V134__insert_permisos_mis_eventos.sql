--Permisos para ver el modulo de Mis Eventos
-- Permisos para el módulo de Mis Eventos
INSERT INTO permiso (nombre, descripcion)
SELECT v.nombre, v.descripcion
FROM (VALUES
    ('misEventos:ver', 'Permiso para ver mis eventos.'),
    ('misEventos:listar', 'Permiso para listar mis eventos.')
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
        'misEventos:ver',
        'misEventos:listar',
        'eventos:obtenerTiposEventos',
        'eventos:obtenerOganizaciones'
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
