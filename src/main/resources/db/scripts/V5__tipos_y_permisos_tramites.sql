-- Catálogo inicial sin habilitar presentaciones. No contiene requisitos oficiales confirmados.
INSERT INTO permiso(nombre,descripcion) SELECT 'tramites:ver','Consultar solicitudes propias' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='tramites:ver');
INSERT INTO permiso(nombre,descripcion) SELECT 'tramites:crear','Iniciar solicitudes' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='tramites:crear');
INSERT INTO permiso(nombre,descripcion) SELECT 'tramites:editar','Editar y subsanar solicitudes propias' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='tramites:editar');
INSERT INTO permiso(nombre,descripcion) SELECT 'tramites:presentar','Presentar solicitudes propias' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='tramites:presentar');
INSERT INTO permiso(nombre,descripcion) SELECT 'bandejas:ver','Consultar expedientes institucionales presentados' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='bandejas:ver');
INSERT INTO permiso(nombre,descripcion) SELECT 'tramites:asignar','Asignar funcionarios a expedientes' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='tramites:asignar');
INSERT INTO permiso(nombre,descripcion) SELECT 'tramites:revisar','Revisar expedientes asignados' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='tramites:revisar');
INSERT INTO permiso(nombre,descripcion) SELECT 'tramites:resolver','Resolver expedientes asignados' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='tramites:resolver');
INSERT INTO permiso(nombre,descripcion) SELECT 'formularios:administrar','Administrar tipos y borradores de formularios' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='formularios:administrar');
INSERT INTO permiso(nombre,descripcion) SELECT 'formularios:publicar','Publicar versiones de formularios' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='formularios:publicar');

INSERT INTO rol(nombre,descripcion,estado)
SELECT 'TRAMITANTE_ANTSV','Solicitante de trámites ANTSV',true WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre='TRAMITANTE_ANTSV');
INSERT INTO rol(nombre,descripcion,estado)
SELECT 'REVISOR_ANTSV','Revisor de trámites ANTSV',true WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre='REVISOR_ANTSV');
INSERT INTO rol(nombre,descripcion,estado)
SELECT 'SUPERVISOR_TRAMITES_ANTSV','Supervisor de trámites ANTSV',true WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre='SUPERVISOR_TRAMITES_ANTSV');
INSERT INTO rol_permiso(id_rol,id_permiso)
SELECT r.id_rol,p.id_permiso FROM rol r CROSS JOIN permiso p
WHERE ((r.nombre='ADMINISTRADOR' AND p.nombre IN ('tramites:ver','tramites:crear','tramites:editar','tramites:presentar','bandejas:ver','tramites:asignar','tramites:revisar','tramites:resolver','formularios:administrar','formularios:publicar'))
OR (r.nombre='TRAMITANTE_ANTSV' AND p.nombre IN ('tramites:ver','tramites:crear','tramites:editar','tramites:presentar','usuarios:obtenerMiPerfil','usuarios:editarMiPerfil'))
OR (r.nombre='REVISOR_ANTSV' AND p.nombre IN ('bandejas:ver','tramites:revisar'))
OR (r.nombre='SUPERVISOR_TRAMITES_ANTSV' AND p.nombre IN ('bandejas:ver','tramites:asignar','tramites:revisar','tramites:resolver')))
AND NOT EXISTS (SELECT 1 FROM rol_permiso rp WHERE rp.id_rol=r.id_rol AND rp.id_permiso=p.id_permiso);
INSERT INTO tipo_tramite(codigo,nombre,descripcion,activo,permite_solicitud,orden) VALUES ('CANJE_CO_PY','Canje de licencias Colombia–Paraguay','Pendiente de completar y validar requisitos institucionales.',true,false,0);
INSERT INTO tipo_tramite(codigo,nombre,descripcion,activo,permite_solicitud,orden) VALUES ('CERT_TARJETAS_LICENCIA','Certificación de proveedores de tarjetas de licencias','Pendiente de completar y validar requisitos institucionales.',true,false,1);
INSERT INTO tipo_tramite(codigo,nombre,descripcion,activo,permite_solicitud,orden) VALUES ('CERT_TARJETAS_HABILITACION','Certificación de proveedores de tarjetas de habilitación','Pendiente de completar y validar requisitos institucionales.',true,false,2);
INSERT INTO tipo_tramite(codigo,nombre,descripcion,activo,permite_solicitud,orden) VALUES ('INSTRUCTORES_ECVA','Acreditación y registro de instructores ECVA','Pendiente de completar y validar requisitos institucionales.',true,false,3);
INSERT INTO tipo_tramite(codigo,nombre,descripcion,activo,permite_solicitud,orden) VALUES ('CANJE_CL_PY','Canje de licencias Chile–Paraguay','Pendiente de completar y validar requisitos institucionales.',true,false,4);
INSERT INTO tipo_tramite(codigo,nombre,descripcion,activo,permite_solicitud,orden) VALUES ('ECVA_MATRIZ','Habilitación o renovación ECVA – Matriz','Pendiente de completar y validar requisitos institucionales.',true,false,5);
INSERT INTO tipo_tramite(codigo,nombre,descripcion,activo,permite_solicitud,orden) VALUES ('HOMOLOGACION_PSICOFISICOS','Homologación de equipos psicofísicos','Pendiente de completar y validar requisitos institucionales.',true,false,6);
INSERT INTO tipo_tramite(codigo,nombre,descripcion,activo,permite_solicitud,orden) VALUES ('RENOVACION_PSICOFISICOS','Renovación de homologación de equipos psicofísicos','Pendiente de completar y validar requisitos institucionales.',true,false,7);

INSERT INTO formulario(id_tipo_tramite,nombre,numero_version,estado)
SELECT id_tipo_tramite,nombre,1,'BORRADOR' FROM tipo_tramite;
INSERT INTO formulario_seccion(id_formulario,codigo,titulo,orden)
SELECT id_formulario,'datos','Datos de la solicitud',0 FROM formulario;
-- Propuesta de captura para el piloto; requiere revisión funcional antes de publicación.
INSERT INTO formulario_seccion(id_formulario,codigo,titulo,orden)
SELECT f.id_formulario,'representante','Representante legal',1 FROM formulario f JOIN tipo_tramite t USING(id_tipo_tramite)
WHERE t.codigo='CERT_TARJETAS_LICENCIA';
INSERT INTO formulario_campo(id_formulario,seccion,codigo,etiqueta,tipo,requerido,longitud_maxima,orden)
SELECT f.id_formulario,c.seccion,c.codigo,c.etiqueta,c.tipo,true,c.longitud,c.orden
FROM formulario f JOIN tipo_tramite t USING(id_tipo_tramite)
CROSS JOIN (VALUES ('datos','razon_social','Razón social','TEXT',250,0),('datos','ruc','RUC','TEXT',30,1),
('representante','nombre_representante','Nombre y apellido','TEXT',250,0),
('representante','documento_representante','Documento de identidad','TEXT',50,1),
('representante','correo_representante','Correo electrónico','EMAIL',250,2)) AS c(seccion,codigo,etiqueta,tipo,longitud,orden)
WHERE t.codigo='CERT_TARJETAS_LICENCIA';
