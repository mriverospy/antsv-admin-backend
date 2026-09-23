--Nombre Script: V132__create_permisos_cursos_encuesta
-- Script de carga de permisos y rol_permisos para el modulo Encuestas.
insert into permiso(nombre, descripcion) values('cursosEncuesta:ver', 'Permiso para ver un Curso Encuesta');
insert into permiso(nombre, descripcion) values('cursosEncuesta:listar', 'Permiso para ver el módulo Curso Encuesta');
insert into permiso(nombre, descripcion) values('cursosEncuesta:crear', 'Permiso para crear un nuevo Curso Encuesta');
insert into permiso(nombre, descripcion) values('cursosEncuesta:editar', 'Permiso para editar un Curso Encuesta');
insert into permiso(nombre, descripcion) values('cursosEncuesta:cambiarEstado', 'Permiso para cambiar estado de un Curso Encuesta');
insert into permiso(nombre, descripcion) values('cursosEncuesta:cambiarEstadoVisible', 'Permiso para cambiar estado visible de una Encuesta');


insert into rol_permiso (id_rol, id_permiso)
select 6, p.id_permiso
from permiso p
where p.nombre in (
    'cursosEncuesta:ver',
    'cursosEncuesta:listar',
    'cursosEncuesta:crear',
    'cursosEncuesta:editar',
    'cursosEncuesta:cambiarEstado',
    'cursosEncuesta:cambiarEstadoVisible'
)
and not exists (
    select 6 from rol_permiso rp
    where rp.id_rol = 6
    and rp.id_permiso = p.id_permiso
);

insert into permiso(nombre, descripcion) values('cursosEncuestaPregunta:ver', 'Permiso para ver una pregunta de la Encuesta');
insert into permiso(nombre, descripcion) values('cursosEncuestaPregunta:listar', 'Permiso para ver el módulo Preguntas de la Encuesta');
insert into permiso(nombre, descripcion) values('cursosEncuestaPregunta:crear', 'Permiso para crear una nueva Pregunta de la Encuesta');
insert into permiso(nombre, descripcion) values('cursosEncuestaPregunta:editar', 'Permiso para editar una Pregunta de la Encuesta');
insert into permiso(nombre, descripcion) values('cursosEncuestaPregunta:cambiarEstado', 'Permiso para cambiar estado de una Pregunta de la Encuesta');



insert into rol_permiso (id_rol, id_permiso)
select 6, p.id_permiso
from permiso p
where p.nombre in (
    'cursosEncuestaPregunta:ver',
    'cursosEncuestaPregunta:listar',
    'cursosEncuestaPregunta:crear',
    'cursosEncuestaPregunta:editar',
    'cursosEncuestaPregunta:cambiarEstado'
)
and not exists (
    select 6 from rol_permiso rp
    where rp.id_rol = 6
    and rp.id_permiso = p.id_permiso
);


