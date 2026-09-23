--Nombre Script: V25__create_permisos_archivos
-- Script de carga de permisos y rol_permisos para el modulo Archivos.
insert into permiso(nombre, descripcion) values('archivos:ver', 'Permiso para ver Archivo');
insert into permiso(nombre, descripcion) values('archivos:listar', 'Permiso para ver lista de archivos');
insert into permiso(nombre, descripcion) values('archivos:crear', 'Permiso para crear un Archivo nuevo');
insert into permiso(nombre, descripcion) values('archivos:editar', 'Permiso para editar un Archivo');
insert into permiso(nombre, descripcion) values('archivos:eliminar', 'Permiso para eliminar un Archivo');

insert into rol_permiso (id_rol, id_permiso)
select 1, p.id_permiso
from permiso p
where p.nombre in ('archivos:ver','archivos:listar','archivos:crear','archivos:editar','archivos:eliminar')
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso 
)