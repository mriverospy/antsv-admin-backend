--Nombre Script: V62__create_permisos_oportunidad_comercial
-- Script de carga de permisos y rol_permisos para el modulo Oportunidad Comercial.
insert into permiso(nombre, descripcion) values('oportunidad:ver', 'Permiso para ver una Oportunidad Comercial');
insert into permiso(nombre, descripcion) values('oportunidad:listar', 'Permiso para ver el módulo Oportunidad Comercials');
insert into permiso(nombre, descripcion) values('oportunidad:crear', 'Permiso para crear una nueva Oportunidad Comercial');
insert into permiso(nombre, descripcion) values('oportunidad:editar', 'Permiso para editar una Oportunidad Comercial');
insert into permiso(nombre, descripcion) values('oportunidad:eliminar', 'Permiso para eliminar una Oportunidad Comercial');
insert into permiso(nombre, descripcion) values('oportunidad:obtenerMoneda', 'Obtener las monedas');

insert into rol_permiso (id_rol, id_permiso)
select 1, p.id_permiso
from permiso p
where p.nombre in ('oportunidad:ver','oportunidad:listar','oportunidad:crear','oportunidad:editar','oportunidad:eliminar', 'oportunidad:obtenerMoneda')
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso 
);

-- Script de carga de permisos y rol_permisos para el modulo Cliente Productos Relacionados.
insert into permiso(nombre, descripcion) values('clienteProductoRelacion:ver', 'Permiso para ver un Producto relacionado');
insert into permiso(nombre, descripcion) values('clienteProductoRelacion:listar', 'Permiso para ver el módulo Producto relacionado');
insert into permiso(nombre, descripcion) values('clienteProductoRelacion:crear', 'Permiso para crear un nuevo Producto relacionado');
insert into permiso(nombre, descripcion) values('clienteProductoRelacion:editar', 'Permiso para editar un Producto relacionado');
insert into permiso(nombre, descripcion) values('clienteProductoRelacion:eliminar', 'Permiso para eliminar un Producto relacionado');
insert into permiso(nombre, descripcion) values('clienteProductoRelacion:obtenerOrganizacionProducto', 'Obtener los productos');

insert into rol_permiso (id_rol, id_permiso)
select 1, p.id_permiso
from permiso p
where p.nombre in ('clienteProductoRelacion:ver','clienteProductoRelacion:listar','clienteProductoRelacion:crear','clienteProductoRelacion:editar','clienteProductoRelacion:eliminar', 'clienteProductoRelacion:obtenerOrganizacionProducto')
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso 
)
