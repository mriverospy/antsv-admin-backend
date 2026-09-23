--Nombre Script: V43__create_table_notificacion_insert_permisos
-- Script de creacion de tabla MOTIFICACION y carga de permisos y rol_permisos para el modulo Notificaciones.

CREATE TABLE public.notificacion (
    id_notificacion SERIAL PRIMARY KEY,       
    titulo VARCHAR(200) NOT NULL,                
    mensaje TEXT NOT NULL,                      
    tipo VARCHAR(50) NOT NULL,                                
    id_usuario_destino BIGINT NULL,             
    id_organizacion_destino BIGINT NULL,        
    fecha_emision TIMESTAMP NOT NULL DEFAULT NOW(), 
    fecha_lectura TIMESTAMP NULL,                
    emisor VARCHAR(100) NOT NULL,                                                
    estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente'                                            
);

COMMENT ON TABLE public.notificacion IS 'Tabla centralizada de notificaciones para usuarios y organizaciones.';
COMMENT ON COLUMN public.notificacion.id_notificacion IS 'Identificador único de la notificación.';
COMMENT ON COLUMN public.notificacion.titulo IS 'Título breve de la notificación (máx. 200 caracteres).';
COMMENT ON COLUMN public.notificacion.mensaje IS 'Mensaje completo de la notificación.';
COMMENT ON COLUMN public.notificacion.tipo IS 'Tipo de notificación: Sistema, Alertas, Recordatorio, Info.';
COMMENT ON COLUMN public.notificacion.id_usuario_destino IS 'Usuario destinatario de la notificación (FK con usuarios).';
COMMENT ON COLUMN public.notificacion.id_organizacion_destino IS 'Organización destinataria de la notificación (FK con organizaciones).';
COMMENT ON COLUMN public.notificacion.fecha_emision IS 'Fecha y hora de emisión de la notificación.';
COMMENT ON COLUMN public.notificacion.fecha_lectura IS 'Fecha y hora en que el usuario la leyó (NULL si aún no se leyó).';
COMMENT ON COLUMN public.notificacion.emisor IS 'Origen de la notificación: módulo que la generó (ej: Proyectos, Eventos, Sistema).';
COMMENT ON COLUMN public.notificacion.estado IS 'Estado actual de la notificación: Pendiente, Leida, Archivada.';


insert into permiso(nombre, descripcion) values('notificacion:crear', 'Permiso para crear una nueva notificación');
insert into permiso(nombre, descripcion) values('notificacion:marcarComoLeida', 'Permiso para marcar una notificación como leída');
insert into permiso(nombre, descripcion) values('notificacion:marcarComoNoLeida', 'Permiso para marcar una notificación como no leída');
insert into permiso(nombre, descripcion) values('notificacion:marcarTodasComoLeidas', 'Permiso para marcar todas las notificaciones como leídas');
insert into permiso(nombre, descripcion) values('notificacion:notificar', 'Permiso para enviar o disparar una notificación a un usuario');


insert into rol_permiso (id_rol, id_permiso)
select 1, p.id_permiso
from permiso p
where p.nombre in ('notificacion:crear','notificacion:marcarComoLeida','notificacion:marcarComoNoLeida','notificacion:marcarTodasComoLeidas','notificacion:notificar')
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso 
)