-- Permisos para ABM de instituciones
INSERT INTO permiso (nombre, descripcion)
SELECT v.nombre, v.descripcion
FROM (VALUES
    ('institucion:listar', 'Permiso para listar instituciones.'),
    ('institucion:crear', 'Permiso para crear instituciones.'),
    ('institucion:editar', 'Permiso para editar instituciones.'),
    ('institucion:borrar', 'Permiso para borrar instituciones.')
) AS v(nombre, descripcion)
WHERE NOT EXISTS (
    SELECT 1
    FROM permiso p
    WHERE p.nombre = v.nombre
);

-- Asignar permisos de instituciones al rol ADMINISTRADOR (id_rol = 1)
INSERT INTO public.rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM public.permiso p
WHERE p.nombre IN (
  'institucion:listar',
  'institucion:crear',
  'institucion:editar',
  'institucion:borrar'
)
AND NOT EXISTS (
  SELECT 1
  FROM public.rol_permiso rp
  WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);
