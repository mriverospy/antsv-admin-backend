-- Renombramiento de tabla inscripcion
ALTER TABLE inscripcion rename TO evento_inscripcion;

-- Permisos para inscripciones de eventos
--insert into permiso(nombre, descripcion) values('inscripciones:ver', 'Permiso para visualizar inscripciones');
--insert into permiso(nombre, descripcion) values('inscripciones:listar', 'Permiso para ver lista de inscripciones');
--insert into permiso(nombre, descripcion) values('inscripciones:crear', 'Permiso para crear una inscripcion');
--insert into permiso(nombre, descripcion) values('inscripciones:eliminar', 'Permiso para eliminar una inscripcion');
--insert into permiso(nombre, descripcion) values('inscripciones:obtenerUsuarios', 'Permiso para obtener usuarios de una inscripcion');
--insert into permiso(nombre, descripcion) values('inscripciones:cambiarEstado', 'Permiso para cambiar de el estado de una inscripcion');
--insert into rol_permiso (id_rol, id_permiso)
select 1, p.id_permiso
from permiso p
where p.nombre in ('inscripciones:ver','inscripciones:listar','inscripciones:crear','inscripciones:obtenerUsuarios',
'inscripciones:cambiarEstado','inscripciones:eliminar')
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso 
);



--Script para crear la tabla curso_inscripcion.

CREATE TABLE public.curso_inscripcion (
	id_inscripcion serial4 NOT NULL,
	id_curso int4 NOT NULL,
	id_usuario int4 NOT NULL,
	fecha_creacion timestamp DEFAULT now() NOT NULL,
	metodo_inscripcion varchar(20) NOT NULL,
	estado bool DEFAULT true NOT NULL,
	CONSTRAINT curso_inscripcion_metodo_inscripcion_check CHECK (((metodo_inscripcion)::text = ANY ((ARRAY['online'::character varying, 'manual'::character varying])::text[]))),
	CONSTRAINT curso_inscripcion_pkey PRIMARY KEY (id_inscripcion),
	CONSTRAINT uq_usuario_curso UNIQUE (id_curso, id_usuario),
	CONSTRAINT fk_curso FOREIGN KEY (id_curso) REFERENCES public.curso(id_curso),
	CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario)
);

-- Permisos para el rol administrador en el módulo de curso_inscripciones.
insert into permiso(nombre, descripcion) values('curso-inscripciones:ver', 'Permiso para ver inscripciones de cursos');
insert into permiso(nombre, descripcion) values('curso-inscripciones:listar', 'Permiso para ver lista de inscripciones de cursos');
insert into permiso(nombre, descripcion) values('curso-inscripciones:crear', 'Permiso para agregar una inscripción de curso');
insert into permiso(nombre, descripcion) values('curso-inscripciones:eliminar', 'Permiso para eliminar una inscripción de curso');
insert into permiso(nombre, descripcion) values('curso-inscripciones:obtenerUsuarios', 'Permiso para listar usuarios en inscripciones de cursos');
insert into permiso(nombre, descripcion) values('curso-inscripciones:cambiarEstado', 'Permiso para cambiar el estado de inscripción de curso');

-- Asignar permisos al rol administrador (id_rol = 1)
insert into rol_permiso (id_rol, id_permiso)
select 1, p.id_permiso
from permiso p
where p.nombre in (
	'curso-inscripciones:ver',
	'curso-inscripciones:listar',
	'curso-inscripciones:crear',
	'curso-inscripciones:eliminar',
	'curso-inscripciones:obtenerUsuarios',
	'curso-inscripciones:cambiarEstado'
)
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso
);
