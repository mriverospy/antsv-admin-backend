--Script para actualizar tabla usuario y agregar permisos de las nuevas funcionalidades
--columna de biografia
ALTER table public.usuario ADD COLUMN  biografia text;
--columna para imagen de bdd mongoDB
ALTER TABLE public.usuario ADD COLUMN foto_perfil_id varchar(50) NULL;
insert into permiso(nombre, descripcion) values('usuarios:editarMiClave', 'Permiso para editar claves de usuario.');
insert into rol_permiso (id_rol, id_permiso) values (1,
(select p.id_permiso
from permiso p
where p.nombre in ('usuarios:editarMiClave')
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso 
)));

--permiso para modulo de Editar biografia
insert into permiso(nombre, descripcion) values('usuarios:editarMiPerfil', 'Permiso para editar perfil de usuario.');
insert into rol_permiso (id_rol, id_permiso) values (1,
(select p.id_permiso
from permiso p
where p.nombre in ('usuarios:editarMiPerfil')
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso 
)));

insert into permiso(nombre, descripcion) values('usuarios:obtenerMiPerfil', 'Permiso para obtener perfil de usuario.');
insert into rol_permiso (id_rol, id_permiso) values (1,
(select p.id_permiso
from permiso p
where p.nombre in ('usuarios:obtenerMiPerfil')
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso 
)));
