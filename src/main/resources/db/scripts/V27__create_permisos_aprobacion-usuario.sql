insert into permiso(nombre, descripcion) values ('aprobar-usuario:listar','Permiso para listar los Usuarios pendientes de aprobacion');
insert into permiso(nombre, descripcion) values ('aprobar-usuario:ver','Permiso para ver detalles del usuario pendiente de probacion.');
insert into permiso(nombre, descripcion) values ('aprobar-usuario:procesar', 'Permiso para aprobar/reclazar el usuario.');


-- Asignar permisos interes:* al rol ADMINISTRADOR (id_rol = 1)
INSERT INTO rol_permiso (id_rol, id_permiso)
        SELECT 1, p.id_permiso
            FROM permiso p
            WHERE p.nombre IN (
                               'aprobar-usuario:listar',
                               'aprobar-usuario:ver',
                               'aprobar-usuario:procesar'
            )
        AND NOT EXISTS (
                SELECT 1 FROM rol_permiso rp WHERE rp.id_rol = 1
                    AND rp.id_permiso = p.id_permiso
        );