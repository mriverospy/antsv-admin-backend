-- Script de carga de permisos para el módulo de Preferencias de Clientes
-- Autor: Luis Cardozo
-- Fecha: 2025-11-04

-- Insertar permisos para preferencias de clientes
INSERT INTO public.permiso(nombre, descripcion) VALUES('clientes:listarPreferencias', 'Permiso para listar las preferencias de comunicación de los clientes');
INSERT INTO public.permiso(nombre, descripcion) VALUES('clientes:crearPreferencia', 'Permiso para crear una preferencia de comunicación para un cliente');
INSERT INTO public.permiso(nombre, descripcion) VALUES('clientes:actualizarPreferencia', 'Permiso para actualizar el estado de una preferencia de comunicación');
INSERT INTO public.permiso(nombre, descripcion) VALUES('clientes:eliminarPreferenciasCliente', 'Permiso para eliminar las preferencias de comunicación de un cliente');

-- Asignar permisos al rol Administrador (id_rol = 1)
INSERT INTO public.rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM public.permiso p
WHERE p.nombre IN ('clientes:listarPreferencias', 'clientes:crearPreferencia', 'clientes:actualizarPreferencia', 'clientes:eliminarPreferenciasCliente')
AND NOT EXISTS (
    SELECT 1 FROM public.rol_permiso rp
    WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);

