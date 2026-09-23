/*PERMISOS A ROLES*/

--PERMISOS PARA EL ROL ORGANIZACION POSTULANTE

insert into rol_permiso (id_rol, id_permiso)
select 6, p.id_permiso
from permiso p
where p.nombre in (
	'menu:administracion:ver',
	'usuarios:editarMiClave',
	'usuarios:editarMiPerfil',
	'usuarios:obtenerMiPerfil',
	'postulacionProgramas:ver',
    'postulacionProgramas:listar',
    'postulacionProgramas:crear',
    'postulacionProgramas:editar',
    'postulacionProgramas:obtenerOrganizaciones',
    'postulacionProgramas:obtenerEstadosPostulaciones',
    'archivos:ver',
	'archivos:listar',
	'archivos:crear',
	'archivos:editar',
	'postulacionObservaciones:ver',
    'postulacionObservaciones:listar',
    'evaluacionPostulaciones:ver',
    'evaluacionPostulaciones:listar',
    'evaluacionPostulaciones:verEvaluacionPostulacion'
)
and not exists (
	select 6 from rol_permiso rp
	where rp.id_rol = 6
	and rp.id_permiso = p.id_permiso
);