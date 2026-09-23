/*Crear permiso para el modulo de programas postulaciones*/
INSERT INTO permiso (nombre, descripcion)
SELECT v.nombre, v.descripcion
FROM (VALUES
    ('programas:postulaciones', 'Permiso para habilitar la postulacion de un usuario a un programa desde la Landing')
) AS v(nombre, descripcion)
WHERE NOT EXISTS (
    SELECT 1
    FROM permiso p
    WHERE p.nombre = v.nombre
);

/*Permisos para roles modulo Programas Postulaciones */
INSERT INTO public.rol_permiso (id_rol, id_permiso)
SELECT
    r.id_rol,
    p.id_permiso
FROM public.rol r
JOIN public.permiso p
    ON p.nombre IN (
        'programas:postulaciones'
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
