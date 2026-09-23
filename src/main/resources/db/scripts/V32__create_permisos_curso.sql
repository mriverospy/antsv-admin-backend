--Nombre Script: V31__create_permisos_cursos
-- Script de carga de permisos y rol_permisos para el modulo Cursos.
insert into permiso(nombre, descripcion) values('cursos:ver', 'Permiso para ver un Curso');
insert into permiso(nombre, descripcion) values('cursos:listar', 'Permiso para ver el módulo cursos');
insert into permiso(nombre, descripcion) values('cursos:crear', 'Permiso para crear un nuevo Curso');
insert into permiso(nombre, descripcion) values('cursos:editar', 'Permiso para editar un Curso');
insert into permiso(nombre, descripcion) values('cursos:eliminar', 'Permiso para eliminar un Curso');
insert into permiso(nombre, descripcion) values('cursos:obtenerCatalogos', 'Obtener los catálogos HTV');
insert into permiso(nombre, descripcion) values('cursos:obtenerTiposCursos', 'Obtener los tipos de cursos');
insert into permiso(nombre, descripcion) values('cursos:obtenerOganizaciones', 'Obtener organizaciones relacionadas al usuario');

insert into rol_permiso (id_rol, id_permiso)
select 1, p.id_permiso
from permiso p
where p.nombre in ('cursos:ver','cursos:listar','cursos:crear','cursos:editar','cursos:eliminar', 'cursos:obtenerCatalogos','cursos:obtenerTiposCursos','cursos:obtenerOganizaciones')
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso 
)