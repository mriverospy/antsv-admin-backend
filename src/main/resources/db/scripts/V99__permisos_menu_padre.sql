insert into permiso(nombre, descripcion) values
                                             ('menu:crm:ver','Menú CRM'),
                                             ('menu:indicadores-metricas:ver','Menú Indicadores y Metricas'),
                                             ('menu:proceso-aprobacion:ver','Menú Proceso de Aprobación'),
                                             ('menu:administracion-sistema:ver','Menú Administracion del Sistema');

-- # ROL PERMISO
insert into rol_permiso(id_rol, id_permiso) values (1, (SELECT id_permiso FROM permiso WHERE nombre = 'menu:crm:ver'));
insert into rol_permiso(id_rol, id_permiso) values (1, (SELECT id_permiso FROM permiso WHERE nombre = 'menu:indicadores-metricas:ver'));
insert into rol_permiso(id_rol, id_permiso) values (1, (SELECT id_permiso FROM permiso WHERE nombre = 'menu:proceso-aprobacion:ver'));
insert into rol_permiso(id_rol, id_permiso) values (1, (SELECT id_permiso FROM permiso WHERE nombre = 'menu:administracion-sistema:ver'));