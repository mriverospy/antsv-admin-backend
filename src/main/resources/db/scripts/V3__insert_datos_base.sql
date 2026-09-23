

-- # ROL 
insert into rol(nombre, descripcion, estado) values ('ADMINISTRADOR', 'Rol Administrador', true);

-- # PERMISO
insert into permiso(nombre, descripcion) values 
('menu:administracion:ver','Ver permiso de Menú Seguridad'),
('usuarios:listar','Permiso para listar usuarios.'),
('usuarios:crear','Permiso para crear usuarios.'),
('usuarios:editar','Permiso para editar usuarios.'),
('usuarios:ver','Permiso para ver usuarios.'),
('usuarios:editarEstado','Permiso para cambiar estado de usuario.'),
('usuarios:editarClave','Permiso para cambiar clave de usuario.'),
('usuarios:asignarRol','Permiso para asignar rol a usuarios.'),
('permisos:listar','Permiso para listar permisos.'),
('permisos:crear','Permiso para crear permisos.'),
('permisos:editar','Permiso para editar permisos.'),
('permisos:borrar','Permiso para borrar permisos.'),
('permisos:ver','Permiso para ver permisos.'),
('roles:listar','Permiso para listar roles.'),
('roles:crear','Permiso para crear rol.'),
('roles:editar','Permiso para editar rol.'),
('roles:borrar','Permiso para borrar rol.'),
('roles:ver','Permiso para ver roles.'),
('auditoria:listar','Permiso para listar auditoria.'),
('auditoria:crear','Permiso para crear noticia.'),
('auditoria:editar','Permiso para editar noticia.'),
('auditoria:borrar','Permiso para borrar noticia.'),
('auditoria:ver','Permiso para ver auditoria.');


-- # ROL PERMISO
insert into rol_permiso(id_rol, id_permiso) values (1,1);
insert into rol_permiso(id_rol, id_permiso) values (1,2);
insert into rol_permiso(id_rol, id_permiso) values (1,3);
insert into rol_permiso(id_rol, id_permiso) values (1,4);
insert into rol_permiso(id_rol, id_permiso) values (1,5);
insert into rol_permiso(id_rol, id_permiso) values (1,6);
insert into rol_permiso(id_rol, id_permiso) values (1,7);
insert into rol_permiso(id_rol, id_permiso) values (1,8);
insert into rol_permiso(id_rol, id_permiso) values (1,9);
insert into rol_permiso(id_rol, id_permiso) values (1,10);
insert into rol_permiso(id_rol, id_permiso) values (1,11);
insert into rol_permiso(id_rol, id_permiso) values (1,12);
insert into rol_permiso(id_rol, id_permiso) values (1,13);
insert into rol_permiso(id_rol, id_permiso) values (1,14);
insert into rol_permiso(id_rol, id_permiso) values (1,15);
insert into rol_permiso(id_rol, id_permiso) values (1,16);
insert into rol_permiso(id_rol, id_permiso) values (1,17);
insert into rol_permiso(id_rol, id_permiso) values (1,18);
insert into rol_permiso(id_rol, id_permiso) values (1,19);
insert into rol_permiso(id_rol, id_permiso) values (1,20);
insert into rol_permiso(id_rol, id_permiso) values (1,21);
insert into rol_permiso(id_rol, id_permiso) values (1,22);
insert into rol_permiso(id_rol, id_permiso) values (1,23);


-- # USUARIO
insert into tipo_usuario(nombre, descripcion, estado) values ('PARAGUAYO', 'Usuario Paraguayo', true);
insert into tipo_usuario(nombre, descripcion, estado) values ('EXTRANJERO', 'Usuario Extranjero', true);

INSERT INTO public.usuario (
    usuario,
    nombre,
    apellido,
    email,
    nro_documento,
    salt,
    "password",
    id_tipo_usuario,
    fecha_creacion,
    fecha_expiracion,
    fecha_actualizacion,
    estado
) VALUES (
    'lcardozo',
    'Luis',
    'Cardozo',
    'luis.cardozo@example.com',
    '1234567',
    '', 
    '$2a$10$Yf5uaGNbiIfsYoW7V10LrOkuAkj7KnXfuzQefgcBTFxxvMrYtVILG', 
    1,
    NOW(),
    NOW() + INTERVAL '1 year',
    NOW(),
    true
);



--# USUARIO ROL
INSERT INTO public.usuario_rol (
    id_usuario,
    id_rol,
    estado
) VALUES (
    1,     -- id_usuario
    1,     -- id_rol
    true  -- estado activo
);


