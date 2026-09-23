-- PERMISOS PARA ADMINISTRADOR GESTION DE SECTORES CLIENTE

-- PERMISOS SECTORES CLIENTE
insert into permiso(nombre, descripcion) values('sectores-cliente:ver', 'Permiso para ver un Sector Cliente');
insert into permiso(nombre, descripcion) values('sectores-cliente:listar', 'Permiso para ver el módulo Sectores Cliente');
insert into permiso(nombre, descripcion) values('sectores-cliente:crear', 'Permiso para crear un nuevo Sector Cliente');
insert into permiso(nombre, descripcion) values('sectores-cliente:editar', 'Permiso para editar un Sector Cliente');
insert into permiso(nombre, descripcion) values('sectores-cliente:actualizarEstado', 'Permiso para actualizar estado de un Sector Cliente');
insert into permiso(nombre, descripcion) values('sectores-cliente:obtenerActivos', 'Obtener los sectores cliente activos');

-- ASIGNAR PERMISOS AL ROL ADMIN (id_rol = 1)
insert into rol_permiso (id_rol, id_permiso)
select 1, p.id_permiso
from permiso p
where p.nombre in (
    'sectores-cliente:ver',
    'sectores-cliente:listar',
    'sectores-cliente:crear',
    'sectores-cliente:editar',
    'sectores-cliente:actualizarEstado',
    'sectores-cliente:obtenerActivos'
)
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso
);
