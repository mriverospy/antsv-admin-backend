insert into permiso(nombre, descripcion) values ('organizacion:listar','Permiso para listar las Organizaciones');
insert into permiso(nombre, descripcion) values ('organizacion:crear','Permiso para crear organizaciones.');
insert into permiso(nombre, descripcion) values ('organizacion:editar','Permiso para editar organizaciones.');
insert into permiso(nombre, descripcion) values ('organizacion:borrar','Permiso para borrar organizaciones.');
insert into permiso(nombre, descripcion) values ('organizacion:ver','Permiso para ver organizaciones.');


-- Asignar permisos interes:* al rol ADMINISTRADOR (id_rol = 1)
INSERT INTO rol_permiso (id_rol, id_permiso)
        SELECT 1, p.id_permiso
            FROM permiso p
            WHERE p.nombre IN (
                               'organizacion:listar',
                               'organizacion:crear',
                               'organizacion:editar',
                               'organizacion:borrar',
                               'organizacion:ver'
            )
        AND NOT EXISTS (
                SELECT 1 FROM rol_permiso rp WHERE rp.id_rol = 1
                    AND rp.id_permiso = p.id_permiso
        );