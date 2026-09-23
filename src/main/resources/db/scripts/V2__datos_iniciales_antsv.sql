-- Catálogos y permisos de la aplicación vigente; sin usuarios ni datos operativos.

INSERT INTO public.rol (nombre, descripcion, estado) VALUES ('ADMINISTRADOR', 'Administrador de ANTSV', true);

INSERT INTO public.permiso (nombre, descripcion) VALUES
    ('aprobar-usuario:procesar', 'Permiso para aprobar/reclazar el usuario.'),
    ('archivos:crear', 'Permiso para crear un Archivo nuevo'),
    ('archivos:editar', 'Permiso para editar un Archivo'),
    ('archivos:eliminar', 'Permiso para eliminar un Archivo'),
    ('archivos:listar', 'Permiso para ver lista de archivos'),
    ('archivos:updateStatus', 'archivos:updateStatus'),
    ('archivos:ver', 'archivos:listar'),
    ('auditoria:list', 'auditoria:list'),
    ('auditoria:listar', 'Permiso para listar auditoria.'),
    ('auditoria:ver', 'Permiso para ver auditoria.'),
    ('metodo-registro:list', 'metodo-registro:list'),
    ('metodo-registro:listar', 'Listar Metodo de Registro'),
    ('notificacion:crear', 'notificacion:marcarComoLeida'),
    ('notificacion:marcarComoLeida', 'Permiso para marcar una notificación como leída'),
    ('notificacion:marcarComoNoLeida', 'Permiso para marcar una notificación como no leída'),
    ('notificacion:marcarTodasComoLeidas', 'Permiso para marcar todas las notificaciones como leídas'),
    ('notificacion:notificar', 'Permiso para enviar o disparar una notificación a un usuario'),
    ('permiso:create', 'permiso:create'),
    ('permiso:delete', 'permiso:delete'),
    ('permiso:list', 'permiso:list'),
    ('permiso:update', 'permiso:update'),
    ('permisos:crear', 'Permiso para crear permisos.'),
    ('permisos:editar', 'Permiso para editar permisos.'),
    ('permisos:listar', 'Permiso para listar permisos.'),
    ('permisos:ver', 'Permiso para ver permisos.'),
    ('rol:asociarPermisos', 'rol:asociarPermisos'),
    ('rol:create', 'rol:create'),
    ('rol:delete', 'rol:delete'),
    ('rol:list', 'rol:list'),
    ('rol:update', 'rol:update'),
    ('rol:updateStatus', 'rol:updateStatus'),
    ('roles:crear', 'Permiso para crear rol.'),
    ('roles:editar', 'Permiso para editar rol.'),
    ('roles:listar', 'Permiso para listar roles.'),
    ('roles:ver', 'Permiso para ver roles.'),
    ('tipo-documento:getByTipoRecurso', 'tipo-documento:getByTipoRecurso'),
    ('tipo-documento:getTiposRecurso', 'tipo-documento:getTiposRecurso'),
    ('tipo-documento:getTiposRecursoNombres', 'tipo-documento:getTiposRecursoNombres'),
    ('tipo-documento:updateStatus', 'tipo-documento:updateStatus'),
    ('tipoDocumento:cambiarEstado', 'Permiso para cambiar estado de tipos de documento'),
    ('tipoDocumento:crear', 'Permiso para crear tipos de documento'),
    ('tipoDocumento:editar', 'Permiso para editar tipos de documento'),
    ('tipoDocumento:listar', 'Permiso para listar los tipos de documento'),
    ('tipoDocumento:ver', 'Permiso para ver el módulo tipo documento'),
    ('usuarios:borrar', 'usuarios:borrar'),
    ('usuarios:crear', 'Permiso para crear usuarios.'),
    ('usuarios:editar', 'Permiso para editar usuarios.'),
    ('usuarios:editarClave', 'Permiso para cambiar clave de usuario.'),
    ('usuarios:editarClaveUsuarioAdmin', 'Permiso para cambio de credenciales de usuarios'),
    ('usuarios:editarEstado', 'Permiso para cambiar estado de usuario.'),
    ('usuarios:editarMiClave', 'Permiso para editar claves de usuario.'),
    ('usuarios:editarMiPerfil', 'Permiso para editar perfil de usuario.'),
    ('usuarios:listar', 'Permiso para listar usuarios.'),
    ('usuarios:obtenerMiPerfil', 'Permiso para obtener perfil de usuario.'),
    ('usuarios:obtenerOrganizaciones', 'Permiso para obtener organizaciones de un usuario'),
    ('usuarios:ver', 'Permiso para ver usuarios.');

INSERT INTO public.rol_permiso (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso FROM public.rol r CROSS JOIN public.permiso p
WHERE r.nombre = 'ADMINISTRADOR';

INSERT INTO public.metodo_registro (nombre, codigo, estado) VALUES
    ('Identidad Electrónica', 'IE', true),
    ('Menú Usuario', 'MU', true);

insert into tipo_organizacion(nombre, descripcion, estado) values ('Sin organización formal','Usá esta opción si aún no formás parte de una organización, o si trabajás de forma individual', true);
UPDATE tipo_organizacion set codigo = 'SO' WHERE nombre = 'Sin organización';

-- Estos nombres siguen formando parte del contrato del servicio de archivos.
INSERT INTO public.tipo_recurso (nombre, estado) VALUES
    ('SISTEMA', true);

INSERT INTO public.tipo_documento (nombre, descripcion, id_tipo_recurso, estado)
SELECT 'Archivo', 'Tipo de documento por defecto para ' || nombre, id_tipo_recurso, true
FROM public.tipo_recurso;
