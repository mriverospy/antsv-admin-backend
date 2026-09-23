INSERT INTO public.permiso (nombre, descripcion) VALUES('paginas:borrar', 'paginas borrar');
INSERT INTO public.permiso (nombre, descripcion) VALUES('paginas:ver', 'paginas ver');
INSERT INTO public.permiso (nombre, descripcion) VALUES('paginas:listar', 'paginas listar');
INSERT INTO public.permiso (nombre, descripcion) VALUES('paginas:crear', 'paginas crear');

--cargo los permisos al administrador
INSERT INTO public.rol_permiso (id_rol,id_permiso)
    SELECT r.id_rol , p.id_permiso
    FROM public.rol r, public.permiso p
    WHERE LOWER(r.nombre) = LOWER('administrador')
      AND p.nombre IN ('paginas:borrar', 'paginas:ver', 'paginas:listar', 'paginas:crear', 'paginas:borrar');

