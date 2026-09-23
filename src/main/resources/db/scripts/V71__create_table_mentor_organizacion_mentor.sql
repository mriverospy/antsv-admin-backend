/*Script de actualizacion para bdd Modulo gestion mentores*/

	--Insertar el Rol Mentor en la tabla rol
	INSERT INTO rol (nombre,descripcion,estado) VALUES
	 ('MENTOR','Mentor HTV',true);

	--tabla mentor
	CREATE TABLE public.mentor (
	id_mentor serial4 NOT NULL,
	id_usuario int4 NOT NULL,
	subtitulo varchar(100) NULL,
	resumen text NULL,
	descripcion text NULL,
	id_catalogo_htv int4 NULL,
	referencia_imagen varchar(100) NULL,
	estado bool DEFAULT false NULL,
	fecha_creacion timestamp DEFAULT now() NULL,
	fecha_actualizacion timestamp NULL,
	CONSTRAINT mentor_pkey PRIMARY KEY (id_mentor),
	CONSTRAINT mentor_id_catalogo_htv_fkey FOREIGN KEY (id_catalogo_htv) REFERENCES public.catalogo_htv(id_catalogo_htv),
	CONSTRAINT mentor_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario)
);
	--Tabla organizacion_mentor
	CREATE TABLE public.organizacion_mentor (
	id_organizacion_mentor serial4 NOT NULL,
	id_organizacion int4 NOT NULL,
	id_usuario int4 NOT NULL,
	fecha_creacion timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	fecha_actualizacion timestamp NULL,
	estado bool NULL,
	visible_publico bool NULL,
	CONSTRAINT organizacion_mentor_pkey PRIMARY KEY (id_organizacion_mentor),
	CONSTRAINT organizacion_mentor_id_organizacion_fkey FOREIGN KEY (id_organizacion) REFERENCES public.organizacion(id_organizacion),
	CONSTRAINT organizacion_mentor_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario)
);

--PERMISOS PARA EL ROL ADMINISTRADOR
insert into permiso(nombre, descripcion) values('mentores:ver', 'Permiso para ver un Mentor');
insert into permiso(nombre, descripcion) values('mentores:listar', 'Permiso para listar mentores');
insert into permiso(nombre, descripcion) values('mentores:cambiarEstadoPublico', 'Permiso para cambiar visibilidad de un Mentor');
insert into permiso(nombre, descripcion) values('mentores:actualizarEstado', 'Permiso para activar/inactivar un Mentor');


-- INSERTAR PERMISOS PARA EL ROL ADMINISTRADOR
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT
    (SELECT id_rol FROM rol WHERE nombre = 'ADMINISTRADOR'), -- Obtener ID del Administrador por nombre
    p.id_permiso
FROM permiso p
WHERE p.nombre IN ('mentores:ver', 'mentores:listar', 'mentores:cambiarEstadoPublico', 'mentores:actualizarEstado')
  AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = (SELECT id_rol FROM rol WHERE nombre = 'ADMINISTRADOR')
      AND rp.id_permiso = p.id_permiso
);


--PERMISOS PARA EL ROL MENTOR
insert into permiso(nombre, descripcion) values('mentores:obtenerMiPerfilMentor', 'Permiso para obtener el perfil del Mentor');
insert into permiso(nombre, descripcion) values('mentores:actualizarMiPerfil', 'Permiso para actualziar el perfil del Mentor');
insert into permiso(nombre, descripcion) values('mentores:obtenerCatalogos', 'Obtener los catálogos HTV');

-- INSERTAR PERMISOS PARA EL ROL MENTOR
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT
    (SELECT id_rol FROM rol WHERE nombre = 'MENTOR'), -- Obtener ID del Mentor por nombre
    p.id_permiso
FROM permiso p
WHERE p.nombre IN ('mentores:obtenerCatalogos','mentores:actualizarMiPerfil', 'mentores:obtenerMiPerfilMentor','menu:administracion:ver','usuarios:obtenerMiPerfil','usuarios:editarMiPerfil','usuarios:editarMiClave')
  AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = (SELECT id_rol FROM rol WHERE nombre = 'MENTOR')
      AND rp.id_permiso = p.id_permiso
);