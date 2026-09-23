insert into permiso(nombre, descripcion) values('mi-organizacion:ver', 'Permiso para ver la sección de Mi Organización');

INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'mi-organizacion:ver'
)
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);
