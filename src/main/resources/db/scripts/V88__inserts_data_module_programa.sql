-- =====================================
-- INSERTS: Estados del Programa
-- =====================================

INSERT INTO programa_estado (nombre, descripcion, estado) VALUES
('Borrador', 'Programa en etapa de elaboración inicial.', TRUE),
('En revisión', 'Programa enviado para revisión y pendiente de aprobación.', TRUE),
('Aprobado', 'Programa aprobado por el comité o responsable correspondiente.', TRUE),
('Publicado', 'Programa publicado y disponible para postulaciones o visualización.', TRUE),
('Cerrado', 'Programa cerrado temporal o permanentemente.', TRUE),
('Finalizado', 'Programa completado con todas sus etapas concluidas.', TRUE),
('Rechazado', 'Programa rechazado en la etapa de revisión o aprobación.', TRUE);

-- =====================================
-- INSERTS: Estados de Postulación
-- =====================================

INSERT INTO estado_postulacion (nombre, estado) VALUES
('Nueva', TRUE),
('En revisión', TRUE),
('Aprobada', TRUE),
('Rechazada', TRUE),
('Finalizada', TRUE);

-- =====================================
-- INSERTS: Tipo de Recurso - PROGRAMA
-- =====================================
insert into tipo_recurso (nombre,estado)values('PROGRAMA', true);
insert into tipo_recurso (nombre,estado)values('POSTULACION', true);
INSERT INTO tipo_recurso (nombre, estado) VALUES ('COMITE', true);


-- PERMISOS MÓDULO PROGRAMAS
insert into permiso(nombre, descripcion) values('programas:ver', 'Permiso para ver un Programa');
insert into permiso(nombre, descripcion) values('programas:listar', 'Permiso para listar el módulo Programas');
insert into permiso(nombre, descripcion) values('programas:crear', 'Permiso para crear un nuevo Programa');
insert into permiso(nombre, descripcion) values('programas:editar', 'Permiso para editar un Programa');
insert into permiso(nombre, descripcion) values('programas:revisar', 'Permiso para revisar un Programa');
insert into permiso(nombre, descripcion) values('programas:publicar', 'Permiso para publicar un Programa');
insert into permiso(nombre, descripcion) values('programas:cerrar', 'Permiso para cerrar un Programa');
insert into permiso(nombre, descripcion) values('programas:finalizar', 'Permiso para finalizar un Programa');
insert into permiso(nombre, descripcion) values('programas:rechazar', 'Permiso para rechazar un Programa');
insert into permiso(nombre, descripcion) values('programas:aprobar', 'Permiso para aprobar un Programa');
insert into permiso(nombre, descripcion) values('programas:borrador', 'Permiso para enviar a borrador un Programa');
insert into permiso(nombre, descripcion) values('programas:obtenerTiposProgramas', 'Permiso para obtener tipos de Programas');
insert into permiso(nombre, descripcion) values('programas:obtenerUsuariosResponsables', 'Permiso para obtener usuarios');
insert into permiso(nombre, descripcion) values('programas:obtenerEstados', 'Permiso para obtener los estados de programas');


-- PERMISOS MÓDULO PROGRAMAS VERSIONES

insert into permiso(nombre, descripcion) values('programasVersion:ver', 'Permiso para ver la version de un Programa');
insert into permiso(nombre, descripcion) values('programasVersion:listar', 'Permiso para listar las versiones de un Programa');
insert into permiso(nombre, descripcion) values('programasVersion:cambiarEstado', 'Permiso para cambiar el estado de una version');

--PERMISOS MODULOS PROGRAMAS HITOS
insert into permiso(nombre, descripcion) values('programasHitos:ver', 'Permiso para ver el modulo  Hito del programa');
insert into permiso(nombre, descripcion) values('programasHitos:listar', 'Permiso para listar el los Hitos');
insert into permiso(nombre, descripcion) values('programasHitos:crear', 'Permiso para crear un nuevo Hito');
insert into permiso(nombre, descripcion) values('programasHitos:editar', 'Permiso para editar un Hito');
insert into permiso(nombre, descripcion) values('programasHitos:cambiarEstado', 'Permiso para cambiar el estado de un hito');


--PERMISOS MODULOS PROGRAMAS HITOS
insert into permiso(nombre, descripcion) values('programasRevisiones:ver', 'Permiso para ver el modulo de Revisiones de Programas');
insert into permiso(nombre, descripcion) values('programasRevisiones:listar', 'Permiso para listar las Revisiones');
insert into permiso(nombre, descripcion) values('programasRevisiones:crear', 'Permiso para crear una nueva Revision');
insert into permiso(nombre, descripcion) values('programasRevisiones:editar', 'Permiso para editar una Revision');


--PERMISOS MODULOS POSTULACIONES A PROGRAMAS
insert into permiso(nombre, descripcion) values('postulacionProgramas:ver', 'Permiso para ver el modulo de programas que se pueden postular');
insert into permiso(nombre, descripcion) values('postulacionProgramas:listar', 'Permiso para listar los programas a postular');
insert into permiso(nombre, descripcion) values('postulacionProgramas:crear', 'Permiso para crear una nueva postulacion');
insert into permiso(nombre, descripcion) values('postulacionProgramas:editar', 'Permiso para editar una postulacion');
insert into permiso(nombre, descripcion) values('postulacionProgramas:cambiarEstado', 'Permiso para cambiar el estado de una postulacion');
insert into permiso(nombre, descripcion) values('postulacionProgramas:obtenerOrganizaciones', 'Permiso para obtener organizaciones');
insert into permiso(nombre, descripcion) values('postulacionProgramas:obtenerEstadosPostulaciones', 'Permiso para obtener estado de postulaciones');

--PERMISOS MODULOS POSTULACIONES OBSERVACIONES
insert into permiso(nombre, descripcion) values('postulacionObservaciones:ver', 'Permiso para ver el modulo de observacion de postulaciones');
insert into permiso(nombre, descripcion) values('postulacionObservaciones:listar', 'Permiso para listar observaciones de postulacion');
insert into permiso(nombre, descripcion) values('postulacionObservaciones:crear', 'Permiso para crear una nueva observacion');
insert into permiso(nombre, descripcion) values('postulacionObservaciones:editar', 'Permiso para editar una observacion');
insert into permiso(nombre, descripcion) values('postulacionObservaciones:cambiarEstado', 'Permiso para cambiar el estado una observacion');


--PERMISOS MODULOS COMITE EVALUACIONES
insert into permiso(nombre, descripcion) values('comiteEvaluaciones:ver', 'Permiso para ver el modulo de comités');
insert into permiso(nombre, descripcion) values('comiteEvaluaciones:listar', 'Permiso para listar comités');
insert into permiso(nombre, descripcion) values('comiteEvaluaciones:crear', 'Permiso para crear un comité');
insert into permiso(nombre, descripcion) values('comiteEvaluaciones:editar', 'Permiso para editar un comité');
insert into permiso(nombre, descripcion) values('comiteEvaluaciones:cambiarEstado', 'Permiso para cambiar el estado un comités');
insert into permiso(nombre, descripcion) values('comiteEvaluaciones:obtenerProgramas', 'Permiso para obtener programas aprobados');


--PERMISOS MODULOS COMITE MIEMBROS
insert into permiso(nombre, descripcion) values('comiteMiembros:ver', 'Permiso para ver el modulo de miembros');
insert into permiso(nombre, descripcion) values('comiteMiembros:listar', 'Permiso para listar miembros');
insert into permiso(nombre, descripcion) values('comiteMiembros:crear', 'Permiso para crear un miembro de comité');
insert into permiso(nombre, descripcion) values('comiteMiembros:editar', 'Permiso para editar un miembro de comite');
insert into permiso(nombre, descripcion) values('comiteMiembros:cambiarEstado', 'Permiso para cambiar el estado de un miembro');
insert into permiso(nombre, descripcion) values('comiteMiembros:obtenerUsuarios', 'Permiso para obtener usuarios para miembros');


--PERMISOS MODULOS COMITE MIEMBROS

insert into permiso(nombre, descripcion) values('evaluacionPostulaciones:ver', 'Permiso para ver el la evaluación de una postulación');
insert into permiso(nombre, descripcion) values('evaluacionPostulaciones:listar', 'Permiso para listar evaluaciones');
insert into permiso(nombre, descripcion) values('evaluacionPostulaciones:crear', 'Permiso para crear una evaluación de postulación');
insert into permiso(nombre, descripcion) values('evaluacionPostulaciones:editar', 'Permiso para editar una evaluación de postulación');
insert into permiso(nombre, descripcion) values('evaluacionPostulaciones:cambiarEstadoRevision', 'Permiso para cambiar el estado de una postulación a Revisión');
insert into permiso(nombre, descripcion) values('evaluacionPostulaciones:evaluarPostulacion', 'Permiso para agregar una evaluación a una postulación');
insert into permiso(nombre, descripcion) values('evaluacionPostulaciones:verEvaluacionPostulacion', 'Visualizar el resultado de una postulación');
insert into permiso(nombre, descripcion) values('evaluacionPostulaciones:cambiarEstadoFinalizado', 'Permiso para cambiar el estado de una postulación a Finalizado');



-- ASIGNAR PERMISOS AL ROL ADMINISTRADOR HTV (id_rol = 1)
insert into rol_permiso (id_rol, id_permiso)
select 1, p.id_permiso
from permiso p
where p.nombre in (
    'programas:ver',
    'programas:listar',
    'programas:crear',
    'programas:editar',
    'programas:publicar',
    'programas:cerrar',
    'programas:revisar',
    'programas:finalizar',
    'programas:aprobar',
    'programas:rechazar',
    'programas:obtenerTiposProgramas',
    'programas:obtenerUsuariosResponsables',
    'programas:obtenerEstados',
    'programasVersion:ver',
    'programasVersion:listar',
    'programasVersion:cambiarEstado',
    'postulacionProgramas:ver',
    'postulacionProgramas:listar',
    'postulacionProgramas:crear',
    'postulacionProgramas:editar',
    'postulacionProgramas:CambiarEstado',
    'postulacionProgramas:getOrganizaciones',
    'postulacionProgramas:getEstadosPostulaciones',
    'comiteEvaluaciones:ver',
    'comiteEvaluaciones:listar',
    'comiteEvaluaciones:crear',
    'comiteEvaluaciones:editar',
    'comiteEvaluaciones:cambiarEstado',
    'comiteEvaluaciones:obtenerProgramas',
    'comiteMiembros:ver',
    'comiteMiembros:listar',
    'comiteMiembros:crear',
    'comiteMiembros:editar',
    'comiteMiembros:cambiarEstado',
    'comiteMiembros:obtenerUsuarios',
    'evaluacionPostulaciones:cambiarEstadoRevision',
    'evaluacionPostulaciones:evaluarPostulacion',
    'evaluacionPostulaciones:verEvaluacionPostulacion',
    'evaluacionPostulaciones:cambiarEstadoFinalizado'
)
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso
);



-- ========================================================
-- ROLES DEL MÓDULO DE PROGRAMAS
-- ========================================================

-- Responsable del Programa
INSERT INTO rol (nombre, descripcion)
VALUES (
  'Responsable Programa',
  'Usuario responsable designado por la institución creadora del programa. Supervisa y gestiona el desarrollo del programa.'
);
--Asignar permisos al rol Responsable modulo de Programas
insert into rol_permiso (id_rol, id_permiso)
select (select id_rol from rol where nombre = 'Responsable Programa'), p.id_permiso
from permiso p
where p.nombre in (
	'menu:administracion:ver',
	'usuarios:editarMiClave',
	'usuarios:editarMiPerfil',
	'usuarios:obtenerMiPerfil',
    'programas:ver',
    'programas:listar',
    'programas:crear',
    'programas:publicar',
    'programas:revisar',
    'programas:editar',
    'programas:obtenerTiposProgramas',
    'programas:obtenerUsuariosResponsables',
    'programas:obtenerEstados',
    'programasHitos:ver',
    'programasHitos:listar',
    'programasHitos:crear',
    'programasHitos:editar',
    'programasHitos:cambiarEstado',
    'programasVersion:ver',
    'programasVersion:listar',
    'archivos:ver',
	'archivos:listar',
	'archivos:crear',
	'archivos:editar',
	'archivos:eliminar',
	'programasRevisiones:ver',
    'programasRevisiones:listar'
)
and not exists (
	select 4 from rol_permiso rp
	where rp.id_rol in (select id_rol from rol where nombre = 'Responsable Programa')
	and rp.id_permiso = p.id_permiso
);


-- Evaluador / Comité de Evaluación
INSERT INTO rol (nombre, descripcion)
VALUES (
  'Evaluador Programa',
  'Usuario con permisos de evaluación o integrante del comité encargado de revisar postulaciones y emitir dictámenes.'
);

--INSERTAR LOS PERMISOS NECESARIOS PARA EL ROL REVISOR
insert into rol_permiso (id_rol, id_permiso)
select (select id_rol from rol where nombre = 'Evaluador Programa'), p.id_permiso
from permiso p
where p.nombre in (
	'menu:administracion:ver',
	'usuarios:editarMiClave',
	'usuarios:editarMiPerfil',
	'usuarios:obtenerMiPerfil',
    'programas:ver',
    'programas:listar',
    'programas:aprobar',
    'programas:rechazar',
    'programas:publicar',
    'programas:borrador'
    'archivos:ver',
	'archivos:listar',
	'archivos:crear',
	'archivos:editar',
	'archivos:eliminar',
	'programasVersion:ver',
    'programasVersion:listar',
    'programasHitos:ver',
    'programasHitos:listar',
    'programasRevisiones:ver',
    'programasRevisiones:listar',
    'programasRevisiones:crear',
    'programasRevisiones:editar',
    'programas:obtenerTiposProgramas',
    'programas:obtenerUsuariosResponsables',
    'programas:obtenerEstados',
    'postulacionProgramas:ver',
    'postulacionProgramas:listar',
    'postulacionProgramas:obtenerOrganizaciones',
    'postulacionProgramas:obtenerEstadosPostulaciones',
    'archivos:ver',
	'archivos:listar',
	'postulacionObservaciones:ver'
    'postulacionObservaciones:listar',
    'postulacionObservaciones:crear',
    'postulacionObservaciones:editar',
    'postulacionObservaciones:cambiarEstado',
    'evaluacionPostulaciones:cambiarEstadoRevision',
    'evaluacionPostulaciones:evaluarPostulacion',
    'evaluacionPostulaciones:verEvaluacionPostulacion',
    'evaluacionPostulaciones:cambiarEstadoFinalizado',
    'evaluacionPostulaciones:ver',
    'evaluacionPostulaciones:listar',
    'evaluacionPostulaciones:crear',
    'evaluacionPostulaciones:editar'
)
and not exists (
	select 5 from rol_permiso rp
	where rp.id_rol in (select id_rol from rol where nombre = 'Evaluador Programa')
	and rp.id_permiso = p.id_permiso
);


-- Administrador de Organización Postulante
INSERT INTO rol (nombre, descripcion)
VALUES (
  'Organizacion Postulante',
  'Usuario externo que representa a una organización postulante.'
);

--PERMISOS PARA USUARIO CON ROL ORGANIZACION POSTULANTE
insert into rol_permiso (id_rol, id_permiso)
select (select id_rol from rol where nombre = 'Organizacion Postulante'), p.id_permiso
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
    'postulacionObservaciones:listar'
)
and not exists (
	select 6 from rol_permiso rp
	where rp.id_rol in (select id_rol from rol where nombre = 'Organizacion Postulante')
	and rp.id_permiso = p.id_permiso
);


-- Usuario General
INSERT INTO rol (nombre, descripcion)
VALUES (
  'Usuario General',
  'Usuario sin permisos especiales, solo consulta.'
);





