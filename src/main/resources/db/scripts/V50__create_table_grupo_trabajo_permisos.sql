--TABLAS PARA ADMINISTRADOR GESTION DE GRUPOS DE TRABAJO

CREATE TABLE grupo_trabajo (
    id_grupo_trabajo SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    estado BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP
);

-- Tabla de registros grupo_trabajo_usuario
CREATE TABLE grupo_trabajo_usuario (
    id_grupo_trabajo_usuario SERIAL PRIMARY KEY,
    id_usuario INTEGER REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    id_grupo_trabajo INTEGER REFERENCES grupo_trabajo(id_grupo_trabajo) ON DELETE CASCADE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_usuario, id_grupo_trabajo)
);

-- Índices para optimización
CREATE INDEX idx_grupo_trabajo_estado ON grupo_trabajo(estado);
CREATE INDEX idx_grupo_trabajo_nombre ON grupo_trabajo(nombre);
CREATE INDEX idx_grupo_trabajo_usuario_grupo ON grupo_trabajo_usuario(id_grupo_trabajo);
CREATE INDEX idx_grupo_trabajo_usuario_usuario ON grupo_trabajo_usuario(id_usuario);

-- PERMISOS GRUPOS DE TRABAJO
INSERT INTO permiso(nombre, descripcion) VALUES('grupo-trabajo:ver', 'Permiso para ver un Grupo de Trabajo');
INSERT INTO permiso(nombre, descripcion) VALUES('grupo-trabajo:listar', 'Permiso para ver el módulo Grupos de Trabajo');
INSERT INTO permiso(nombre, descripcion) VALUES('grupo-trabajo:crear', 'Permiso para crear un nuevo Grupo de Trabajo');
INSERT INTO permiso(nombre, descripcion) VALUES('grupo-trabajo:editar', 'Permiso para editar un Grupo de Trabajo');
INSERT INTO permiso(nombre, descripcion) VALUES('grupo-trabajo:actualizarEstado', 'Permiso para actualizar estado de un Grupo de Trabajo');
INSERT INTO permiso(nombre, descripcion) VALUES('grupo-trabajo:asociarUsuarios', 'Permiso para asociar usuarios a un Grupo de Trabajo');
INSERT INTO permiso(nombre, descripcion) VALUES('grupo-trabajo:desasociarUsuarios', 'Permiso para desasociar usuarios de un Grupo de Trabajo');
INSERT INTO permiso(nombre, descripcion) VALUES('grupo-trabajo:verUsuarios', 'Permiso para ver usuarios asociados a un Grupo de Trabajo');

-- ASIGNAR PERMISOS AL ROL ADMIN (id_rol = 1)
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'grupo-trabajo:ver',
    'grupo-trabajo:listar',
    'grupo-trabajo:crear',
    'grupo-trabajo:editar',
    'grupo-trabajo:actualizarEstado',
    'grupo-trabajo:asociarUsuarios',
    'grupo-trabajo:desasociarUsuarios',
    'grupo-trabajo:verUsuarios'
)
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);
