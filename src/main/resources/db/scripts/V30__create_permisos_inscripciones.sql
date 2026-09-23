--Permisos para el rol administrador en el modulo de inscripciones.
insert into permiso(nombre, descripcion) values('inscripciones:ver', 'Permiso para ver inscripciones');
insert into permiso(nombre, descripcion) values('inscripciones:listar', 'Permiso para ver lista de inscripciones');
insert into permiso(nombre, descripcion) values('inscripciones:crear', 'Permiso para agregar una Inscripcion');
insert into permiso(nombre, descripcion) values('inscripciones:eliminar', 'Permiso para eliminar una Inscripcion');
insert into permiso(nombre, descripcion) values('inscripciones:obtenerUsuarios', 'Permiso para litar usuarios');
insert into permiso(nombre, descripcion) values('inscripciones:cambiarEstado', 'Permiso para cambiar el estado de inscripcion');

insert into rol_permiso (id_rol, id_permiso)
select 1, p.id_permiso
from permiso p
where p.nombre in ('inscripciones:ver','inscripciones:listar','inscripciones:crear','inscripciones:eliminar','inscripciones:obtenerUsuarios', 'inscripciones:cambiarEstado')
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso 
)