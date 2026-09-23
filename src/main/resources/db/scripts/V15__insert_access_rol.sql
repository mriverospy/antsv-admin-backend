--Nombre Script: V__16_insert_access_rol
-- Script de carga de permisos y rol_permisos para el modulo Organizacion Producto (Productos).
insert into permiso(nombre, descripcion) values('productos:ver', 'Permiso para ver un Producto o Servicio');
insert into permiso(nombre, descripcion) values('productos:listar', 'Permiso para ver el módulo Productos y Servicios');
insert into permiso(nombre, descripcion) values('productos:crear', 'Permiso para crear un nuevo Producto o Servicio');
insert into permiso(nombre, descripcion) values('productos:editar', 'Permiso para editar un Producto o Servicio');
insert into permiso(nombre, descripcion) values('productos:eliminar', 'Permiso para eliminar un Producto o Servicio');
insert into permiso(nombre, descripcion) values('productos:obtenerCatalogos', 'Obtener los catálogos HTV');
insert into permiso(nombre, descripcion) values('productos:obtenerTiposProductos', 'Obtener los tipos de productos');
insert into permiso(nombre, descripcion) values('productos:obtenerOganizaciones', 'Obtener organizaciones relacionadas al usuario');

insert into rol_permiso (id_rol, id_permiso)
select 1, p.id_permiso
from permiso p
where p.nombre in ('productos:ver','productos:listar','productos:crear','productos:editar','productos:eliminar', 'productos:obtenerCatalogos','productos:obtenerTiposProductos','productos:obtenerOganizaciones')
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso 
)