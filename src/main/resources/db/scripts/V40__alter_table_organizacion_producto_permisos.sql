-- Agregar la columna estado con default true (si todavía no existe)
ALTER TABLE public.organizacion_producto
ADD COLUMN IF NOT EXISTS estado boolean DEFAULT true;

-- Actualizar todas las filas existentes a true
UPDATE public.organizacion_producto
SET estado = true;

-- Opcional: impedir nulos
ALTER TABLE public.organizacion_producto
ALTER COLUMN estado SET NOT NULL;

--En el apartado de agregar contenido el permiso dice productos:eliminar, se cambia a productos:AdministrarContenido
insert into permiso(nombre, descripcion) values('productos:administrarContenido', 'Permiso para administrar contenido en el modulo de Productos');
insert into rol_permiso (id_rol, id_permiso)
select 1, p.id_permiso
from permiso p
where p.nombre in ('productos:administrarContenido')
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso 
)