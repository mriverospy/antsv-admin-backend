/*Crear permisos para el modulo de mis postulaciones*/
INSERT INTO permiso (nombre, descripcion)
SELECT v.nombre, v.descripcion
FROM (VALUES
    ('misPostulaciones:ver', 'Permiso para ver mis postulaciones.'),
    ('misPostulaciones:listar', 'Permiso para listar mis postulaciones.'),
    ('misPostulaciones:verPostulacionObservaciones', 'Permiso para ver las observaciones de una postulación.'),
    ('misPostulaciones:verActividadesPostulacion', 'Permiso para ver las actividades del postulante.'),
    ('misPostulaciones:verEvaluacionPostulacion', 'Permiso para ver la evaluación de una postulación.'),
    ('misPostulaciones:verProgramaMentorSesiones', 'Permiso para ver las sesiones de mentoría del programa.')
) AS v(nombre, descripcion)
WHERE NOT EXISTS (
    SELECT 1
    FROM permiso p
    WHERE p.nombre = v.nombre
);

/*Permisos para roles modulo Mis Postulaciones */
INSERT INTO public.rol_permiso (id_rol, id_permiso)
SELECT
    r.id_rol,
    p.id_permiso
FROM public.rol r
JOIN public.permiso p
    ON p.nombre IN (
        'misPostulaciones:ver',
        'misPostulaciones:listar',
        'misPostulaciones:verPostulacionObservaciones',
        'misPostulaciones:verActividadesPostulacion',
        'misPostulaciones:verEvaluacionPostulacion',
        'misPostulaciones:verProgramaMentorSesiones',
        'postulacionObservaciones:listar',
        'programa-mentor-actividad-respuesta:listar',
        'evaluacionPostulaciones:listar',
        'programa-mentor-sesion:listar',
        'programa-mentor-mensaje:listar',
        'programa-mentor-sesion-historial:listar',
        'programa-mentor-sesion-evaluacion:listar'
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
