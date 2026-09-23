--Se elimina la columna metodo de registro
ALTER TABLE usuario DROP COLUMN metodo_registro;

--Se agrega la columna codigo a la tabla Metodo de Registro
ALTER TABLE metodo_registro add column codigo character varying(2) UNIQUE;
UPDATE metodo_registro SET codigo = 'IE' WHERE nombre = 'Identidad Electrónica';
UPDATE metodo_registro SET codigo = 'MU', nombre = 'Menú Usuario' WHERE nombre = 'Básico';
UPDATE metodo_registro SET codigo = 'RL', nombre = 'Landing' WHERE nombre = 'LinkedIn';

INSERT INTO usuario_metodo_registro (id_usuario, id_metodo_registro)
SELECT
    u.id_usuario,
    mr.id_metodo_registro
FROM
    usuario u
        CROSS JOIN
    metodo_registro mr
WHERE
    mr.codigo = 'MU'
  AND NOT EXISTS (
    SELECT 1
    FROM usuario_metodo_registro umr
    WHERE umr.id_usuario = u.id_usuario
      AND umr.id_metodo_registro = mr.id_metodo_registro
);


INSERT INTO public.permiso (nombre, descripcion) VALUES('metodo-registro:listar', 'Listar Metodo de Registro');

--cargo los permisos al administrador
INSERT INTO public.rol_permiso (id_rol,id_permiso)
SELECT r.id_rol , p.id_permiso
FROM public.rol r, public.permiso p
WHERE LOWER(r.nombre) = LOWER('administrador')
  AND p.nombre IN ('metodo-registro:listar');


