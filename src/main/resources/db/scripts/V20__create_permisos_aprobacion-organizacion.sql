insert into permiso(nombre, descripcion) values ('aprobar-organizacion:listar','Permiso para listar las Organizacion pendientes de aprobacion');
insert into permiso(nombre, descripcion) values ('aprobar-organizacion:ver','Permiso para ver detalles de la organizacion pendiente de probacion.');
insert into permiso(nombre, descripcion) values ('aprobar-organizacion:procesar', 'Permiso para aprobar/reclazar la organizacion.');


-- Asignar permisos interes:* al rol ADMINISTRADOR (id_rol = 1)
INSERT INTO rol_permiso (id_rol, id_permiso)
        SELECT 1, p.id_permiso
            FROM permiso p
            WHERE p.nombre IN (
                               'aprobar-organizacion:listar',
                               'aprobar-organizacion:ver',
                               'aprobar-organizacion:procesar'
            )
        AND NOT EXISTS (
                SELECT 1 FROM rol_permiso rp WHERE rp.id_rol = 1
                    AND rp.id_permiso = p.id_permiso
        );