--Script para permisos de Tipos Programas
--Nombre: V96__insert_permisos_tipo_programa.sql

insert into permiso(nombre, descripcion) values('tipoPrograma:ver', 'Permiso para ver el modulo tipo programas');
insert into permiso(nombre, descripcion) values('tipoPrograma:listar', 'Permiso para listar los tipos de programas');
insert into permiso(nombre, descripcion) values('tipoPrograma:crear', 'Permiso para crear un nuevo tipo de programa');
insert into permiso(nombre, descripcion) values('tipoPrograma:editar', 'Permiso para editar un tipo programa');
insert into permiso(nombre, descripcion) values('tipoPrograma:cambiarEstado', 'Permiso para cambiar el estado de un tipo de programa');

--Asignar los permisos al rol Administrador HTV
insert into rol_permiso (id_rol, id_permiso)
select 1, p.id_permiso
from permiso p
where p.nombre in (
    'tipoPrograma:ver',
    'tipoPrograma:listar',
    'tipoPrograma:crear',
    'tipoPrograma:editar',
    'tipoPrograma:cambiarEstado'
)
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso
);


