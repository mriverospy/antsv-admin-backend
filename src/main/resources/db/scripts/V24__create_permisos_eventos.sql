--Nombre Script: V24__create_permisos_eventos
-- Script de carga de permisos y rol_permisos para el modulo Eventos.
insert into permiso(nombre, descripcion) values('eventos:ver', 'Permiso para ver un Evento');
insert into permiso(nombre, descripcion) values('eventos:listar', 'Permiso para ver el módulo Eventos');
insert into permiso(nombre, descripcion) values('eventos:crear', 'Permiso para crear un nuevo Evento');
insert into permiso(nombre, descripcion) values('eventos:editar', 'Permiso para editar un Evento');
insert into permiso(nombre, descripcion) values('eventos:eliminar', 'Permiso para eliminar un Evento');
insert into permiso(nombre, descripcion) values('eventos:obtenerCatalogos', 'Obtener los catálogos HTV');
insert into permiso(nombre, descripcion) values('eventos:obtenerTiposEventos', 'Obtener los tipos de eventos');
insert into permiso(nombre, descripcion) values('eventos:obtenerOganizaciones', 'Obtener organizaciones relacionadas al usuario');

insert into rol_permiso (id_rol, id_permiso)
select 1, p.id_permiso
from permiso p
where p.nombre in ('eventos:ver','eventos:listar','eventos:crear','eventos:editar','eventos:eliminar', 'eventos:obtenerCatalogos','eventos:obtenerTiposEventos','eventos:obtenerOganizaciones')
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso 
)