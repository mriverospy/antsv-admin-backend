CREATE TABLE public.noticia (
    id_noticia SERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    contenido TEXT NOT NULL,
    fecha_creacion TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    estado BOOLEAN
);


---- permisos
insert into permiso(nombre, descripcion) values ('noticias:listar','Permiso para listar noticias.');
insert into permiso(nombre, descripcion) values ('noticias:crear','Permiso para crear noticia.');
insert into permiso(nombre, descripcion) values ('noticias:editar','Permiso para editar noticia.');
insert into permiso(nombre, descripcion) values ('noticias:borrar','Permiso para borrar noticia.');
insert into permiso(nombre, descripcion) values ('noticias:ver','Permiso para ver noticias.');

---- asignar a un rol
insert into rol_permiso(id_rol, id_permiso) values (1,24);
insert into rol_permiso(id_rol, id_permiso) values (1,25);
insert into rol_permiso(id_rol, id_permiso) values (1,26);
insert into rol_permiso(id_rol, id_permiso) values (1,27);